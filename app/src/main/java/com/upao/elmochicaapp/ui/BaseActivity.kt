package com.upao.elmochicaapp.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.upao.elmochicaapp.R

open class BaseActivity : AppCompatActivity() {

    protected lateinit var drawerLayout: DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    // Método para configurar el menú lateral
    protected fun setupDrawer(drawerLayout: DrawerLayout, navigationView: NavigationView) {
        this.drawerLayout = drawerLayout

        navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.menu_contact -> {
                    Toast.makeText(this, "Contacto", Toast.LENGTH_SHORT).show()
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