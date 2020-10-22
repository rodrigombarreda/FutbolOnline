package com.example.futbolonline.fragments

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.futbolonline.R
import com.example.futbolonline.entidades.Partido
import com.example.futbolonline.entidades.PartidoUsuario
import com.example.futbolonline.entidades.Usuario
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.*
import kotlin.contracts.Returns

class CrearEventoViewModel : ViewModel() {
    // valores
    val NRO_MINIMO_CARACTERES_NOMBRE_EVENTO: Int = 4
    val NRO_MAXIMO_CARACTERES_NOMBRE_EVENTO: Int = 25

    val NRO_MINIMO_JUGADORES_TOTALES: Int = 6
    val NRO_MAXIMO_JUGADORES_TOTALES: Int = 32

    val EDAD_MINIMA_USUARIO: Int = 12
    val EDAD_MAXIMA_USUARIO: Int = 60

    val NRO_DIAS_MINIMO_PARA_ARMAR_PARTIDO: Int = 1

    // mensajes de error
    val MENSAJE_ERROR_CARACTERES_NOMBRE_EVENTO_FUERA_DE_RANGO: String =
        "El nombre debe tener entre $NRO_MINIMO_CARACTERES_NOMBRE_EVENTO y $NRO_MAXIMO_CARACTERES_NOMBRE_EVENTO caracteres"
    val MENSAJE_ERROR_NOMBRE_EVENTO_USADO: String = "Nombre de evento usado"

    val MENSAJE_ERROR_JUGADORES_TOTALES_FUERA_DE_RANGO: String =
        "Cantidad de jugadores totales debe ser entre $NRO_MINIMO_JUGADORES_TOTALES y $NRO_MAXIMO_JUGADORES_TOTALES"
    val MENSAJE_ERROR_JUGADORES_TOTALES_VACIO: String =
        "Se debe especificar la cantidad de jugadores totales"

    val MENSAJE_ERROR_JUGADORES_FALTANTES_NO_MENOR_A_TOTALES: String =
        "Jugadores faltantes debe ser menor a la cantidad de jugadores totales"
    val MENSAJE_ERROR_ESPECIFICACION_FALTANTES_SIN_ESPECIFICAR_TOTALES: String =
        "Se debe especificar la cantidad faltante con la total"
    val MENSAJE_ERROR_JUGADORES_FALTANTES_VACIO: String =
        "Se debe especificar la cantidad de jugadores faltantes"

    val MENSAJE_ERROR_EDAD_MINIMA_FUERA_DE_RANGO: String =
        "Edad minima debe ser entre $EDAD_MINIMA_USUARIO y $EDAD_MAXIMA_USUARIO"
    val MENSAJE_ERROR_EDAD_MINIMA_MAYOR_A_EDAD_USUARIO: String =
        "La edad minima debe ser menor o igual a su edad"
    val MENSAJE_ERROR_EDAD_MINIMA_VACIA: String = "Se debe especificar la edad minima"

    val MENSAJE_ERROR_EDAD_MAXIMA_MENOR_A_EDAD_MINIMA: String =
        "Edad máxima debe ser mayor o igual a edad mínima"
    val MENSAJE_ERROR_EDAD_MAXIMA_MENOR_A_EDAD_USUARIO: String =
        "La edad maxima debe ser mayor o igual a su edad"
    val MENSAJE_ERROR_ESPECIFICACION_EDAD_MAXIMA_SIN_EDAD_MINIMA: String =
        "Se debe especificar edad maxima con la minima"
    val MENSAJE_ERROR_EDAD_MAXIMA_VACIA: String = "Se debe especificar la edad maxima"

    val MENSAJE_ERROR_CALIFICACION_MINIMA_MAYOR_A_LA_DEL_USUARIO: String =
        "La calificacion minima debe ser igual o mayor a su calificacion"
    val MENSAJE_ERROR_CALIFICACION_MINIMA_VACIA: String =
        "Se debe especificar la calificacion minima"

    // colecciones
    val NOMBRE_COLECCION_PARTIDOS: String = "partidos"
    val NOMBRE_COLECCION_USUARIOS: String = "usuarios"
    val NOMBRE_COLECCION_PARTIDO_USUARIO = "partidousuario"

    // firestore
    val db = Firebase.firestore

    // preferences
    val USUARIO_PREFERENCES: String = "usuarioPreferences"

