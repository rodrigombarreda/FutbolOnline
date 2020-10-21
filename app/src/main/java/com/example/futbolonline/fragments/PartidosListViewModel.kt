package com.example.futbolonline.fragments

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.futbolonline.entidades.Partido
import com.example.futbolonline.entidades.PartidoUsuario
import com.example.futbolonline.entidades.Usuario
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.firestore.ktx.toObjects
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class PartidosListViewModel : ViewModel() {
    // TODO: Implement the ViewModel

    private var _partidosList: MutableLiveData<MutableList<Partido>> = MutableLiveData()
    val partidosList: LiveData<MutableList<Partido>> get() = _partidosList

    val NOMBRE_COLECCION_PARTIDOS = "partidos"
    val NOMBRE_COLECCION_USUARIOS = "usuarios"
    val NOMBRE_COLECCION_PARTIDO_USUARIO = "partidousuario"

    val db = Firebase.firestore

    suspend fun getTodosLosPartidos(): MutableLiveData<MutableList<Partido>> {
        val questionRef = db.collection(NOMBRE_COLECCION_PARTIDOS)
        val query = questionRef

        try {
            val data = query
                .get()
                .await()
            if (data != null) {
                _partidosList.value = data.toObjects<Partido>() as MutableList<Partido>
            }
        } catch (e: Exception) {

        }
        return _partidosList
    }

    fun refrescarListaPartidos() {
        viewModelScope.launch(Dispatchers.Main) {
            _partidosList = getTodosLosPartidos()
        }
    }

    suspend fun sePuedeUnir(emailUsuarioLogeado: String, partido: Partido): Boolean {
        var sePuedeUnir: Boolean = false
        val usuario: Usuario? = getUsuarioPorMail(emailUsuarioLogeado)
        // TODO: Validar q se pueda unir

        return true//sePuedeUnir
    }

    suspend fun unirUsuarioAPartido(emailUsuarioLogeado: String, nombrePartido: String): Boolean {
        var seUnio: Boolean = true
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
            seUnio = false
        }
        return seUnio
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
}