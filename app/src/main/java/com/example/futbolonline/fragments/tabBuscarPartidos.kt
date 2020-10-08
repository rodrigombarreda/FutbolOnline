package com.example.futbolonline.fragments

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.example.futbolonline.R

class tabBuscarPartidos : Fragment() {

    companion object {
        fun newInstance() = tabBuscarPartidos()
    }

    private lateinit var viewModel: TabBuscarPartidosViewModel
    lateinit var v: View
    lateinit var btnIrACrearEventoDesdeBuscarPartidos: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        v = inflater.inflate(R.layout.tab_buscar_partidos_fragment, container, false)
        btnIrACrearEventoDesdeBuscarPartidos =
            v.findViewById(R.id.btnIrACrearEventoDesdeBuscarPartidos)
        return v
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(TabBuscarPartidosViewModel::class.java)
        // TODO: Use the ViewModel
    }

    override fun onStart() {
        super.onStart()

        btnIrACrearEventoDesdeBuscarPartidos.setOnClickListener {
            val accion = tabBuscarPartidosDirections.actionTabBuscarPartidosToCrearEvento()
            v.findNavController().navigate(accion)
        }
    }

}