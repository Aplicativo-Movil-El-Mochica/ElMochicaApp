package com.upao.elmochicaapp.ui

import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.upao.elmochicaapp.R

class MenuActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu) // Puedes renombrar también a activity_menu si prefieres

        // Cargar el fragmento por defecto (Entradas)
        loadFragment("Entradas")

        // Cambiar categoría al seleccionar un botón
        findViewById<Button>(R.id.btn_entrees).setOnClickListener {
            loadFragment("Entradas")
        }

        findViewById<Button>(R.id.btn_seafood).setOnClickListener {
            loadFragment("Mariscos")
        }

        findViewById<Button>(R.id.btn_criollo).setOnClickListener {
            loadFragment("Criollo")
        }
    }

    private fun loadFragment(category: String) {
        val fragment = ProductListFragment().apply {
            arguments = Bundle().apply {
                putString("CATEGORY", category)
            }
        }

        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
}