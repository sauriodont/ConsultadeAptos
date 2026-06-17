package com.example.consultadeaptos

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.consultadeaptos.adapters.HistorialAdapter
import com.example.consultadeaptos.adapters.MesesAdapter
import com.example.consultadeaptos.models.DatabaseHelper
import com.example.consultadeaptos.models.HistorialItem
import com.example.consultadeaptos.models.MesPendiente

class Consulta : AppCompatActivity() {

    private lateinit var spinner: Spinner
    private lateinit var tvPropietario: TextView
    private lateinit var tvInquilino: TextView
    private lateinit var tvDeudaBs: TextView
    private lateinit var tvDeudaUsd: TextView
    private lateinit var tvAbonos: TextView
    private lateinit var recyclerMeses: RecyclerView
    private lateinit var recyclerHistorial: RecyclerView

    private lateinit var dbHelper: DatabaseHelper
    private val historialItems = mutableListOf<HistorialItem>()

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
        recyclerHistorial = findViewById(R.id.recyclerMeses)
        dbHelper = DatabaseHelper(this)

        // Configurar spinner
        val aptos = dbHelper.getPropietarios()
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, aptos)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        // Preparar historial RecyclerView
        val historialAdapter = HistorialAdapter(historialItems)
        recyclerHistorial.layoutManager = LinearLayoutManager(this)
        recyclerHistorial.adapter = historialAdapter

        // Listener de selección de apartamento
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val aptoSeleccionado = aptos[position]
                val propietario = dbHelper.getDatosPropietario(aptoSeleccionado)
                val inquilino = dbHelper.getDatosInquilino(aptoSeleccionado)
                val (deudas, totalBs) = dbHelper.getDeudas(aptoSeleccionado)
                val abono = dbHelper.getAbonos(aptoSeleccionado)
                val mesesPendientes = dbHelper.getMesesPendientes(aptoSeleccionado)
                val historial = dbHelper.getHistorial(aptoSeleccionado)

                tvPropietario.text = "Propietario: ${propietario?.nombre ?: "No disponible"}"
                tvInquilino.text = "Inquilino: ${inquilino?.nombre ?: "No disponible"}"
                tvDeudaBs.text = "Deuda Bs: %.2f".format(totalBs)
                tvDeudaUsd.text = "Deuda \$: %.2f".format(deudas.sumOf { it.dolares })
                tvAbonos.text = "Abonos: Bs %.2f / \$ %.2f".format(abono.bs, abono.usd)

                // Actualizar lista de meses pendientes
                val listaMeses = mesesPendientes.map { fila ->
                    MesPendiente(
                        mes = fila[0],
                        bs = "Bs ${fila[1]}",
                        usd = "\$ ${fila[2]}"
                    )
                }
                val mesesAdapter = MesesAdapter(listaMeses)
                recyclerMeses.layoutManager = LinearLayoutManager(this@Consulta)
                recyclerMeses.adapter = mesesAdapter

                // Actualizar historial
                historialItems.clear()
                historialItems.addAll(historial)
                historialAdapter.notifyDataSetChanged()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }
}