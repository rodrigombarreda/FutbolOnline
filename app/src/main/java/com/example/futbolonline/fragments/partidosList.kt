package com.example.futbolonline.fragments

import android.Manifest
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.app.ActivityCompat
import androidx.core.content.ContentProviderCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.futbolonline.R
import com.example.futbolonline.adapters.PartidosListAdapter
import com.example.futbolonline.entidades.Partido
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.material.snackbar.Snackbar
import com.google.maps.android.SphericalUtil
import kotlinx.android.synthetic.main.partidos_list_fragment.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.lang.Exception
import kotlin.properties.Delegates

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

    lateinit var fusedLocationClient: FusedLocationProviderClient
    lateinit var latLngUsuario: LatLng
    lateinit var latLngPartido: LatLng
    var distanciaDesdeUbicacionAPartido by Delegates.notNull<Int>()

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
        partidosListViewModel =
            ViewModelProvider(requireActivity()).get(PartidosListViewModel::class.java)
        proximosPartidosViewModel =
            ViewModelProvider(requireActivity()).get(TabProximosPartidosViewModel::class.java)
        // TODO: Use the ViewModel

        partidosListViewModel.partidosList.observe(viewLifecycleOwner, Observer { lista ->
            //partidosListAdapter.setData(lista)
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
        val accion =
            paginaPrincipalContainerDirections.actionPaginaPrincipalContainerToDetallePartido(
                partidos[position].nombreEvento
            )
        v.findNavController().navigate(accion)
    }

    fun onBotonUnirse(position: Int) {
        val parentJob = Job()
        val scope = CoroutineScope(Dispatchers.Default + parentJob)

        fusedLocationClient =
            LocationServices.getFusedLocationProviderClient(requireContext())

        val sharedPref: SharedPreferences = requireContext().getSharedPreferences(
            USUARIO_PREFERENCES,
            Context.MODE_PRIVATE
        )
        val emailUsuario: String = sharedPref.getString("EMAIL_USUARIO", "default")!!
        val nombreEvento: String = partidos[position].nombreEvento

        scope.launch {
            val partido: Partido? = partidosListViewModel.getPartidoPorNombre(nombreEvento)
            if (partido != null) {
                latLngPartido = LatLng(partido.latitud, partido.longitud)
                enableMyLocation()
                if (distanciaDesdeUbicacionAPartido <= 15000) {
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
                        "Estas muy lejos del partido",
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

    private suspend fun enableMyLocation() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            == PackageManager.PERMISSION_GRANTED
        ) {
            try {
                var location = fusedLocationClient.lastLocation.await()
                if (location != null) {
                    latLngUsuario = LatLng(location.latitude, location.longitude)
                    distanciaDesdeUbicacionAPartido =
                        SphericalUtil.computeDistanceBetween(latLngUsuario, latLngPartido)
                            .toInt()
                }
            } catch (e: Exception) {

            }
        } else {
            // Permission to access the location is missing. Show rationale and request permission
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                1
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>, grantResults: IntArray
    ) {
        when (requestCode) {
            1 -> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() &&
                            grantResults[0] == PackageManager.PERMISSION_GRANTED)
                ) {
                    if (ActivityCompat.checkSelfPermission(
                            requireContext(),
                            Manifest.permission.ACCESS_FINE_LOCATION
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        return
                    }
                    fusedLocationClient.lastLocation
                        .addOnSuccessListener {
                            if (it != null) {
                                latLngUsuario = LatLng(it.latitude, it.longitude)
                                distanciaDesdeUbicacionAPartido =
                                    SphericalUtil.computeDistanceBetween(
                                        latLngUsuario,
                                        latLngPartido
                                    ).toInt()
                            }
                        }
                } else {
                    Snackbar.make(
                        v,
                        "SE REQUIERE UBICACION",
                        Snackbar.LENGTH_SHORT
                    ).show()
                }
                return
            }

            // Add other 'when' lines to check for other
            // permissions this app might request.
            else -> {
                // Ignore all other requests.
            }
        }
    }

}