    // live data
    var errorNombreEvento = MutableLiveData<String>()
    var errorJugadoresTotalesEvento = MutableLiveData<String>()
    var errorJugadoresFaltantesEvento = MutableLiveData<String>()
    var errorEdadMinimaEvento = MutableLiveData<String>()
    var errorEdadMaximaEvento = MutableLiveData<String>()
    var errorCalificacionMinimaEvento = MutableLiveData<String>()

    suspend fun eventoEsValido(
        inputNombreEvento: EditText,
        inputJugadoresTotales: EditText,
        inputJugadoresFaltantes: EditText,
        inputEdadMinima: EditText,
        inputEdadMaxima: EditText,
        inputCalificacionMinima: EditText,
        emailUsuarioLogeado: String,
        fecha: Date
    ): Boolean {
        var eventoEsValido: Boolean = false
        nombreEventoEsValido(inputNombreEvento)
        jugadoresTotalesEsValido(inputJugadoresTotales)
        jugadoresFaltantesEsValido(
            inputJugadoresFaltantes,
            inputJugadoresTotales
        )
        edadMinimaEsValida(inputEdadMinima, emailUsuarioLogeado)
        edadMaximaEsValida(inputEdadMaxima, inputEdadMinima, emailUsuarioLogeado)
        calificacionMinimaEsValida(inputCalificacionMinima, emailUsuarioLogeado)
        if (nombreEventoEsValido(inputNombreEvento) && jugadoresTotalesEsValido(
                inputJugadoresTotales
            ) && jugadoresFaltantesEsValido(
                inputJugadoresFaltantes,
                inputJugadoresTotales
            ) && edadMinimaEsValida(inputEdadMinima, emailUsuarioLogeado) && edadMaximaEsValida(
                inputEdadMaxima,
                inputEdadMinima, emailUsuarioLogeado
            ) && calificacionMinimaEsValida(
                inputCalificacionMinima, emailUsuarioLogeado
            ) && fechaEsValida(fecha)
        ) {
            eventoEsValido = true
        }
        Log.d("Evento es valido: ", eventoEsValido.toString())
        return eventoEsValido
    }

    suspend fun registrarEvento(
        nombreEvento: String,
        jugadoresTotales: Int,
        jugadoresFaltantes: Int,
        edadMinima: Int,
        edadMaxima: Int,
        calificacionMinima: Int,
        emailUsuarioLogeado: String,
        cadenaFecha: String
    ): Boolean {
        var seRegistro: Boolean = true
        var generoAdmitido: String = obtenerGenero(emailUsuarioLogeado)
        var partidoNuevo: Partido = Partido(
            nombreEvento,
            jugadoresTotales,
            jugadoresFaltantes,
            generoAdmitido,
            edadMinima,
            edadMaxima,
            calificacionMinima,
            emailUsuarioLogeado, cadenaFecha
        )

        try {
            db.collection(NOMBRE_COLECCION_PARTIDOS).document(partidoNuevo.nombreEvento)
                .set(partidoNuevo)
                .await()
        } catch (ex: Exception) {
            seRegistro = false
        }

        return seRegistro
    }

    suspend fun obtenerGenero(emailUsuarioLogeado: String): String {
        var genero: String = ""
        val usuarioLogeado: Usuario? = getUsuarioPorMail(emailUsuarioLogeado)
        if (usuarioLogeado != null) {
            genero = usuarioLogeado.genero
        }
        return genero
    }

    suspend fun nombreEventoEsValido(inputNombreEvento: EditText): Boolean {
        var nombreEventoEsValido: Boolean = false
        if (nombreEventoTieneCaracteresEnRango(
                inputNombreEvento,
                NRO_MINIMO_CARACTERES_NOMBRE_EVENTO,
                NRO_MAXIMO_CARACTERES_NOMBRE_EVENTO
            ) && !nombreEventoEstaUsado(inputNombreEvento)
        ) {
            nombreEventoEsValido = true
        }
        return nombreEventoEsValido
    }

    fun nombreEventoTieneCaracteresEnRango(
        inputNombreEvento: EditText,
        nroMinimoCaracteresNombreEvento: Int,
        nroMaximoCaracteresNombreEvento: Int
    ): Boolean {
        var nombreEventoTieneCaracteresEnRango: Boolean = false
        var nombreEvento = inputNombreEvento.text.toString()
        if (nombreEvento.length >= nroMinimoCaracteresNombreEvento && nombreEvento.length <= nroMaximoCaracteresNombreEvento) {
            nombreEventoTieneCaracteresEnRango = true
        } else {
            errorNombreEvento.postValue(
                MENSAJE_ERROR_CARACTERES_NOMBRE_EVENTO_FUERA_DE_RANGO
            )

        }
        return nombreEventoTieneCaracteresEnRango
    }

