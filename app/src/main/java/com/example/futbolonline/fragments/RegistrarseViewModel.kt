package com.example.futbolonline.fragments

import android.content.ContentValues.TAG
import android.util.Log
import android.widget.EditText
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.futbolonline.entidades.Usuario
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

class RegistrarseViewModel : ViewModel() {
    val NRO_MINIMO_CARACTERES_NOMBRE_USUARIO: Int = 4
    val NRO_MAXIMO_CARACTERES_NOMBRE_USUARIO: Int = 20

    val EDAD_MINIMA_USUARIO: Int = 12
    val EDAD_MAXIMA_USUARIO: Int = 60

    val NRO_MINIMO_CARACTERES_CONTRASENIA: Int = 6
    val NRO_MAXIMO_CARACTERES_CONTRASENIA: Int = 20

    val VALOR_GENERO_MASCULINO: String = "masculino"
    val VALOR_GENERO_FEMENINO: String = "femenino"

    val CALIFICACION_INICIAL_USUARIO: Int = 100

    val NOMBRE_COLECCION_USUARIOS: String = "usuarios"

    val db = Firebase.firestore

    //val registroValido = MutableLiveData<Boolean>()

    // TODO: Completar valores para mensajes en el front
    val emailEnUso = MutableLiveData<Boolean>()

    suspend fun registroEsValido(
        inputEmail: EditText,
        inputNombre: EditText,
        inputEdad: EditText,
        inputContrasenia: EditText,
        radioBtnMasculinoIsChecked: Boolean,
        radioBtnFemeninoIsChecked: Boolean
    ): Boolean {
        var registroEsValido: Boolean = false
        emailEsValido(inputEmail)
        nombreEsValido(inputNombre)
        edadEsValida(inputEdad)
        contraseniaEsValida(inputContrasenia)
        if (emailEsValido(inputEmail) && nombreEsValido(inputNombre) && edadEsValida(inputEdad) && contraseniaEsValida(
                inputContrasenia
            ) && seSeleccionoGenero(radioBtnMasculinoIsChecked, radioBtnFemeninoIsChecked)
        ) {
            registroEsValido = true
        }
        return registroEsValido
    }

    suspend fun registrarUsuario(
        email: String,
        nombre: String,
        edad: Int,
        contrasenia: String,
        radioBtnFemeninoIsChecked: Boolean
    ): Boolean {
        var seRegistro: Boolean = true
        var genero: String = obtenerGenero(radioBtnFemeninoIsChecked)
        var usuarioNuevo: Usuario =
            Usuario(email, nombre, genero, edad, contrasenia, CALIFICACION_INICIAL_USUARIO)
        try {
            db.collection(NOMBRE_COLECCION_USUARIOS).document(usuarioNuevo.email).set(usuarioNuevo)
                .await()
        } catch (ex: Exception) {
            seRegistro = false
        }
        return seRegistro
    }

