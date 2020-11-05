package com.example.futbolonline.fragments

import android.Manifest
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Geocoder
import androidx.fragment.app.Fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.example.futbolonline.R
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.material.snackbar.Snackbar
import com.google.maps.android.SphericalUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import java.util.ArrayList
import kotlin.properties.Delegates

class MapsFragment : Fragment() {
    val DISTANCIA_MAXIMA_EN_METROS: Int = 15000

    lateinit var crearEventoViewModel: CrearEventoViewModel
    lateinit var v: View
    lateinit var txtDistanciaElegirUbicacion: TextView
    lateinit var inputUbicacion: EditText
    lateinit var btnElegirUbicacion: Button

    lateinit var map: GoogleMap

    private lateinit var fusedLocationClient: FusedLocationProviderClient//LocationServices.getFusedLocationProviderClient(requireContext())
    private lateinit var geocoder: Geocoder

    lateinit var ubicacionUsuario: LatLng
    lateinit var ubicacionPartido: LatLng
    var nombreUbicacionPartido: String = ""
    var distanciaDesdeUbicacionAPartido by Delegates.notNull<Int>()

    @SuppressLint("SetTextI18n")
    private val callback = OnMapReadyCallback { googleMap ->
        googleMap.setMinZoomPreference(15f)
        googleMap.setMaxZoomPreference(20f)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
        geocoder = Geocoder(requireContext())

        map = googleMap
        enableMyLocation(googleMap)

        googleMap.setOnMapClickListener(object : GoogleMap.OnMapClickListener {
            override fun onMapClick(latlng: LatLng) {
                ubicacionPartido = LatLng(latlng.latitude, latlng.longitude)

                try {
                    nombreUbicacionPartido =
                        geocoder.getFromLocation(
                            latlng.latitude,
                            latlng.longitude,
                            1
                        )[0].getAddressLine(0)
                    inputUbicacion.text.clear()
                    inputUbicacion.setText(nombreUbicacionPartido)
                } catch (e: Exception) {
                    inputUbicacion.text.clear()
                    nombreUbicacionPartido = ""
                }

                /* var latLngDeUbicacion = geocoder.getFromLocationName(ubicacionPartido, 1)
                 Log.d("latLngDeUbicacion", latLngDeUbicacion[0].toString())*/

                distanciaDesdeUbicacionAPartido =
                    SphericalUtil.computeDistanceBetween(ubicacionUsuario, latlng).toInt()
                txtDistanciaElegirUbicacion.text =
                    "Distancia: " + distanciaDesdeUbicacionAPartido.toInt().toString() + " ms."

                crearEventoViewModel.guardarEstadoUbicacion(
                    ubicacionPartido,
                    nombreUbicacionPartido,
                    distanciaDesdeUbicacionAPartido
                )

                if (crearEventoViewModel.valorUbicacionPartido.value != null) {
                    Log.d("estado", crearEventoViewModel.valorUbicacionPartido.value!!.toString())
                } else {
                    Log.d("estado", "no se guardo")
                }
                if (crearEventoViewModel.valorNombreUbicacionPartido.value != null) {
                    Log.d(
                        "estado",
                        crearEventoViewModel.valorNombreUbicacionPartido.value!!.toString()
                    )
                } else {
                    Log.d("estado", "no se guardo")
                }
                if (crearEventoViewModel.valorDistanciaAPartido.value != null) {
                    Log.d("estado", crearEventoViewModel.valorDistanciaAPartido.value!!.toString())
                } else {
                    Log.d("estado", "no se guardo")
                }

                googleMap.addMarker(
                    MarkerOptions().position(ubicacionPartido).title(nombreUbicacionPartido)
                )
                googleMap.addPolyline(PolylineOptions().add(ubicacionUsuario).add(latlng))
                googleMap.animateCamera(CameraUpdateFactory.newLatLng(latlng))
            }
        })

        googleMap.setOnMyLocationButtonClickListener {
            googleMap.animateCamera(CameraUpdateFactory.newLatLng(ubicacionUsuario))
            true
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        v = inflater.inflate(R.layout.fragment_maps, container, false)
        txtDistanciaElegirUbicacion = v.findViewById(R.id.txtDistanciaElegirUbicacion)
        inputUbicacion = v.findViewById(R.id.inputUbicacion)
        btnElegirUbicacion = v.findViewById(R.id.btnElegirUbicacion)
        return v
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        crearEventoViewModel = ViewModelProvider(this).get(CrearEventoViewModel::class.java)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
    }

    override fun onStart() {
        super.onStart()

        btnElegirUbicacion.setOnClickListener {
            if (!inputUbicacion.text.isBlank() && crearEventoViewModel.valorUbicacionPartido.value != null) {
                if (distanciaDesdeUbicacionAPartido <= DISTANCIA_MAXIMA_EN_METROS) {
                    irACrearEvento()
                } else {
                    Snackbar.make(
                        v,
                        "La distancia maxima hasta el partido debe ser hasta $DISTANCIA_MAXIMA_EN_METROS",
                        Snackbar.LENGTH_SHORT
                    ).show()
                }
            } else {
                inputUbicacion.setError("Debe especificar la ubicacion")
            }
        }
    }

    fun irACrearEvento() {
        v.findNavController().popBackStack()
    }

    // miUbicacion

    private fun enableMyLocation(googleMap: GoogleMap) {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            == PackageManager.PERMISSION_GRANTED
        ) {
            googleMap.isMyLocationEnabled = true
            googleMap.setOnMyLocationButtonClickListener { true }
            googleMap.setOnMyLocationClickListener {}
            fusedLocationClient.lastLocation
                .addOnSuccessListener {
                    if (it != null) {
                        ubicacionUsuario = LatLng(it.latitude, it.longitude)
                    }
                    googleMap.addMarker(
                        MarkerOptions().position(ubicacionUsuario).title("Tu ubicación")
                    )
                    googleMap.animateCamera(CameraUpdateFactory.newLatLng(ubicacionUsuario))
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
                            ACCESS_FINE_LOCATION
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        return
                    }
                    map.isMyLocationEnabled = true
                    map.setOnMyLocationButtonClickListener { true }
                    map.setOnMyLocationClickListener {}
                    fusedLocationClient.lastLocation
                        .addOnSuccessListener {
                            if (it != null) {
                                ubicacionUsuario = LatLng(it.latitude, it.longitude)
                            }
                            map.addMarker(
                                MarkerOptions().position(ubicacionUsuario).title("Tu ubicación")
                            )
                            map.animateCamera(CameraUpdateFactory.newLatLng(ubicacionUsuario))
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
            else -> {
            }
        }
    }

}