package com.example.futbolonline.fragments

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.futbolonline.entidades.Partido
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObjects
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class PartidosListViewModel : ViewModel() {
    // TODO: Implement the ViewModel

    private var _partidosList: MutableLiveData<MutableList<Partido>> = MutableLiveData()
    val partidosList: LiveData<MutableList<Partido>> get() = _partidosList

    val NOMBRE_COLECCION_PARTIDOS = "partidos"

    val db = Firebase.firestore

    suspend fun getTodosLosPartidos(): MutableLiveData<MutableList<Partido>> {
        val questionRef = db.collection(NOMBRE_COLECCION_PARTIDOS)
        val query = questionRef

        try {
            val data = query
                .get()
                .await()
            if (data != null) {
                _partidosList.value = data.toObjects<Partido>() as MutableList<Partido>
            }
        } catch (e: Exception) {

        }
        return _partidosList
    }

    fun refrescarListaPartidos() {
        viewModelScope.launch(Dispatchers.Main) {
            _partidosList = getTodosLosPartidos()
        }
    }
}