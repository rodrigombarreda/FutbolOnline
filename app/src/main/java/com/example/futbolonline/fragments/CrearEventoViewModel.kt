package com.example.futbolonline.fragments

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.lifecycle.ViewModel
import com.example.futbolonline.R
import com.example.futbolonline.entidades.Partido
import com.example.futbolonline.entidades.Usuario
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlin.contracts.Returns

class CrearEventoViewModel : ViewModel() {
    // TODO: Implementar view model
    val NRO_MINIMO_CARACTERES_NOMBRE_EVENTO: Int = 4
    val NRO_MAXIMO_CARACTERES_NOMBRE_EVENTO: Int = 25

    val NRO_MINIMO_JUGADORES_TOTALES: Int = 6
    val NRO_MAXIMO_JUGADORES_TOTALES: Int = 32

    val EDAD_MINIMA_USUARIO: Int = 12
    val EDAD_MAXIMA_USUARIO: Int = 60

    val VALOR_GENERO_MASCULINO: String = "masculino"
    val VALOR_GENERO_FEMENINO: String = "femenino"

    val MENSAJE_ERROR_CARACTERES_NOMBRE_EVENTO_FUERA_DE_RANGO: String =
        "El nombre debe tener entre $NRO_MINIMO_CARACTERES_NOMBRE_EVENTO y $NRO_MAXIMO_CARACTERES_NOMBRE_EVENTO"
    val MENSAJE_ERROR_NOMBRE_EVENTO_USADO: String = "Nombre de evento usado"

    val MENSAJE_ERROR_JUGADORES_TOTALES_FUERA_DE_RANGO: String =
        "Cantidad de jugadores totales debe ser entre $NRO_MINIMO_JUGADORES_TOTALES y $NRO_MAXIMO_JUGADORES_TOTALES"

    val MENSAJE_ERROR_JUGADORES_FALTANTES_FUERA_DE_RANGO: String =
        "Cantidad de jugadores faltantes debe ser entre $NRO_MINIMO_JUGADORES_TOTALES y $NRO_MAXIMO_JUGADORES_TOTALES"
    val MENSAJE_ERROR_JUGADORES_FALTANTES_MAYOR_A_TOTALES: String =
        "Jugadores faltantes debe ser menor a la cantidad de jugadores totales"

    val MENSAJE_ERROR_EDAD_MINIMA_FUERA_DE_RANGO: String =
        "Edad minima debe ser entre $EDAD_MINIMA_USUARIO y $EDAD_MAXIMA_USUARIO"

    val MENSAJE_ERROR_EDAD_MAXIMA_FUERA_DE_RANGO: String =
        "Edad máxima debe ser entre $EDAD_MINIMA_USUARIO y $EDAD_MAXIMA_USUARIO"
    val MENSAJE_ERROR_EDAD_MAXIMA_MENOR_A_EDAD_MINIMA: String =
        "Edad máxima debe ser mayor o igual a edad mínima"

    val NOMBRE_COLECCION_PARTIDOS: String = "partidos"
    val NOMBRE_COLECCION_USUARIOS: String = "usuarios"

    val db = Firebase.firestore

    val parentJob = Job()
    val scope = CoroutineScope(Dispatchers.Default + parentJob)

    val USUARIO_PREFERENCES: String = "usuarioPreferences"

    suspend fun eventoEsValido(
        inputNombreEvento: EditText,
        inputJugadoresTotales: EditText,
        inputJugadoresFaltantes: EditText,
        radioBtnMasculinoIsChecked: Boolean,
        radioBtnFemeninoIsChecked: Boolean,
        inputEdadMinima: EditText,
        inputEdadMaxima: EditText,
        inputCalificacionMinima: EditText,
        emailUsuarioLogeado: String
    ): Boolean {
        var eventoEsValido: Boolean = false
        nombreEventoEsValido(inputNombreEvento)
        jugadoresTotalesEsValido(inputJugadoresTotales)
        jugadoresFaltantesEsValido(
            inputJugadoresFaltantes,
            inputJugadoresTotales.text.toString().toInt()
        )
        seSeleccionoGenero(radioBtnMasculinoIsChecked, radioBtnFemeninoIsChecked)
        edadMinimaEsValida(inputEdadMinima)
        edadMaximaEsValida(inputEdadMaxima, inputEdadMinima.text.toString().toInt())
        calificacionMinimaEsValida(inputCalificacionMinima, emailUsuarioLogeado)
        if (nombreEventoEsValido(inputNombreEvento) && jugadoresTotalesEsValido(
                inputJugadoresTotales
            ) && jugadoresFaltantesEsValido(
                inputJugadoresFaltantes,
                inputJugadoresTotales.text.toString().toInt()
            ) && seSeleccionoGenero(
                radioBtnMasculinoIsChecked,
                radioBtnFemeninoIsChecked
            ) && edadMinimaEsValida(inputEdadMinima) && edadMaximaEsValida(
                inputEdadMaxima,
                inputEdadMinima.text.toString().toInt()
            ) && calificacionMinimaEsValida(
                inputCalificacionMinima, emailUsuarioLogeado
            )
        ) {
            eventoEsValido = true
        }
        return eventoEsValido
    }

