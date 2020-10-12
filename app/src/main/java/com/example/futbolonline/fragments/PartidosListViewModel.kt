package com.example.futbolonline.fragments

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.futbolonline.entidades.Partido
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObjects
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

val NOMBRE_COLECCION_PARTIDOS = "partidos"

val db = Firebase.firestore

class PartidosListViewModel : ViewModel() {
    // TODO: Implement the ViewModel


    suspend fun getTodosLosPartidos(): MutableList<Partido> {
        var partidos: MutableList<Partido> = ArrayList<Partido>()

        val questionRef = db.collection(NOMBRE_COLECCION_PARTIDOS)
        val query = questionRef

        try {
            val data = query
                .get()
                .await()
            if (data != null) {
                partidos = data.toObjects<Partido>() as MutableList<Partido>
            }
        } catch (e: Exception) {

        }
        return partidos
    }
}