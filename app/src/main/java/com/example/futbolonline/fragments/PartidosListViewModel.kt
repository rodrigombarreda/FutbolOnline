package com.example.futbolonline.fragments

import android.Manifest
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.futbolonline.entidades.Partido
import com.example.futbolonline.entidades.PartidoUsuario
import com.example.futbolonline.entidades.Usuario
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.firestore.ktx.toObjects
import com.google.firebase.ktx.Firebase
import com.google.maps.android.SphericalUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.*
import kotlin.collections.ArrayList

class PartidosListViewModel : ViewModel() {
    // TODO: Implement the ViewModel

    private var _partidosList: MutableLiveData<MutableList<Partido>> = MutableLiveData()
    val partidosList: LiveData<MutableList<Partido>> get() = _partidosList

    val NOMBRE_COLECCION_PARTIDOS = "partidos"
    val NOMBRE_COLECCION_USUARIOS = "usuarios"
    val NOMBRE_COLECCION_PARTIDO_USUARIO = "partidousuario"

    val db = Firebase.firestore

    suspend fun getTodosLosPartidos(emailUsuarioLogeado: String): MutableLiveData<MutableList<Partido>> {
        val questionRef = db.collection(NOMBRE_COLECCION_PARTIDOS)
        val query = questionRef

        try {
            val data = query
                .get()
                .await()
            if (data != null) {
                var partidos = data.toObjects<Partido>() as MutableList<Partido>
                var partidosAMostrar: MutableList<Partido> = ArrayList<Partido>()
                for (partido in partidos) {
                    if (partido.cantidadJugadoresFaltantes > 0 && !usuarioEstaEnPartido(
                            partido.nombreEvento,
                            emailUsuarioLogeado
                        ) && !pasoFechaDelEvento(partido.fechaYHora)
                    ) {
                        partidosAMostrar.add(partido)
                    }
                }
                //_partidosList.value = data.toObjects<Partido>() as MutableList<Partido>
                _partidosList.value = partidosAMostrar
            }
        } catch (e: Exception) {
        }
        return _partidosList
    }

    suspend fun usuarioEstaEnPartido(nombrePartido: String, emailUsuarioLogeado: String): Boolean {
        var usuarioEstaEnPartido = false

        val questionRef = db.collection(NOMBRE_COLECCION_PARTIDO_USUARIO)
        val query = questionRef

        try {
            val data = query
                .get()
                .await()
            if (data != null) {
                var partidosUsuarios =
                    data.toObjects<PartidoUsuario>() as MutableList<PartidoUsuario>
                for (partidoUsuario in partidosUsuarios) {
                    if (partidoUsuario.emailUsuario == emailUsuarioLogeado && partidoUsuario.nombrePartido == nombrePartido) {
                        usuarioEstaEnPartido = true
                        break
                    }
                }
            }
        } catch (e: Exception) {
        }
        return usuarioEstaEnPartido
    }

    fun pasoFechaDelEvento(fechaEvento: String): Boolean {
        var pasoFechaDelEvento = false
        try {
            var fechaActual = Date()
            var fechaDelEvento = Date(fechaEvento)
            if (fechaActual > fechaDelEvento) {
                pasoFechaDelEvento = true
            }
        } catch (e: Exception) {
            Log.d("errorFecha", e.toString())
        }
        Log.d("pasoBuscar", "$pasoFechaDelEvento $fechaEvento")
        return pasoFechaDelEvento
    }

    fun refrescarListaPartidos(emailUsuarioLogeado: String) {
        viewModelScope.launch(Dispatchers.Main) {
            _partidosList = getTodosLosPartidos(emailUsuarioLogeado)
        }
    }

    suspend fun sePuedeUnir(emailUsuarioLogeado: String, partido: Partido): Boolean {
        var sePuedeUnir: Boolean = false
        val usuario: Usuario? = getUsuarioPorMail(emailUsuarioLogeado)
        if (usuario != null) {
            if (usuarioTieneCalificacionRequerida(
                    usuario.calificacion,
                    partido.calificacionMinima
                ) && usuarioTieneEdadRequerida(
                    usuario.fechaNacimiento,
                    partido.edadMinima,
                    partido.edadMaxima
                ) && usuarioTieneGeneroAdmitido(usuario.genero, partido.generoAdmitido)
                && partido.cantidadJugadoresFaltantes > 0
            ) {
                sePuedeUnir = true
            }
        }
        return sePuedeUnir
    }

    suspend fun unirUsuarioAPartido(emailUsuarioLogeado: String, nombrePartido: String): Boolean {
        var seUnio: Boolean = true
        val partidoUsuario = PartidoUsuario(
            emailUsuarioLogeado + nombrePartido,
            emailUsuarioLogeado,
            nombrePartido,
            true
        )
        try {
            db.collection(NOMBRE_COLECCION_PARTIDO_USUARIO).document(partidoUsuario.id)
                .set(partidoUsuario)
                .await()
        } catch (ex: Exception) {
            seUnio = false
        }
        val partido: Partido? = getPartidoPorNombre(nombrePartido)
        actualizarCantidadJugadoresFaltantes(partido)
        return seUnio
    }

    suspend fun actualizarCantidadJugadoresFaltantes(partido: Partido?) {
        if (partido != null) {
            partido.cantidadJugadoresFaltantes -= 1
            try {
                db.collection(NOMBRE_COLECCION_PARTIDOS).document(partido.nombreEvento)
                    .set(partido)
                    .await()
            } catch (ex: Exception) {

            }
        }
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
                if (usuarioDeBaseDeDatos != null) {
                    usuario = usuarioDeBaseDeDatos
                }
            }
        } catch (e: Exception) {
        }
        return usuario
    }

    suspend fun getPartidoPorNombre(nombre: String): Partido? {
        var partido: Partido? = null

        val questionRef = db.collection(NOMBRE_COLECCION_PARTIDOS).document(nombre)
        val query = questionRef

        try {
            val data = query
                .get()
                .await()
            if (data != null) {
                val partidoDeBaseDeDatos = data.toObject<Partido>()
                if (partidoDeBaseDeDatos != null) {
                    partido = partidoDeBaseDeDatos
                }
            }
        } catch (e: Exception) {
        }
        return partido
    }

    fun usuarioTieneCalificacionRequerida(
        calificacionUsuario: Int,
        calificacionMinimaRequerida: Int
    ): Boolean {
        var usuarioTieneCalificacionRequerida: Boolean = false
        if (calificacionUsuario >= calificacionMinimaRequerida) {
            usuarioTieneCalificacionRequerida = true
        }
        return usuarioTieneCalificacionRequerida
    }

    fun usuarioTieneEdadRequerida(
        fechaNacimiento: String,
        edadMinimaAdmitida: Int,
        edadMaximaAdmitida: Int
    ): Boolean {
        var usuarioTieneEdadRequerida: Boolean = false
        val fechaActual = Date()
        val dateNacimiento = Date(fechaNacimiento)
        val edad = fechaActual.year - dateNacimiento.year
        Log.d("edad", edad.toString())
        if (edad in edadMinimaAdmitida..edadMaximaAdmitida) {
            usuarioTieneEdadRequerida = true
        }
        return usuarioTieneEdadRequerida
    }

    fun usuarioTieneGeneroAdmitido(generoUsuario: String, generoAdmitido: String): Boolean {
        var usuarioTieneGeneroAdmitido: Boolean = false
        if (generoUsuario == generoAdmitido) {
            usuarioTieneGeneroAdmitido = true
        }
        return usuarioTieneGeneroAdmitido
    }

}