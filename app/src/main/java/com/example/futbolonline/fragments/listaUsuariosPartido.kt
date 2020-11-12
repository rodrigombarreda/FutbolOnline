package com.example.futbolonline.fragments

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.futbolonline.R
import com.example.futbolonline.adapters.HistorialPartidosListAdapter
import com.example.futbolonline.adapters.UsuariosDePartidoListAdapter
import com.example.futbolonline.entidades.Partido
import com.example.futbolonline.entidades.Usuario
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class listaUsuariosPartido : Fragment() {

    companion object {
        fun newInstance() = listaUsuariosPartido()
    }

    var usuariosDePartidos: MutableList<Usuario> = ArrayList<Usuario>()

    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var usuariosDePartidoListAdapter: UsuariosDePartidoListAdapter

    private lateinit var usuariosDePartidoViewModel: ListaUsuariosPartidoViewModel
    lateinit var v: View
    lateinit var listaUsuariosDePartido: RecyclerView

    lateinit var nombreEvento: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        v = inflater.inflate(R.layout.lista_usuarios_partido_fragment, container, false)
        listaUsuariosDePartido = v.findViewById(R.id.listaUsuariosDePartido)
        return v
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        usuariosDePartidoViewModel =
            ViewModelProvider(this).get(ListaUsuariosPartidoViewModel::class.java)
        // TODO: Use the ViewModel

        usuariosDePartidoViewModel.usuariosList.observe(
            viewLifecycleOwner,
            Observer { lista ->
                usuariosDePartidos = lista
                usuariosDePartidoListAdapter = UsuariosDePartidoListAdapter(usuariosDePartidos,
                    { position -> alClickearCardUsuario(position) }
                )

                listaUsuariosDePartido.adapter = usuariosDePartidoListAdapter
            })
    }

    override fun onStart() {
        super.onStart()

        nombreEvento = listaUsuariosPartidoArgs.fromBundle(requireArguments()).nombreEvento

        val parentJob = Job()
        val scope = CoroutineScope(Dispatchers.Default + parentJob)

        listaUsuariosDePartido.setHasFixedSize(true)
        linearLayoutManager = LinearLayoutManager(context)
        listaUsuariosDePartido.layoutManager = linearLayoutManager

        usuariosDePartidoListAdapter = UsuariosDePartidoListAdapter(usuariosDePartidos,
            { position -> alClickearCardUsuario(position) }
        )

        listaUsuariosDePartido.adapter = usuariosDePartidoListAdapter

        scope.launch {
            usuariosDePartidoViewModel.refrescarListaUsuariosDePartido(
                nombreEvento
            )
        }
    }

    fun alClickearCardUsuario(position: Int) {
        val accion =
            listaUsuariosPartidoDirections.actionListaUsuariosPartidoToDetalleUsuarioDePartido(
                usuariosDePartidos[position].email
            )
        v.findNavController().navigate(accion)
    }

}