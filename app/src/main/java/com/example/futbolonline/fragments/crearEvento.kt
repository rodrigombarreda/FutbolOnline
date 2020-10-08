package com.example.futbolonline.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.lifecycle.ViewModelProvider
import com.example.futbolonline.R

class crearEvento : Fragment() {

    companion object {
        fun newInstance() = crearEvento()
    }

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
    }

    override fun onStart() {
        super.onStart()

        btnCrearEvento.setOnClickListener {

        }
    }

}