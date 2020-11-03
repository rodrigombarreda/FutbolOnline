package com.example.futbolonline.fragments

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.futbolonline.entidades.Partido
import com.example.futbolonline.entidades.Usuario
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class DetallePartidoViewModel : ViewModel() {
    // TODO: Implement the ViewModel
    val NOMBRE_COLECCION_PARTIDOS: String = "partidos"

    val db = Firebase.firestore

    var partido = MutableLiveData<Partido>()

    suspend fun getPartidoPorNombreEvento(nombreEvento: String) {
        val questionRef = db.collection(NOMBRE_COLECCION_PARTIDOS).document(nombreEvento)
        val query = questionRef

        try {
            val data = query
                .get()
                .await()
            if (data != null) {
                val partidoDeBaseDeDatos = data.toObject<Partido>()
                if (partidoDeBaseDeDatos != null) {
                    partido.value = partidoDeBaseDeDatos
                }
            }
        } catch (e: Exception) {

        }
    }

    fun refrescarDetallePartido(nombreEvento: String) {
        viewModelScope.launch(Dispatchers.Main) {
            getPartidoPorNombreEvento(nombreEvento)
        }
    }
}