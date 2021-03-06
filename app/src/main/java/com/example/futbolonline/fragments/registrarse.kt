package com.example.futbolonline.fragments

import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.example.futbolonline.R
import com.example.futbolonline.activities.MainActivity
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.registrarse_fragment.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.util.*

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

    val fechaNacimiento = Date()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        v = inflater.inflate(R.layout.registrarse_fragment, container, false)
        inputMailRegistrarse = v.findViewById(R.id.inputMailRegistrarse)
        inputNombreRegistrarse = v.findViewById(R.id.inputNombreRegistrarse)
        inputEdadRegistrarse = v.findViewById(R.id.inputFechaNacimientoRegistrarse)
        radioGeneroRegistrarse = v.findViewById(R.id.radioGeneroCrearEvento)
        radioBtnMasculinoRegistrarse = v.findViewById(R.id.radioBtnMasculinoCrearEvento)
        radioBtnFemeninoRegistrarse = v.findViewById(R.id.radioBtnFemeninoCrearEvento)
        inputContraseniaRegistrarse = v.findViewById(R.id.inputContraseniaRegistrarse)
        btnRegistrarse = v.findViewById(R.id.btnRegistrarse)
        btnIrAIniciarSesionDeRegistrarse = v.findViewById(R.id.btnIrAIniciarSesionDeRegistrarse)
        return v
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        registrarseViewModel = ViewModelProvider(this).get(RegistrarseViewModel::class.java)
        // TODO: Use the ViewModel
        registrarseViewModel.errorEmailUsuario.observe(viewLifecycleOwner, Observer { error ->
            inputMailRegistrarse.setError(error)
        })
        registrarseViewModel.errorNombreUsuario.observe(viewLifecycleOwner, Observer { error ->
            inputNombreRegistrarse.setError(error)
        })
        registrarseViewModel.errorEdadUsuario.observe(viewLifecycleOwner, Observer { error ->
            inputEdadRegistrarse.setError(error)
        })
        registrarseViewModel.errorGeneroUsuario.observe(viewLifecycleOwner, Observer { error ->
            radioBtnMasculinoRegistrarse.setError(error)
            radioBtnFemeninoRegistrarse.setError(error)
        })
        registrarseViewModel.errorContraseniaUsuario.observe(viewLifecycleOwner, Observer { error ->
            inputContraseniaRegistrarse.setError(error)
        })
    }

    override fun onStart() {
        super.onStart()

        val parentJob = Job()
        val scope = CoroutineScope(Dispatchers.Default + parentJob)

        inputFechaNacimientoRegistrarse.setOnClickListener {
            var c: Calendar = Calendar.getInstance()
            var selectorFecha = DatePickerDialog(
                requireContext(),
                { datePicker: DatePicker, anio: Int, mes: Int, dia: Int ->
                    var mesAdaptado = mes + 1
                    var fecha: String = "$dia/$mesAdaptado/$anio"
                    inputFechaNacimientoRegistrarse.setText(fecha)

                    fechaNacimiento.year = anio - 1900
                    fechaNacimiento.month = mes
                    fechaNacimiento.date = dia
                    registrarseViewModel.fechaDate.value = fechaNacimiento.toString()
                },
                c.get(Calendar.YEAR),
                c.get(Calendar.MONTH),
                c.get(Calendar.DAY_OF_MONTH)
            )
            selectorFecha.updateDate(2000, 1, 1)
            selectorFecha.show()
        }

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
                        inputEdadRegistrarse.text.toString(),
                        inputContraseniaRegistrarse.text.toString(),
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

                        // TODO: Ir a main activity

                        val mainActivity = Intent(getActivity(), MainActivity::class.java)
                        startActivity(mainActivity)

                        /*var accion =
                            registrarseDirections.actionRegistrarseToPaginaPrincipalContainer()
                        v.findNavController().navigate(accion)*/
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

    override fun onResume() {
        super.onResume()
        (activity as AppCompatActivity?)!!.supportActionBar!!.hide()
    }

    override fun onStop() {
        super.onStop()
        (activity as AppCompatActivity?)!!.supportActionBar!!.show()
    }
}