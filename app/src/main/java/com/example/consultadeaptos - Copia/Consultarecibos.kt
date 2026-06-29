package com.example.consultadeaptos

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import kotlinx.coroutines.*
import okhttp3.ResponseBody
import java.io.File
import java.io.FileOutputStream

class Consultarecibos : AppCompatActivity() {

    private lateinit var spinnerYear: Spinner
    private lateinit var spinnerMonth: Spinner
    private lateinit var btnDescargar: Button
    private lateinit var prefs: android.content.SharedPreferences

    private var yearFolders: List<DriveFile> = emptyList()
    private var monthFolders: List<DriveFile> = emptyList()
    private var selectedYearId: String? = null
    private var selectedMonthId: String? = null

    private val api = RetrofitClient.api
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_consultarecibos)

        spinnerYear = findViewById(R.id.spinnerYear)
        spinnerMonth = findViewById(R.id.spinnerMonth)
        btnDescargar = findViewById(R.id.btnDescargar)

        prefs = getSharedPreferences("SesionUsuario", MODE_PRIVATE)
        val apartment = prefs.getString("apartamento", null) ?: return

        cargarAnios()

        spinnerYear.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: android.view.View?, position: Int, id: Long) {
                selectedYearId = yearFolders.getOrNull(position)?.id
                selectedYearId?.let { cargarMeses(it) }
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        spinnerMonth.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: android.view.View?, position: Int, id: Long) {
                selectedMonthId = monthFolders.getOrNull(position)?.id
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        btnDescargar.setOnClickListener {
            if (selectedMonthId != null) {
                descargarRecibo(selectedMonthId!!, apartment)
            } else {
                Toast.makeText(this, "Seleccione año y mes", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun cargarAnios() {
        scope.launch {
            try {
                val result = api.getAnos()
                yearFolders = result
                withContext(Dispatchers.Main) {
                    val adapter = ArrayAdapter(this@Consultarecibos, android.R.layout.simple_spinner_item, yearFolders.map { it.name })
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    spinnerYear.adapter = adapter
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@Consultarecibos, "Error al cargar años: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun cargarMeses(yearId: String) {
        scope.launch {
            try {
                val result = api.getMeses(yearId)
                monthFolders = result
                withContext(Dispatchers.Main) {
                    val adapter = ArrayAdapter(this@Consultarecibos, android.R.layout.simple_spinner_item, monthFolders.map { it.name })
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    spinnerMonth.adapter = adapter
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@Consultarecibos, "Error al cargar meses: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun descargarRecibo(monthId: String, apartment: String) {
        scope.launch {
            try {
                val response = api.downloadRecibo(monthId, apartment)
                if (response.isSuccessful) {
                    val body: ResponseBody? = response.body()
                    if (body != null) {
                        val outputFile = File(filesDir, "$apartment.pdf")
                        FileOutputStream(outputFile).use { fos ->
                            fos.write(body.bytes())
                        }
                        withContext(Dispatchers.Main) { abrirPDF(outputFile) }
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@Consultarecibos, "Recibo no encontrado", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@Consultarecibos, "Error al descargar recibo: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun abrirPDF(file: File) {
        val uri: Uri = FileProvider.getUriForFile(this, "${packageName}.provider", file)
        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, "application/pdf")
            flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        }
        startActivity(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        scope.cancel() // ✅ Cancela coroutines ao destruir a Activity
    }
}
