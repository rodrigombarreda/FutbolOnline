package com.example.futbolonline.fragments

import android.content.ContentValues
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
    lateinit var uss: Usuario
    val db = Firebase.firestore

    suspend fun getUsuarioPorMail(email: String): Usuario? {
        var usuario: Usuario? = null
        val questionRef = db.collection(NOMBRE_COLECCION_USUARIOS).document(email)
        val query = questionRef
        Log.d("hola","4____________________________________________________________________")
        try {
            val data = query
                .get()
                .await()
            Log.d("hola","5____________________________________________________________________")
            if (data != null) {
                val usuarioDeBaseDeDatos = data.toObject<Usuario>()
                Log.d("usuario base de datos: ", usuarioDeBaseDeDatos.toString())
                Log.d("hola","6____________________________________________________________________")
                if (usuarioDeBaseDeDatos != null) {
                    Log.d("hola","7____________________________________________________________________")
                    usuario = usuarioDeBaseDeDatos
                    Log.d("usuario bd: ", usuario.toString())
                }
            }
        } catch (e: Exception) {

        }
        Log.d("user: ", usuario.toString())
        return usuario
    }

    fun refrescarPerfil(hola:String):Usuario? {
        var usuario: Usuario? = null
        Log.d("hola","3____________________________________________________________________")
        viewModelScope.launch(Dispatchers.Main) {
        usuario= getUsuarioPorMail(hola)
        }
        return usuario
    }
}