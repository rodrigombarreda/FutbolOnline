package com.example.futbolonline.fragments

import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.navigation.findNavController
import com.example.futbolonline.entidades.Usuario
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase

class RegistrarseViewModel : ViewModel() {
    val NRO_MINIMO_CARACTERES_NOMBRE_USUARIO: Int = 4
    val NRO_MAXIMO_CARACTERES_NOMBRE_USUARIO: Int = 20

    val EDAD_MINIMA_USUARIO: Int = 12
    val EDAD_MAXIMA_USUARIO: Int = 60

    val NRO_MINIMO_CARACTERES_CONTRASENIA: Int = 6
    val NRO_MAXIMO_CARACTERES_CONTRASENIA: Int = 20

    val VALOR_GENERO_MASCULINO: String = "masculino"
    val VALOR_GENERO_FEMENINO: String = "femenino"

    //val NOMBRE_COLECCION_USUARIOS: String = "usuarios"

    val db = Firebase.firestore

    //val registroValido = MutableLiveData<Boolean>()

    // TODO: Completar valores para mensajes en el front
    val emailEnUso = MutableLiveData<Boolean>()

    fun registrarUsuario(
        email: String,
        nombre: String,
        edad: Int,
        contrasenia: String,
        radioBtnMasculinoIsChecked: Boolean,
        radioBtnFemeninoIsChecked: Boolean
    ): Boolean {
        var seRegistro: Boolean = false
        seRegistro = registrarUsuarioEnBaseDeDatos(
            email,
            nombre,
            edad,
            contrasenia,
            radioBtnMasculinoIsChecked,
            radioBtnFemeninoIsChecked
        )
        return seRegistro
    }

    fun registroEsValido(
        email: String,
        nombre: String,
        edad: Int,
        contrasenia: String,
        radioBtnMasculinoIsChecked: Boolean,
        radioBtnFemeninoIsChecked: Boolean
    ): Boolean {
        var registroEsValido: Boolean = false
        if (emailEsValido(email) && nombreEsValido(nombre) && edadEsValida(edad) && contraseniaEsValida(
                contrasenia
            ) && seSeleccionoGenero(radioBtnMasculinoIsChecked, radioBtnFemeninoIsChecked)
        ) {
            registroEsValido = true
        }
        return registroEsValido
    }

    fun registrarUsuarioEnBaseDeDatos(
        email: String,
        nombre: String,
        edad: Int,
        contrasenia: String,
        radioBtnMasculinoIsChecked: Boolean,
        radioBtnFemeninoIsChecked: Boolean
    ): Boolean {
        var seRegistro: Boolean = true
        var genero: String = obtenerGenero(radioBtnMasculinoIsChecked, radioBtnFemeninoIsChecked)
        var usuarioNuevo: Usuario = Usuario(email, nombre, genero, edad, contrasenia)
        try {
            db.collection("usuarios").document(usuarioNuevo.email).set(usuarioNuevo)
        } catch (ex: Exception) {
            seRegistro = false
        }
        return seRegistro
    }

    fun obtenerGenero(
        radioBtnMasculinoIsChecked: Boolean,
        radioBtnFemeninoIsChecked: Boolean
    ): String {
        lateinit var genero: String
        if (radioBtnFemeninoIsChecked) {
            genero = VALOR_GENERO_FEMENINO
        } else {
            genero = VALOR_GENERO_MASCULINO
        }
        return genero
    }

    fun emailEsValido(email: String): Boolean {
        var emailEsValido: Boolean = false
        if (tieneFormatoEmailValido(email) && !emailTieneCuentaAsociada(email)) {
            emailEsValido = true
        }
        Log.w("email", emailEsValido.toString())
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
        Log.w("nombre", nombreEsValido.toString())
        return nombreEsValido
    }

    fun edadEsValida(edad: Int): Boolean {
        var edadEsValida = false
        if (edad >= EDAD_MINIMA_USUARIO && edad <= EDAD_MAXIMA_USUARIO) {
            edadEsValida = true
        }
        Log.w("edad", edadEsValida.toString())
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
        Log.w("contra", contrasenia.toString())
        return contraseniaEsValida
    }

    fun tieneFormatoEmailValido(email: String): Boolean {
        return EmailValidator.isEmailValid(email)
    }

    fun emailTieneCuentaAsociada(email: String): Boolean {
        var tieneCuentaAsociada: Boolean = false
        db.collection("usuarios")
            .whereEqualTo("email", email)
            .get()
            .addOnSuccessListener { snapshot ->
                if (snapshot != null) {
                    var objectUsuario: Usuario
                    for (usuario in snapshot) {
                        objectUsuario = usuario.toObject()
                        if (objectUsuario.email == email) {
                            tieneCuentaAsociada = true
                            emailEnUso.value = true
                        } else {
                            emailEnUso.value = true
                        }
                    }
                }
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents: ", exception)
            }
        Log.w("emailCuentaAsociada", tieneCuentaAsociada.toString())
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
        var nombreEstaUsado: Boolean = false
        db.collection("usuarios")
            .whereEqualTo("nombre", nombre)
            .get()
            .addOnSuccessListener { snapshot ->
                if (snapshot != null) {
                    var objectUsuario: Usuario
                    for (usuario in snapshot) {
                        objectUsuario = usuario.toObject()
                        if (objectUsuario.nombre == nombre) {
                            nombreEstaUsado = true
                        }
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

    fun seSeleccionoGenero(
        radioBtnMasculinoIsCkecked: Boolean,
        radioBtnFemeninoIsChecked: Boolean
    ): Boolean {
        var seSeleccionoGenero: Boolean = radioBtnMasculinoIsCkecked || radioBtnFemeninoIsChecked
        Log.w("genero", seSeleccionoGenero.toString())
        return seSeleccionoGenero
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

