package com.example.futbolonline.fragments

import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class RegistrarseViewModel : ViewModel() {
    val NRO_MINIMO_CARACTERES_NOMBRE_USUARIO: Int = 4
    val NRO_MAXIMO_CARACTERES_NOMBRE_USUARIO: Int = 20

    val EDAD_MINIMA_USUARIO: Int = 12
    val EDAD_MAXIMA_USUARIO: Int = 60

    val NRO_MINIMO_CARACTERES_CONTRASENIA: Int = 6
    val NRO_MAXIMO_CARACTERES_CONTRASENIA: Int = 20

    val db = Firebase.firestore

    val registroValido = MutableLiveData<Boolean>()

    fun determinarRegistroValido(email: String, nombre: String, edad: Int, contrasenia: String) {
        if (emailEsValido(email) && nombreEsValido(nombre) && edadEsValida(edad) && contraseniaEsValida(
                contrasenia
            )
        ) {
            registroValido.value = true
        }
    }

    fun emailEsValido(email: String): Boolean {
        var emailEsValido: Boolean = false
        if (tieneFormatoEmailValido(email) && !emailTieneCuentaAsociada(email)) {
            emailEsValido = true
        }
        return emailEsValido
    }

    fun nombreEsValido(nombre: String): Boolean {
        var nombreEsValido = false
        if (nombreTieneNroCaracteresEnRango(
                nombre,
                NRO_MINIMO_CARACTERES_NOMBRE_USUARIO,
                NRO_MAXIMO_CARACTERES_NOMBRE_USUARIO
            ) && !nombreEstaUsado(nombre)
        ) {
            nombreEsValido = true
        }
        return nombreEsValido
    }

    fun edadEsValida(edad: Int): Boolean {
        var edadEsValida = false
        if (edad >= EDAD_MINIMA_USUARIO && edad <= EDAD_MAXIMA_USUARIO) {
            edadEsValida = true
        }
        return edadEsValida
    }

    fun contraseniaEsValida(contrasenia: String): Boolean {
        var contraseniaEsValida = false
        if (contraseniaTieneNroCaracteresEnRango(
                contrasenia,
                NRO_MINIMO_CARACTERES_CONTRASENIA,
                NRO_MAXIMO_CARACTERES_CONTRASENIA
            )
        ) {
            contraseniaEsValida = true
        }
        return contraseniaEsValida
    }

    fun tieneFormatoEmailValido(email: String): Boolean {
        return EmailValidator.isEmailValid(email)
    }

    fun emailTieneCuentaAsociada(email: String): Boolean {
        var tieneCuentaAsociada: Boolean = true
        db.collection("usuarios")
            .whereEqualTo("email", email)
            .get()
            .addOnSuccessListener { snapshot ->
                if (snapshot != null) {
                    if (snapshot.size() == 0) {
                        tieneCuentaAsociada = false
                    }
                }
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents: ", exception)
            }
        return tieneCuentaAsociada
    }

    fun nombreTieneNroCaracteresEnRango(
        nombre: String,
        nroMinimoCaracteres: Int,
        nroMaximoCaracteres: Int
    ): Boolean {
        var tieneNroCaracteresEnRango: Boolean = false
        if (nombre.length >= nroMinimoCaracteres && nombre.length <= nroMaximoCaracteres) {
            tieneNroCaracteresEnRango = true
        }
        return tieneNroCaracteresEnRango
    }

    fun nombreEstaUsado(nombre: String): Boolean {
        var nombreEstaUsado: Boolean = true
        db.collection("usuarios")
            .whereEqualTo("nombre", nombre)
            .get()
            .addOnSuccessListener { snapshot ->
                if (snapshot != null) {
                    if (snapshot.size() == 0) {
                        nombreEstaUsado = false
                    }
                }
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents: ", exception)
            }
        return nombreEstaUsado
    }

    fun contraseniaTieneNroCaracteresEnRango(
        contrasenia: String,
        nroMinimoCaracteres: Int,
        nroMaximoCaracteres: Int
    ): Boolean {
        var tieneNroCaracteresEnRango: Boolean = false
        if (contrasenia.length >= nroMinimoCaracteres && contrasenia.length <= nroMaximoCaracteres) {
            tieneNroCaracteresEnRango = true
        }
        return tieneNroCaracteresEnRango
    }

    class EmailValidator {
        companion object {
            @JvmStatic
            val EMAIL_REGEX = "^[A-Za-z](.*)([@]{1})(.{1,})(\\.)(.{1,})";
            fun isEmailValid(email: String): Boolean {
                return EMAIL_REGEX.toRegex().matches(email);
            }
        }
    }
}

