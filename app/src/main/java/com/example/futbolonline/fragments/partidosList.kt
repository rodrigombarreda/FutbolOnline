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
import androidx.lifecycle.viewModelScope
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.futbolonline.R
import com.example.futbolonline.adapters.PartidosListAdapter
import com.example.futbolonline.entidades.Partido
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.partidos_list_fragment.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class partidosList : Fragment() {

    companion object {
        fun newInstance() = partidosList()
    }

    val USUARIO_PREFERENCES: String = "usuarioPreferences"

    var partidos: MutableList<Partido> = ArrayList<Partido>()

    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var partidosListAdapter: PartidosListAdapter

    private lateinit var partidosListViewModel: PartidosListViewModel
    lateinit var v: View
    lateinit var listaPartidos: RecyclerView

    private lateinit var proximosPartidosViewModel: TabProximosPartidosViewModel

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
        partidosListViewModel = ViewModelProvider(requireActivity()).get(PartidosListViewModel::class.java)
        proximosPartidosViewModel =
            ViewModelProvider(requireActivity()).get(TabProximosPartidosViewModel::class.java)
        // TODO: Use the ViewModel

        partidosListViewModel.partidosList.observe(viewLifecycleOwner, Observer { lista ->
            //partidosListAdapter.setData(lista)
            Log.d("pl", "meactualize")
            partidos = lista
            partidosListAdapter = PartidosListAdapter(partidos,
                { position -> alClickearCardPartido(position) },
                { position -> onBotonUnirse(position) }
            )

            listaPartidos.adapter = partidosListAdapter
        })

    }

    override fun onStart() {
        super.onStart()

        val parentJob = Job()
        val scope = CoroutineScope(Dispatchers.Default + parentJob)

        listaPartidos.setHasFixedSize(true)
        linearLayoutManager = LinearLayoutManager(context)
        Log.d("linearLayoutManager: ", linearLayoutManager.toString())
        listaPartidos.layoutManager = linearLayoutManager
        Log.d("linearDeLista: ", listaPartidos.layoutManager.toString())

        partidosListAdapter = PartidosListAdapter(partidos,
            { position -> alClickearCardPartido(position) },
            { position -> onBotonUnirse(position) }
        )

        listaPartidos.adapter = partidosListAdapter

        val sharedPref: SharedPreferences = requireContext().getSharedPreferences(
            USUARIO_PREFERENCES,
            Context.MODE_PRIVATE
        )
        scope.launch {
            partidosListViewModel.refrescarListaPartidos(
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

    fun onBotonUnirse(position: Int) {
        val parentJob = Job()
        val scope = CoroutineScope(Dispatchers.Default + parentJob)

        val sharedPref: SharedPreferences = requireContext().getSharedPreferences(
            USUARIO_PREFERENCES,
            Context.MODE_PRIVATE
        )
        val emailUsuario: String = sharedPref.getString("EMAIL_USUARIO", "default")!!
        val nombreEvento: String = partidos[position].nombreEvento

        scope.launch {
            val partido: Partido? = partidosListViewModel.getPartidoPorNombre(nombreEvento)
            if (partido != null) {
                var sePuedeUnir = partidosListViewModel.sePuedeUnir(emailUsuario, partido)
                if (sePuedeUnir) {
                    Snackbar.make(
                        v,
                        "Uniendose a partido...",
                        Snackbar.LENGTH_SHORT
                    ).show()
                    var seUnio: Boolean =
                        partidosListViewModel.unirUsuarioAPartido(emailUsuario, nombreEvento)
                    if (seUnio) {
                        Snackbar.make(
                            v,
                            "Te uniste al partido$nombreEvento",
                            Snackbar.LENGTH_SHORT
                        ).show()
                        partidosListViewModel.refrescarListaPartidos(
                            sharedPref.getString(
                                "EMAIL_USUARIO",
                                "default"
                            )!!
                        )
                        proximosPartidosViewModel.refrescarListaProximosPartidos(
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
                        "No cumples los requisitos para unirte",
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