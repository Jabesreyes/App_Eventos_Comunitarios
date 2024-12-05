package com.example.login.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.RatingBar
import android.widget.TextView
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

class EventDetailsActivity : AppCompatActivity() {

    private lateinit var apiService: ApiService
    private lateinit var tvEventTitle: TextView
    private lateinit var tvEventDate: TextView
    private lateinit var tvEventTime: TextView
    private lateinit var tvEventLocation: TextView
    private lateinit var tvEventDescription: TextView
    private lateinit var btnRSVP: Button
    private lateinit var btnSubmitComment: Button
    private lateinit var edtComment: EditText
    private lateinit var ratingBar: RatingBar
    private lateinit var btnShare: Button
    private var eventId: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_event_details)

        tvEventTitle = findViewById(R.id.tvEventTitle)
        tvEventDate = findViewById(R.id.tvEventDate)
        tvEventTime = findViewById(R.id.tvEventTime)
        tvEventLocation = findViewById(R.id.tvEventLocation)
        tvEventDescription = findViewById(R.id.tvEventDescription)
        btnRSVP = findViewById(R.id.btnRSVP)
        btnSubmitComment = findViewById(R.id.btnSubmitComment)
        edtComment = findViewById(R.id.edtComment)
        ratingBar = findViewById(R.id.ratingBar)
        btnShare = findViewById(R.id.btnShare)

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

        // Verificar si se está viendo un evento existente
        eventId = intent.getIntExtra("event_id", -1).takeIf { it != -1 }
        if (eventId != null) {
            loadEventData(eventId!!)
        }

        btnRSVP.setOnClickListener {
            eventId?.let { id -> rsvpToEvent(id, authToken!!) }
        }

        btnSubmitComment.setOnClickListener {
            eventId?.let { id -> submitComment(id, authToken!!) }
        }


    }

    private fun loadEventData(eventId: Int) {
        apiService.getEventDetails(eventId).enqueue(object : Callback<Event> {
            override fun onResponse(call: Call<Event>, response: Response<Event>) {
                if (response.isSuccessful) {
                    val event = response.body()
                    if (event != null) {
                        tvEventTitle.text = event.title
                        tvEventDate.text = event.date
                        tvEventTime.text = event.time
                        tvEventLocation.text = event.location
                        tvEventDescription.text = event.description

                        // Configura el botón de compartir para usar el evento cargado
                        btnShare.setOnClickListener {
                            shareEvent(event)
                        }
                    }
                } else {
                    Toast.makeText(this@EventDetailsActivity, "Error al cargar el evento", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Event>, t: Throwable) {
                Toast.makeText(this@EventDetailsActivity, "Error de red: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }


    private fun rsvpToEvent(eventId: Int, authToken: String) {
        val rsvpStatus = mapOf("status" to "confirmed")
        apiService.rsvpEvent(eventId, "Bearer $authToken", rsvpStatus).enqueue(object : Callback<Map<String, String>> {
            override fun onResponse(call: Call<Map<String, String>>, response: Response<Map<String, String>>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@EventDetailsActivity, "Asistencia confirmada", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@EventDetailsActivity, "Error al confirmar asistencia", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Map<String, String>>, t: Throwable) {
                Toast.makeText(this@EventDetailsActivity, "Error de red: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun submitComment(eventId: Int, authToken: String) {
        val comment = mapOf("comment" to edtComment.text.toString())
        apiService.commentEvent(eventId, "Bearer $authToken", comment).enqueue(object : Callback<Map<String, String>> {
            override fun onResponse(call: Call<Map<String, String>>, response: Response<Map<String, String>>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@EventDetailsActivity, "Comentario enviado", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@EventDetailsActivity, "Error al enviar comentario", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Map<String, String>>, t: Throwable) {
                Toast.makeText(this@EventDetailsActivity, "Error de red: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun shareEvent(event: Event) {
        val shareText = """
        ¡Echa un vistazo a este evento increíble que encontré!
        
        Título: ${event.title}
        Fecha: ${event.date}
        Hora: ${event.time}
        Ubicación: ${event.location}
        Descripción: ${event.description}
    """.trimIndent()

        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, shareText)
            type = "text/plain"
        }
        startActivity(Intent.createChooser(shareIntent, "Compartir evento a través de"))
    }

}
