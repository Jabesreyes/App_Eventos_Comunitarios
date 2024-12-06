package com.example.login.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.login.R

class MainActivity2 : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)

        val btnEventos: Button = findViewById(R.id.btnEventos)
        btnEventos.setOnClickListener {
            val intent = Intent(this, Eventos::class.java)
            startActivity(intent)
        }

        val btnHistorial: Button = findViewById(R.id.btnHistorial)
        btnHistorial.setOnClickListener {
            val sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
            val intent = Intent(this, EventHistoryActivity::class.java)
            startActivity(intent)
        }

        val ivBell: ImageView = findViewById(R.id.ivBell)
        ivBell.setOnClickListener {
            val intent = Intent(this, NotificationsActivity::class.java)
            startActivity(intent)
        }
    }
}
