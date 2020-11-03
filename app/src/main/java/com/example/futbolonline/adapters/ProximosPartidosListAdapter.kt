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

        fun getCardLayout(): CardView {
            return view.findViewById(R.id.cardProximoPartido)
        }

        fun getBtnSalirDePartido(): Button {
            return view.findViewById(R.id.btnSalirDePartidoItemProximoPartido)
        }
    }
}