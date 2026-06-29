package com.example.consultadeaptos

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class SimpleAdapter(private val items: List<String>) :
    RecyclerView.Adapter<SimpleAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val text: TextView = view.findViewById(android.R.id.text1)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(android.R.layout.simple_list_item_1, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.text.text = items[position]

        // Alternar colores de fondo
        if (position % 2 == 0) {
            holder.itemView.setBackgroundColor(Color.parseColor("#F0F0F0")) // gris claro
        } else {
            holder.itemView.setBackgroundColor(Color.parseColor("#FFFFFF")) // blanco
        }
    }

    override fun getItemCount(): Int = items.size
}
