package com.upao.elmochicaapp.ui

import android.content.Intent
import android.graphics.PorterDuff
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.upao.elmochicaapp.R

class MenuActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var btnCheckout: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        drawerLayout = findViewById(R.id.drawer_layout)
        val menuIcon = findViewById<ImageView>(R.id.menu_icon)
        val cartIcon = findViewById<ImageView>(R.id.cart_icon)
        btnCheckout = findViewById(R.id.btn_checkout)

        // Configurar apertura del Drawer al hacer clic en el icono de menú
        menuIcon.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        // Ir a CartActivity al hacer clic en el ícono del carrito
        cartIcon.setOnClickListener {
            startActivity(Intent(this, CartActivity::class.java))
        }

        // Ir a CartActivity al hacer clic en el botón de pago
        btnCheckout.setOnClickListener {
            startActivity(Intent(this, CartActivity::class.java))
        }

        // Configurar SearchView
        val searchView = findViewById<SearchView>(R.id.search_view)
        val hintColor = searchView.findViewById<TextView>(androidx.appcompat.R.id.search_src_text)
        hintColor.setTextColor(ContextCompat.getColor(this, R.color.brown))
        hintColor.setHintTextColor(ContextCompat.getColor(this, R.color.brown))
        val searchIcon = searchView.findViewById<ImageView>(androidx.appcompat.R.id.search_mag_icon)
        searchIcon.setColorFilter(ContextCompat.getColor(this, R.color.brown), PorterDuff.Mode.SRC_IN)

        // Configurar botones de categorías
        setupCategoryButtons()

        // Configuración de elementos en el NavigationView
        val navigationView = findViewById<NavigationView>(R.id.navigation_view)
        navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.menu_contact -> {
                    // Acción al hacer clic en "Contactanos"
                    true
                }
                R.id.menu_logout -> {
                    // Acción al hacer clic en "Cerrar Sesión"
                    true
                }
                else -> false
            }
        }

        // Manejar el evento de retroceso utilizando OnBackPressedDispatcher
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START)
                } else {
                    finish()
                }
            }
        })
    }

    private fun setupCategoryButtons() {
        val categoryMap = mapOf(
            R.id.btn_entrees to "Entradas",
            R.id.btn_criollo to "Platos Criollos",
            R.id.btn_seafood to "Pescados y Mariscos",
            R.id.btn_ceviches to "Ceviches",
            R.id.btn_causas to "Causas",
            R.id.btn_soups to "Sopas",
            R.id.btn_salads to "Ensaladas",
            R.id.btn_snacks to "Piqueos",
            R.id.btn_desserts to "Postres de la Casa",
            R.id.btn_specialties to "Otras Especialidades"
        )

        for ((buttonId, category) in categoryMap) {
            findViewById<Button>(buttonId).setOnClickListener {
                loadFragment(category)
                findViewById<TextView>(R.id.category_title).text = category
            }
        }
    }

    private fun loadFragment(category: String) {
        val fragment = ProductListFragment.newInstance(category)
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
}
