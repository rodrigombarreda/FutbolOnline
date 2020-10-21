package com.example.futbolonline.fragments

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.example.futbolonline.R
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.internal.IGoogleMapDelegate
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class elegirUbicacion : Fragment() {

    companion object {
        fun newInstance() = elegirUbicacion()
    }

    private lateinit var viewModel: ElegirUbicacionViewModel
    lateinit var v: View
    lateinit var mapaElegirUbicacion: MapView

    private lateinit var mMap: GoogleMap

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        v = inflater.inflate(R.layout.elegir_ubicacion_fragment, container, false)
        mapaElegirUbicacion = v.findViewById(R.id.mapaElegirUbicacion)
        return v
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(ElegirUbicacionViewModel::class.java)
        // TODO: Use the ViewModel
    }

    override fun onStart() {
        super.onStart()
        mapaElegirUbicacion.getMapAsync(OnMapReadyCallback { googleMap: GoogleMap ->
            mMap = googleMap

            // Add a marker in Sydney and move the camera
            val sydney = LatLng(-34.0, 151.0)
            mMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
            mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
        })
    }

}