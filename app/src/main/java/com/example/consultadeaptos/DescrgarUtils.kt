package com.example.consultadeaptos
import android.content.Context
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL

object DescargaUtils {

    fun descargarBaseDeDatos(context: Context, url: String, nombreArchivo: String, callback: (Boolean) -> Unit) {
        Thread {
            try {
                val connection = URL(url).openConnection() as HttpURLConnection
                connection.connect()

                val inputStream = connection.inputStream
                val file = File(context.filesDir, nombreArchivo)
                val outputStream = FileOutputStream(file)

                inputStream.copyTo(outputStream)
                inputStream.close()
                outputStream.close()
                connection.disconnect()

                callback(true)
            } catch (e: Exception) {
                e.printStackTrace()
                callback(false)
            }
        }.start()
    }
}