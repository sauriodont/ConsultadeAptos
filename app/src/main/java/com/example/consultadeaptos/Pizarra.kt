package com.example.consultadeaptos

import android.graphics.Color
import android.os.Bundle
import android.widget.GridLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.consultadeaptos.models.DatabaseHelper
import com.example.consultadeaptos.models.PizarraItem

class Pizarra : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pizarra)

        val dbHelper = DatabaseHelper(this)
        val gridLayout = findViewById<GridLayout>(R.id.gridResultados)
        val textViewTotalMonto = findViewById<TextView>(R.id.textViewTotalMonto)

        val datos = dbHelper.getPizarra()
        gridLayout.columnCount = 4

        for ((index, item) in datos.withIndex()) {
            val rowColor = if (index % 2 == 0) "#F0F0F0" else "#FFFFFF"

            val celdaApto = crearCelda(
                texto = item.apto,
                fondoHex = rowColor
            )

            val celdaCuenta = crearCelda(
                texto = item.cuenta.toString(),
                fondoHex = rowColor,
                textoColor = getColorPorCuenta(item.cuenta)
            )

            val celdaBs = crearCelda(
                texto = "Bs %.2f".format(item.bolivares),
                fondoHex = rowColor
            )

            val celdaUsd = crearCelda(
                texto = "$%.2f".format(item.dolares),
                fondoHex = rowColor
            )

            gridLayout.addView(celdaApto)
            gridLayout.addView(celdaCuenta)
            gridLayout.addView(celdaBs)
            gridLayout.addView(celdaUsd)
        }

        val totalDolares = dbHelper.getTotalDolares()
        textViewTotalMonto.text = "$%.2f".format(totalDolares)
    }

    private fun crearCelda(
        texto: String,
        fondoHex: String,
        textoColor: Int = Color.BLACK
    ): TextView {
        val celda = TextView(this)
        celda.text = texto
        celda.setPadding(8, 8, 8, 8)
        celda.textSize = 14f
        celda.setTextColor(textoColor)
        celda.setBackgroundColor(Color.parseColor(fondoHex))

        val params = GridLayout.LayoutParams(
            GridLayout.spec(GridLayout.UNDEFINED),
            GridLayout.spec(GridLayout.UNDEFINED, 1f)
        )
        params.width = 0
        params.height = GridLayout.LayoutParams.WRAP_CONTENT
        celda.layoutParams = params

        return celda
    }

    private fun getColorPorCuenta(cuenta: Int): Int {
        return when {
            cuenta == 0 -> Color.parseColor("#2E7D32") // Verde
            cuenta == 1 -> Color.parseColor("#1565C0") // Azul
            cuenta > 1  -> Color.parseColor("#C62828") // Rojo
            else        -> Color.BLACK
        }
    }
}