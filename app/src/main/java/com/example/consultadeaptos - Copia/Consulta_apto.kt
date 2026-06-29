package com.example.consultadeaptos

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.consultadeaptos.adapters.MesesAdapter
import com.example.consultadeaptos.DatabaseHelper
import com.example.consultadeaptos.models.MesPendiente

class Consulta_apto : AppCompatActivity() {

    private lateinit var usuPropietario: TextView
    private lateinit var usuInquilino: TextView
    private lateinit var usuDeudaBs: TextView
    private lateinit var usuDeudaUsd: TextView
    private lateinit var usuAbonos: TextView
    private lateinit var recyclerMeses: RecyclerView
    private lateinit var recyclerHistorial: RecyclerView
    private lateinit var dbHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_consulta_apto)

        usuPropietario = findViewById(R.id.usu_propietario)
        usuInquilino = findViewById(R.id.usu_inquilino)
        usuDeudaBs = findViewById(R.id.usu_deuda_bs)
        usuDeudaUsd = findViewById(R.id.usu_deuda_usd)
        usuAbonos = findViewById(R.id.usu_abonos)
        recyclerMeses = findViewById(R.id.usu_recycler_meses)
        recyclerHistorial = findViewById(R.id.usu_recycler_historial)

        dbHelper = DatabaseHelper(this)

        // Recuperar apartamento desde sesión
        val prefs = getSharedPreferences("SesionUsuario", MODE_PRIVATE)
        val apartment = prefs.getString("apartamento", null) ?: return

        // Propietario e Inquilino
        val propietario = dbHelper.getDatosPropietario(apartment)
        val inquilino = dbHelper.getDatosInquilino(apartment)
        usuPropietario.text = "Propietario: ${propietario?.nombre ?: "N/A"}"
        usuInquilino.text = "Inquilino: ${inquilino?.nombre ?: "N/A"}"


        // Deudas y abonos
        val deudas = dbHelper.getDeudas(apartment)
        usuDeudaBs.text = "Deuda Bs: ${String.format("%.2f", deudas.second)} Bs"
        usuDeudaUsd.text = "Deuda USD: ${String.format("%.2f", deudas.first.firstOrNull()?.dolares ?: 0.0)} $"

        val abonos = dbHelper.getAbonos(apartment)
        usuAbonos.text = "Abonos: Bs ${abonos.bs} | USD ${abonos.usd}"

        // Meses pendientes
        val mesesPendientes = dbHelper.getMesesPendientes(apartment)

        val listaMeses = mesesPendientes.map { fila ->
            MesPendiente(
                mes = fila[0],
                bs = "Bs ${fila[1]}",
                usd = "\$ ${fila[2]}"
            )
        }
        val mesesAdapter = MesesAdapter(listaMeses)
        recyclerMeses.layoutManager = LinearLayoutManager(this)
        recyclerMeses.adapter = mesesAdapter

        // Historial
        val historial = dbHelper.getHistorial(apartment)
        recyclerHistorial.layoutManager = LinearLayoutManager(this)
        recyclerHistorial.adapter = SimpleAdapter(historial.map {
            "Fecha: ${it.fecha}\n" +
            "Mes: ${it.mes} \n" +
            "Bs: ${it.monto} \n" +
            "USD: ${it.dolares} \n" +
            "Ref: ${it.referencia} \n"

        })
    }
}
