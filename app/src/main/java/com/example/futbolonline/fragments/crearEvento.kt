package com.example.futbolonline.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.example.futbolonline.R
import com.example.futbolonline.adapters.PartidosListAdapter
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class crearEvento : Fragment() {

    companion object {
        fun newInstance() = crearEvento()
    }

    val USUARIO_PREFERENCES: String = "usuarioPreferences"

    lateinit var crearEventoViewModel: CrearEventoViewModel
    lateinit var v: View
    lateinit var inputNombreEventoCrearEvento: EditText
    lateinit var inputJugadoresTotalesCrearEvento: EditText
    lateinit var inputJugadoresFaltantesCrearEvento: EditText
    lateinit var radioGeneroCrearEvento: RadioGroup
    lateinit var radioBtnMasculinoCrearEvento: RadioButton
    lateinit var radioBtnFemeninoCrearEvento: RadioButton
    lateinit var inputEdadMinimaCrearEvento: EditText
    lateinit var inputEdadMaximaCrearEvento: EditText
    lateinit var inputCalificacionMinimaCrearEvento: EditText
    lateinit var btnCrearEvento: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        v = inflater.inflate(R.layout.crear_evento_fragment, container, false)
        inputNombreEventoCrearEvento = v.findViewById(R.id.inputNombreEventoCrearEvento)
        inputJugadoresTotalesCrearEvento = v.findViewById(R.id.inputJugadoresTotalesCrearEvento)
        inputJugadoresFaltantesCrearEvento = v.findViewById(R.id.inputJugadoresFaltantesCrearEvento)
        radioGeneroCrearEvento = v.findViewById(R.id.radioGeneroCrearEvento)
        radioBtnMasculinoCrearEvento = v.findViewById(R.id.radioBtnMasculinoCrearEvento)
        radioBtnFemeninoCrearEvento = v.findViewById(R.id.radioBtnFemeninoCrearEvento)
        inputEdadMinimaCrearEvento = v.findViewById(R.id.inputEdadMinimaCrearEvento)
        inputEdadMaximaCrearEvento = v.findViewById(R.id.inputEdadMaximaCrearEvento)
        inputCalificacionMinimaCrearEvento = v.findViewById(R.id.inputCalificacionMinimaCrearEvento)
        btnCrearEvento = v.findViewById(R.id.btnCrearEvento)
        return v
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        crearEventoViewModel = ViewModelProvider(this).get(CrearEventoViewModel::class.java)

        // TODO: Use the ViewModel
        crearEventoViewModel.errorCaracteresNombreEventoFueraDeRango.observe(
            viewLifecycleOwner,
            Observer { error ->
                inputNombreEventoCrearEvento.setError(error)
            })
    }


    override fun onStart() {
        super.onStart()

        inputJugadoresTotalesCrearEvento.setText("10")
        inputJugadoresFaltantesCrearEvento.setText("9")
        inputEdadMinimaCrearEvento.setText("12")
        inputEdadMaximaCrearEvento.setText("60")
        inputCalificacionMinimaCrearEvento.setText("100")

        val parentJob = Job()
        val scope = CoroutineScope(Dispatchers.Default + parentJob)

        btnCrearEvento.setOnClickListener {
            scope.launch {
                val sharedPref: SharedPreferences = requireContext().getSharedPreferences(
                    USUARIO_PREFERENCES,
                    Context.MODE_PRIVATE
                )

                var partidoEsValido = crearEventoViewModel.eventoEsValido(
                    inputNombreEventoCrearEvento,
                    inputJugadoresTotalesCrearEvento,
                    inputJugadoresFaltantesCrearEvento,
                    radioBtnMasculinoCrearEvento.isChecked,
                    radioBtnFemeninoCrearEvento.isChecked,
                    inputEdadMinimaCrearEvento,
                    inputEdadMaximaCrearEvento,
                    inputCalificacionMinimaCrearEvento,
                    sharedPref.getString("EMAIL_USUARIO", "default")!!
                )
                if (partidoEsValido) {
                    Snackbar.make(
                        v,
                        "Creando partido...",
                        Snackbar.LENGTH_SHORT
                    ).show()
                    val seCreoPartido: Boolean = crearEventoViewModel.registrarEvento(
                        inputNombreEventoCrearEvento.text.toString(),
                        inputJugadoresTotalesCrearEvento.text.toString().toInt(),
                        inputJugadoresFaltantesCrearEvento.text.toString().toInt(),
                        radioBtnFemeninoCrearEvento.isChecked,
                        inputEdadMinimaCrearEvento.text.toString().toInt(),
                        inputEdadMaximaCrearEvento.text.toString().toInt(),
                        inputCalificacionMinimaCrearEvento.text.toString().toInt(),
                        sharedPref.getString("EMAIL_USUARIO", "default")!!
                    )
                    if (seCreoPartido) {
                        Snackbar.make(
                            v,
                            "Partido creado.",
                            Snackbar.LENGTH_SHORT
                        ).show()
                        var accion =
                            crearEventoDirections.actionCrearEventoToPaginaPrincipalContainer()
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
    }
}