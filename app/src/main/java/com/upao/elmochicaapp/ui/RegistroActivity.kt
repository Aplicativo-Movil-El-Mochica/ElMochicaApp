package com.upao.elmochicaapp.ui

import android.content.Intent
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
import com.upao.elmochicaapp.models.User
import kotlinx.coroutines.launch

class RegistroActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro)

        // Configurar el texto clicable para "Iniciar Sesión"
        setupClickableLoginText()

        // Referencias a los campos del formulario
        val nombreEditText = findViewById<EditText>(R.id.et_nombre)
        val telefonoEditText = findViewById<EditText>(R.id.et_telefono)
        val dniEditText = findViewById<EditText>(R.id.et_dni)
        val correoEditText = findViewById<EditText>(R.id.et_correo)
        val contrasenaEditText = findViewById<EditText>(R.id.et_password)
        val registrarButton = findViewById<Button>(R.id.btn_registrar)

        // Al hacer clic en el botón de registro
        registrarButton.setOnClickListener {
            val nombre = nombreEditText.text.toString().trim()
            val telefono = telefonoEditText.text.toString().trim()
            val dni = dniEditText.text.toString().trim()
            val correo = correoEditText.text.toString().trim()
            val contrasena = contrasenaEditText.text.toString().trim()

            // Validar que los campos no estén vacíos y convertir a Int donde sea necesario
            if (nombre.isNotEmpty() && telefono.isNotEmpty() && dni.isNotEmpty() && correo.isNotEmpty() && contrasena.isNotEmpty()) {
                try {
                    val telefonoInt = telefono.toInt()
                    val dniInt = dni.toInt()

                    // Crear el objeto User con el orden correcto de los parámetros
                    val user = User(
                        name = nombre,
                        email = correo,
                        dni = dniInt,
                        password = contrasena,
                        phone = telefonoInt
                    )

                    // Registrar al usuario llamando a la API
                    registrarUsuario(user)

                } catch (e: NumberFormatException) {
                    Toast.makeText(this, "DNI y Teléfono deben ser números válidos", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Configurar el texto clicable "Iniciar Sesión"
    private fun setupClickableLoginText() {
        val textView: TextView = findViewById(R.id.text_iniciar_sesion)
        val pregunta = getString(R.string.pregunta_form) // "¿Ya tienes una cuenta?"
        val iniciarSesion = getString(R.string.iniciar_form) // "Iniciar Sesión"

        val fullText = "$pregunta $iniciarSesion"
        val spannableString = SpannableString(fullText)

        val startOfClickable = fullText.indexOf(iniciarSesion)
        val endOfClickable = startOfClickable + iniciarSesion.length

        // Aplicar estilo black a "Iniciar Sesión"
        spannableString.setSpan(
            StyleSpan(android.graphics.Typeface.BOLD),
            startOfClickable,
            endOfClickable,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        // Hacer "Iniciar Sesión" clicable
        val clickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                val intent = Intent(this@RegistroActivity, LoginActivity::class.java)
                startActivity(intent)
            }
        }

        spannableString.setSpan(clickableSpan, startOfClickable, endOfClickable, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        textView.text = spannableString
        textView.movementMethod = LinkMovementMethod.getInstance()
    }

    // Método para registrar el usuario en la API
    private fun registrarUsuario(user: User) {
        lifecycleScope.launch {
            try {
                val response = ApiClient.apiService.registerUser(user)
                if (response.isSuccessful) {
                    Toast.makeText(this@RegistroActivity, "Usuario registrado con éxito", Toast.LENGTH_SHORT).show()
                    // Redirigir a la pantalla de inicio de sesión o actividad principal
                    val intent = Intent(this@RegistroActivity, LoginActivity::class.java)
                    startActivity(intent)
                } else {
                    Toast.makeText(this@RegistroActivity, "Error al registrar usuario", Toast.LENGTH_SHORT).show()
                }
            }catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this@RegistroActivity, "Error en la conexión: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
