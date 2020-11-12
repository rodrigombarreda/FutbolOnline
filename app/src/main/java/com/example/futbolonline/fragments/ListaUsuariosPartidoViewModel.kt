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
import com.google.firebase.firestore.ktx.toObjects
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class ListaUsuariosPartidoViewModel : ViewModel() {

    private var _usuariosList: MutableLiveData<MutableList<Usuario>> = MutableLiveData()
    val usuariosList: LiveData<MutableList<Usuario>> get() = _usuariosList

    val NOMBRE_COLECCION_USUARIOS = "usuarios"
    val NOMBRE_COLECCION_PARTIDO_USUARIO = "partidousuario"

    val db = Firebase.firestore

    fun refrescarListaUsuariosDePartido(nombreEvento: String) {
        viewModelScope.launch(Dispatchers.Main) {
            _usuariosList = getUsuariosDePartido(nombreEvento)
        }
    }

    suspend fun getUsuariosDePartido(nombreEvento: String): MutableLiveData<MutableList<Usuario>> {
        val questionRef = db.collection(NOMBRE_COLECCION_USUARIOS)
        val query = questionRef

        try {
            val data = query
                .get()
                .await()
            if (data != null) {
                var usuarios = data.toObjects<Usuario>() as MutableList<Usuario>
                var usuariosAMostrar: MutableList<Usuario> = ArrayList<Usuario>()
                for (u in usuarios) {
                    if (usuarioEstaEnPartido(nombreEvento, u.email)) {
                        usuariosAMostrar.add(u)
                    }
                }
                //_partidosList.value = data.toObjects<Partido>() as MutableList<Partido>
                _usuariosList.value = usuariosAMostrar
            }
        } catch (e: Exception) {

        }
        return _usuariosList
    }

    suspend fun usuarioEstaEnPartido(nombrePartido: String, emailUsuario: String): Boolean {
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
                    if (partidoUsuario.emailUsuario == emailUsuario && partidoUsuario.nombrePartido == nombrePartido) {
                        usuarioEstaEnPartido = true
                        break
                    }
                }
            }
        } catch (e: Exception) {
        }
        return usuarioEstaEnPartido
    }
}