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
import com.example.login.retrofit.ApiService
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import android.content.Intent
import android.widget.Button

class Eventos : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var eventAdapter: EventAdapter
    private lateinit var apiService: ApiService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_eventos)

        // Obtén el token del almacenamiento compartido
        val sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val authToken = sharedPreferences.getString("auth_token", null)

        // Verifica que el token no sea nulo
        if (authToken != null) {
            // Inicializa Retrofit y ApiService con el token en los encabezados
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
            recyclerView = findViewById(R.id.recyclerViewEvents)
            recyclerView.layoutManager = LinearLayoutManager(this)

            // Botón para crear evento
            val btnCreateEvent: Button = findViewById(R.id.btnCreateEvent)
            btnCreateEvent.setOnClickListener {
                val intent = Intent(this, CreateOrUpdateEventActivity::class.java)
                startActivity(intent)
            }

            // Llamada a la API para listar eventos
            fetchCurrentAndFutureEvents()


        } else {
            Toast.makeText(this, "Error: Token de autenticación no disponible", Toast.LENGTH_SHORT).show()
        }

        val btnSeeAllEvents : Button = findViewById(R.id.btnAllEvents)

        btnSeeAllEvents.setOnClickListener(){
            apiService.listEvents().enqueue(object : Callback<List<Event>> {
                override fun onResponse(call: Call<List<Event>>, response: Response<List<Event>>) {
                    if (response.isSuccessful) {
                        val events = response.body() ?: emptyList()
                        setupRecyclerView(events)
                    } else {
                        Toast.makeText(this@Eventos, "Error al obtener todos los eventos: ${response.code()}", Toast.LENGTH_SHORT).show()
                        Log.e("Eventos", "Error en la respuesta: ${response.errorBody()?.string()}")
                    }
                }

                override fun onFailure(call: Call<List<Event>>, t: Throwable) {
                    Toast.makeText(this@Eventos, "Error de red: ${t.message}", Toast.LENGTH_SHORT).show()
                    Log.e("Eventos", "Error: ${t.message}")
                }
            })
        }
    }

    private fun fetchCurrentAndFutureEvents() {
        apiService.getCurrentAndFutureEvents().enqueue(object : Callback<List<Event>> {
            override fun onResponse(call: Call<List<Event>>, response: Response<List<Event>>) {
                if (response.isSuccessful) {
                    val events = response.body() ?: emptyList()
                    setupRecyclerView(events)
                } else {
                    Toast.makeText(this@Eventos, "Error al obtener eventos: ${response.code()}", Toast.LENGTH_SHORT).show()
                    Log.e("Eventos", "Error en la respuesta: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<List<Event>>, t: Throwable) {
                Toast.makeText(this@Eventos, "Error de red: ${t.message}", Toast.LENGTH_SHORT).show()
                Log.e("Eventos", "Error: ${t.message}")
            }
        })
    }

    private fun setupRecyclerView(events: List<Event>) {
        eventAdapter = EventAdapter(events) { event ->
            Toast.makeText(this, "Seleccionaste el evento: ${event.title}", Toast.LENGTH_SHORT).show()
            obtenerDetallesEvento(event.id)
        }
        recyclerView.adapter = eventAdapter
    }

    private fun obtenerDetallesEvento(eventId: Int) {
        apiService.getEventDetails(eventId).enqueue(object : Callback<Event> {
            override fun onResponse(call: Call<Event>, response: Response<Event>) {
                if (response.isSuccessful) {
                    val evento = response.body()
                    println("Detalles del Evento: $evento")

                    // Abrir la actividad para actualizar el evento
                    val intent = Intent(this@Eventos, EventDetailsActivity::class.java)
                    intent.putExtra("event_id", evento?.id)
                    startActivity(intent)
                } else {
                    println("Error en la respuesta: ${response.errorBody()}")
                }
            }

            override fun onFailure(call: Call<Event>, t: Throwable) {
                println("Error en la llamada: ${t.message}")
            }
        })
    }


}
