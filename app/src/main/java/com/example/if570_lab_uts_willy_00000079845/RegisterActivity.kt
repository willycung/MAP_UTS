package com.example.if570_lab_uts_willy_00000079845

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class RegisterActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var registerButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        // Inisialisasi FirebaseAuth
        auth = FirebaseAuth.getInstance()

        // Inisialisasi komponen UI
        emailEditText = findViewById(R.id.emailEditText)
        passwordEditText = findViewById(R.id.passwordEditText)
        registerButton = findViewById(R.id.registerButton)

        // Set listener untuk tombol register
        registerButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Email dan password harus diisi", Toast.LENGTH_SHORT).show()
            } else if (password.length < 6) {
                Toast.makeText(this, "Password minimal 6 karakter", Toast.LENGTH_SHORT).show()
            } else {
                registerUser(email, password)
            }
        }
    }

    // Fungsi untuk registrasi pengguna
    private fun registerUser(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Jika registrasi berhasil, arahkan pengguna ke halaman login
                    Toast.makeText(this, "Registrasi berhasil, silakan login", Toast.LENGTH_SHORT).show()

                    // Arahkan ke LoginActivity
                    val intent = Intent(this, LoginActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK) // Hapus aktivitas sebelumnya
                    startActivity(intent)
                    finish() // Tutup RegisterActivity
                } else {
                    // Jika registrasi gagal, tampilkan pesan error
                    Toast.makeText(this, "Registrasi gagal: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }
}