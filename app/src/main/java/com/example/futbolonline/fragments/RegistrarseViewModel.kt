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
    // valores
    val NRO_MINIMO_CARACTERES_NOMBRE_USUARIO: Int = 4
    val NRO_MAXIMO_CARACTERES_NOMBRE_USUARIO: Int = 20

    val EDAD_MINIMA_USUARIO: Int = 12
    val EDAD_MAXIMA_USUARIO: Int = 60

    val NRO_MINIMO_CARACTERES_CONTRASENIA: Int = 6
    val NRO_MAXIMO_CARACTERES_CONTRASENIA: Int = 20

    val VALOR_GENERO_MASCULINO: String = "masculino"
    val VALOR_GENERO_FEMENINO: String = "femenino"

    val CALIFICACION_INICIAL_USUARIO: Int = 100

    // mensajes de error
    val MENSAJE_ERROR_EMAIL_FORMATO_INVALIDO: String = "Formato mail invalido"
    val MENSAJE_ERROR_EMAIL_EN_USO: String = "Email en uso"

    val MENSAJE_ERROR_NOMBRE_USUARIO_FUERA_DE_RANGO: String =
        "El nombre debe tener entre $NRO_MINIMO_CARACTERES_NOMBRE_USUARIO Y $NRO_MAXIMO_CARACTERES_NOMBRE_USUARIO caracteres"
    val MENSAJE_ERROR_NOMBRE_USUARIO_EN_USO: String = "Nombre en uso"

    val MENSAJE_ERROR_EDAD_USUARIO_FUERA_DE_RANGO: String =
        "La edad debe estar entre $EDAD_MINIMA_USUARIO y $EDAD_MAXIMA_USUARIO"
    val MENSAJE_ERROR_EDAD_USUARIO_VACIA: String = "Se debe especificar la edad"

    val MENSAJE_ERROR_GENERO_NO_SELECCIONADO: String = "Se debe seleccionar un genero"

    val MENSAJE_ERROR_CONTRASENIA_USUARIO_FUERA_DE_RANGO: String =
        "La contraseña debe tener entre $NRO_MINIMO_CARACTERES_CONTRASENIA Y $NRO_MAXIMO_CARACTERES_CONTRASENIA caracteres"

    // colecciones
    val NOMBRE_COLECCION_USUARIOS: String = "usuarios"

    // firestore
    val db = Firebase.firestore

    // live data
    var errorEmailUsuario = MutableLiveData<String>()
    var errorNombreUsuario = MutableLiveData<String>()
    var errorEdadUsuario = MutableLiveData<String>()
    var errorGeneroUsuario = MutableLiveData<String>()
    var errorContraseniaUsuario = MutableLiveData<String>()

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
        if (inputEdad.text.toString() != "") {
            if (inputEdad.text.toString()
                    .toInt() in EDAD_MINIMA_USUARIO..EDAD_MAXIMA_USUARIO
            ) {
                edadEsValida = true
            } else {
                errorEdadUsuario.postValue(MENSAJE_ERROR_EDAD_USUARIO_FUERA_DE_RANGO)
            }
        } else {
            errorEdadUsuario.postValue(MENSAJE_ERROR_EDAD_USUARIO_VACIA)
        }
        return edadEsValida
    }

    fun contraseniaEsValida(inputContrasenia: EditText): Boolean {
        var contraseniaEsValida = false
        if (contraseniaTieneNroCaracteresEnRango(
                inputContrasenia
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
            errorEmailUsuario.postValue(MENSAJE_ERROR_EMAIL_FORMATO_INVALIDO)
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
                } else {
                    errorEmailUsuario.postValue(MENSAJE_ERROR_EMAIL_EN_USO)
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
        if (inputNombre.text.toString().length in nroMinimoCaracteres..nroMaximoCaracteres) {
            tieneNroCaracteresEnRango = true
        } else {
            errorNombreUsuario.postValue(MENSAJE_ERROR_NOMBRE_USUARIO_FUERA_DE_RANGO)
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
                if (data.size() == 0) {
                    nombreEstaUsado = false
                } else {
                    errorNombreUsuario.postValue(MENSAJE_ERROR_NOMBRE_USUARIO_EN_USO)
                }
            }
        } catch (e: Exception) {

        }
        return nombreEstaUsado
    }

    fun contraseniaTieneNroCaracteresEnRango(
        inputContrasenia: EditText
    ): Boolean {
        var tieneNroCaracteresEnRango: Boolean = false
        if (inputContrasenia.text.toString().length >= NRO_MINIMO_CARACTERES_CONTRASENIA && inputContrasenia.text.toString().length <= NRO_MAXIMO_CARACTERES_CONTRASENIA) {
            tieneNroCaracteresEnRango = true
        } else {
            errorContraseniaUsuario.postValue(MENSAJE_ERROR_CONTRASENIA_USUARIO_FUERA_DE_RANGO)
        }
        return tieneNroCaracteresEnRango
    }

    fun seSeleccionoGenero(
        radioBtnMasculinoIsChecked: Boolean,
        radioBtnFemeninoIsChecked: Boolean
    ): Boolean {
        var seSeleccionoGenero: Boolean = radioBtnMasculinoIsChecked || radioBtnFemeninoIsChecked
        if (!seSeleccionoGenero) {
            errorGeneroUsuario.postValue(MENSAJE_ERROR_GENERO_NO_SELECCIONADO)
        }
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

