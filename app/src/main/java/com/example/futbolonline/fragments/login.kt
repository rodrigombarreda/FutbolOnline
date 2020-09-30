package com.example.futbolonline.fragments

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import androidx.navigation.findNavController
import com.example.futbolonline.R
import com.google.android.material.snackbar.Snackbar


class login : Fragment() {

    companion object {
        fun newInstance() = login()
    }

    val USUARIO_PREFERENCES: String = "usuarioPreferences"

    private lateinit var loginViewModel: LoginViewModel
    lateinit var v: View
    lateinit var inputMailLogin: EditText
    lateinit var inputPasswordLogin: EditText
    lateinit var btnIniciarSesionLogin: Button
    lateinit var txtOlvidoSuContraseniaLogin: TextView
    lateinit var btnOlvidoSuContraseniaLogin: Button
    lateinit var txtNoTieneCuentaLogin: TextView
    lateinit var btnRegistreseLogin: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        v = inflater.inflate(R.layout.login_fragment, container, false)
        inputMailLogin = v.findViewById(R.id.inputMailLogin)
        inputPasswordLogin = v.findViewById(R.id.inputPasswordLogin)
        btnIniciarSesionLogin = v.findViewById(R.id.btnIniciarSesionLogin)
        txtOlvidoSuContraseniaLogin = v.findViewById(R.id.txtOlvidoSuContraseniaLogin)
        btnOlvidoSuContraseniaLogin = v.findViewById(R.id.btnOlvidoSuContraseniaLogin)
        txtNoTieneCuentaLogin = v.findViewById(R.id.txtNoTieneCuentaLogin)
        btnRegistreseLogin = v.findViewById(R.id.btnRegistreseLogin)
        return v
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        loginViewModel = ViewModelProvider(this).get(LoginViewModel::class.java)
        // TODO: Use the ViewModel
    }

    override fun onStart() {
        super.onStart()
        btnIniciarSesionLogin.setOnClickListener {
            var autenticacionExitosa = loginViewModel.mailYContraseniaCorrectas(
                inputMailLogin.text.toString(),
                inputPasswordLogin.text.toString()
            )
            if(autenticacionExitosa){
                val sharedPref: SharedPreferences = requireContext().getSharedPreferences(
                    USUARIO_PREFERENCES,
                    Context.MODE_PRIVATE
                )
                val editor = sharedPref.edit()
                editor.putString("EMAIL_USUARIO", inputMailLogin.text.toString())
                editor.apply()
                val accion = loginDirections.actionLoginToPaginaPrincipalContainer()
                v.findNavController().navigate(accion)
            }else{
                Snackbar.make(
                    v,
                    "Mail o contraseña incorrectos",
                    Snackbar.LENGTH_SHORT
                ).show()
            }
        }
        btnRegistreseLogin.setOnClickListener {
            val accion = loginDirections.actionLoginToRegistrarse()
            v.findNavController().navigate(accion)
        }
    }

}