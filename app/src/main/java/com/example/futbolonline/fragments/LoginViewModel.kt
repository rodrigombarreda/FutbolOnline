package com.example.futbolonline.fragments

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.*

class LoginViewModel : ViewModel() {
    val db = Firebase.firestore
    // TODO: Implement the ViewModel
    //val mailYContraseniaSonCorrectas = MutableLiveData<Boolean>()

    val parentJob = Job()
    val handler = CoroutineExceptionHandler { _, throwable ->
        Log.d("demo", "handler: $throwable") // Prints "handler: java.io.IOException"
    }
    val scope = CoroutineScope(Dispatchers.Default + parentJob)

    suspend fun mailYContraseniaCorrectas(email: String, contrasenia: String): Boolean {

        var mailYContraseniaCorrectas: Boolean = false

        scope.launch {
            setMailYContraseniaSonCorrectas(email, contrasenia, mailYContraseniaCorrectas)
        }
        return mailYContraseniaCorrectas
    }

    suspend fun setMailYContraseniaSonCorrectas(
        email: String,
        contrasenia: String,
        mailYContraseniaCorrectas: Boolean
    ) {
        db.collection("usuarios").document(email).get().addOnSuccessListener { document ->

            if (document != null) {
                val contra = document.getString("contrasenia")
                if (contrasenia == contra) {
                    mailYContraseniaCorrectas == true
                }
            }
        }
    }
}
