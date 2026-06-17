package com.example.consultadeaptos.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.consultadeaptos.databinding.ItemPorcobrarBinding
import com.example.consultadeaptos.models.Porcobrar

class PorcobrarAdapter(private val lista: List<Porcobrar>) :
    RecyclerView.Adapter<PorcobrarAdapter.PorcobrarViewHolder>() {

    inner class PorcobrarViewHolder(val binding: ItemPorcobrarBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PorcobrarViewHolder {
        val binding = ItemPorcobrarBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PorcobrarViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PorcobrarViewHolder, position: Int) {
        val item = lista[position]
        holder.binding.tvDetalle.text = "Monto: ${item.monto} Bs / ${item.dolares} $"
    }

    override fun getItemCount() = lista.size
}