package com.pemogramanmobile2.amanbersamaapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class ProfileActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        auth = FirebaseAuth.getInstance()
        db = FirebaseDatabase.getInstance()

        val tvNama: TextView = findViewById(R.id.tvProfileNama)
        val tvEmail: TextView = findViewById(R.id.tvProfileEmail)
        val tvBlok: TextView = findViewById(R.id.tvProfileBlok)
        val btnUbahPassword: Button = findViewById(R.id.btnUbahPassword)
        val btnLogout: Button = findViewById(R.id.btnProfileLogout)

        supportActionBar?.title = "Profil Saya"

        loadProfileData(tvNama, tvEmail, tvBlok)

        btnUbahPassword.setOnClickListener {
            val email = auth.currentUser?.email
            if (email != null) {
                auth.sendPasswordResetEmail(email)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Email untuk ubah password telah dikirim ke $email", Toast.LENGTH_LONG).show()
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "Gagal mengirim email: ${it.message}", Toast.LENGTH_SHORT).show()
                    }
            }
        }

        btnLogout.setOnClickListener {
            auth.signOut()
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }

    private fun loadProfileData(tvNama: TextView, tvEmail: TextView, tvBlok: TextView) {
        val user = auth.currentUser
        if (user != null) {
            tvEmail.text = user.email

            db.getReference("users").child(user.uid).get()
                .addOnSuccessListener { snapshot ->
                    if (snapshot.exists()) {
                        tvNama.text = snapshot.child("nama").getValue(String::class.java)
                        tvBlok.text = snapshot.child("nomorBlok").getValue(String::class.java)
                    }
                }
        }
    }
}