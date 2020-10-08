package com.example.futbolonline.fragments

import android.content.ContentValues
import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.futbolonline.entidades.Usuario
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class TabMiPerfilViewModel : ViewModel() {
    // TODO: Implement the ViewModel
    val NOMBRE_COLECCION_USUARIOS: String = "usuarios"

    val db = Firebase.firestore

    val parentJob = Job()
    val scope = CoroutineScope(Dispatchers.Default + parentJob)

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