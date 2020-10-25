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

class PartidosListAdapter(
    private var partidosList: MutableList<Partido>,
    val onItemClick: (Int) -> Unit,
    val onBotonUnirse: (Int) -> Unit
) : RecyclerView.Adapter<PartidosListAdapter.PartidoHolder>() {

    // var items: List<Partido> by Delegates.observable(emptyList()) { _, _, _ -> notifyDataSetChanged() }

    companion object {
        private val TAG = "PartidosListAdapter"
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PartidoHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_partido, parent, false)
        return (PartidoHolder(view))
    }

    override fun getItemCount(): Int {
        return partidosList.size
    }

    fun setData(newData: ArrayList<Partido>) {
        this.partidosList = newData
        this.notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: PartidoHolder, position: Int) {

        holder.setName(partidosList[position].nombreEvento)

        holder.getCardLayout().setOnClickListener {
            onItemClick(position)
        }

        holder.getBtnUnirseAPartido().setOnClickListener {
            onBotonUnirse(position)
        }

    }

    /*fun setData(data: MutableList<Partido>) {
        this.items = data
    }*/

    class PartidoHolder(v: View) : RecyclerView.ViewHolder(v) {

        var view: View

        init {
            this.view = v
        }

        fun setName(name: String) {
            val txt: TextView = view.findViewById(R.id.txtTituloEventoItemPartido)
            txt.text = name
        }

        fun getCardLayout(): CardView {
            return view.findViewById(R.id.card)
        }

        fun getBtnUnirseAPartido(): Button {
            return view.findViewById(R.id.btnUnirseAPartidoItemPartido)
        }
    }

}