    fun obtenerGenero(
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

    suspend fun emailEsValido(inputEmail: EditText): Boolean {
        var emailEsValido: Boolean = false
        if (tieneFormatoEmailValido(inputEmail) && !emailTieneCuentaAsociada(inputEmail)) {
            emailEsValido = true
        }
        return emailEsValido
    }

    suspend fun nombreEsValido(inputNombre: EditText): Boolean {
        var nombreEsValido = false
        if (nombreTieneNroCaracteresEnRango(
                inputNombre,
                NRO_MINIMO_CARACTERES_NOMBRE_USUARIO,
                NRO_MAXIMO_CARACTERES_NOMBRE_USUARIO
            ) && !nombreEstaUsado(inputNombre)
        ) {
            nombreEsValido = true
        }
        return nombreEsValido
    }

    fun edadEsValida(inputEdad: EditText): Boolean {
        var edadEsValida = false
        if (inputEdad.text.toString().toInt() >= EDAD_MINIMA_USUARIO && inputEdad.text.toString()
                .toInt() <= EDAD_MAXIMA_USUARIO
        ) {
            edadEsValida = true
        } else {
            inputEdad.setError("La edad debe estar entre " + EDAD_MINIMA_USUARIO + " Y " + EDAD_MAXIMA_USUARIO)
        }
        return edadEsValida
    }

    fun contraseniaEsValida(inputContrasenia: EditText): Boolean {
        var contraseniaEsValida = false
        if (contraseniaTieneNroCaracteresEnRango(
                inputContrasenia,
                NRO_MINIMO_CARACTERES_CONTRASENIA,
                NRO_MAXIMO_CARACTERES_CONTRASENIA
            )
        ) {
            contraseniaEsValida = true
        }
        return contraseniaEsValida
    }

    fun tieneFormatoEmailValido(inputMail: EditText): Boolean {
        var tieneFormatoValido: Boolean = false
        if (EmailValidator.isEmailValid(inputMail.text.toString())) {
            tieneFormatoValido = true
        } else {
            inputMail.setError("El formato del mail es inválido")
        }
        return tieneFormatoValido
    }

    suspend fun emailTieneCuentaAsociada(inputEmail: EditText): Boolean {
        var tieneCuentaAsociada: Boolean = true
        var email: String = inputEmail.text.toString()
        val questionRef = db.collection(NOMBRE_COLECCION_USUARIOS).document(email)
        val query = questionRef

        try {
            val data = query
                .get()
                .await()
            if (data != null) {
                val usuario = data.toObject<Usuario>()
                if (usuario == null) {
                    tieneCuentaAsociada = false
                }
            }
        } catch (e: Exception) {

        }
        return tieneCuentaAsociada
    }

    fun nombreTieneNroCaracteresEnRango(
        inputNombre: EditText,
        nroMinimoCaracteres: Int,
        nroMaximoCaracteres: Int
    ): Boolean {
        var tieneNroCaracteresEnRango: Boolean = false
        if (inputNombre.text.toString().length >= nroMinimoCaracteres && inputNombre.text.toString().length <= nroMaximoCaracteres) {
            tieneNroCaracteresEnRango = true
        } else {
            inputNombre.setError("El nombre debe tener entre " + nroMinimoCaracteres + " y " + nroMaximoCaracteres + " caracteres")
        }
        return tieneNroCaracteresEnRango
    }

    suspend fun nombreEstaUsado(inputNombre: EditText): Boolean {
        var nombreEstaUsado: Boolean = true
        var nombre = inputNombre.text.toString()

        val questionRef = db.collection("usuarios")
        val query = questionRef

        try {
            val data = query
                .whereEqualTo("nombre", nombre)
                .get()
                .await()
            if (data != null) {
                Log.d("ES NULL", data.toString())
                if (data.size() == 0) {
                    Log.d("ESTA VACIO", data.size().toString())
                    nombreEstaUsado = false
                }
            }
        } catch (e: Exception) {

        }
        return nombreEstaUsado
    }

    fun contraseniaTieneNroCaracteresEnRango(
        inputContrasenia: EditText,
        nroMinimoCaracteres: Int,
        nroMaximoCaracteres: Int
    ): Boolean {
        var tieneNroCaracteresEnRango: Boolean = false
        if (inputContrasenia.text.toString().length >= nroMinimoCaracteres && inputContrasenia.text.toString().length <= nroMaximoCaracteres) {
            tieneNroCaracteresEnRango = true
        } else {
            inputContrasenia.setError("La contraseña debe tener entre " + nroMinimoCaracteres + " y " + nroMaximoCaracteres + " caracteres")
        }
        return tieneNroCaracteresEnRango
    }

    fun seSeleccionoGenero(
        radioBtnMasculinoIsChecked: Boolean,
        radioBtnFemeninoIsChecked: Boolean
    ): Boolean {
        var seSeleccionoGenero: Boolean = radioBtnMasculinoIsChecked || radioBtnFemeninoIsChecked
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

