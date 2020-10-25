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
import android.widget.Button
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.futbolonline.R
import com.example.futbolonline.adapters.PartidosListAdapter
import com.example.futbolonline.adapters.ProximosPartidosListAdapter
import com.example.futbolonline.entidades.Partido
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class tabProximosPartidos : Fragment() {

    companion object {
        fun newInstance() = tabProximosPartidos()
    }

    val USUARIO_PREFERENCES: String = "usuarioPreferences"

    var proximosPartidos: MutableList<Partido> = ArrayList<Partido>()

    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var proximosPartidosListAdapter: ProximosPartidosListAdapter

    private lateinit var proximosPartidosViewModel: TabProximosPartidosViewModel
    lateinit var v: View
    lateinit var listaProximosPartidos: RecyclerView

    private lateinit var partidosListViewModel: PartidosListViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        v = inflater.inflate(R.layout.tab_proximos_partidos_fragment, container, false)
        listaProximosPartidos = v.findViewById(R.id.listaProximosPartidos)
        return v
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        proximosPartidosViewModel =
            ViewModelProvider(requireActivity()).get(TabProximosPartidosViewModel::class.java)
        partidosListViewModel =
            ViewModelProvider(requireActivity()).get(PartidosListViewModel::class.java)
        // TODO: Use the ViewModel

        proximosPartidosViewModel.proximosPartidosList.observe(
            viewLifecycleOwner,
            Observer { lista ->
                //partidosListAdapter.setData(lista)
                Log.d("pp", "meactualize")
                proximosPartidos = lista
                proximosPartidosListAdapter = ProximosPartidosListAdapter(proximosPartidos,
                    { position -> alClickearCardPartido(position) },
                    { position -> onBotonSalir(position) }
                )

                listaProximosPartidos.adapter = proximosPartidosListAdapter
            })
    }

    override fun onStart() {
        super.onStart()

        val parentJob = Job()
        val scope = CoroutineScope(Dispatchers.Default + parentJob)

        listaProximosPartidos.setHasFixedSize(true)
        linearLayoutManager = LinearLayoutManager(context)
        listaProximosPartidos.layoutManager = linearLayoutManager

        proximosPartidosListAdapter = ProximosPartidosListAdapter(proximosPartidos,
            { position -> alClickearCardPartido(position) },
            { position -> onBotonSalir(position) }
        )

        listaProximosPartidos.adapter = proximosPartidosListAdapter

        val sharedPref: SharedPreferences = requireContext().getSharedPreferences(
            USUARIO_PREFERENCES,
            Context.MODE_PRIVATE
        )
        scope.launch {
            proximosPartidosViewModel.refrescarListaProximosPartidos(
                sharedPref.getString(
                    "EMAIL_USUARIO",
                    "default"
                )!!
            )
        }
    }

    fun alClickearCardPartido(position: Int) {
        // TODO: Implementar funcion
    }

    fun onBotonSalir(position: Int) {
        val parentJob = Job()
        val scope = CoroutineScope(Dispatchers.Default + parentJob)

        val sharedPref: SharedPreferences = requireContext().getSharedPreferences(
            USUARIO_PREFERENCES,
            Context.MODE_PRIVATE
        )
        val emailUsuario: String = sharedPref.getString("EMAIL_USUARIO", "default")!!
        val nombreEvento: String = proximosPartidos[position].nombreEvento

        scope.launch {
            val partido: Partido? = proximosPartidosViewModel.getPartidoPorNombre(nombreEvento)
            if (partido != null) {
                Snackbar.make(
                    v,
                    "Saliendo del partido $nombreEvento",
                    Snackbar.LENGTH_SHORT
                ).show()
                var salio: Boolean =
                    proximosPartidosViewModel.sacarUsuarioDePartido(emailUsuario, nombreEvento)
                if (salio) {
                    Snackbar.make(
                        v,
                        "Saliste del partido $nombreEvento",
                        Snackbar.LENGTH_SHORT
                    ).show()
                    proximosPartidosViewModel.refrescarListaProximosPartidos(
                        sharedPref.getString(
                            "EMAIL_USUARIO",
                            "default"
                        )!!
                    )
                    partidosListViewModel.refrescarListaPartidos(
                        sharedPref.getString(
                            "EMAIL_USUARIO",
                            "default"
                        )!!
                    )
                } else {
                    Snackbar.make(
                        v,
                        "Error en la red. Intentelo mas tarde",
                        Snackbar.LENGTH_SHORT
                    ).show()
                }

            } else {
                Snackbar.make(
                    v,
                    "Error en la red. Intentelo mas tarde",
                    Snackbar.LENGTH_SHORT
                ).show()
            }
        }
    }

}