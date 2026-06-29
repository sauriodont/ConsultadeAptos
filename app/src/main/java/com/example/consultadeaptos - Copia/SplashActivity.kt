package com.example.consultadeaptos

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import pl.droidsonroids.gif.GifImageView
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL

class SplashActivity : AppCompatActivity() {

    private val urlBD = "https://drive.google.com/uc?export=download&id=1MOO4w2D-yrQdvR4bkaeohK9lJlIHwCPZ"
    private val nombreBD = "karuai2.sqlite3"
    private val splashDuration: Long = 3000 // 3 segundos

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // Descargar BD en segundo plano
        descargarBaseDeDatos(urlBD, nombreBD) { exito ->
            runOnUiThread {
                if (exito) {
                    Log.i("BD", "Base de datos descargada correctamente")
                } else {
                    Log.e("BD", "Error al descargar la base de datos")
                }

                // Esperar duración del splash y abrir LoginActivity
                Handler(Looper.getMainLooper()).postDelayed({
                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                    finish()
                }, splashDuration)
            }
        }
    }

    private fun descargarBaseDeDatos(url: String, nombreArchivo: String, callback: (Boolean) -> Unit) {
        Thread {
            try {
                val connection = URL(url).openConnection() as HttpURLConnection
                connection.connect()

                if (connection.responseCode != HttpURLConnection.HTTP_OK) {
                    throw Exception("Respuesta HTTP: ${connection.responseCode}")
                }

                val inputStream = connection.inputStream
                val file = File(filesDir, nombreArchivo)
                val outputStream = FileOutputStream(file)

                inputStream.use { input ->
                    outputStream.use { output ->
                        input.copyTo(output)
                    }
                }

                connection.disconnect()
                callback(true)
            } catch (e: Exception) {
                Log.e("BD_ERROR", "Descarga fallida: ${e.message}")
                callback(false)
            }
        }.start()
    }
}
