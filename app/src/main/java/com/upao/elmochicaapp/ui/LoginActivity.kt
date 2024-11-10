package com.upao.elmochicaapp.ui

import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.StyleSpan
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.upao.elmochicaapp.R
import com.upao.elmochicaapp.api.apiClient.ApiClient
import com.upao.elmochicaapp.models.requestModels.LoginRequest
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_login)

        // Configurar texto clicable para "Registrarse"
        setupClickableRegisterText()

        // Referencias a los campos del formulario
        val emailEditText = findViewById<EditText>(R.id.et_correo)
        val passwordEditText = findViewById<EditText>(R.id.et_password)
        val loginButton = findViewById<Button>(R.id.btn_iniciar)

        // Al hacer clic en el botón de inicio de sesión
        loginButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                if (!email.contains("@")) {
                    Toast.makeText(this, "Ingrese un correo Valido", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                if (password.length < 8) {
                    Toast.makeText(this, "La contraseña debe tener al menos 8 caracteres", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                loginUser(email, password)
            } else {
                Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupClickableRegisterText() {
        val textView = findViewById<TextView>(R.id.text_registro)
        val pregunta = getString(R.string.iniciar_pregunta_form)
        val registrar = getString(R.string.iniciar_registrate_form)
        val fullText = "$pregunta $registrar"

        val spannableString = SpannableString(fullText)
        val startOfClickable = fullText.indexOf(registrar)
        val endOfClickable = startOfClickable + registrar.length

        spannableString.setSpan(
            StyleSpan(Typeface.BOLD),
            startOfClickable,
            endOfClickable,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        val clickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                val intent = Intent(this@LoginActivity, RegistroActivity::class.java)
                startActivity(intent)
            }
        }

        spannableString.setSpan(clickableSpan, startOfClickable, endOfClickable, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        textView.text = spannableString
        textView.movementMethod = LinkMovementMethod.getInstance()
    }

    private fun loginUser(email: String, password: String) {
        lifecycleScope.launch {
            try {
                val response = ApiClient.apiService.loginUser(LoginRequest(email, password))
                if (response.isSuccessful) {
                    val loginResponse = response.body()
                    val token = loginResponse?.token
                    val dni = loginResponse?.dni

                    if (token != null && dni != null) {
                        saveTokenAndDni(token, dni)
                        Toast.makeText(this@LoginActivity, "Inicio de sesión exitoso", Toast.LENGTH_SHORT).show()

                        // Redirige al usuario a la pantalla principal
                        startActivity(Intent(this@LoginActivity, MenuActivity::class.java))
                        finish()
                    } else {
                        Toast.makeText(this@LoginActivity, "Error al obtener token o DNI", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@LoginActivity, "Correo o contraseña incorrectas", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this@LoginActivity, "Error en la conexión: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun saveTokenAndDni(token: String, dni: Int) {
        println("Almacenando DNI: $dni")
        val sharedPref = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            putString("JWT_TOKEN", token)
            putInt("USER_DNI", dni)
            apply()
        }
    }

}
