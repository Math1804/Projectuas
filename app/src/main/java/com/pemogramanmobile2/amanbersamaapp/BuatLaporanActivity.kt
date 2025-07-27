package com.pemogramanmobile2.amanbersamaapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ServerValue

class BuatLaporanActivity : AppCompatActivity() {

    private lateinit var etDeskripsi: EditText
    private lateinit var btnKirim: Button
    private lateinit var progressBar: ProgressBar

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_buat_laporan)

        auth = FirebaseAuth.getInstance()
        db = FirebaseDatabase.getInstance()

        etDeskripsi = findViewById(R.id.etDeskripsiLaporan)
        btnKirim = findViewById(R.id.btnKirimLaporan)
        progressBar = findViewById(R.id.progressBar)

        btnKirim.setOnClickListener {
            kirimLaporan()
        }
    }

    private fun kirimLaporan() {
        val deskripsi = etDeskripsi.text.toString().trim()
        if (deskripsi.isEmpty()) {
            Toast.makeText(this, "Deskripsi tidak boleh kosong", Toast.LENGTH_SHORT).show()
            return
        }

        val currentUser = auth.currentUser
        if (currentUser == null) {
            Toast.makeText(this, "Gagal mendapatkan data pengguna, silakan login ulang.", Toast.LENGTH_LONG).show()
            return
        }

        progressBar.setVisibility(View.VISIBLE);
        btnKirim.isEnabled = false

        val laporan = hashMapOf(
            "pelaporId" to currentUser.uid,
            "deskripsi" to deskripsi,
            "timestamp" to ServerValue.TIMESTAMP,
            "status" to "baru"
        )

        db.getReference("laporan").push()
            .setValue(laporan)
            .addOnSuccessListener {
                progressBar.visibility = View.GONE
                Toast.makeText(this, "Laporan berhasil dikirim!", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener { e ->
                progressBar.visibility = View.GONE
                btnKirim.isEnabled = false
                Toast.makeText(this, "Gagal mengirim laporan: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }
}