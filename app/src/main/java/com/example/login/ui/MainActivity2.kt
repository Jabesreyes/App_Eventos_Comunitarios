package com.example.login.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.login.R

class MainActivity2 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main2)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }



        val btnEventos : Button = findViewById(R.id.btnEventos)
        val btnHistorial : Button = findViewById(R.id.btnHistorial)

        btnEventos.setOnClickListener(){
            val i = Intent(this, Eventos::class.java)
            startActivity(i)
        }

        btnHistorial.setOnClickListener(){
            val y = Intent(this, Historial::class.java)
            startActivity(y)
        }

    }
}