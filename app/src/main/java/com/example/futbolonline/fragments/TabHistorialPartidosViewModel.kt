package com.example.futbolonline.fragments

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.futbolonline.entidades.Partido
import com.example.futbolonline.entidades.PartidoUsuario
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.firestore.ktx.toObjects
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.*
import kotlin.collections.ArrayList

class TabHistorialPartidosViewModel : ViewModel() {
    private var _partidosList: MutableLiveData<MutableList<Partido>> = MutableLiveData()
    val historialPartidosList: LiveData<MutableList<Partido>> get() = _partidosList

    val NOMBRE_COLECCION_PARTIDOS = "partidos"
    val NOMBRE_COLECCION_USUARIOS = "usuarios"
    val NOMBRE_COLECCION_PARTIDO_USUARIO = "partidousuario"

    val db = Firebase.firestore

    fun refrescarListaHistorialPartidos(emailUsuarioLogeado: String) {
        viewModelScope.launch(Dispatchers.Main) {
            _partidosList = getHistorialPartidos(emailUsuarioLogeado)
        }
    }

    suspend fun getHistorialPartidos(emailUsuarioLogeado: String): MutableLiveData<MutableList<Partido>> {
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
                    if (usuarioEstaEnPartido(
                            partido.nombreEvento,
                            emailUsuarioLogeado
                        ) && partidoUsuarioEsVisible(
                            partido.nombreEvento,
                            emailUsuarioLogeado
                        ) && pasoFechaDelEvento(partido.fechaYHora)
                    ) {
                        partidosAMostrar.add(partido)
                    }
                }
                //_partidosList.value = data.toObjects<Partido>() as MutableList<Partido>
                _partidosList.value = partidosAMostrar
            }
        } catch (e: Exception) {
            Log.d("error", "aca paso algooo")
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

    suspend fun partidoUsuarioEsVisible(
        nombrePartido: String,
        emailUsuarioLogeado: String
    ): Boolean {
        var esVisible = false

        var partidoUsuario: PartidoUsuario

        val questionRef = db.collection(NOMBRE_COLECCION_PARTIDO_USUARIO)
            .document(emailUsuarioLogeado + nombrePartido)
        val query = questionRef

        try {
            val data = query
                .get()
                .await()
            if (data != null) {
                val partidoUsuarioDeBaseDeDatos = data.toObject<PartidoUsuario>()
                if (partidoUsuarioDeBaseDeDatos != null) {
                    if (partidoUsuarioDeBaseDeDatos.visibleEnHistorial) {
                        esVisible = true
                    }
                }
            }
        } catch (e: Exception) {
        }
        return esVisible
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
        Log.d("pasoHistorial", "$pasoFechaDelEvento $fechaEvento")
        return pasoFechaDelEvento
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

    suspend fun eliminarPartidoDeHistorial(
        emailUsuarioLogeado: String,
        nombrePartido: String
    ): Boolean {
        var eliminadoDeHistorial: Boolean = true
        val idPartidoUsuarioAEliminarDeHistorial: String = emailUsuarioLogeado + nombrePartido
        val partidoUsuarioNoVisibleEnHistorial = PartidoUsuario(
            idPartidoUsuarioAEliminarDeHistorial,
            emailUsuarioLogeado,
            nombrePartido,
            false
        )
        try {
            db.collection(NOMBRE_COLECCION_PARTIDO_USUARIO)
                .document(idPartidoUsuarioAEliminarDeHistorial)
                .set(partidoUsuarioNoVisibleEnHistorial)
                .await()
        } catch (ex: Exception) {
            eliminadoDeHistorial = false
        }
        return eliminadoDeHistorial
    }
}