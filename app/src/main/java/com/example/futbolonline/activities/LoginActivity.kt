package com.example.futbolonline.activities

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.futbolonline.R

class LoginActivity : AppCompatActivity() {

    val USUARIO_PREFERENCES: String = "usuarioPreferences"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val sharedPref: SharedPreferences = getSharedPreferences(
            USUARIO_PREFERENCES,
            Context.MODE_PRIVATE
        )

        if (sharedPref.getString(
                "EMAIL_USUARIO",
                "default"
            ) != ""
        ) {
            val mainActivity = Intent(this, MainActivity::class.java)
            startActivity(mainActivity)
        }
    }
}