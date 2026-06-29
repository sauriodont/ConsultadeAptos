package com.example.consultadeaptos

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MenuConsultaActivity : AppCompatActivity() {

    private lateinit var btnPagos: Button
    private lateinit var btnRecibos: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu_consulta)

        btnPagos = findViewById(R.id.btnPagos)
        btnRecibos = findViewById(R.id.btnRecibos)

        // Recuperar apartamento desde sesión
        val prefs = getSharedPreferences("SesionUsuario", MODE_PRIVATE)
        val apartment = prefs.getString("apartamento", "")

//         Botón de consulta de pagos
        btnPagos.setOnClickListener {
            val intent = Intent(this, Consulta_apto::class.java)
            intent.putExtra("APARTMENT", apartment)
            startActivity(intent)
        }

        // Botón de consulta de recibos
        btnRecibos.setOnClickListener {
            val intent = Intent(this, Consultarecibos::class.java)
            intent.putExtra("APARTMENT", apartment)
            startActivity(intent)
        }
    }
}
