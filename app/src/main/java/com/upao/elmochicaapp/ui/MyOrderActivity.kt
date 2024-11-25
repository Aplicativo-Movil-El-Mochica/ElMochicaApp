package com.upao.elmochicaapp.ui

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import com.upao.elmochicaapp.R

class MyOrderActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_my_order)

    }
}