package com.example.futbolonline.fragments

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.futbolonline.entidades.Partido
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObjects
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

class PartidosListViewModel : ViewModel() {
    // TODO: Implement the ViewModel

    val listaPartidos = MutableLiveData<MutableList<Partido>>()

    val NOMBRE_COLECCION_PARTIDOS = "partidos"

    val db = Firebase.firestore

    suspend fun getTodosLosPartidos() {
        val questionRef = db.collection(NOMBRE_COLECCION_PARTIDOS)
        val query = questionRef

        try {
            val data = query
                .get()
                .await()
            if (data != null) {

                listaPartidos.value = data.toObjects<Partido>() as MutableList<Partido>
                Log.d("Partidos: ", listaPartidos.value.toString())
            }
        } catch (e: Exception) {

        }
    }

}