package com.example.login.ui

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.login.R
import com.example.login.model.Event
import com.example.login.model.Notification
import com.example.login.retrofit.ApiService
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.*

class NotificationsActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var notificationAdapter: NotificationAdapter
    private lateinit var apiService: ApiService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notifications)

        // Inicializa Retrofit
        val sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val authToken = sharedPreferences.getString("auth_token", null)

        if (authToken != null) {
            val okHttpClient = OkHttpClient.Builder().addInterceptor { chain: Interceptor.Chain ->
                val original = chain.request()
                val requestBuilder: Request.Builder = original.newBuilder()
                    .header("Authorization", "Bearer $authToken")
                val request: Request = requestBuilder.build()
                chain.proceed(request)
            }.build()

            val retrofit = Retrofit.Builder()
                .baseUrl("http://10.0.2.2:8000/")
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            apiService = retrofit.create(ApiService::class.java)

            // Configuración del RecyclerView
            recyclerView = findViewById(R.id.recyclerViewNotifications)
            recyclerView.layoutManager = LinearLayoutManager(this)

            // Llamada a la API para recolectar eventos
            fetchEvents(authToken)
        } else {
            Toast.makeText(this, "Error: Token de autenticación no disponible", Toast.LENGTH_SHORT).show()
        }
    }

    private fun fetchEvents(authToken: String) {
        apiService.listEvents().enqueue(object : Callback<List<Event>> {
            override fun onResponse(call: Call<List<Event>>, response: Response<List<Event>>) {
                if (response.isSuccessful) {
                    val events = response.body() ?: emptyList()
                    val notifications = generateNotificationsFromEvents(events)
                    setupRecyclerView(notifications)
                } else {
                    Toast.makeText(this@NotificationsActivity, "Error al obtener eventos: ${response.code()}", Toast.LENGTH_SHORT).show()
                    Log.e("Notifications", "Error en la respuesta: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<List<Event>>, t: Throwable) {
                Toast.makeText(this@NotificationsActivity, "Error de red: ${t.message}", Toast.LENGTH_SHORT).show()
                Log.e("Notifications", "Error: ${t.message}")
            }
        })
    }

    private fun generateNotificationsFromEvents(events: List<Event>): List<Notification> {
        val notifications = mutableListOf<Notification>()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val today = Calendar.getInstance().time

        for (event in events) {
            val eventDate = dateFormat.parse(event.date)
            if (eventDate != null && eventDate.after(today)) {
                notifications.add(Notification(
                    id = event.id,
                    title = "Próximo evento: ${event.title}",
                    message = "No te olvides del evento \"${event.title}\" el ${event.date} a las ${event.time}.",
                    timestamp = event.date + " " + event.time
                ))
            }
        }
        return notifications
    }

    private fun setupRecyclerView(notifications: List<Notification>) {
        notificationAdapter = NotificationAdapter(notifications)
        recyclerView.adapter = notificationAdapter
    }
}
