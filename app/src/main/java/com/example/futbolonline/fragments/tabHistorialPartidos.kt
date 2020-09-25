package com.example.futbolonline.fragments

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.example.futbolonline.R

class tabHistorialPartidos : Fragment() {

    companion object {
        fun newInstance() = tabHistorialPartidos()
    }

    private lateinit var viewModel: TabHistorialPartidosViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.tab_historial_partidos_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(TabHistorialPartidosViewModel::class.java)
        // TODO: Use the ViewModel
    }

}