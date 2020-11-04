package com.example.futbolonline.fragments

import android.Manifest
import android.content.pm.PackageManager
import android.location.Geocoder
import androidx.fragment.app.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
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
import kotlin.properties.Delegates

class mapaUbicacionDetallePartido : Fragment() {

    lateinit var fusedLocationClient: FusedLocationProviderClient

    lateinit var v: View
    lateinit var txtDistanciaAPartido: TextView

    lateinit var googleMap: GoogleMap

    lateinit var latLngPartido: LatLng
    lateinit var latLngUsuario: LatLng

    lateinit var nombreUbicacion: String

    var distanciaDesdeUbicacionAPartido by Delegates.notNull<Int>()

    private val callback = OnMapReadyCallback { googleMap ->
        fusedLocationClient =
            LocationServices.getFusedLocationProviderClient(requireContext())
        this.googleMap = googleMap
        enableMyLocation(googleMap)
        setearZoomMinimoYMaximoDelMapa(googleMap)
        googleMap.addMarker(
            MarkerOptions().position(latLngPartido).title(nombreUbicacion)
        )
        googleMap.animateCamera(
            CameraUpdateFactory.newLatLng(
                latLngPartido
            )
        )

        googleMap.setOnMyLocationButtonClickListener {
            googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLngUsuario))
            true
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        v = inflater.inflate(R.layout.fragment_mapa_ubicacion_detalle_partido, container, false)
        txtDistanciaAPartido = v.findViewById(R.id.txtDistanciaAPartido)
        return v
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment =
            childFragmentManager.findFragmentById(R.id.mapDetallePartido) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
    }

    override fun onStart() {
        super.onStart()

        val latPartido = mapaUbicacionDetallePartidoArgs.fromBundle(requireArguments()).lat
        val lngPartido = mapaUbicacionDetallePartidoArgs.fromBundle(requireArguments()).lng
        latLngPartido = LatLng(latPartido.toDouble(), lngPartido.toDouble())

        nombreUbicacion =
            mapaUbicacionDetallePartidoArgs.fromBundle(requireArguments()).nombreUbicacion

    }

    fun setearZoomMinimoYMaximoDelMapa(googleMap: GoogleMap) {
        googleMap.setMinZoomPreference(15f)
        googleMap.setMaxZoomPreference(20f)
    }

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
                        latLngUsuario = LatLng(it.latitude, it.longitude)
                        googleMap.addMarker(
                            MarkerOptions().position(latLngUsuario).title("Tu ubicacion")
                        )
                        googleMap.addPolyline(
                            PolylineOptions().add(latLngUsuario).add(latLngPartido)
                        )
                        distanciaDesdeUbicacionAPartido =
                            SphericalUtil.computeDistanceBetween(latLngUsuario, latLngPartido)
                                .toInt()
                        txtDistanciaAPartido.text =
                            "Distancia: $distanciaDesdeUbicacionAPartido ms."
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
                            Manifest.permission.ACCESS_FINE_LOCATION
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        return
                    }
                    googleMap.isMyLocationEnabled = true
                    googleMap.setOnMyLocationButtonClickListener { true }
                    googleMap.setOnMyLocationClickListener {}
                    fusedLocationClient.lastLocation
                        .addOnSuccessListener {
                            if (it != null) {
                                latLngUsuario = LatLng(it.latitude, it.longitude)
                                googleMap.addMarker(
                                    MarkerOptions().position(latLngUsuario).title("Tu ubicacion")
                                )
                                googleMap.addPolyline(
                                    PolylineOptions().add(latLngUsuario).add(latLngPartido)
                                )
                                distanciaDesdeUbicacionAPartido =
                                    SphericalUtil.computeDistanceBetween(
                                        latLngUsuario,
                                        latLngPartido
                                    ).toInt()
                                txtDistanciaAPartido.text =
                                    "Distancia: $distanciaDesdeUbicacionAPartido ms."
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