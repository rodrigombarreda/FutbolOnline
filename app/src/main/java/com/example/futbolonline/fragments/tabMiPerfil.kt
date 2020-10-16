package com.example.futbolonline.fragments

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.futbolonline.R
import com.example.futbolonline.entidades.Usuario
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class tabMiPerfil : Fragment() {

    companion object {
        fun newInstance() = tabMiPerfil()
    }

    val USUARIO_PREFERENCES: String = "usuarioPreferences"

    private lateinit var miPefilViewModel: TabMiPerfilViewModel
    lateinit var v: View
    lateinit var txtEmailTabMiPerfil: TextView
    lateinit var txtNombreTabMiPerfil: TextView
    lateinit var txtEdadTabMiPerfil: TextView
    lateinit var txtGeneroTabMiPerfil: TextView
    lateinit var txtCalificacionTabMiPerfil: TextView
    lateinit var txtCantidadPartidosJugadosTabMiPerfil: TextView
    lateinit var txtCantidadPartidosCreadosTabMiPerfil: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        v = inflater.inflate(R.layout.tab_mi_perfil_fragment, container, false)
        txtEmailTabMiPerfil = v.findViewById(R.id.txtEmailTabMiPerfil)
        txtNombreTabMiPerfil = v.findViewById(R.id.txtNombreTabMiPerfil)
        txtEdadTabMiPerfil = v.findViewById(R.id.txtEdadTabMiPerfil)
        txtGeneroTabMiPerfil = v.findViewById(R.id.txtGeneroTabMiPerfil)
        txtCalificacionTabMiPerfil = v.findViewById(R.id.txtCalificacionTabMiPerfil)
        txtCantidadPartidosJugadosTabMiPerfil =
            v.findViewById(R.id.txtCantidadPartidosJugadosTabMiPerfil)
        txtCantidadPartidosCreadosTabMiPerfil =
            v.findViewById(R.id.txtCantidadPartidosCreadosTabMiPerfil)
        return v
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        miPefilViewModel = ViewModelProvider(this).get(TabMiPerfilViewModel::class.java)
        // TODO: Use the ViewModel

        miPefilViewModel.usuario.observe(viewLifecycleOwner,
            Observer { usuario -> setearDatos(usuario) })
    }

    override fun onStart() {
        super.onStart()

        val parentJob = Job()
        val scope = CoroutineScope(Dispatchers.Default + parentJob)

        scope.launch {
            val sharedPref: SharedPreferences = requireContext().getSharedPreferences(
                USUARIO_PREFERENCES,
                Context.MODE_PRIVATE
            )

            txtEmailTabMiPerfil.text =
                "Email: " + sharedPref.getString("EMAIL_USUARIO", "default")!!

            miPefilViewModel.refrescarPerfil(
                sharedPref.getString(
                    "EMAIL_USUARIO",
                    "default"
                )!!
            )
        }
    }


    fun setearDatos(us: Usuario) {
        if (us != null) {
            txtNombreTabMiPerfil.text = "Nombre: " + us.nombre
            txtEdadTabMiPerfil.text = "Edad: " + us.edad.toString()
            txtGeneroTabMiPerfil.text = "Genero: " + us.genero
            txtCalificacionTabMiPerfil.text = "Calificacion: " + us.calificacion
        }
    }
}