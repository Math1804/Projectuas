package com.pemogramanmobile2.amanbersamaapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class RegisterActivity : AppCompatActivity() {

    private lateinit var etNama: EditText
    private lateinit var etBlok: EditText
    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnDaftar: Button
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        auth = FirebaseAuth.getInstance()
        db = FirebaseDatabase.getInstance()

        etNama = findViewById(R.id.etRegisterNama)
        etBlok = findViewById(R.id.etRegisterBlok)
        etEmail = findViewById(R.id.etRegisterEmail)
        etPassword = findViewById(R.id.etRegisterPassword)
        btnDaftar = findViewById(R.id.btnRegister)

        btnDaftar.setOnClickListener {
            val nama = etNama.text.toString().trim()
            val blok = etBlok.text.toString().trim()
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (nama.isEmpty() || blok.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Semua kolom harus diisi!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        val userId = auth.currentUser!!.uid
                        val userMap = hashMapOf(
                            "userId" to userId,
                            "nama" to nama,
                            "nomorBlok" to blok,
                            "role" to "warga"
                        )

                        db.getReference("users").child(userId)
                            .setValue(userMap)
                            .addOnCompleteListener { taskDb ->
                                if (taskDb.isSuccessful) {
                                    Toast.makeText(this, "Pendaftaran berhasil!", Toast.LENGTH_SHORT).show()
                                    startActivity(Intent(this, LoginActivity::class.java))
                                    finish()
                                } else {
                                    Toast.makeText(this, "Gagal menyimpan data: ${taskDb.exception?.message}", Toast.LENGTH_LONG).show()
                                }
                            }
                    } else {
                        Toast.makeText(this, "Pendaftaran Gagal: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                    }
                }
        }
    }
}