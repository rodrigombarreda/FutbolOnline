package com.example.futbolonline.fragments

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
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
import java.util.*

class detalleUsuarioDePartido : Fragment() {

    companion object {
        fun newInstance() = detalleUsuarioDePartido()
    }

    private lateinit var detalleUsuarioDePartidoViewModel: DetalleUsuarioDePartidoViewModel
    lateinit var v: View
    lateinit var txtEmailDetalleUsuarioDePartido: TextView
    lateinit var txtNombreDetalleUsuarioDePartido: TextView
    lateinit var txtEdadDetalleUsuarioDePartido: TextView
    lateinit var txtGeneroDetalleUsuarioDePartido: TextView
    lateinit var txtCalificacionDetalleUsuarioDePartido: TextView
    lateinit var txtCantidadPartidosJugadosDetalleUsuarioDePartido: TextView
    lateinit var txtCantidadPartidosCreadosDetalleUsuarioDePartido: TextView

    lateinit var emailUsuario: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        v = inflater.inflate(R.layout.detalle_usuario_de_partido_fragment, container, false)
        txtEmailDetalleUsuarioDePartido = v.findViewById(R.id.txtEmailDetalleUsuarioDePartido)
        txtNombreDetalleUsuarioDePartido = v.findViewById(R.id.txtNombreDetalleUsuarioDePartido)
        txtEdadDetalleUsuarioDePartido = v.findViewById(R.id.txtEdadDetalleUsuarioDePartido)
        txtGeneroDetalleUsuarioDePartido = v.findViewById(R.id.txtGeneroDetalleUsuarioDePartido)
        txtCalificacionDetalleUsuarioDePartido =
            v.findViewById(R.id.txtCalificacionDetalleUsuarioDePartido)
        txtCantidadPartidosJugadosDetalleUsuarioDePartido =
            v.findViewById(R.id.txtCantidadPartidosJugadosDetalleUsuarioDePartido)
        txtCantidadPartidosCreadosDetalleUsuarioDePartido =
            v.findViewById(R.id.txtCantidadPartidosCreadosDetalleUsuarioDePartido)
        return v
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        detalleUsuarioDePartidoViewModel =
            ViewModelProvider(this).get(DetalleUsuarioDePartidoViewModel::class.java)
        // TODO: Use the ViewModel

        detalleUsuarioDePartidoViewModel.usuario.observe(viewLifecycleOwner,
            Observer { usuario -> setearDatos(usuario) })

        detalleUsuarioDePartidoViewModel.cantidadPartidosJugados.observe((viewLifecycleOwner),
            Observer { cantidad ->
                txtCantidadPartidosJugadosDetalleUsuarioDePartido.text =
                    "Jugados: " + cantidad.toString()
            })

        detalleUsuarioDePartidoViewModel.cantidadPartidosCreados.observe((viewLifecycleOwner),
            Observer { cantidad ->
                txtCantidadPartidosCreadosDetalleUsuarioDePartido.text =
                    "Creados: " + cantidad.toString()
            })
    }

    override fun onStart() {
        super.onStart()

        emailUsuario = detalleUsuarioDePartidoArgs.fromBundle(requireArguments()).emailUsuario

        val parentJob = Job()
        val scope = CoroutineScope(Dispatchers.Default + parentJob)

        scope.launch {
            detalleUsuarioDePartidoViewModel.refrescarPerfil(emailUsuario)
        }
    }

    fun setearDatos(us: Usuario) {
        if (us != null) {
            txtEmailDetalleUsuarioDePartido.text = "Email: " + us.email
            txtNombreDetalleUsuarioDePartido.text = "Nombre: " + us.nombre
            txtEdadDetalleUsuarioDePartido.text =
                "Edad: " + (Date().year - Date(us.fechaNacimiento).year)
            txtGeneroDetalleUsuarioDePartido.text = "Genero: " + us.genero
            txtCalificacionDetalleUsuarioDePartido.text = "Calificacion: " + us.calificacion
        }
    }

}