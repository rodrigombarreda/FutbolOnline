package com.example.futbolonline.activities

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import com.example.futbolonline.R
import com.example.futbolonline.fragments.cambiocontraseniaDirections
import com.example.futbolonline.fragments.paginaPrincipalContainerDirections


class MainActivity : AppCompatActivity() {

    val USUARIO_PREFERENCES: String = "usuarioPreferences"
    val fragmentManager = supportFragmentManager
    val fragmentTransaction = fragmentManager.beginTransaction()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        getMenuInflater().inflate(R.menu.option, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.Cerrarsesion -> {
                val loginActivity = Intent(this, LoginActivity::class.java)
                startActivity(loginActivity)

                val sharedPref: SharedPreferences = getSharedPreferences(
                    USUARIO_PREFERENCES,
                    Context.MODE_PRIVATE
                )
                val editor = sharedPref.edit()
                editor.putString("EMAIL_USUARIO", "")
                editor.apply()
            }
            R.id.cambiarcontra -> {
                val accion =
                    paginaPrincipalContainerDirections.actionPaginaPrincipalContainerToCambiocontrasenia()
                this.findNavController(R.id.fragment).navigate(accion)
            }
        }
        return super.onOptionsItemSelected(item)
    }
}