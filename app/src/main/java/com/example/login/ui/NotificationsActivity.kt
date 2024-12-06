package com.example.login.ui

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.login.R

class NotificationsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notifications)

        // Aquí puedes cargar y mostrar las notificaciones
        Toast.makeText(this, "Mostrando notificaciones", Toast.LENGTH_SHORT).show()
    }
}
