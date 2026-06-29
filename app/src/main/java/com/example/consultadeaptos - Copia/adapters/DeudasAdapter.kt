package com.example.consultadeaptos.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.consultadeaptos.databinding.ItemDeudaBinding
import com.example.consultadeaptos.models.Deuda

class DeudasAdapter(private val deudas: List<Deuda.Deuda>) : RecyclerView.Adapter<DeudasAdapter.DeudaViewHolder>() {

    inner class DeudaViewHolder(val binding: ItemDeudaBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeudaViewHolder {
        val binding = ItemDeudaBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DeudaViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DeudaViewHolder, position: Int) {
        val deuda = deudas[position]
        holder.binding.tvMes.text = deuda.mes
        holder.binding.tvMontoBs.text = "%.2f Bs".format(deuda.monto)
        holder.binding.tvMontoUsd.text = "%.2f $".format(deuda.dolares)
    }

    override fun getItemCount() = deudas.size
}