package com.upao.elmochicaapp.ui

import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.RadioGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.upao.elmochicaapp.R

class OrderActivity : AppCompatActivity() {

    private lateinit var sectionRecojo: LinearLayout
    private lateinit var sectionDelivery: LinearLayout
    private lateinit var radioGroupTipoPedido: RadioGroup
    private lateinit var direccionEntrega: EditText
    private lateinit var referenciaDomicilio: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order)

        // Configurar la Toolbar
        val toolbar = findViewById<Toolbar>(R.id.toolbar2)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed() // Volver a la actividad anterior con el nuevo método
        }

        sectionRecojo = findViewById(R.id.section_recojo)
        sectionDelivery = findViewById(R.id.section_delivery)
        radioGroupTipoPedido = findViewById(R.id.radio_group_tipo_pedido)
        direccionEntrega = findViewById(R.id.et_direccion_entrega)
        referenciaDomicilio = findViewById(R.id.et_referencia_domicilio)

        radioGroupTipoPedido.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.radio_recojo -> {
                    sectionRecojo.visibility = View.VISIBLE
                    sectionDelivery.visibility = View.GONE
                }
                R.id.radio_delivery -> {
                    sectionRecojo.visibility = View.GONE
                    sectionDelivery.visibility = View.VISIBLE
                }
            }
        }
    }
}
