package com.example.consultadeaptos.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.example.consultadeaptos.R
import com.example.consultadeaptos.models.MesPendiente

class TablaMesesAdapter(private val context: Context, private val meses: List<MesPendiente>) : BaseAdapter() {
    override fun getCount() = meses.size * 3 // tres columnas por fila

    override fun getItem(position: Int): String {
        val row = position / 3
        val column = position % 3
        val item = meses[row]

        return when (column) {
            0 -> item.mes
            1 -> item.bs
            else -> item.usd
        }
    }

    override fun getItemId(position: Int) = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.item_celda, parent, false)
        val textView = view.findViewById<TextView>(R.id.tv_valor)
        textView.text = getItem(position)
        return view
    }
}