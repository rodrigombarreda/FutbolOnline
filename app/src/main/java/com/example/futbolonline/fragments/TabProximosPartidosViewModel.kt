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

class TabProximosPartidosViewModel : ViewModel() {
    private var _partidosList: MutableLiveData<MutableList<Partido>> = MutableLiveData()
    val proximosPartidosList: LiveData<MutableList<Partido>> get() = _partidosList

    val NOMBRE_COLECCION_PARTIDOS = "partidos"
    val NOMBRE_COLECCION_USUARIOS = "usuarios"
    val NOMBRE_COLECCION_PARTIDO_USUARIO = "partidousuario"

    val db = Firebase.firestore

    fun refrescarListaProximosPartidos(emailUsuarioLogeado: String) {
        viewModelScope.launch(Dispatchers.Main) {
            _partidosList = getPartidosProximos(emailUsuarioLogeado)
        }
    }

    suspend fun getPartidosProximos(emailUsuarioLogeado: String): MutableLiveData<MutableList<Partido>> {
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
                        ) && !pasoFechaDelEvento(partido.fechaYHora)
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
        Log.d("pasoProximos", "$pasoFechaDelEvento $fechaEvento")
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

    suspend fun sacarUsuarioDePartido(emailUsuarioLogeado: String, nombrePartido: String): Boolean {
        var salio: Boolean = true
        val idPartidoUsuarioAEliminar: String = emailUsuarioLogeado + nombrePartido
        try {
            db.collection(NOMBRE_COLECCION_PARTIDO_USUARIO).document(idPartidoUsuarioAEliminar)
                .delete()
                .await()
        } catch (ex: Exception) {
            salio = false
        }
        val partido: Partido? = getPartidoPorNombre(nombrePartido)
        actualizarCantidadJugadoresFaltantes(partido)
        return salio
    }

    suspend fun actualizarCantidadJugadoresFaltantes(partido: Partido?) {
        if (partido != null) {
            partido.cantidadJugadoresFaltantes += 1
            try {
                db.collection(NOMBRE_COLECCION_PARTIDOS).document(partido.nombreEvento)
                    .set(partido)
                    .await()
            } catch (ex: Exception) {

            }
        }
    }
}