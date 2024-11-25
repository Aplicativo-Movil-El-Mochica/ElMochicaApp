package com.upao.elmochicaapp.ui

import android.content.Intent
import android.graphics.PorterDuff
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.lifecycle.lifecycleScope
import com.google.android.material.navigation.NavigationView
import com.upao.elmochicaapp.R
import com.upao.elmochicaapp.api.apiClient.ApiClient
import kotlinx.coroutines.launch

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

        // Listener para cambios en el texto del SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                // No necesitamos hacer nada en el submit
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                val fragment = supportFragmentManager.findFragmentById(R.id.fragment_container)
                if (fragment is ProductListFragment) {
                    fragment.filterProducts(newText ?: "")
                }
                return true
            }
        })
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


    private fun validateSession() {
        lifecycleScope.launch {
            try {
                val jwt = getJwtToken() // Obtener el token JWT desde el almacenamiento local (SharedPreferences)
                if (jwt != null) {
                    val response = ApiClient.apiService.getUserByDni(dni = getUserDni(), token = "Bearer $jwt")
                    if (response.isSuccessful) {
                        // La sesión es válida
                        // Procesar los datos del usuario si es necesario
                    } else if (response.code() == 403) {
                        // El JWT ha expirado o es inválido, eliminamos el JWT y redirigimos
                        clearJwtToken()
                        Toast.makeText(this@MenuActivity, "Tu sesión ha caducado, por favor inicia sesión nuevamente", Toast.LENGTH_LONG).show()
                        redirectToMain()
                    }
                } else {
                    redirectToMain()
                }
            } catch (e: Exception) {
                // Manejar cualquier otro error
                e.printStackTrace()
                Toast.makeText(this@MenuActivity, "Error al validar la sesión: ${e.message}", Toast.LENGTH_LONG).show()
                Log.d("MenuActivity", "Error al validar la sesión: ${e.message}")
            }
        }
    }

    private fun getJwtToken(): String? {
        // Implementa la lógica para obtener el JWT almacenado en SharedPreferences
        val sharedPreferences = getSharedPreferences("AppPrefs", MODE_PRIVATE)
        return sharedPreferences.getString("JWT_TOKEN", null)
    }

    private fun getUserDni(): Int {
        // Devuelve el DNI del usuario almacenado
        val sharedPreferences = getSharedPreferences("AppPrefs", MODE_PRIVATE)
        return sharedPreferences.getInt("USER_DNI", 0)
    }

    private fun clearJwtToken() {
        // Elimina el JWT almacenado en SharedPreferences
        val sharedPreferences = getSharedPreferences("AppPrefs", MODE_PRIVATE)
        sharedPreferences.edit().remove("JWT_TOKEN").apply()
    }

    private fun redirectToMain() {
        // Redirige al MainActivity
        val intent = Intent(this@MenuActivity, MainActivity::class.java)
        startActivity(intent)
        finish() // Finaliza la actividad actual para evitar que el usuario regrese a ella
    }
}
