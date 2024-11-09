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
//Importaciones Add
import android.net.Uri
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
        println("DNI recuperado de SharedPreferences: $userDni")


        val headerView = navigationView.getHeaderView(0)
        val userNameTextView = headerView.findViewById<TextView>(R.id.user_name)
        val arrowIcon = headerView.findViewById<ImageView>(R.id.header_arrow_icon)

        // Cerrar el menú lateral cuando se hace clic en el icono de flecha
        arrowIcon.setOnClickListener {
            drawerLayout.closeDrawer(GravityCompat.START)
        }

        // Llamada a fetchUserName para obtener el nombre del usuario y actualizar el encabezado
        fetchUserName(userDni) { name ->
            userNameTextView.text = name
        }

        navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.menu_contact -> {
                    // Número de teléfono con código de país, sin "+" ni espacios
                    val phoneNumberWithCountryCode = "51949494754" // Reemplaza con el número deseado
                    val message = "Hola, me gustaría hacer un pedido."

                    val url = "https://api.whatsapp.com/send?phone=$phoneNumberWithCountryCode&text=${URLEncoder.encode(message, "UTF-8")}"

                    try {
                        val intent = Intent(Intent.ACTION_VIEW)
                        intent.setPackage("com.whatsapp")
                        intent.data = Uri.parse(url)
                        if (intent.resolveActivity(packageManager) != null) {
                            startActivity(intent)
                        } else {
                            // WhatsApp no está instalado
                            Toast.makeText(this, "WhatsApp no está instalado", Toast.LENGTH_SHORT).show()
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        Toast.makeText(this, "Error al abrir WhatsApp", Toast.LENGTH_SHORT).show()
                    }

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

// Método para obtener el nombre del usuario
    private fun fetchUserName(dni: Int, callback: (String) -> Unit) {
        if (dni == 0) {
            Toast.makeText(this, "DNI no encontrado", Toast.LENGTH_SHORT).show()
            return
        }

        // Obtener el token de SharedPreferences
        val sharedPref = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val token = sharedPref.getString("JWT_TOKEN", null)

        if (token.isNullOrEmpty()) {
            Toast.makeText(this, "Token no encontrado. Inicia sesión nuevamente.", Toast.LENGTH_SHORT).show()
            logoutUser()
            return
        }

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = ApiClient.apiService.getUserByDni(dni, "Bearer $token")

                withContext(Dispatchers.Main) {
                    println("Código de respuesta: ${response.code()}")
                    println("Mensaje de respuesta: ${response.message()}")

                    if (response.isSuccessful) {
                        val userResponse = response.body()
                        if (userResponse != null) {
                            val userName = userResponse.data // Aquí `data` es el nombre directamente
                            println("Nombre del usuario obtenido: $userName")
                            callback(userName)
                        } else {
                            println("El cuerpo de la respuesta es nulo.")
                            Toast.makeText(this@BaseActivity, "Respuesta vacía al obtener el usuario", Toast.LENGTH_SHORT).show()
                        }
                    }

                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    println("Excepción: ${e.message}")
                    Toast.makeText(this@BaseActivity, "Error de red", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


    // Método para cerrar sesión
    private fun logoutUser() {
        val sharedPref = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            remove("JWT_TOKEN")
            apply()
        }

        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}
