package com.example.futbolonline.fragments

import androidx.lifecycle.ViewModel
import com.example.futbolonline.entidades.Usuario
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

class cambioContraseniaViewModel: ViewModel(){
    val db = Firebase.firestore
    suspend fun esLaMismaContrasenia(
        contrasenia: String,
        mail: String?
    ): Boolean {
        var ContraseniaCorrectas: Boolean = false
        if (mail != null) {
            if(!contrasenia.isBlank() && !mail.isBlank()){
                val questionRef = db.collection("usuarios").document(mail)
                val query = questionRef
                try {
                    val data = query
                        .get().await()
                    if(data!=null){
                        val usuario = data.toObject<Usuario>()
                        if (usuario != null){
                            if(usuario.contrasenia==contrasenia){
                                ContraseniaCorrectas=true
                            }
                        }
                    }
                }catch (e: Exception){

                }
            }
        }


        return ContraseniaCorrectas
    }
    suspend fun cambiarContrasenia(contraseniaNuevaUno: String,contraseniaNuevaDos:String,mail: String?):Boolean{
        var contraseniaCambiada: Boolean = false
        if(mail!=null){
            if(!contraseniaNuevaUno.isBlank() && !contraseniaNuevaDos.isBlank()){
                val questionRef = db.collection("usuarios").document(mail)
                val query = questionRef
                try {
                    if (contraseniaNuevaDos==contraseniaNuevaUno){
                        val data = query
                            .get()
                            .await()
                        if(data!=null){
                            val usuarioBd = data.toObject<Usuario>()
                            if(usuarioBd!=null){
                                val usuario:Usuario=Usuario(mail,usuarioBd.nombre,usuarioBd.genero,usuarioBd.fechaNacimiento,contraseniaNuevaUno,usuarioBd.calificacion)
                                db.collection("usuarios").document(mail).set(usuario)
                                contraseniaCambiada=true
                            }
                        }

                    }

                }catch (e: Exception){

                }
            }
        }
        return contraseniaCambiada
    }

}