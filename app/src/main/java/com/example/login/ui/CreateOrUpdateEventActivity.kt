package com.example.login.ui

import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.login.R
import com.example.login.model.Event
import com.example.login.retrofit.ApiService
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class CreateOrUpdateEventActivity : AppCompatActivity() {

    private lateinit var apiService: ApiService
    private lateinit var edtTitle: EditText
    private lateinit var edtDate: EditText
    private lateinit var edtTime: EditText
    private lateinit var edtLocation: EditText
    private lateinit var edtDescription: EditText
    private var eventId: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_or_update_event)

        edtTitle = findViewById(R.id.edtTitle)
        edtDate = findViewById(R.id.edtDate)
        edtTime = findViewById(R.id.edtTime)
        edtLocation = findViewById(R.id.edtLocation)
        edtDescription = findViewById(R.id.edtDescription)
        val btnSubmit: Button = findViewById(R.id.btnSubmit)

        // Inicializa Retrofit
        val sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val authToken = sharedPreferences.getString("auth_token", null)

        if (authToken != null) {
            val okHttpClient = OkHttpClient.Builder().addInterceptor { chain ->
                val original = chain.request()
                val requestBuilder = original.newBuilder()
                    .header("Authorization", "Bearer $authToken")
                val request = requestBuilder.build()
                chain.proceed(request)
            }.build()

            val retrofit = Retrofit.Builder()
                .baseUrl("http://10.0.2.2:8000/")
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            apiService = retrofit.create(ApiService::class.java)
        } else {
            Toast.makeText(this, "Error: Token de autenticación no disponible", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Verificar si se está actualizando un evento existente
        eventId = intent.getIntExtra("event_id", -1).takeIf { it != -1 }
        if (eventId != null) {
            loadEventData(eventId!!)
        }

        btnSubmit.setOnClickListener {
            if (eventId == null) {
                createEvent()
            }
        }
    }

    private fun loadEventData(eventId: Int) {
        apiService.getEventDetails(eventId).enqueue(object : Callback<Event> {
            override fun onResponse(call: Call<Event>, response: Response<Event>) {
                if (response.isSuccessful) {
                    val event = response.body()
                    if (event != null) {
                        edtTitle.setText(event.title)
                        edtDate.setText(event.date)
                        edtTime.setText(event.time)
                        edtLocation.setText(event.location)
                        edtDescription.setText(event.description)
                    }
                } else {
                    Toast.makeText(this@CreateOrUpdateEventActivity, "Error al cargar el evento", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Event>, t: Throwable) {
                Toast.makeText(this@CreateOrUpdateEventActivity, "Error de red: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun createEvent() {
        val event = Event(
            id = 0,  // Se supone que el ID lo asignará el servidor
            title = edtTitle.text.toString(),
            date = edtDate.text.toString(),
            time = edtTime.text.toString(),
            location = edtLocation.text.toString(),
            description = edtDescription.text.toString()
        )

        // Obtén el token del almacenamiento compartido
        val sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val authToken = sharedPreferences.getString("auth_token", null)

        if (authToken != null) {
            val token = "Bearer $authToken"
            apiService.createEvent(token, event).enqueue(object : Callback<Event> {
                override fun onResponse(call: Call<Event>, response: Response<Event>) {
                    if (response.isSuccessful) {
                        Toast.makeText(this@CreateOrUpdateEventActivity, "Evento creado con éxito", Toast.LENGTH_SHORT).show()
                        finish()
                    } else {
                        Toast.makeText(this@CreateOrUpdateEventActivity, "Error al crear el evento", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<Event>, t: Throwable) {
                    Toast.makeText(this@CreateOrUpdateEventActivity, "Error de red: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        } else {
            Toast.makeText(this, "Error: Token de autenticación no disponible", Toast.LENGTH_SHORT).show()
        }
    }




}
