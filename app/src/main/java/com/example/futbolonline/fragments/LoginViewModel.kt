package com.example.futbolonline.fragments

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class LoginViewModel : ViewModel() {
    // TODO: Implement the ViewModel
    val mailYContraseniaSonCorrectas = MutableLiveData<Boolean>()

    fun autenticarUsuario (email: String, contrasenia: String){
        // TODO: Implementar la funcion
        mailYContraseniaSonCorrectas.value = true
    }
}