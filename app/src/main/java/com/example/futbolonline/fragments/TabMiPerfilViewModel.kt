package com.example.futbolonline.fragments

import android.content.ContentValues
import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.futbolonline.entidades.Usuario
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase

class TabMiPerfilViewModel : ViewModel() {
    // TODO: Implement the ViewModel
    val NOMBRE_COLECCION_USUARIOS: String = "usuarios"

    val db = Firebase.firestore

    fun getUsuarioPorMail(email: String): Usuario{
        var usuario : Usuario = Usuario()
        db.collection(NOMBRE_COLECCION_USUARIOS).document(email)
            .get()
            .addOnSuccessListener { snapshot ->
                if (snapshot != null) {
                    usuario = snapshot.toObject<Usuario>()!!
                }
            }
            .addOnFailureListener { exception ->
                Log.w(ContentValues.TAG, "Error getting documents: ", exception)
            }
        return usuario
    }
}