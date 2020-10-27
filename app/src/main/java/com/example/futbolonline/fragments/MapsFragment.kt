package com.example.futbolonline.fragments

import android.Manifest
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import androidx.fragment.app.Fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
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
import com.google.android.material.snackbar.Snackbar
import java.util.ArrayList

class MapsFragment : Fragment() {

    lateinit var v: View
    lateinit var inputUbicacion: EditText
    lateinit var btnElegirUbicacion: Button

    lateinit var map: GoogleMap

    lateinit var ubicacionUsuario: LatLng

    private lateinit var fusedLocationClient: FusedLocationProviderClient//LocationServices.getFusedLocationProviderClient(requireContext())
    private lateinit var geocoder: Geocoder

    var argumentoArrayLatLng = ArrayList<String>() as MutableList<String>
    var ubicacionPartido: String = ""

    lateinit var latLngAnteriorDeMapa: Array<String>

    private val callback = OnMapReadyCallback { googleMap ->
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
        geocoder = Geocoder(requireContext())

        map = googleMap
        enableMyLocation(googleMap)

        googleMap.setMinZoomPreference(15f)
        googleMap.setMaxZoomPreference(20f)

        latLngAnteriorDeMapa =
            MapsFragmentArgs.fromBundle(requireArguments()).latLngAnteriorSeleccionado!!

        if (latLngAnteriorDeMapa.size == 3) {
            argumentoArrayLatLng.add(latLngAnteriorDeMapa[0])
            argumentoArrayLatLng.add(latLngAnteriorDeMapa[1])
            argumentoArrayLatLng.add(latLngAnteriorDeMapa[2])
            ubicacionPartido = argumentoArrayLatLng[2]
            var latLngAnterior = LatLng(
                argumentoArrayLatLng[0].toDouble(),
                argumentoArrayLatLng[1].toDouble()
            )
            inputUbicacion.text.clear()
            inputUbicacion.text.append(ubicacionPartido)
            googleMap.animateCamera(
                CameraUpdateFactory.newLatLng(
                    latLngAnterior
                )
            )
            googleMap.addMarker(MarkerOptions().position(latLngAnterior).title(ubicacionPartido))
        }

        googleMap.setOnMapClickListener(object : GoogleMap.OnMapClickListener {
            override fun onMapClick(latlng: LatLng) {
                googleMap.clear()
                argumentoArrayLatLng.clear()

                googleMap.addMarker(
                    MarkerOptions().position(ubicacionUsuario).title("Tu ubicacion")
                )

                googleMap.animateCamera(CameraUpdateFactory.newLatLng(latlng))

                val location = LatLng(latlng.latitude, latlng.longitude)
                try {
                    ubicacionPartido =
                        geocoder.getFromLocation(
                            latlng.latitude,
                            latlng.longitude,
                            1
                        )[0].getAddressLine(0)
                    inputUbicacion.text.clear()
                    inputUbicacion.text.append(ubicacionPartido)
                } catch (e: Exception) {
                    ubicacionPartido = ""
                }

                /* var latLngDeUbicacion = geocoder.getFromLocationName(ubicacionPartido, 1)
                 Log.d("latLngDeUbicacion", latLngDeUbicacion[0].toString())*/

                argumentoArrayLatLng.add(latlng.latitude.toString())
                argumentoArrayLatLng.add(latlng.longitude.toString())
                argumentoArrayLatLng.add(inputUbicacion.text.toString())

                googleMap.addMarker(MarkerOptions().position(location).title(ubicacionPartido))
            }
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        v = inflater.inflate(R.layout.fragment_maps, container, false)
        inputUbicacion = v.findViewById(R.id.inputUbicacion)
        btnElegirUbicacion = v.findViewById(R.id.btnElegirUbicacion)
        return v
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
    }

    override fun onStart() {
        super.onStart()

        btnElegirUbicacion.setOnClickListener {
            Log.d("args", argumentoArrayLatLng.size.toString())
            if (argumentoArrayLatLng.size == 3) {
                if (!inputUbicacion.text.toString().isBlank()) {
                    argumentoArrayLatLng[2] = inputUbicacion.text.toString()
                }
                irACrearEvento(argumentoArrayLatLng.toTypedArray())
            } else {
                Snackbar.make(
                    v,
                    "Debe elegir ubicación",
                    Snackbar.LENGTH_SHORT
                ).show()
            }
        }
    }

    fun irACrearEvento(argumentoArrayLatLng: Array<String>) {
        val accion =
            MapsFragmentDirections.actionMapsFragmentToCrearEvento(argumentoArrayLatLng)
        v.findNavController().navigate(accion)
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
                        googleMap.addMarker(
                            MarkerOptions().position(ubicacionUsuario).title("Tu ubicacion")
                        )
                        if (latLngAnteriorDeMapa.size != 3) {
                            googleMap.moveCamera(CameraUpdateFactory.newLatLng(ubicacionUsuario))
                        }
                    }
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
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return
                    }
                    map.isMyLocationEnabled = true
                    map.setOnMyLocationButtonClickListener { true }
                    map.setOnMyLocationClickListener {}
                    fusedLocationClient.lastLocation
                        .addOnSuccessListener {
                            if (it != null) {
                                ubicacionUsuario = LatLng(it.latitude, it.longitude)
                                map.addMarker(
                                    MarkerOptions().position(ubicacionUsuario).title("Tu ubicacion")
                                )
                                if (latLngAnteriorDeMapa.size != 3) {
                                    map.moveCamera(CameraUpdateFactory.newLatLng(ubicacionUsuario))
                                }
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