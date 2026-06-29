package com.example.consultadeaptos

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.consultadeaptos.DatabaseHelper

class LoginActivity : AppCompatActivity() {

    private lateinit var emailInput: EditText
    private lateinit var passwordInput: EditText
    private lateinit var apartmentSpinner: Spinner
    private lateinit var loginButton: Button
    private lateinit var registerButton: Button
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var prefs: android.content.SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        emailInput = findViewById(R.id.emailInput)
        passwordInput = findViewById(R.id.passwordInput)
        apartmentSpinner = findViewById(R.id.apartmentSpinner)
        loginButton = findViewById(R.id.loginButton)
        registerButton = findViewById(R.id.registerButton)

        dbHelper = DatabaseHelper(this)
        prefs = getSharedPreferences("SesionUsuario", MODE_PRIVATE)

        // Configurar spinner con propietarios desde la BD
        val aptos = dbHelper.getPropietarios()
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, aptos)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        apartmentSpinner.adapter = adapter

        // Recuperar sesión guardada
        val savedEmail = prefs.getString("correo", null)
        val savedApto = prefs.getString("apartamento", null)

        if (savedEmail != null && savedApto != null) {
            emailInput.setText(savedEmail)
            val position = aptos.indexOf(savedApto)
            if (position >= 0) apartmentSpinner.setSelection(position)
        }

        // Acción de login
        loginButton.setOnClickListener {
            val email = emailInput.text.toString()
            val password = passwordInput.text.toString()
            val apartment = apartmentSpinner.selectedItem.toString()

            if (validateLogin(email, password, apartment)) {
                // Guardar sesión
                prefs.edit().apply {
                    putString("correo", email)
                    putString("apartamento", apartment)
                    apply()
                }

                Toast.makeText(this, "Login exitoso", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, MenuConsultaActivity::class.java)
                intent.putExtra("APARTMENT", apartment)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, "Credenciales inválidas", Toast.LENGTH_SHORT).show()
            }
        }

        // Acción de registro
        registerButton.setOnClickListener {
            val email = emailInput.text.toString().trim()
            val password = passwordInput.text.toString().trim()
            val apartment = apartmentSpinner.selectedItem.toString()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Debe ingresar correo y contraseña", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (registerUser(email, password, apartment)) {
                // Guardar sesión automáticamente
                prefs.edit().apply {
                    putString("correo", email)
                    putString("apartamento", apartment)
                    apply()
                }

                Toast.makeText(this, "Registro exitoso", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, MenuConsultaActivity::class.java)
                intent.putExtra("APARTMENT", apartment)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, "Error en registro", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun validateLogin(email: String, password: String, apartment: String): Boolean {
        return dbHelper.validateUser(email, password, apartment)
    }

    private fun registerUser(email: String, password: String, apartment: String): Boolean {
        if (!dbHelper.isPropietarioValido(email, apartment)) {
            return false
        }
        if (dbHelper.isUsuarioRegistrado(apartment)) {
            return false
        }
        return dbHelper.insertUsuario(email, password, apartment)
    }
}
