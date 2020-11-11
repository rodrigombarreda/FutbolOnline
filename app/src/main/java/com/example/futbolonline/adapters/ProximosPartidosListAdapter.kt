package com.example.futbolonline.adapters

import android.location.Geocoder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.futbolonline.R
import com.example.futbolonline.entidades.Partido
import java.lang.Exception
import java.util.*
import kotlin.collections.ArrayList

class ProximosPartidosListAdapter(
    private var proximosPartidosList: MutableList<Partido>,
    val onItemClick: (Int) -> Unit,
    val onBotonSalir: (Int) -> Unit
) : RecyclerView.Adapter<ProximosPartidosListAdapter.ProximoPartidoHolder>() {

    // var items: List<Partido> by Delegates.observable(emptyList()) { _, _, _ -> notifyDataSetChanged() }

    companion object {
        private val TAG = "ProximosPartidosListAdapter"
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProximoPartidoHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_proximo_partido, parent, false)
        return (ProximoPartidoHolder(view))
    }

    override fun getItemCount(): Int {
        return proximosPartidosList.size
    }

    fun setData(newData: ArrayList<Partido>) {
        this.proximosPartidosList = newData
        this.notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: ProximoPartidoHolder, position: Int) {

        holder.setName(proximosPartidosList[position].nombreEvento)
        holder.setFecha(proximosPartidosList[position].fechaYHora)
        holder.setUbicacion(
            proximosPartidosList[position].latitud,
            proximosPartidosList[position].longitud
        )

        holder.getCardLayout().setOnClickListener {
            onItemClick(position)
        }

        holder.getBtnSalirDePartido().setOnClickListener {
            onBotonSalir(position)
        }

    }

    class ProximoPartidoHolder(v: View) : RecyclerView.ViewHolder(v) {

        var view: View

        init {
            this.view = v
        }

        fun setName(name: String) {
            val txt: TextView = view.findViewById(R.id.txtTituloEventoItemProximoPartido)
            txt.text = name
        }

        fun setFecha(fechaString: String) {
            val txt: TextView = view.findViewById(R.id.txtFechaItemProximoPartido)
            txt.text = getTxtFechaYHora(fechaString)
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
                    "Fecha: $dia/$mes/${fechaYHora.year + 1900} $hora:$minutos"
            } catch (e: Exception) {

            }
            return txtFechaYHora
        }

        fun getCardLayout(): CardView {
            return view.findViewById(R.id.cardProximoPartido)
        }

        fun setUbicacion(latitud: Double, longitud: Double) {
            val txt: TextView = view.findViewById(R.id.txtUbicacionItemProximoPartido)
            val geocoder = Geocoder(view.context)
            val ubicacion = geocoder.getFromLocation(
                latitud,
                longitud,
                1
            )[0]
            var sublocality = ubicacion.subLocality
            if(sublocality == null){
                sublocality = ""
            }
            txt.text =
                ubicacion.thoroughfare + " " + ubicacion.featureName + " " + sublocality + " " + ubicacion.adminArea
        }

        fun getBtnSalirDePartido(): Button {
            return view.findViewById(R.id.btnSalirDePartidoItemProximoPartido)
        }
    }
}