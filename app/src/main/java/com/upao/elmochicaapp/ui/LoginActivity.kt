package com.upao.elmochicaapp.ui

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.StyleSpan
import android.view.View
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.upao.elmochicaapp.R

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)

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
                //Aqui redirigimos a Registrarse
                val intent = Intent(this@LoginActivity, RegistroActivity::class.java)
                startActivity(intent)
            }
        }

        spannableString.setSpan(clickableSpan, startOfClickable, endOfClickable, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

        textView.text = spannableString
        textView.movementMethod = LinkMovementMethod.getInstance()

    }
}