    suspend fun nombreEventoEstaUsado(inputNombreEvento: EditText): Boolean {
        var nombreEventoEstaUsado: Boolean = true
        val nombreEvento: String = inputNombreEvento.text.toString()

        val questionRef = db.collection(NOMBRE_COLECCION_PARTIDOS).document(nombreEvento)
        val query = questionRef

        try {
            val data = query
                .get()
                .await()
            if (data != null) {
                val partido = data.toObject<Partido>()
                if (partido == null) {
                    nombreEventoEstaUsado = false
                } else {
                    errorNombreEvento.postValue(
                        MENSAJE_ERROR_NOMBRE_EVENTO_USADO
                    )
                }
            }
        } catch (e: Exception) {

        }
        return nombreEventoEstaUsado
    }

    fun jugadoresTotalesEsValido(inputJugadoresTotales: EditText): Boolean {
        var jugadoresTotalesEsValido: Boolean = false
        if (inputJugadoresTotales.text.toString() != "") {
            val jugadoresTotales: Int = inputJugadoresTotales.text.toString().toInt()
            if (jugadoresTotales in NRO_MINIMO_JUGADORES_TOTALES..NRO_MAXIMO_JUGADORES_TOTALES) {
                jugadoresTotalesEsValido = true
            } else {
                errorJugadoresTotalesEvento.postValue(MENSAJE_ERROR_JUGADORES_TOTALES_FUERA_DE_RANGO)
            }
        } else {
            errorJugadoresTotalesEvento.postValue(MENSAJE_ERROR_JUGADORES_TOTALES_VACIO)
        }
        return jugadoresTotalesEsValido
    }

    fun jugadoresFaltantesEsValido(
        inputJugadoresFaltantes: EditText,
        inputJugadoresTotales: EditText
    ): Boolean {
        var jugadoresFaltantesEsValido: Boolean = false
        if (inputJugadoresFaltantes.text.toString() != "" && inputJugadoresTotales.text.toString() != "") {
            val jugadoresFaltantes: Int = inputJugadoresFaltantes.text.toString().toInt()
            val jugadoresTotales: Int = inputJugadoresTotales.text.toString().toInt()

            if (jugadoresFaltantes < jugadoresTotales) {
                jugadoresFaltantesEsValido = true
            } else {
                errorJugadoresFaltantesEvento.postValue(
                    MENSAJE_ERROR_JUGADORES_FALTANTES_NO_MENOR_A_TOTALES
                )
            }
        } else {
            if (inputJugadoresFaltantes.text.toString() != "") {
                errorJugadoresFaltantesEvento.postValue(
                    MENSAJE_ERROR_ESPECIFICACION_FALTANTES_SIN_ESPECIFICAR_TOTALES
                )
            } else {
                errorJugadoresFaltantesEvento.postValue(MENSAJE_ERROR_JUGADORES_FALTANTES_VACIO)
            }
        }
        return jugadoresFaltantesEsValido
    }

    suspend fun edadMinimaEsValida(
        inputEdadMinima: EditText,
        emailUsuarioLogeado: String
    ): Boolean {
        var edadMinimaEsValida: Boolean = false
        if (inputEdadMinima.text.toString() != "") {
            val edadMinima: Int = inputEdadMinima.text.toString().toInt()
            if (edadMinima in EDAD_MINIMA_USUARIO..EDAD_MAXIMA_USUARIO) {
                val edadUsuario = obtenerEdadUsuario(emailUsuarioLogeado)
                if (edadUsuario >= edadMinima) {
                    edadMinimaEsValida = true
                } else {
                    errorEdadMinimaEvento.postValue(MENSAJE_ERROR_EDAD_MINIMA_MAYOR_A_EDAD_USUARIO)
                }
            } else {
                errorEdadMinimaEvento.postValue(MENSAJE_ERROR_EDAD_MINIMA_FUERA_DE_RANGO)
            }
        } else {
            errorEdadMinimaEvento.postValue(MENSAJE_ERROR_EDAD_MINIMA_VACIA)
        }
        return edadMinimaEsValida
    }

