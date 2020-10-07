package com.example.futbolonline.activities

import android.content.Intent
import android.content.ServiceConnection
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.UserHandle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
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


}