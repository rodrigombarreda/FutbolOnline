package com.example.futbolonline.fragments

import android.content.ContentValues
import android.util.Log
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.*

class TabMiPerfilViewModel : ViewModel() {
    // TODO: Implement the ViewModel
    val NOMBRE_COLECCION_USUARIOS: String = "usuarios"
    val NOMBRE_COLECCION_PARTIDOS_USUARIOS: String = "partidousuario"
    val NOMBRE_COLECCION_PARTIDOS: String = "partidos"

    val db = Firebase.firestore

    var usuario = MutableLiveData<Usuario>()
    var cantidadPartidosJugados = MutableLiveData<Int>()
    var cantidadPartidosCreados = MutableLiveData<Int>()

    suspend fun getUsuarioPorMail(email: String) {
        val questionRef = db.collection(NOMBRE_COLECCION_USUARIOS).document(email)
        val query = questionRef

        try {
            val data = query
                .get()
                .await()
            if (data != null) {
                val usuarioDeBaseDeDatos = data.toObject<Usuario>()
                if (usuarioDeBaseDeDatos != null) {
                    usuario.value = usuarioDeBaseDeDatos
                }
            }
        } catch (e: Exception) {

        }
    }

    suspend fun getCantidadPartidosJugados(email: String) {
        var partidosJugados = 0
        val questionRef = db.collection(NOMBRE_COLECCION_PARTIDOS_USUARIOS)
        val query = questionRef

        try {
            val data = query.get().await()
            if (data != null) {
                val puList = data.toObjects<PartidoUsuario>()
                for (pu in puList) {
                    if (pu.emailUsuario == email && partidoPaso(pu.nombrePartido)) {
                        partidosJugados++
                    }
                }
                cantidadPartidosJugados.value = partidosJugados
            }
        } catch (e: Exception) {

        }
    }

    suspend fun partidoPaso(nombrePartido: String): Boolean {
        var paso = false

        val questionRef = db.collection(NOMBRE_COLECCION_PARTIDOS).document(nombrePartido)
        val query = questionRef

        try {
            val data = query.get().await()
            if (data != null) {
                val p = data.toObject<Partido>()
                if (p != null) {
                    if (Date(p.fechaYHora) < Date()) {
                        paso = true
                    }
                }
            }
        } catch (e: Exception) {

        }
        return paso
    }

    suspend fun getCantidadPartidosCreados(email: String) {
        var partidosCreados = 0
        val questionRef = db.collection(NOMBRE_COLECCION_PARTIDOS)
        val query = questionRef

        try {
            val data = query.get().await()
            if (data != null) {
                val pList = data.toObjects<Partido>()
                for (p in pList) {
                    if (p.emailCreador == email) {
                        partidosCreados++
                    }
                }
                cantidadPartidosCreados.value = partidosCreados
            }
        } catch (e: Exception) {

        }
    }

    fun refrescarPerfil(emailUsuario: String) {
        viewModelScope.launch(Dispatchers.Main) {
            getUsuarioPorMail(emailUsuario)
            getCantidadPartidosJugados(emailUsuario)
            getCantidadPartidosCreados(emailUsuario)
        }
    }
}