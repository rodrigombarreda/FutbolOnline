package com.example.futbolonline.fragments

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.example.futbolonline.R
import com.example.futbolonline.entidades.Usuario
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.registrarse_fragment.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class registrarse : Fragment() {

    companion object {
        fun newInstance() = registrarse()
    }

    val USUARIO_PREFERENCES: String = "usuarioPreferences"

    private lateinit var registrarseViewModel: RegistrarseViewModel
    lateinit var v: View
    lateinit var inputMailRegistrarse: EditText
    lateinit var inputNombreRegistrarse: EditText
    lateinit var inputEdadRegistrarse: EditText
    lateinit var radioGeneroRegistrarse: RadioGroup
    lateinit var radioBtnMasculinoRegistrarse: RadioButton
    lateinit var radioBtnFemeninoRegistrarse: RadioButton
    lateinit var inputContraseniaRegistrarse: EditText
    lateinit var btnRegistrarse: Button
    lateinit var txtYaTienesUnaCuentaRegistrarse: TextView
    lateinit var btnIrAIniciarSesionDeRegistrarse: Button

    val db = Firebase.firestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        v = inflater.inflate(R.layout.registrarse_fragment, container, false)
        inputMailRegistrarse = v.findViewById(R.id.inputMailRegistrarse)
        inputNombreRegistrarse = v.findViewById(R.id.inputNombreRegistrarse)
        inputEdadRegistrarse = v.findViewById(R.id.inputEdadRegistrarse)
        radioGeneroRegistrarse = v.findViewById(R.id.radioGeneroRegistrarse)
        radioBtnMasculinoRegistrarse = v.findViewById(R.id.radioBtnMasculinoRegistrarse)
        radioBtnFemeninoRegistrarse = v.findViewById(R.id.radioBtnFemeninoRegistrarse)
        inputContraseniaRegistrarse = v.findViewById(R.id.inputContraseniaRegistrarse)
        btnRegistrarse = v.findViewById(R.id.btnRegistrarse)
        txtYaTienesUnaCuentaRegistrarse = v.findViewById(R.id.txtYaTienesUnaCuentaRegistrarse)
        btnIrAIniciarSesionDeRegistrarse = v.findViewById(R.id.btnIrAIniciarSesionDeRegistrarse)
        return v
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        registrarseViewModel = ViewModelProvider(this).get(RegistrarseViewModel::class.java)
        // TODO: Use the ViewModel
        registrarseViewModel.emailEnUso.observe(viewLifecycleOwner, Observer { result ->
            if (result) {
                Snackbar.make(
                    v,
                    "EMAIL EN USO",
                    Snackbar.LENGTH_SHORT
                ).show()
            } else {
                Snackbar.make(
                    v,
                    "EMAIL NO USADO",
                    Snackbar.LENGTH_SHORT
                ).show()
            }
        })
    }

    override fun onStart() {
        super.onStart()

        val parentJob = Job()
        val scope = CoroutineScope(Dispatchers.Default + parentJob)

        btnRegistrarse.setOnClickListener {
            scope.launch {
                var registroEsValido = registrarseViewModel.registroEsValido(
                    inputMailRegistrarse,
                    inputNombreRegistrarse,
                    inputEdadRegistrarse,
                    inputContraseniaRegistrarse,
                    radioBtnMasculinoRegistrarse.isChecked,
                    radioBtnFemeninoRegistrarse.isChecked
                )
                if (registroEsValido) {
                    Snackbar.make(
                        v,
                        "Registrando...",
                        Snackbar.LENGTH_SHORT
                    ).show()
                    var seRegistro: Boolean = registrarseViewModel.registrarUsuario(
                        inputMailRegistrarse.text.toString(),
                        inputNombreRegistrarse.text.toString(),
                        inputEdadRegistrarse.text.toString().toInt(),
                        inputContraseniaRegistrarse.text.toString(),
                        radioBtnMasculinoRegistrarse.isChecked,
                        radioBtnFemeninoRegistrarse.isChecked
                    )
                    if (seRegistro) {
                        Snackbar.make(
                            v,
                            "Usuario registrado.",
                            Snackbar.LENGTH_SHORT
                        ).show()
                        val sharedPref: SharedPreferences = requireContext().getSharedPreferences(
                            USUARIO_PREFERENCES,
                            Context.MODE_PRIVATE
                        )
                        val editor = sharedPref.edit()
                        editor.putString("EMAIL_USUARIO", inputMailRegistrarse.text.toString())
                        editor.apply()
                        var accion =
                            registrarseDirections.actionRegistrarseToPaginaPrincipalContainer()
                        v.findNavController().navigate(accion)
                    } else {
                        Snackbar.make(
                            v,
                            "Error de red. Intentelo de nuevo mas tarde.",
                            Snackbar.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    Snackbar.make(
                        v,
                        "Datos no validos.",
                        Snackbar.LENGTH_SHORT
                    ).show()
                }
            }
        }

        btnIrAIniciarSesionDeRegistrarse.setOnClickListener {
            var accion = registrarseDirections.actionRegistrarseToLogin()
            v.findNavController().navigate(accion)
        }
    }

}