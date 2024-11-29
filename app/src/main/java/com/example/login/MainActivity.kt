package com.example.login

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuth.AuthStateListener
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import okhttp3.OkHttpClient
import okhttp3.FormBody
import okhttp3.Request
import okhttp3.Response
import okhttp3.Call
import okhttp3.Callback
import java.io.IOException

class MainActivity : AppCompatActivity() {
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var authStateListener: FirebaseAuth.AuthStateListener

    private val GOOGLE_SIGN_IN = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val btnIngresar : Button = findViewById(R.id.btnIngresar)
        val googleButton : Button = findViewById(R.id.googleButton)
        val txtemail : TextView = findViewById(R.id.edtEmail)
        val txtpass : TextView = findViewById(R.id.edtPassword)
        firebaseAuth= Firebase.auth
        btnIngresar.setOnClickListener()
        {
            signIn(txtemail.text.toString(), txtpass.text.toString())
        }

        googleButton.setOnClickListener()
        {
            val googleConf = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()

            val googleClient = GoogleSignIn.getClient(this, googleConf)
            googleClient.signOut()
            startActivityForResult(googleClient.signInIntent, GOOGLE_SIGN_IN)
        }
    }
    private fun signIn(email: String, password: String) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = firebaseAuth.currentUser
                    user?.getIdToken(true)?.addOnCompleteListener { tokenTask ->
                        if (tokenTask.isSuccessful) {
                            val idToken = tokenTask.result?.token
                            if (idToken != null) {
                                sendTokenToBackend(idToken) // Enviar token al backend
                            } else {
                                Toast.makeText(baseContext, "Error: Token vacío", Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            Toast.makeText(baseContext, "Error obteniendo token: ${tokenTask.exception}", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    Toast.makeText(baseContext, "Error de email o contraseña", Toast.LENGTH_SHORT).show()
                }
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == GOOGLE_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                if (account != null) {
                    val credential = GoogleAuthProvider.getCredential(account.idToken, null)
                    firebaseAuth.signInWithCredential(credential)
                        .addOnCompleteListener(this) { task ->
                            if (task.isSuccessful) {
                                val user = firebaseAuth.currentUser
                                user?.getIdToken(true)?.addOnCompleteListener { tokenTask ->
                                    if (tokenTask.isSuccessful) {
                                        val idToken = tokenTask.result?.token
                                        if (idToken != null) {
                                            sendTokenToBackend(idToken) // Enviar token al backend
                                        } else {
                                            Toast.makeText(baseContext, "Error: Token vacío", Toast.LENGTH_SHORT).show()
                                        }
                                    } else {
                                        Toast.makeText(baseContext, "Error obteniendo token: ${tokenTask.exception}", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            } else {
                                Toast.makeText(baseContext, "Error de login con Google", Toast.LENGTH_SHORT).show()
                            }
                        }
                }
            } catch (e: ApiException) {
                Toast.makeText(baseContext, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun sendTokenToBackend(idToken: String) {

        println("Token: $idToken")

        val client = OkHttpClient()
        val requestBody = FormBody.Builder()
            .add("idToken", idToken)
            .build()

        val request = Request.Builder()
            .url("http://10.0.2.2:8000/api/test-user/")  // Reemplaza con tu endpoint backend
            .header("Authorization", "Bearer $idToken")   // Agrega el token al encabezado
            .post(requestBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    Toast.makeText(baseContext, "Error enviando token: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                runOnUiThread {
                    if (response.isSuccessful) {
                        Toast.makeText(baseContext, "Token enviado con éxito", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(baseContext, "Error en la respuesta del servidor: ${response.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })
    }

}