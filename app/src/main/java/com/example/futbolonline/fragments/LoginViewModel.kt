package com.example.futbolonline.fragments

import android.content.ContentValues
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.futbolonline.entidades.Usuario
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await

class LoginViewModel : ViewModel() {
    val db = Firebase.firestore
    // TODO: Implement the ViewModel

    suspend fun mailYContraseniaCorrectas(email: String, contrasenia: String): Boolean {

        var mailYContraseniaCorrectas: Boolean = false

        val questionRef = db.collection("usuarios")
        val query = questionRef

        try {
            val data = query
                .get()
                .await()
            if(data != null){
                //val usuario  = data.toObject<Usuario>()
            }
        } catch (e: Exception) {

        }
        return mailYContraseniaCorrectas
    }

}