    suspend fun edadMaximaEsValida(
        inputEdadMaxima: EditText,
        inputEdadMinima: EditText,
        emailUsuarioLogeado: String
    ): Boolean {
        var edadMaximaEsValida: Boolean = false
        if (inputEdadMaxima.text.toString() != "" && inputEdadMinima.text.toString() != "") {
            val edadMaxima: Int = inputEdadMaxima.text.toString().toInt()
            val edadMinima: Int = inputEdadMinima.text.toString().toInt()

            if (edadMaxima >= edadMinima) {
                val edadUsuario = obtenerEdadUsuario(emailUsuarioLogeado)
                if (edadUsuario <= edadMaxima) {
                    edadMaximaEsValida = true
                } else {
                    errorEdadMaximaEvento.postValue(MENSAJE_ERROR_EDAD_MAXIMA_MENOR_A_EDAD_USUARIO)
                }
            } else {
                errorEdadMaximaEvento.postValue(MENSAJE_ERROR_EDAD_MAXIMA_MENOR_A_EDAD_MINIMA)
            }
        } else {
            if (inputEdadMaxima.text.toString() != "") {
                errorEdadMaximaEvento.postValue(
                    MENSAJE_ERROR_ESPECIFICACION_EDAD_MAXIMA_SIN_EDAD_MINIMA
                )
            } else {
                errorEdadMaximaEvento.postValue(MENSAJE_ERROR_EDAD_MAXIMA_VACIA)
            }
        }
        return edadMaximaEsValida
    }

    suspend fun obtenerEdadUsuario(emailUsuarioLogeado: String): Int {
        var edadUsuario: Int = 0
        val usuarioLogeado: Usuario? = getUsuarioPorMail(emailUsuarioLogeado)
        if (usuarioLogeado != null) {
            edadUsuario = usuarioLogeado.edad
        }
        return edadUsuario
    }

    suspend fun calificacionMinimaEsValida(
        inputCalificacionMinima: EditText,
        emailUsuarioLogeado: String
    ): Boolean {
        var calificacionMinimaEsValida: Boolean = false
        if (inputCalificacionMinima.text.toString() != "") {
            val calificacionMinima = inputCalificacionMinima.text.toString().toInt()
            val calificacionUsuario: Int = obtenerCalificacionUsuario(emailUsuarioLogeado)
            if (calificacionUsuario >= calificacionMinima) {
                calificacionMinimaEsValida = true
            } else {
                errorCalificacionMinimaEvento.postValue(
                    MENSAJE_ERROR_CALIFICACION_MINIMA_MAYOR_A_LA_DEL_USUARIO
                )
            }
        } else {
            errorCalificacionMinimaEvento.postValue(MENSAJE_ERROR_CALIFICACION_MINIMA_VACIA)
        }
        return calificacionMinimaEsValida
    }

    suspend fun obtenerCalificacionUsuario(emailUsuarioLogeado: String): Int {
        var calificacionUsuario: Int = 0
        val usuarioLogeado: Usuario? = getUsuarioPorMail(emailUsuarioLogeado)
        if (usuarioLogeado != null) {
            calificacionUsuario = usuarioLogeado.calificacion
        }
        return calificacionUsuario
    }

    suspend fun getUsuarioPorMail(email: String): Usuario? {
        var usuario: Usuario? = null

        val questionRef = db.collection(NOMBRE_COLECCION_USUARIOS).document(email)
        val query = questionRef

        try {
            val data = query
                .get()
                .await()
            if (data != null) {
                val usuarioDeBaseDeDatos = data.toObject<Usuario>()
                if (usuario == null) {
                    usuario = usuarioDeBaseDeDatos
                }
            }
        } catch (e: Exception) {

        }
        return usuario
    }

    fun fechaEsValida(fecha: Date): Boolean {
        var fechaEsValida = false
        var diaDespuesFechaActual = Date()
        diaDespuesFechaActual.date += NRO_DIAS_MINIMO_PARA_ARMAR_PARTIDO

        if (fecha > diaDespuesFechaActual) {
            fechaEsValida = true
        }
        return fechaEsValida
    }

    suspend fun unirCreadorAPartido(emailUsuarioLogeado: String, nombrePartido: String) {
        val partidoUsuario = PartidoUsuario(
            emailUsuarioLogeado + nombrePartido,
            emailUsuarioLogeado,
            nombrePartido
        )
        try {
            db.collection(NOMBRE_COLECCION_PARTIDO_USUARIO).document(partidoUsuario.id)
                .set(partidoUsuario)
                .await()
        } catch (ex: Exception) {
        }
    }
}