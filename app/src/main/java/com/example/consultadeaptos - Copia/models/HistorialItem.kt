package com.example.consultadeaptos.models
data class HistorialItem(
    val fecha: String,
    val mes: String,
    val monto: Double,
    val dolares: Double,
    val tasa: Double,
    val referencia: String,
    val formapago: String,
    val fechaOperacion: String
)