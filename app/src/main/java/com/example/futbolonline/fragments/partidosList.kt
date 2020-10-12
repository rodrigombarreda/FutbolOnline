package com.example.futbolonline.fragments

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.futbolonline.R
import com.example.futbolonline.adapters.PartidosListAdapter
import com.example.futbolonline.entidades.Partido
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class partidosList : Fragment() {

    companion object {
        fun newInstance() = partidosList()
    }

    var partidos: MutableList<Partido> = ArrayList<Partido>()

    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var partidosListAdapter: PartidosListAdapter

    private lateinit var partidosListViewModel: PartidosListViewModel
    lateinit var v: View
    lateinit var listaPartidos: RecyclerView


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        v = inflater.inflate(R.layout.partidos_list_fragment, container, false)
        listaPartidos = v.findViewById(R.id.listaPartidos)
        return v
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        partidosListViewModel = ViewModelProvider(this).get(PartidosListViewModel::class.java)
        // TODO: Use the ViewModel
    }

    override fun onStart() {
        super.onStart()

        val parentJob = Job()
        val scope = CoroutineScope(Dispatchers.Default + parentJob)

        scope.launch {
            // TODO: Setear partidos
            partidos = partidosListViewModel.getTodosLosPartidos()

            listaPartidos.setHasFixedSize(true)
            linearLayoutManager = LinearLayoutManager(context)
            listaPartidos.layoutManager = linearLayoutManager

            partidosListAdapter = PartidosListAdapter(partidos,
                { position -> onItemClick(position) })

            listaPartidos.adapter = partidosListAdapter
        }
    }

    fun onItemClick(position: Int) {
        // TODO: Implementar funcion
    }

}