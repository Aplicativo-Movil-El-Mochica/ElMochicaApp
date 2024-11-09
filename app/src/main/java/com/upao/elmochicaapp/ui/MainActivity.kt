package com.upao.elmochicaapp.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.StyleSpan
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.upao.elmochicaapp.R

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        if (isUserLoggedIn()) {
            // Si hay un token, redirigir a la pantalla principal
            startActivity(Intent(this, MenuActivity::class.java))
            finish()
            return
        }

        // Ajuste de los insets de la ventana (mantener)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Configurar el botón para ir a LoginActivity
        val exploreButton: Button = findViewById(R.id.btn_explore)
        exploreButton.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        // Referencia al TextView
        val textView: TextView = findViewById(R.id.text_register)

        // Obtener los textos desde strings.xml
        val textNoCuenta = getString(R.string.pregunta)
        val textRegistrate = getString(R.string.registrate)

        // Combinar ambos textos
        val fullText = "$textNoCuenta $textRegistrate"

        // Crear SpannableString
        val spannableString = SpannableString(fullText)

        // Aplicar estilo Nunito normal a "¿No tienes una cuenta?"
        val startOfClickable = fullText.indexOf(textRegistrate)
        val endOfClickable = startOfClickable + textRegistrate.length

        // Aplicar el estilo black a "Regístrate"
        spannableString.setSpan(
            StyleSpan(android.graphics.Typeface.BOLD),
            startOfClickable,
            endOfClickable,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        // Hacer "Regístrate" clicable
        val clickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                // Aquí navegas a la actividad de registro
                val intent = Intent(this@MainActivity, RegistroActivity::class.java)
                startActivity(intent)
            }
        }

        // Aplicar el span de clic a "Regístrate"
        spannableString.setSpan(clickableSpan, startOfClickable, endOfClickable, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

        // Asignar el SpannableString al TextView
        textView.text = spannableString
        textView.movementMethod = LinkMovementMethod.getInstance()
    }

    private fun isUserLoggedIn(): Boolean {
        val sharedPref = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        return sharedPref.getString("JWT_TOKEN", null) != null
    }
}
