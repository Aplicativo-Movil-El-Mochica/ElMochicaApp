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

        setupClickableLoginText()

        val nombreEditText = findViewById<EditText>(R.id.et_nombre)
        val telefonoEditText = findViewById<EditText>(R.id.et_telefono)
        val dniEditText = findViewById<EditText>(R.id.et_dni)
        val correoEditText = findViewById<EditText>(R.id.et_correo)
        val contrasenaEditText = findViewById<EditText>(R.id.et_password)
        val registrarButton = findViewById<Button>(R.id.btn_registrar)

        registrarButton.setOnClickListener {
            val nombre = nombreEditText.text.toString().trim()
            val telefono = telefonoEditText.text.toString().trim()
            val dni = dniEditText.text.toString().trim()
            val correo = correoEditText.text.toString().trim()
            val contrasena = contrasenaEditText.text.toString().trim()

            // Validaciones
            if (nombre.isEmpty() || telefono.isEmpty() || dni.isEmpty() || correo.isEmpty() || contrasena.isEmpty()) {
                Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!telefono.matches(Regex("^[0-9]{9}$"))) {
                Toast.makeText(this, "Ingrese un Teléfono Válido de 9 dígitos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!dni.matches(Regex("^[0-9]{8}$"))) {
                Toast.makeText(this, "Ingrese un DNI Válido de 8 dígitos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!correo.contains("@")) {
                Toast.makeText(this, "Ingrese un correo válido", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (contrasena.length < 8) {
                Toast.makeText(this, "La contraseña debe tener al menos 8 caracteres", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            try {
                val telefonoInt = telefono.toInt()
                val dniInt = dni.toInt()

                val user = User(
                    name = nombre,
                    email = correo,
                    dni = dniInt,
                    password = contrasena,
                    phone = telefonoInt
                )

                registrarUsuario(user)

            } catch (e: NumberFormatException) {
                Toast.makeText(this, "DNI y Teléfono deben ser números válidos", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupClickableLoginText() {
        val textView: TextView = findViewById(R.id.text_iniciar_sesion)
        val pregunta = getString(R.string.pregunta_form)
        val iniciarSesion = getString(R.string.iniciar_form)

        val fullText = "$pregunta $iniciarSesion"
        val spannableString = SpannableString(fullText)

        val startOfClickable = fullText.indexOf(iniciarSesion)
        val endOfClickable = startOfClickable + iniciarSesion.length

        spannableString.setSpan(
            StyleSpan(android.graphics.Typeface.BOLD),
            startOfClickable,
            endOfClickable,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

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

    private fun registrarUsuario(user: User) {
        lifecycleScope.launch {
            try {
                val response = ApiClient.apiService.registerUser(user)
                if (response.isSuccessful) {
                    Toast.makeText(this@RegistroActivity, "Usuario registrado con éxito", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this@RegistroActivity, LoginActivity::class.java)
                    startActivity(intent)
                } else if (response.code() == 409) {
                    val responseBody = response.errorBody()?.string() ?: ""
                    handleConflictError(responseBody)
                } else {
                    Toast.makeText(this@RegistroActivity, "Error al registrar usuario", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this@RegistroActivity, "Error en la conexión: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun handleConflictError(responseBody: String) {
        when {
            responseBody.contains("email") && responseBody.contains("DNI") && responseBody.contains("teléfono") ->
                Toast.makeText(this, "El correo, DNI y teléfono ya están registrados", Toast.LENGTH_SHORT).show()

            responseBody.contains("email") && responseBody.contains("DNI") ->
                Toast.makeText(this, "El correo y DNI ya están registrados", Toast.LENGTH_SHORT).show()

            responseBody.contains("email") && responseBody.contains("teléfono") ->
                Toast.makeText(this, "El correo y teléfono ya están registrados", Toast.LENGTH_SHORT).show()

            responseBody.contains("DNI") && responseBody.contains("teléfono") ->
                Toast.makeText(this, "El DNI y teléfono ya están registrados", Toast.LENGTH_SHORT).show()

            responseBody.contains("email") ->
                Toast.makeText(this, "Este correo ya está registrado", Toast.LENGTH_SHORT).show()

            responseBody.contains("DNI") ->
                Toast.makeText(this, "Este DNI ya está registrado", Toast.LENGTH_SHORT).show()

            responseBody.contains("teléfono") ->
                Toast.makeText(this, "Este teléfono ya está registrado", Toast.LENGTH_SHORT).show()

            else ->
                Toast.makeText(this, "El usuario ya existe", Toast.LENGTH_SHORT).show()
        }
    }

}
