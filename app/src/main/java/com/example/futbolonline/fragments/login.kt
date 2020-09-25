package com.example.futbolonline.fragments

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


class login : Fragment() {

    companion object {
        fun newInstance() = login()
    }

    private lateinit var loginViewModel: LoginViewModel
    lateinit var v : View
    lateinit var inputMailLogin : EditText
    lateinit var inputPasswordLogin : EditText
    lateinit var btnIniciarSesionLogin : Button
    lateinit var txtOlvidoSuContraseniaLogin : TextView
    lateinit var btnOlvidoSuContraseniaLogin : Button
    lateinit var txtNoTieneCuentaLogin : TextView
    lateinit var btnRegistreseLogin : Button

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
        loginViewModel.mailYContraseniaSonCorrectas.observe(viewLifecycleOwner, Observer { result ->
            if(result){
                val accion = loginDirections.actionLoginToPaginaPrincipalContainer()
                v.findNavController().navigate(accion)
            }
        })
    }

    override fun onStart() {
        super.onStart()
        btnIniciarSesionLogin.setOnClickListener {
            loginViewModel.autenticarUsuario(inputMailLogin.text.toString(), inputPasswordLogin.text.toString())
        }
        btnRegistreseLogin.setOnClickListener {
            val accion = loginDirections.actionLoginToRegistrarse()
            v.findNavController().navigate(accion)
        }
    }

}