package com.upao.elmochicaapp.ui

import android.content.Intent
import android.graphics.PorterDuff
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import com.google.android.material.navigation.NavigationView
import com.upao.elmochicaapp.R

class MenuActivity : BaseActivity() {

    private lateinit var btnCheckout: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        // Inicializar drawerLayout y navigationView
        drawerLayout = findViewById(R.id.drawer_layout)
        val navigationView = findViewById<NavigationView>(R.id.navigation_view)
        setupDrawer(drawerLayout, navigationView)

        // Inicializar los botones y componentes de la UI
        btnCheckout = findViewById(R.id.btn_checkout)
        val cartIcon = findViewById<ImageView>(R.id.cart_icon)
        val menuIcon = findViewById<ImageView>(R.id.menu_icon)

        // Configurar el icono de menú para abrir el drawer
        menuIcon.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        // Navegar a CartActivity al hacer clic en el ícono del carrito o en el botón de pago
        cartIcon.setOnClickListener {
            startActivity(Intent(this, CartActivity::class.java))
        }
        btnCheckout.setOnClickListener {
            startActivity(Intent(this, CartActivity::class.java))
        }

        // Configurar SearchView
        val searchView = findViewById<SearchView>(R.id.search_view)
        configureSearchView(searchView)

        // Cargar "Entradas" al inicio
        loadFragment("ENTRADAS")
        findViewById<TextView>(R.id.category_title).text = "Entradas"

        // Configurar botones de categorías
        setupCategoryButtons()

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

    private fun configureSearchView(searchView: SearchView) {
        val hintColor = searchView.findViewById<TextView>(androidx.appcompat.R.id.search_src_text)
        hintColor.setTextColor(ContextCompat.getColor(this, R.color.brown))
        hintColor.setHintTextColor(ContextCompat.getColor(this, R.color.brown))
        val searchIcon = searchView.findViewById<ImageView>(androidx.appcompat.R.id.search_mag_icon)
        searchIcon.setColorFilter(ContextCompat.getColor(this, R.color.brown), PorterDuff.Mode.SRC_IN)
    }

    private fun setupCategoryButtons() {
        val categoryMap = mapOf(
            R.id.btn_entrees to Pair("ENTRADAS", "Entradas"),
            R.id.btn_criollo to Pair("PLATOS_CRIOLLOS", "Platos Criollos"),
            R.id.btn_seafood to Pair("PESCADOS_Y_MARISCOS", "Pescados y Mariscos"),
            R.id.btn_ceviches to Pair("CEVICHES", "Ceviches"),
            R.id.btn_causas to Pair("CAUSAS", "Causas"),
            R.id.btn_soups to Pair("SOPAS", "Sopas"),
            R.id.btn_salads to Pair("ENSALADAS", "Ensaladas"),
            R.id.btn_snacks to Pair("PIQUEOS", "Piqueos"),
            R.id.btn_desserts to Pair("POSTRES", "Postres de la Casa"),
            R.id.btn_specialties to Pair("OTRAS_ESPECIALIDADES", "Otras Especialidades")
        )

        for ((buttonId, categoryInfo) in categoryMap) {
            findViewById<Button>(buttonId).setOnClickListener {
                val (categoryCode, categoryDisplayName) = categoryInfo
                loadFragment(categoryCode)
                findViewById<TextView>(R.id.category_title).text = categoryDisplayName
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
