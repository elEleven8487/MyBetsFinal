package com.example.mybets

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Simplemente le decimos que cargue tu activity_main.xml
        // (y este a su vez cargará tu mapa de navegación)
        setContentView(R.layout.activity_main)
    }
}