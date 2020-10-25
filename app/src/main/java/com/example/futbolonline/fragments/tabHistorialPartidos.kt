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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.futbolonline.R
import com.example.futbolonline.adapters.ProximosPartidosListAdapter
import com.example.futbolonline.entidades.Partido
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class tabHistorialPartidos : Fragment() {

    companion object {
        fun newInstance() = tabHistorialPartidos()
    }

    val USUARIO_PREFERENCES: String = "usuarioPreferences"

    var historialPartidos: MutableList<Partido> = ArrayList<Partido>()

    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var proximosPartidosListAdapter: ProximosPartidosListAdapter

    private lateinit var historialPartidosViewModel: TabHistorialPartidosViewModel
    lateinit var v: View
    lateinit var listaHistorialPartidos: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        v = inflater.inflate(R.layout.tab_historial_partidos_fragment, container, false)
        listaHistorialPartidos = v.findViewById(R.id.listaHistorialPartidos)
        return v
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        historialPartidosViewModel =
            ViewModelProvider(this).get(TabHistorialPartidosViewModel::class.java)
        // TODO: Use the ViewModel

        historialPartidosViewModel.historialPartidosList.observe(
            viewLifecycleOwner,
            Observer { lista ->
                //partidosListAdapter.setData(lista)
                Log.d("pp", "meactualize")
                historialPartidos = lista
                proximosPartidosListAdapter = ProximosPartidosListAdapter(historialPartidos,
                    { position -> alClickearCardPartido(position) },
                    { position -> onBotonEliminar(position) }
                )

                listaHistorialPartidos.adapter = proximosPartidosListAdapter
            })
    }

    override fun onStart() {
        super.onStart()

        val parentJob = Job()
        val scope = CoroutineScope(Dispatchers.Default + parentJob)

        listaHistorialPartidos.setHasFixedSize(true)
        linearLayoutManager = LinearLayoutManager(context)
        listaHistorialPartidos.layoutManager = linearLayoutManager

        proximosPartidosListAdapter = ProximosPartidosListAdapter(historialPartidos,
            { position -> alClickearCardPartido(position) },
            { position -> onBotonEliminar(position) }
        )

        listaHistorialPartidos.adapter = proximosPartidosListAdapter

        val sharedPref: SharedPreferences = requireContext().getSharedPreferences(
            USUARIO_PREFERENCES,
            Context.MODE_PRIVATE
        )
        scope.launch {
            historialPartidosViewModel.refrescarListaHistorialPartidos(
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

    fun onBotonEliminar(position: Int) {
        val parentJob = Job()
        val scope = CoroutineScope(Dispatchers.Default + parentJob)

        val sharedPref: SharedPreferences = requireContext().getSharedPreferences(
            USUARIO_PREFERENCES,
            Context.MODE_PRIVATE
        )
        val emailUsuario: String = sharedPref.getString("EMAIL_USUARIO", "default")!!
        val nombreEvento: String = historialPartidos[position].nombreEvento

        scope.launch {
            val partido: Partido? = historialPartidosViewModel.getPartidoPorNombre(nombreEvento)
            if (partido != null) {
                Snackbar.make(
                    v,
                    "Eliminando $nombreEvento del historial",
                    Snackbar.LENGTH_SHORT
                ).show()
                var salio: Boolean =
                    historialPartidosViewModel.eliminarPartidoDeHistorial(emailUsuario, nombreEvento)
                if (salio) {
                    Snackbar.make(
                        v,
                        "Se elimino $nombreEvento del historial",
                        Snackbar.LENGTH_SHORT
                    ).show()
                    historialPartidosViewModel.refrescarListaHistorialPartidos(
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