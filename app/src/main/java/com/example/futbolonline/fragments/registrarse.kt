package com.example.futbolonline.fragments

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

class registrarse : Fragment() {

    companion object {
        fun newInstance() = registrarse()
    }

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
        registrarseViewModel.registroValido.observe(viewLifecycleOwner, Observer { result ->
            if (result) {
                Snackbar.make(
                    v,
                    "Se puede crear",
                    Snackbar.LENGTH_SHORT
                ).show()
                var usuarioNuevo: Usuario = Usuario(
                    inputMailRegistrarse.text.toString(),
                    inputNombreRegistrarse.text.toString(),
                    radioGeneroRegistrarse.checkedRadioButtonId.toString(),
                    inputEdadRegistrarse.text.toString().toInt(),
                    inputContraseniaRegistrarse.text.toString()
                )
                db.collection("usuarios").document(usuarioNuevo.email).set(usuarioNuevo)
                var accion = registrarseDirections.actionRegistrarseToPaginaPrincipalContainer()
                v.findNavController().navigate(accion)
            }
        })
    }

    override fun onStart() {
        super.onStart()
        btnRegistrarse.setOnClickListener {
            if (radioBtnMasculinoRegistrarse.isChecked || radioBtnFemeninoRegistrarse.isChecked) {
                registrarseViewModel.determinarRegistroValido(
                    inputMailRegistrarse.text.toString(),
                    inputNombreRegistrarse.text.toString(),
                    inputEdadRegistrarse.text.toString().toInt(),
                    inputContraseniaRegistrarse.text.toString()
                )
            } else {
                Snackbar.make(
                    v,
                    "Debe elegir un genero",
                    Snackbar.LENGTH_SHORT
                ).show()
            }
        }

        btnIrAIniciarSesionDeRegistrarse.setOnClickListener {
            var accion = registrarseDirections.actionRegistrarseToLogin()
            v.findNavController().navigate(accion)
        }
    }

}