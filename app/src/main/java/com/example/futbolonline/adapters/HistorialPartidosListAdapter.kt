package com.example.futbolonline.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.futbolonline.R
import com.example.futbolonline.entidades.Partido

class HistorialPartidosListAdapter(
    private var historialPartidosList: MutableList<Partido>,
    val onItemClick: (Int) -> Unit,
    val onBotonEliminar: (Int) -> Unit
) : RecyclerView.Adapter<HistorialPartidosListAdapter.HistorialPartidoHolder>() {

    // var items: List<Partido> by Delegates.observable(emptyList()) { _, _, _ -> notifyDataSetChanged() }

    companion object {
        private val TAG = "HistorialPartidosListAdapter"
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistorialPartidoHolder {
        val view =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_historial_partido, parent, false)
        return (HistorialPartidoHolder(view))
    }

    override fun getItemCount(): Int {
        return historialPartidosList.size
    }

    fun setData(newData: ArrayList<Partido>) {
        this.historialPartidosList = newData
        this.notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: HistorialPartidoHolder, position: Int) {

        holder.setName(historialPartidosList[position].nombreEvento)

        holder.getCardLayout().setOnClickListener {
            onItemClick(position)
        }

        holder.getBtnEliminarPartidoDeHistorial().setOnClickListener {
            onBotonEliminar(position)
        }

    }

    class HistorialPartidoHolder(v: View) : RecyclerView.ViewHolder(v) {

        var view: View

        init {
            this.view = v
        }

        fun setName(name: String) {
            val txt: TextView = view.findViewById(R.id.txtTituloEventoItemHistorialPartido)
            txt.text = name
        }

        fun getCardLayout(): CardView {
            return view.findViewById(R.id.card)
        }

        fun getBtnEliminarPartidoDeHistorial(): Button {
            return view.findViewById(R.id.btnEliminarItemHistorialPartido)
        }
    }
}