    suspend fun registrarEvento(
        nombreEvento: String,
        jugadoresTotales: Int,
        jugadoresFaltantes: Int,
        radioBtnFemeninoIsChecked: Boolean,
        edadMinima: Int,
        edadMaxima: Int,
        calificacionMinima: Int
    ): Boolean {
        var seRegistro: Boolean = false
        var generoAdmitido: String = obtenerGenero(radioBtnFemeninoIsChecked)
        var partidoNuevo: Partido = Partido(
            nombreEvento,
            jugadoresTotales,
            jugadoresFaltantes,
            generoAdmitido,
            edadMinima,
            edadMaxima,
            calificacionMinima
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

    fun obtenerGenero(
        radioBtnFemeninoIsChecked: Boolean
    ): String {
        lateinit var genero: String
        if (radioBtnFemeninoIsChecked) {
            genero = VALOR_GENERO_FEMENINO
        } else {
            genero = VALOR_GENERO_MASCULINO
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
            inputNombreEvento.setError(MENSAJE_ERROR_CARACTERES_NOMBRE_EVENTO_FUERA_DE_RANGO)
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
                    inputNombreEvento.setError(MENSAJE_ERROR_NOMBRE_EVENTO_USADO)
                }
            }
        } catch (e: Exception) {

        }
        return nombreEventoEstaUsado
    }

    fun jugadoresTotalesEsValido(inputJugadoresTotales: EditText): Boolean {
        var jugadoresTotalesEsValido: Boolean = false
        val jugadoresTotales = inputJugadoresTotales.text.toString().toInt()
        if (jugadoresTotales >= NRO_MINIMO_JUGADORES_TOTALES && jugadoresTotales <= NRO_MAXIMO_JUGADORES_TOTALES) {
            jugadoresTotalesEsValido = true
        } else {
            inputJugadoresTotales.setError(MENSAJE_ERROR_JUGADORES_TOTALES_FUERA_DE_RANGO)
        }
        return jugadoresTotalesEsValido
    }

    fun jugadoresFaltantesEsValido(
        inputJugadoresFaltantes: EditText,
        cantidadJugadoresTotales: Int
    ): Boolean {
        var jugadoresFaltantesEsValido: Boolean = false
        val jugadoresFaltantes: Int = inputJugadoresFaltantes.text.toString().toInt()
        if (jugadoresTotalesEsValido(inputJugadoresFaltantes)) {
            if (jugadoresFaltantes < cantidadJugadoresTotales) {
                jugadoresFaltantesEsValido = true
            } else {
                inputJugadoresFaltantes.setError(MENSAJE_ERROR_JUGADORES_FALTANTES_MAYOR_A_TOTALES)
            }
        } else {
            inputJugadoresFaltantes.setError(MENSAJE_ERROR_JUGADORES_FALTANTES_FUERA_DE_RANGO)
        }
        return jugadoresFaltantesEsValido
    }

    fun seSeleccionoGenero(
        radioBtnMasculinoIsChecked: Boolean,
        radioBtnFemeninoIsChecked: Boolean
    ): Boolean {
        var seSeleccionoGenero: Boolean = radioBtnMasculinoIsChecked || radioBtnFemeninoIsChecked
        return seSeleccionoGenero
    }

    fun edadMinimaEsValida(inputEdadMinima: EditText): Boolean {
        var edadMinimaEsValida: Boolean = false
        val edadMinima: Int = inputEdadMinima.text.toString().toInt()
        if (edadMinima >= EDAD_MINIMA_USUARIO && edadMinima <= EDAD_MAXIMA_USUARIO) {
            edadMinimaEsValida = true
        } else {
            inputEdadMinima.setError(MENSAJE_ERROR_EDAD_MINIMA_FUERA_DE_RANGO)
        }
        return edadMinimaEsValida
    }

    fun edadMaximaEsValida(inputEdadMaxima: EditText, edadMinima: Int): Boolean {
        var edadMaximaEsValida: Boolean = false
        val edadMaxima: Int = inputEdadMaxima.text.toString().toInt()
        if (edadMinimaEsValida(inputEdadMaxima)) {
            if (edadMaxima >= edadMinima) {
                edadMaximaEsValida = true
            } else {
                inputEdadMaxima.setError(MENSAJE_ERROR_EDAD_MAXIMA_MENOR_A_EDAD_MINIMA)
            }
        } else {
            inputEdadMaxima.setError(MENSAJE_ERROR_EDAD_MAXIMA_FUERA_DE_RANGO)
        }
        return edadMaximaEsValida
    }

    fun calificacionMinimaEsValida(
        inputCalificacionMinima: EditText,
        emailUsuarioLogeado: String
    ): Boolean {
        var calificacionMinimaEsValida: Boolean = false
        val calificacionMinima = inputCalificacionMinima.text.toString().toInt()
        val calificacionUsuario: Int = obtenerCalificacionUsuario(emailUsuarioLogeado)
        if (calificacionUsuario >= calificacionMinima) {
            calificacionMinimaEsValida = true
        }
        return calificacionMinimaEsValida
    }

    fun obtenerCalificacionUsuario(emailUsuarioLogeado: String): Int {
        var calificacionUsuario: Int = 0
        val usuarioLogeado: Usuario? = getUsuarioPorMail(emailUsuarioLogeado)
        if (usuarioLogeado != null) {
            calificacionUsuario = usuarioLogeado.calificacion
        }
        return calificacionUsuario
    }

    fun getUsuarioPorMail(email: String): Usuario? {
        var usuario: Usuario? = null
        scope.launch {
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
        }
        return usuario
    }
}