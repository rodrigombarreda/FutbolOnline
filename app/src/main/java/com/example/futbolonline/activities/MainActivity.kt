package com.example.futbolonline.activities

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.example.futbolonline.R



class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
       getMenuInflater().inflate(R.menu.option,menu)


        return true
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.Cerrarsesion -> {
            val accion =
            true
        }
        R.id.action_nuevo -> {
            Log.i("ActionBar", "Nuevo!")
            true
        }
        R.id.action_buscar -> {
            Log.i("ActionBar", "Buscar!")
            true
        }
        else -> {
            super.onOptionsItemSelected(item)
        }
    }

}