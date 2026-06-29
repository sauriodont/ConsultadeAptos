package com.example.consultadeaptos.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.consultadeaptos.databinding.ItemMensualBinding
import com.example.consultadeaptos.models.MesPendiente

class MesesAdapter(
    private val meses: List<MesPendiente>
) : RecyclerView.Adapter<MesesAdapter.MesViewHolder>() {

    inner class MesViewHolder(private val binding: ItemMensualBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(mesPendiente: MesPendiente) {
            binding.tvMes.text = mesPendiente.mes ?: ""
            binding.tvBs.text = mesPendiente.bs ?: ""
            binding.tvUsd.text = mesPendiente.usd ?: ""
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MesViewHolder {
        val binding = ItemMensualBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MesViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MesViewHolder, position: Int) {
        holder.bind(meses[position])
    }

    override fun getItemCount(): Int = meses.size
}