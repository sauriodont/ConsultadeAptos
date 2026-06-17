package com.example.consultadeaptos.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.consultadeaptos.R
import com.example.consultadeaptos.models.HistorialItem

class HistorialAdapter(private val items: List<HistorialItem>) :
    RecyclerView.Adapter<HistorialAdapter.HistorialItemViewHolder>() {

    class HistorialItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvFecha: TextView = itemView.findViewById(R.id.tv_fecha)
        val tvMes: TextView = itemView.findViewById(R.id.tv_mes)
        val tvMonto: TextView = itemView.findViewById(R.id.tv_monto)
        val tvDolares: TextView = itemView.findViewById(R.id.tv_dolares)
        val tvTasa: TextView = itemView.findViewById(R.id.tv_tasa)
        val tvReferencia: TextView = itemView.findViewById(R.id.tv_referencia)
        val tvFormaPago: TextView = itemView.findViewById(R.id.tv_formapago)
        val tvFechaOperacion: TextView = itemView.findViewById(R.id.tv_fecha_operacion)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistorialItemViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_historial, parent, false)
        return HistorialItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: HistorialItemViewHolder, position: Int) {
        val item = items[position]

        holder.tvFecha.text = "Fecha: ${item.fecha}"
        holder.tvMes.text = "Mes: ${item.mes}"
        holder.tvMonto.text = "Bs: ${"%,.2f".format(item.monto)}"
        holder.tvDolares.text = "USD: ${"%.2f".format(item.dolares)}"
        holder.tvTasa.text = "Tasa: ${item.tasa}"
        holder.tvReferencia.text = "Ref: ${item.referencia}"
        holder.tvFormaPago.text = "Pago: ${item.formapago}"
        holder.tvFechaOperacion.text = "Operación: ${item.fechaOperacion}"

        // 🎨 Color distintivo según tipo de pago
        val pago = item.formapago.lowercase()
        holder.tvFormaPago.setTextColor(
            when {
                "transferencia" in pago -> Color.parseColor("#00796B") // verde azulado
                "efectivo" in pago -> Color.parseColor("#388E3C")      // verde
                "zelle" in pago -> Color.parseColor("#5D1049")         // púrpura oscuro
                else -> Color.BLACK
            }
        )
    }

    override fun getItemCount(): Int = items.size
}