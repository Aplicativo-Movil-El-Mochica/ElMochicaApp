package com.upao.elmochicaapp.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.upao.elmochicaapp.R
import com.upao.elmochicaapp.api.apiClient.ApiClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import android.net.Uri
import android.util.Log
import java.net.URLEncoder

open class BaseActivity : AppCompatActivity() {

    protected lateinit var drawerLayout: DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    // Método para configurar el menú lateral
    protected fun setupDrawer(drawerLayout: DrawerLayout, navigationView: NavigationView) {
        this.drawerLayout = drawerLayout

        // Obtén el DNI del usuario de SharedPreferences
        val sharedPref = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val userDni = sharedPref.getInt("USER_DNI", 0)
        val token = sharedPref.getString("JWT_TOKEN", null)

        val headerView = navigationView.getHeaderView(0)
        val userNameTextView = headerView.findViewById<TextView>(R.id.user_name)
        val arrowIcon = headerView.findViewById<ImageView>(R.id.header_arrow_icon)

        // Cerrar el menú lateral cuando se hace clic en el icono de flecha
        arrowIcon.setOnClickListener {
            drawerLayout.closeDrawer(GravityCompat.START)
        }

        // Llamada a fetchUserName para obtener el nombre y userId del usuario
        if (token != null) {
            fetchUserNameAndUserId(userDni, token) { name, userId ->
                userNameTextView.text = name
                saveUserId(userId) // Guardar userId en SharedPreferences
            }
        } else {
            Toast.makeText(this, "Token no encontrado. Inicia sesión nuevamente.", Toast.LENGTH_SHORT).show()
            logoutUser()
        }

        navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.menu_contact -> {
                    openWhatsApp()
                    drawerLayout.closeDrawer(GravityCompat.START)
                    true
                }
                R.id.menu_logout -> {
                    Toast.makeText(this, "Cerrando sesión", Toast.LENGTH_SHORT).show()
                    logoutUser()
                    true
                }
                else -> false
            }
        }
    }

    private fun openWhatsApp() {
        val phoneNumberWithCountryCode = "51949494754"
        val message = "Hola, me gustaría hacer un pedido."
        val url = "https://api.whatsapp.com/send?phone=$phoneNumberWithCountryCode&text=${URLEncoder.encode(message, "UTF-8")}"

        try {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.setPackage("com.whatsapp")
            intent.data = Uri.parse(url)
            if (intent.resolveActivity(packageManager) != null) {
                startActivity(intent)
            } else {
                Toast.makeText(this, "WhatsApp no está instalado", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Error al abrir WhatsApp", Toast.LENGTH_SHORT).show()
        }
    }

    // Método para obtener el nombre del usuario y userId
    private fun fetchUserNameAndUserId(dni: Int, token: String, callback: (String, String) -> Unit) {
        if (dni == 0) {
            Toast.makeText(this, "DNI no encontrado", Toast.LENGTH_SHORT).show()
            return
        }

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = ApiClient.apiService.getUserByDni(dni, "Bearer $token")

                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        val userResponse = response.body()
                        if (userResponse != null) {
                            val userName = userResponse.name
                            val userId = userResponse.id
                            callback(userName, userId)
                        } else {
                            Toast.makeText(this@BaseActivity, "Respuesta vacía al obtener el usuario", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(this@BaseActivity, "Error: ${response.message()}", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Log.d("MenuActivity", "Error al validar la sesión: ${e.message}")
                    Toast.makeText(this@BaseActivity, "Error de red: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    // Guardar userId en SharedPreferences
    private fun saveUserId(userId: String) {
        val sharedPref = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            putString("USER_ID", userId)
            apply()
        }
    }

    fun getToken(): String? {
        val sharedPref = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        return sharedPref.getString("JWT_TOKEN", null)
    }

    protected fun getUserId(): String? {
        val sharedPref = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        return sharedPref.getString("USER_ID", null)
    }

    // Método para cerrar sesión
    private fun logoutUser() {
        val sharedPref = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            remove("JWT_TOKEN")
            remove("USER_DNI")
            remove("USER_ID")
            apply()
        }

        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}
