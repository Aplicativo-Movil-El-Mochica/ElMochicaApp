package com.upao.elmochicaapp.ui

import android.content.Intent
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.StyleSpan
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.upao.elmochicaapp.R

class RegistroActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro)

        // Referencia al TextView
        val textView: TextView = findViewById(R.id.text_iniciar_sesion)

        // Obtener los textos desde strings.xml
        val pregunta = getString(R.string.pregunta_form) // "¿Ya tienes una cuenta?"
        val iniciarSesion = getString(R.string.iniciar_form) // "Iniciar Sesión"

        // Crear SpannableString con ambos textos
        val fullText = "$pregunta $iniciarSesion"

        // Crear SpannableString
        val spannableString = SpannableString(fullText)

        // Aplicar estilo Nunito normal a "¿Ya tienes una cuenta?"
        val startOfClickable = fullText.indexOf(iniciarSesion)
        val endOfClickable = startOfClickable + iniciarSesion.length

        // Aplicar el estilo black a "Iniciar Sesión"
        spannableString.setSpan(
            StyleSpan(android.graphics.Typeface.BOLD),
            startOfClickable,
            endOfClickable,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        // Hacer "Iniciar Sesión" clicable
        val clickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                // Aquí navegas a la actividad de inicio de sesión
                val intent = Intent(this@RegistroActivity, LoginActivity::class.java)
                startActivity(intent)
            }
        }

        // Aplicar el span de clic a "Iniciar Sesión"
        spannableString.setSpan(clickableSpan, startOfClickable, endOfClickable, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

        // Asignar el SpannableString al TextView
        textView.text = spannableString
        textView.movementMethod = LinkMovementMethod.getInstance()
    }
}
