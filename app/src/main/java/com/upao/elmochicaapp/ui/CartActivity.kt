package com.upao.elmochicaapp.ui

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.widget.Button
import android.widget.ImageView
import androidx.core.view.GravityCompat
import com.google.android.material.navigation.NavigationView
import com.upao.elmochicaapp.R

class CartActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)

        val menuIcon = findViewById<ImageView>(R.id.menu_icon)
        menuIcon.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        drawerLayout = findViewById(R.id.drawer_layout)
        val navigationView = findViewById<NavigationView>(R.id.navigation_view)
        setupDrawer(drawerLayout, navigationView)

        // Configurar botón para ir a OrderActivity
        val btnCheckout = findViewById<Button>(R.id.btn_checkout)
        btnCheckout.setOnClickListener {
            val intent = Intent(this, OrderActivity::class.java)
            startActivity(intent)
        }
    }

}
