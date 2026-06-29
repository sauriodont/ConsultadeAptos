package com.example.consultadeaptos

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.consultadeaptos.models.*
import java.io.File

class DatabaseHelper(context: Context) : SQLiteOpenHelper(
    context, File(context.filesDir, "karuai2.sqlite3").path, null, 1
) {
    override fun onCreate(db: SQLiteDatabase?) {
        // No se usa porque la BD viene preconstruida
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        // No se usa en esta implementación
    }

    fun getPropietarios(): List<String> {
        val aptos = mutableListOf<String>()
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT apto FROM propietarios ORDER BY apto", null)

        while (cursor.moveToNext()) {
            aptos.add(cursor.getString(0))
        }

        cursor.close()
        db.close()
        return aptos
    }

    fun getDatosPropietario(apto: String): Propietario? {
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT nombres FROM propietarios WHERE apto = ?",
            arrayOf(apto)
        )
        val propietario = if (cursor.moveToFirst()) Propietario(cursor.getString(0)) else null
        cursor.close()
        db.close()
        return propietario
    }

    fun getDatosInquilino(apto: String): Inquilino? {
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT nombres FROM inquilinos WHERE apto = ?",
            arrayOf(apto)
        )
        val inquilino = if (cursor.moveToFirst()) Inquilino(cursor.getString(0)) else null
        cursor.close()
        db.close()
        return inquilino
    }

    fun getDeudas(apto: String): Pair<List<Porcobrar>, Double> {
        val deudas = mutableListOf<Porcobrar>()
        var totalBs = 0.0
        var totalUsd = 0.0

        val db = readableDatabase
        val cursor = db.rawQuery("""
            SELECT 
                SUM(CAST(REPLACE(monto, ',', '.') AS REAL)) as monto,
                SUM(CAST(REPLACE(dolares, ',', '.') AS REAL)) as dolares
            FROM xcobrar
            WHERE apto = ?
        """.trimIndent(), arrayOf(apto))

        if (cursor.moveToFirst()) {
            totalBs = cursor.getDouble(0)
            totalUsd = cursor.getDouble(1)
            deudas.add(Porcobrar(totalBs, totalUsd))
        }

        cursor.close()
        db.close()
        return Pair(deudas, totalBs)
    }

    fun getAbonos(apto: String): Abono {
        var bs = 0.0
        var usd = 0.0

        val db = readableDatabase
        val cursor = db.rawQuery("""
            SELECT 
                CAST(REPLACE(monto, ',', '.') AS REAL) as monto,
                CAST(REPLACE(dolares, ',', '.') AS REAL) as dolares
            FROM abonos
            WHERE apto = ?
        """.trimIndent(), arrayOf(apto))

        while (cursor.moveToNext()) {
            bs += cursor.getDouble(0)
            usd += cursor.getDouble(1)
        }

        cursor.close()
        db.close()
        return Abono(bs, usd)
    }
    fun getMesesPendientes(apto: String): List<List<String>> {
        val db = readableDatabase
        val cursor = db.rawQuery(
            """
        SELECT mes, REPLACE(monto, ',', '.'), REPLACE(dolares, ',', '.'), nromov
        FROM xcobrar
        WHERE apto = ?
        ORDER BY nromov ASC
        """.trimIndent(),
            arrayOf(apto)
        )

        val resultado = mutableListOf<List<String>>()
        while (cursor.moveToNext()) {
            val mes = cursor.getString(0)
            val monto = cursor.getString(1)
            val dolares = cursor.getString(2)
            resultado.add(listOf(mes, monto, dolares))
        }

        cursor.close()
        db.close()
        return resultado
    }
    fun getHistorial(apto: String): List<HistorialItem> {
        val historialList = mutableListOf<HistorialItem>()
        val db = this.readableDatabase

        val query = """
        SELECT fecha, mes, monto, dolares, tasa, referencia, formapago, fechaoperacion, ref 
        FROM historial 
        WHERE apto = ? 
        ORDER BY ref DESC
    """.trimIndent()

        val cursor = db.rawQuery(query, arrayOf(apto))

        if (cursor.moveToFirst()) {
            do {
                val item = HistorialItem(
                    fecha = cursor.getString(cursor.getColumnIndexOrThrow("fecha")),
                    mes = cursor.getString(cursor.getColumnIndexOrThrow("mes")),
                    monto = cursor.getDouble(cursor.getColumnIndexOrThrow("monto")),
                    dolares = cursor.getDouble(cursor.getColumnIndexOrThrow("dolares")),
                    tasa = cursor.getDouble(cursor.getColumnIndexOrThrow("tasa")),
                    referencia = cursor.getString(cursor.getColumnIndexOrThrow("referencia")),
                    formapago = cursor.getString(cursor.getColumnIndexOrThrow("formapago")),
                    fechaOperacion = cursor.getString(cursor.getColumnIndexOrThrow("fechaoperacion"))
                )
                historialList.add(item)
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()
        return historialList
    }

    fun getPizarra(): List<PizarraItem> {
        val lista = mutableListOf<PizarraItem>()
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT apto, cuenta, REPLACE(bolivares, ',', '.'), REPLACE(dolares, ',', '.') FROM pizarra",
            null
        )

        while (cursor.moveToNext()) {
            val apto = cursor.getString(0)
            val cuenta = cursor.getString(1)?.toIntOrNull() ?: 0
            val bolivares = cursor.getString(2)?.toDoubleOrNull() ?: 0.0
            val dolares = cursor.getString(3)?.toDoubleOrNull() ?: 0.0

            lista.add(PizarraItem(apto, cuenta, bolivares, dolares))
        }

        cursor.close()
        db.close()
        return lista
    }
    fun getTotalDolares(): Double {
        var total = 0.0
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT SUM(CAST(REPLACE(dolares, ',', '.') AS REAL)) FROM xcobrar",
            null
        )

        if (cursor.moveToFirst()) {
            total = cursor.getDouble(0)
        }

        cursor.close()
        db.close()
        return total
    }
    fun isPropietarioValido(correo: String, apto: String): Boolean {
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT correo FROM propietarios WHERE apto = ? AND correo = ?",
            arrayOf(apto, correo)
        )
        val valido = cursor.moveToFirst()
        cursor.close()
        db.close()
        return valido
    }

    fun isUsuarioRegistrado(apto: String): Boolean {
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT apartamento FROM usuarios WHERE apartamento = ?",
            arrayOf(apto)
        )
        val registrado = cursor.moveToFirst()
        cursor.close()
        db.close()
        return registrado
    }

    fun insertUsuario(correo: String, contrasena: String, apto: String): Boolean {
        return try {
            val db = writableDatabase
            db.execSQL(
                "INSERT INTO usuarios (correo, contrasena, apartamento) VALUES (?, ?, ?)",
                arrayOf(correo, contrasena, apto)
            )
            db.close()
            true
        } catch (e: Exception) {
            false
        }
    }

    fun validateUser(correo: String, contrasena: String, apto: String): Boolean {
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT correo FROM usuarios WHERE correo = ? AND contrasena = ? AND apartamento = ?",
            arrayOf(correo, contrasena, apto)
        )
        val valido = cursor.moveToFirst()
        cursor.close()
        db.close()
        return valido
    }


}