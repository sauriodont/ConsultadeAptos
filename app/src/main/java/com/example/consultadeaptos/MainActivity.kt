package com.example.consultadeaptos

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL

class MainActivity : AppCompatActivity() {

    private val urlBD = "https://drive.google.com/uc?export=download&id=1MOO4w2D-yrQdvR4bkaeohK9lJlIHwCPZ"
    private val nombreBD = "karuai2.sqlite3"

    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        progressBar = findViewById(R.id.progress_bar)
        progressBar.visibility = View.VISIBLE

        descargarBaseDeDatos(urlBD, nombreBD) { exito ->
            runOnUiThread {
                progressBar.visibility = View.GONE
                if (exito) {
                    val archivoBD = File(filesDir, nombreBD)
                    if (archivoBD.exists() && archivoBD.length() > 0) {

                        // 🔘 Botón: abrir Consulta.kt
                        val btnIrConsulta = findViewById<Button>(R.id.btnConsulta)
                        btnIrConsulta.visibility = View.VISIBLE
                        btnIrConsulta.setOnClickListener {
                            startActivity(Intent(this, Consulta::class.java))
                        }

                        // 🔘 Botón: abrir Pizarra.kt
                        val btnIrPizarra = findViewById<Button>(R.id.btnPizarra)
                        btnIrPizarra.visibility = View.VISIBLE
                        btnIrPizarra.setOnClickListener {
                            startActivity(Intent(this, Pizarra::class.java))
                        }

                    } else {
                        Toast.makeText(this, "Archivo BD dañado o inexistente", Toast.LENGTH_LONG).show()
                    }
                } else {
                    Toast.makeText(this, "Fallo al descargar la base de datos", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun descargarBaseDeDatos(url: String, nombreArchivo: String, callback: (Boolean) -> Unit) {
        Thread {
            try {
                val connection = URL(url).openConnection() as HttpURLConnection
                connection.connect()
                val inputStream = connection.inputStream
                val file = File(filesDir, nombreArchivo)
                val outputStream = FileOutputStream(file)

                inputStream.copyTo(outputStream)
                inputStream.close()
                outputStream.close()
                connection.disconnect()

                callback(true)
            } catch (e: Exception) {
                Log.e("BD_ERROR", "Descarga fallida: ${e.message}")
                callback(false)
            }

        }.start()
    }
}