package com.upao.elmochicaapp.ui

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import com.google.android.material.navigation.NavigationView
import com.upao.elmochicaapp.R

class CartActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)

        val toolbar = findViewById<Toolbar>(R.id.toolbar2)
        setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener {
            // Acción de retroceso
            onBackPressedDispatcher.onBackPressed()
        }

        // Configurar botón para ir a OrderActivity
        val btnCheckout = findViewById<Button>(R.id.btn_checkout)
        btnCheckout.setOnClickListener {
            val intent = Intent(this, OrderActivity::class.java)
            startActivity(intent)
        }
    }
}
