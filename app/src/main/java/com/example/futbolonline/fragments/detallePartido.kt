package com.example.futbolonline.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.futbolonline.R
import com.example.futbolonline.entidades.Partido
import com.example.futbolonline.entidades.Usuario
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.lang.Exception
import java.util.*

class detallePartido : Fragment() {

    companion object {
        fun newInstance() = detallePartido()
    }

    private lateinit var detallePartidoViewModel: DetallePartidoViewModel
    lateinit var v: View
    lateinit var txtNombreEventoDetallePartido: TextView
    lateinit var txtEmailCreadorDetallePartido: TextView
    lateinit var txtJugadoresTotalesDetallePartido: TextView
    lateinit var txtJugadoresFaltantesDetallePartido: TextView
    lateinit var txtEdadMinimaDetallePartido: TextView
    lateinit var txtEdadMaximaDetallePartido: TextView
    lateinit var txtCalificacionMinimaDetallePartido: TextView
    lateinit var txtGeneroAdmitidoDetallePartido: TextView
    lateinit var txtFechaYHoraDetallePartido: TextView
    lateinit var txtUbicacionDetallePartido: TextView
    lateinit var btnVerUbicacionEnMapaDetallePartido: TextView

    lateinit var nombreEvento: String


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        v = inflater.inflate(R.layout.detalle_partido_fragment, container, false)
        txtNombreEventoDetallePartido = v.findViewById(R.id.txtNombreEventoDetallePartido)
        txtEmailCreadorDetallePartido = v.findViewById(R.id.txtEmailCreadorDetallePartido)
        txtJugadoresTotalesDetallePartido = v.findViewById(R.id.txtJugadoresTotalesDetallePartido)
        txtJugadoresFaltantesDetallePartido =
            v.findViewById(R.id.txtJugadoresFaltantesDetallePartido)
        txtEdadMinimaDetallePartido = v.findViewById(R.id.txtEdadMinimaDetallePartido)
        txtEdadMaximaDetallePartido = v.findViewById(R.id.txtEdadMaximaDetallePartido)
        txtCalificacionMinimaDetallePartido =
            v.findViewById(R.id.txtCalificacionMinimaDetallePartido)
        txtGeneroAdmitidoDetallePartido = v.findViewById(R.id.txtGeneroAdmitidoDetallePartido)
        txtFechaYHoraDetallePartido = v.findViewById(R.id.txtFechaYHoraDetallePartido)
        txtUbicacionDetallePartido = v.findViewById(R.id.txtUbicacionDetallePartido)
        btnVerUbicacionEnMapaDetallePartido =
            v.findViewById(R.id.btnVerUbicacionEnMapaDetallePartido)
        return v
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        detallePartidoViewModel = ViewModelProvider(this).get(DetallePartidoViewModel::class.java)
        // TODO: Use the ViewModel

        detallePartidoViewModel.partido.observe(viewLifecycleOwner,
            Observer { partido -> setearDatos(partido) })
    }

    override fun onStart() {
        super.onStart()

        nombreEvento = detallePartidoArgs.fromBundle(requireArguments()).nombreEvento

        val parentJob = Job()
        val scope = CoroutineScope(Dispatchers.Default + parentJob)

        scope.launch {
            detallePartidoViewModel.refrescarDetallePartido(nombreEvento) //TODO: PASAR NOMBRE EVENTO
        }
    }

    fun setearDatos(p: Partido) {
        if (p != null) {
            txtNombreEventoDetallePartido.text = "Nombre evento: " + p.nombreEvento
            txtEmailCreadorDetallePartido.text = "Email creador: " + p.emailCreador
            txtJugadoresTotalesDetallePartido.text =
                "Jugadores totales: " + p.cantidadJugadoresTotales
            txtJugadoresFaltantesDetallePartido.text =
                "Jugadores faltantes: " + p.cantidadJugadoresFaltantes
            txtEdadMinimaDetallePartido.text = "Edad minima: " + p.edadMinima
            txtEdadMaximaDetallePartido.text = "Edad maxima: " + p.edadMaxima
            txtCalificacionMinimaDetallePartido.text =
                "Calificacion minima: " + p.calificacionMinima
            txtGeneroAdmitidoDetallePartido.text = "Genero admitido: " + p.generoAdmitido
            txtFechaYHoraDetallePartido.text = getTxtFechaYHora(p.fechaYHora)
            txtUbicacionDetallePartido.text = "Ubicacion: " + p.ubicacion
        }
    }

    fun getTxtFechaYHora(fechaYHoraString: String): String {
        var txtFechaYHora: String = "Fecha y hora: "
        try {
            var fechaYHora = Date(fechaYHoraString)
            var dia = fechaYHora.date.toString()
            var mes = fechaYHora.month.toString()
            var hora = fechaYHora.hours.toString()
            var minutos = fechaYHora.minutes.toString()
            if (fechaYHora.date < 10) {
                dia = "0$dia"
            }
            if (fechaYHora.month < 10) {
                mes = "0$mes"
            }
            if (fechaYHora.hours < 10) {
                hora = "0$hora"
            }
            if (fechaYHora.minutes < 10) {
                minutos = "0$minutos"
            }
            txtFechaYHora =
                "Fecha y hora: $dia/$mes/${fechaYHora.year + 1900} $hora:$minutos"
        } catch (e: Exception) {
            Log.d("error", "no se pudo obtener hora" + e.message)
        }
        return txtFechaYHora
    }
}



