package com.example.consultadeaptos

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity

class Consulta : AppCompatActivity() {

    private lateinit var spinner: android.widget.Spinner
    private lateinit var tvPropietario: android.widget.TextView
    private lateinit var tvInquilino: android.widget.TextView
    private lateinit var tvDeudaBs: android.widget.TextView
    private lateinit var tvDeudaUsd: android.widget.TextView
    private lateinit var tvAbonos: android.widget.TextView
    private lateinit var recyclerMeses: androidx.recyclerview.widget.RecyclerView
    private lateinit var recyclerHistorial: androidx.recyclerview.widget.RecyclerView

    private lateinit var dbHelper: com.example.consultadeaptos.DatabaseHelper
    private val historialItems = mutableListOf<com.example.consultadeaptos.models.HistorialItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_consulta)

        // Inicializar vistas
        spinner = findViewById(R.id.spinner_apto)
        tvPropietario = findViewById(R.id.tv_propietario)
        tvInquilino = findViewById(R.id.tv_inquilino)
        tvDeudaBs = findViewById(R.id.tv_deuda_bs)
        tvDeudaUsd = findViewById(R.id.tv_deuda_usd)
        tvAbonos = findViewById(R.id.tv_abonos)
        recyclerMeses = findViewById(R.id.recycler_meses)
        recyclerHistorial = findViewById(R.id.recycler_historial)
        dbHelper = com.example.consultadeaptos.DatabaseHelper(this)

        // Configurar spinner
        val aptos = dbHelper.getPropietarios()
        val adapter = android.widget.ArrayAdapter(this, android.R.layout.simple_spinner_item, aptos)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        // Preparar historial RecyclerView
        val historialAdapter = com.example.consultadeaptos.adapters.HistorialAdapter(historialItems)
        recyclerHistorial.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this)
        recyclerHistorial.adapter = historialAdapter

        // Listener de selección de apartamento
        spinner.onItemSelectedListener = object : android.widget.AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: android.widget.AdapterView<*>, view: View?, position: Int, id: Long) {
                val aptoSeleccionado = aptos[position]
                val propietario = dbHelper.getDatosPropietario(aptoSeleccionado)
                val inquilino = dbHelper.getDatosInquilino(aptoSeleccionado)
                val pairDeudas = dbHelper.getDeudas(aptoSeleccionado)
                val deudas = pairDeudas.first
                val totalBs = pairDeudas.second
                val abono = dbHelper.getAbonos(aptoSeleccionado)
                val mesesPendientes = dbHelper.getMesesPendientes(aptoSeleccionado)
                val historial = dbHelper.getHistorial(aptoSeleccionado)

                tvPropietario.text = "Propietario: ${propietario?.nombre ?: "No disponible"}"
                tvInquilino.text = "Inquilino: ${inquilino?.nombre ?: "No disponible"}"
                tvDeudaBs.text = "Deuda Bs: %.2f".format(totalBs)
                
                var totalDolares = 0.0
                for (deuda in deudas) {
                    totalDolares += deuda.dolares
                }
                tvDeudaUsd.text = "Deuda $: %.2f".format(totalDolares)
                tvAbonos.text = "Abonos: Bs %.2f / $ %.2f".format(abono.bs, abono.usd)

                // Actualizar lista de meses pendientes
                val listaMeses = mesesPendientes.map { fila ->
                    com.example.consultadeaptos.models.MesPendiente(
                        mes = fila[0],
                        bs = "Bs ${fila[1]}",
                        usd = "$ ${fila[2]}"
                    )
                }
                val mesesAdapter = com.example.consultadeaptos.adapters.MesesAdapter(listaMeses)
                recyclerMeses.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this@Consulta)
                recyclerMeses.adapter = mesesAdapter

                // Actualizar historial
                historialItems.clear()
                historialItems.addAll(historial)
                historialAdapter.notifyDataSetChanged()
            }

            override fun onNothingSelected(parent: android.widget.AdapterView<*>) {}
        }
    }
}
