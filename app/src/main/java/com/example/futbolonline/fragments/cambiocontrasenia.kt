package com.example.futbolonline.fragments

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.example.futbolonline.R
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch


class cambiocontrasenia : Fragment() {


    companion object {
        fun newInstance() = cambiocontrasenia()
    }

    val USUARIO_PREFERENCES: String = "usuarioPreferences"
    private lateinit var CambioContraseniaViewModel: cambioContraseniaViewModel

    lateinit var passActual: EditText
    lateinit var passNewUno: EditText
    lateinit var passNewDos: EditText
    lateinit var confirmar: Button
    lateinit var volver: Button
    lateinit var v: View
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        v = inflater.inflate(R.layout.fragment_cambiocontrasenia, container, false)
        passActual = v.findViewById(R.id.passActual)
        passNewUno = v.findViewById(R.id.passNewUno)
        passNewDos = v.findViewById(R.id.passNewDos)
        confirmar = v.findViewById(R.id.buttonConfirmar)
        volver = v.findViewById(R.id.buttonVolver)

        return v
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        CambioContraseniaViewModel =
            ViewModelProvider(this).get(cambioContraseniaViewModel::class.java)


        // TODO: Use the ViewModel
    }

    override fun onStart() {
        super.onStart()

        val parentJob = Job()
        val scope = CoroutineScope(Dispatchers.Default + parentJob)


        confirmar.setOnClickListener {
            scope.launch {
                val sharedPref: SharedPreferences = requireContext().getSharedPreferences(
                    USUARIO_PREFERENCES,
                    Context.MODE_PRIVATE
                )
                var mail = sharedPref.getString(
                    "EMAIL_USUARIO",
                    "default"
                )
                var esMismaContrasenia = CambioContraseniaViewModel.esLaMismaContrasenia(
                    passActual.text.toString(),
                    mail
                )

                if (esMismaContrasenia) {
                    var seCambio = CambioContraseniaViewModel.cambiarContrasenia(
                        passNewUno.text.toString(),
                        passNewDos.text.toString(),
                        mail
                    )
                    if (seCambio) {
                        Snackbar.make(
                            v,
                            "contraseña cambiada correctamente",
                            Snackbar.LENGTH_SHORT
                        ).show()
                        val accion =
                            cambiocontraseniaDirections.actionCambiocontraseniaToPaginaPrincipalContainer()
                        v.findNavController().navigate(accion)

                    }else{
                        Snackbar.make(
                            v,
                            "La contraseña no coincide",
                            Snackbar.LENGTH_SHORT
                        ).show()
                    }


                }else{
                    Snackbar.make(
                        v,
                        "Las contraseñas es incorrecta",
                        Snackbar.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }
}