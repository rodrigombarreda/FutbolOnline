package com.example.futbolonline.fragments

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.core.text.set
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.example.futbolonline.R
import com.google.android.gms.maps.GoogleMap
import com.google.android.material.snackbar.Snackbar
import com.google.type.Date
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.lang.Double
import java.util.*

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
    lateinit var inputEdadMinimaCrearEvento: EditText
    lateinit var inputEdadMaximaCrearEvento: EditText
    lateinit var inputCalificacionMinimaCrearEvento: EditText
    lateinit var btnElegirFechaCrearEvento: Button
    lateinit var txtFechaCrearEvento: TextView
    lateinit var btnElegirHoraCrearEvento: Button
    lateinit var txtHoraCrearEvento: TextView
    lateinit var btnElegirUbicacionCrearEvento: Button
    lateinit var btnCrearEvento: Button

    var fechaEvento = Date()
    var cadenaFechaEvento: String = ""

    var latLngAnteriorDeMapa = ArrayList<String>() as MutableList<String>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        v = inflater.inflate(R.layout.crear_evento_fragment, container, false)
        inputNombreEventoCrearEvento = v.findViewById(R.id.inputNombreEventoCrearEvento)
        inputJugadoresTotalesCrearEvento = v.findViewById(R.id.inputJugadoresTotalesCrearEvento)
        inputJugadoresFaltantesCrearEvento = v.findViewById(R.id.inputJugadoresFaltantesCrearEvento)
        inputEdadMinimaCrearEvento = v.findViewById(R.id.inputEdadMinimaCrearEvento)
        inputEdadMaximaCrearEvento = v.findViewById(R.id.inputEdadMaximaCrearEvento)
        inputCalificacionMinimaCrearEvento = v.findViewById(R.id.inputCalificacionMinimaCrearEvento)
        btnElegirFechaCrearEvento = v.findViewById(R.id.btnElegirFechaCrearEvento)
        txtFechaCrearEvento = v.findViewById(R.id.txtFechaCrearEvento)
        btnElegirHoraCrearEvento = v.findViewById(R.id.btnElegirHoraCrearEvento)
        txtHoraCrearEvento = v.findViewById(R.id.txtHoraCrearEvento)
        btnElegirUbicacionCrearEvento = v.findViewById(R.id.btnElegirUbicacionCrearEvento)
        btnCrearEvento = v.findViewById(R.id.btnCrearEvento)
        return v
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        crearEventoViewModel = ViewModelProvider(this).get(CrearEventoViewModel::class.java)

        crearEventoViewModel.errorNombreEvento.observe(
            viewLifecycleOwner,
            Observer { error ->
                inputNombreEventoCrearEvento.setError(error)
            })
        crearEventoViewModel.errorJugadoresTotalesEvento.observe(
            viewLifecycleOwner,
            Observer { error -> inputJugadoresTotalesCrearEvento.setError(error) })
        crearEventoViewModel.errorJugadoresFaltantesEvento.observe(
            viewLifecycleOwner,
            Observer { error -> inputJugadoresFaltantesCrearEvento.setError(error) })
        crearEventoViewModel.errorEdadMinimaEvento.observe(
            viewLifecycleOwner,
            Observer { error -> inputEdadMinimaCrearEvento.setError(error) })
        crearEventoViewModel.errorEdadMaximaEvento.observe(
            viewLifecycleOwner,
            Observer { error -> inputEdadMaximaCrearEvento.setError(error) })
        crearEventoViewModel.errorCalificacionMinimaEvento.observe(
            viewLifecycleOwner,
            Observer { error -> inputCalificacionMinimaCrearEvento.setError(error) })
        setEstadoInputDeLiveData()
    }

    //@RequiresApi(Build.VERSION_CODES.N)
    override fun onStart() {
        super.onStart()

        val parentJob = Job()
        val scope = CoroutineScope(Dispatchers.Default + parentJob)

        var latlngDeMapa: Array<String>? =
            crearEventoArgs.fromBundle(requireArguments()).latLngDeMapa

        if (latlngDeMapa != null) {
            var Lat: kotlin.Double = latlngDeMapa[0].toDouble()
            Log.d("lat", Lat.toString())
            Log.d(
                "latlangcrearEvento",
                latlngDeMapa[0] + " " + latlngDeMapa[1] + " Ubicacion: " + latlngDeMapa[2] + " " + latlngDeMapa[3]
            )
            latLngAnteriorDeMapa.add(latlngDeMapa[0])
            latLngAnteriorDeMapa.add(latlngDeMapa[1])
            latLngAnteriorDeMapa.add(latlngDeMapa[2])
            latLngAnteriorDeMapa.add(latlngDeMapa[3])
        }

        btnElegirFechaCrearEvento.setOnClickListener {
            var c: Calendar = Calendar.getInstance()
            var selectorFecha = DatePickerDialog(
                requireContext(),
                { datePicker: DatePicker, anio: Int, mes: Int, dia: Int ->
                    var mesAdaptado = mes + 1
                    var fecha: String = "$dia/$mesAdaptado/$anio"
                    txtFechaCrearEvento.text = "Fecha: $fecha"

                    //fechaEvento.year = anio
                    fechaEvento.month = mes
                    fechaEvento.date = dia

                    /*var date1 = Date(2020, 9, 30, 12, 54)  // mes -1 anio normal, dia normal
                    date1.hours = 21
                    date1.minutes = 120
                    Log.d("date1:", date1.hours.toString())
                    var stringDate1 = date1.toString()
                    var date2 = Date(stringDate1)
                    Log.d("date2", date2.toString())
                    Log.d("anio", date2.year.toString())
                    Log.d("mes", date2.month.toString())
                    Log.d("dia", date2.date.toString())
                    Log.d("hora", date2.hours.toString())
                    Log.d("minuto", date2.minutes.toString())*/
                },
                c.get(Calendar.YEAR),
                c.get(Calendar.MONTH),
                c.get(Calendar.DAY_OF_MONTH)
            )
            selectorFecha.show()
        }

        btnElegirHoraCrearEvento.setOnClickListener {
            var selectorHorario = TimePickerDialog(
                requireContext(),
                { timePicker: TimePicker, hora: Int, minuto: Int ->
                    var horaAdaptada: String = hora.toString()
                    var minutoAdaptado: String = minuto.toString()
                    if (hora < 10) {
                        horaAdaptada = "0$hora"
                    }
                    if (minuto < 10) {
                        minutoAdaptado = "0$minuto"
                    }
                    var horario: String = "$horaAdaptada:$minutoAdaptado"
                    txtHoraCrearEvento.text = "Hora: $horario"

                    fechaEvento.hours = hora
                    fechaEvento.minutes = minuto
                }, 12, 0, true
            )
            selectorHorario.show()
        }

        btnElegirUbicacionCrearEvento.setOnClickListener {
            guardarEstadoDeInputCompletados()
            setEstadoInputDeLiveData()
            val accion =
                crearEventoDirections.actionCrearEventoToMapsFragment(latLngAnteriorDeMapa.toTypedArray())
            v.findNavController().navigate(accion)
        }

        btnCrearEvento.setOnClickListener {
            if (latlngDeMapa != null) {
                scope.launch {
                    val sharedPref: SharedPreferences = requireContext().getSharedPreferences(
                        USUARIO_PREFERENCES,
                        Context.MODE_PRIVATE
                    )
                    if (crearEventoViewModel.fechaEsValida(fechaEvento)) {
                        var partidoEsValido = crearEventoViewModel.eventoEsValido(
                            inputNombreEventoCrearEvento,
                            inputJugadoresTotalesCrearEvento,
                            inputJugadoresFaltantesCrearEvento,
                            inputEdadMinimaCrearEvento,
                            inputEdadMaximaCrearEvento,
                            inputCalificacionMinimaCrearEvento,
                            sharedPref.getString("EMAIL_USUARIO", "default")!!,
                            fechaEvento
                        )
                        if (partidoEsValido) {
                            Snackbar.make(
                                v,
                                "Creando partido...",
                                Snackbar.LENGTH_SHORT
                            ).show()
                            cadenaFechaEvento = fechaEvento.toString()
                            val seCreoPartido: Boolean = crearEventoViewModel.registrarEvento(
                                inputNombreEventoCrearEvento.text.toString(),
                                inputJugadoresTotalesCrearEvento.text.toString().toInt(),
                                inputJugadoresFaltantesCrearEvento.text.toString().toInt(),
                                inputEdadMinimaCrearEvento.text.toString().toInt(),
                                inputEdadMaximaCrearEvento.text.toString().toInt(),
                                inputCalificacionMinimaCrearEvento.text.toString().toInt(),
                                sharedPref.getString("EMAIL_USUARIO", "default")!!,
                                cadenaFechaEvento,
                                latlngDeMapa[0].toDouble(),
                                latlngDeMapa[1].toDouble(),
                                latlngDeMapa[2]
                            )
                            if (seCreoPartido) {
                                Snackbar.make(
                                    v,
                                    "Partido creado.",
                                    Snackbar.LENGTH_SHORT
                                ).show()
                                crearEventoViewModel.unirCreadorAPartido(
                                    sharedPref.getString(
                                        "EMAIL_USUARIO",
                                        "default"
                                    )!!, inputNombreEventoCrearEvento.text.toString()
                                )
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
                    } else {
                        Snackbar.make(
                            v,
                            "El partido debe ser en al menos un día.",
                            Snackbar.LENGTH_SHORT
                        ).show()
                    }
                }
            } else {
                Snackbar.make(
                    v,
                    "Debe elegir ubicacion.",
                    Snackbar.LENGTH_SHORT
                ).show()
            }
        }
    }

    fun guardarEstadoDeInputCompletados() {
        crearEventoViewModel.guardarEstadoInputCompletados(
            inputNombreEventoCrearEvento.text.toString(),
            inputJugadoresTotalesCrearEvento.text.toString(),
            inputJugadoresFaltantesCrearEvento.text.toString(),
            inputEdadMinimaCrearEvento.text.toString(),
            inputEdadMaximaCrearEvento.text.toString(),
            inputCalificacionMinimaCrearEvento.text.toString()
        )
    }

    fun setEstadoInputDeLiveData() {
        if (crearEventoViewModel.valorNombreEvento.value != null) {
            Log.d("nombre", crearEventoViewModel.valorNombreEvento.value!!)
        } else {
            Log.d("nombre", "no se actualizo")
        }
        inputNombreEventoCrearEvento.setText(crearEventoViewModel.valorNombreEvento.value)
        inputJugadoresTotalesCrearEvento.setText(crearEventoViewModel.valorJugadoresTotalesEvento.value)
        inputJugadoresFaltantesCrearEvento.setText(crearEventoViewModel.valorJugadoresFaltantesEvento.value)
        inputEdadMinimaCrearEvento.setText(crearEventoViewModel.valorEdadMinimaEvento.value)
        inputEdadMaximaCrearEvento.setText(crearEventoViewModel.valorEdadMaximaEvento.value)
        inputCalificacionMinimaCrearEvento.setText(crearEventoViewModel.valorCalificacionMinimaEvento.value)
    }
}