package com.pemogramanmobile2.amanbersamaapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ServerValue

class BuatPengumumanActivity : AppCompatActivity() {

    private lateinit var etJudul: EditText
    private lateinit var etIsi: EditText
    private lateinit var btnPublikasi: Button
    private lateinit var db: FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_buat_pengumuman)

        db = FirebaseDatabase.getInstance()
        etJudul = findViewById(R.id.etJudulPengumuman)
        etIsi = findViewById(R.id.etIsiPengumuman)
        btnPublikasi = findViewById(R.id.btnPublikasi)

        btnPublikasi.setOnClickListener {
            publikasikan()
        }
    }

    private fun publikasikan() {
        val judul = etJudul.text.toString().trim()
        val isi = etIsi.text.toString().trim()

        if (judul.isEmpty() || isi.isEmpty()) {
            Toast.makeText(this, "Judul dan isi tidak boleh kosong", Toast.LENGTH_SHORT).show()
            return
        }

        val pengumumanRef = db.getReference("pengumuman").push()
        val pengumumanData = hashMapOf(
            "judul" to judul,
            "isi" to isi,
            "timestamp" to ServerValue.TIMESTAMP
        )

        pengumumanRef.setValue(pengumumanData)
            .addOnSuccessListener {
                Toast.makeText(this, "Pengumuman berhasil dipublikasikan!", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Gagal mempublikasikan pengumuman", Toast.LENGTH_SHORT).show()
            }
    }
}