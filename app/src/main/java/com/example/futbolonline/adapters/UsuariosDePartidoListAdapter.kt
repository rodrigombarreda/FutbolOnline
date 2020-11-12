package com.example.futbolonline.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.futbolonline.R
import com.example.futbolonline.entidades.Usuario
import kotlin.collections.ArrayList

class UsuariosDePartidoListAdapter(
    private var usuariosDePartidoList: MutableList<Usuario>,
    val onItemClick: (Int) -> Unit
) : RecyclerView.Adapter<UsuariosDePartidoListAdapter.UsuarioDePartidoHolder>() {

    // var items: List<Partido> by Delegates.observable(emptyList()) { _, _, _ -> notifyDataSetChanged() }

    companion object {
        private val TAG = "UsuariosDePartidoListAdapter"
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UsuarioDePartidoHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_usuario_partido, parent, false)
        return (UsuarioDePartidoHolder(view))
    }

    override fun getItemCount(): Int {
        return usuariosDePartidoList.size
    }

    fun setData(newData: ArrayList<Usuario>) {
        this.usuariosDePartidoList = newData
        this.notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: UsuarioDePartidoHolder, position: Int) {

        holder.setMail(usuariosDePartidoList[position].email)
        holder.setName(usuariosDePartidoList[position].nombre)

        holder.getCardLayout().setOnClickListener {
            onItemClick(position)
        }

    }

    class UsuarioDePartidoHolder(v: View) : RecyclerView.ViewHolder(v) {

        var view: View

        init {
            this.view = v
        }

        fun setName(name: String) {
            val txt: TextView = view.findViewById(R.id.txtNombreUsuarioItemUsuarioPartido)
            txt.text = name
        }

        fun setMail(email: String) {
            val txt: TextView = view.findViewById(R.id.txtEmailUsuarioItemUsuarioPartido)
            txt.text = email
        }

        fun getCardLayout(): CardView {
            return view.findViewById(R.id.cardUsuarioDePartido)
        }

    }
}