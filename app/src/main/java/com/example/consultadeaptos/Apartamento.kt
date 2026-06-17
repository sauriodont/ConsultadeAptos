package com.example.consultadeaptos

data class Apartamento(
    val numero: String,
    val propietario: String,
    val inquilino: String,
    val deudaBs: Double,
    val deudaUsd: Double
) {
    override fun toString(): String {
        return "Apto $numero"
    }
}