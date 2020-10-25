package com.example.futbolonline.fragments

import android.Manifest
import android.content.pm.PackageManager
import android.location.Geocoder
import androidx.fragment.app.Fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.navigation.findNavController
import com.example.futbolonline.R
import com.example.futbolonline.activities.MainActivity
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.zzt

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.api.Context

class MapsFragment : Fragment() {

    lateinit var v: View

    //    lateinit var geocoder: Geocoder
    var ubicacionPartido: String = ""

    private val callback = OnMapReadyCallback { googleMap ->
        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * In this case, we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         */
        val sydney = LatLng(-34.0, 151.0)
        googleMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))

        googleMap.setOnMapClickListener(object : GoogleMap.OnMapClickListener {
            override fun onMapClick(latlng: LatLng) {
                googleMap.clear();
                googleMap.animateCamera(CameraUpdateFactory.newLatLng(latlng));

                val location = LatLng(latlng.latitude, latlng.longitude)
                var argumentoArrayLatLng = LongArray(2)
                argumentoArrayLatLng[0] = latlng.latitude.toLong()
                argumentoArrayLatLng[1] = latlng.longitude.toLong()

                /*  ubicacionPartido =
                      geocoder.getFromLocation(latlng.latitude, latlng.longitude, 1)[0].toString()
                  Log.d("ubi", ubicacionPartido)*/

                googleMap.addMarker(MarkerOptions().position(location))
                irACrearEvento(argumentoArrayLatLng)
            }
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        v = inflater.inflate(R.layout.fragment_maps, container, false)
        return v
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
    }

    override fun onStart() {
        super.onStart()
    }

    fun irACrearEvento(argumentoArrayLatLng: LongArray) {
        val accion =
            MapsFragmentDirections.actionMapsFragmentToCrearEvento(argumentoArrayLatLng)
        v.findNavController().navigate(accion)
    }
}