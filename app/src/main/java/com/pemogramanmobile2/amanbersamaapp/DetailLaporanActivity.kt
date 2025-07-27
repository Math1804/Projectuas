package com.pemogramanmobile2.amanbersamaapp

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DetailLaporanActivity : AppCompatActivity() {

    private lateinit var db: FirebaseDatabase
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_laporan)

        db = FirebaseDatabase.getInstance()
        auth = FirebaseAuth.getInstance()

        // Inisialisasi View yang selalu ada untuk semua pengguna
        // val ivDetailGambar: ImageView = findViewById(R.id.ivDetailGambar) // Dihapus
        val tvDetailDeskripsi: TextView = findViewById(R.id.tvDetailDeskripsi)
        val tvDetailStatus: TextView = findViewById(R.id.tvDetailStatus)
        val tvDetailTimestamp: TextView = findViewById(R.id.tvDetailTimestamp)
        val layoutUpdate: View = findViewById(R.id.layoutUpdateStatus)

        // Mengambil objek Laporan dari Intent
        val laporan = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getSerializableExtra("EXTRA_LAPORAN", Laporan::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getSerializableExtra("EXTRA_LAPORAN") as Laporan?
        }

        if (laporan != null) {
            // Mengisi data yang umum untuk semua pengguna
            tvDetailDeskripsi.text = laporan.deskripsi
            tvDetailStatus.text = laporan.status?.replaceFirstChar { it.uppercase() }
            // Glide.with(this).load(laporan.fotoUrl).into(ivDetailGambar) // Dihapus

            laporan.timestamp?.let { timestamp ->
                val date = Date(timestamp)
                val format = SimpleDateFormat("dd MMMM yyyy, HH:mm", Locale("id", "ID"))
                tvDetailTimestamp.text = "Dilaporkan pada: ${format.format(date)}"
            }

            // Cek role pengguna
            val currentUser = auth.currentUser
            if (currentUser != null) {
                db.getReference("users").child(currentUser.uid).get()
                    .addOnSuccessListener { snapshot ->
                        val userRole = snapshot.child("role").getValue(String::class.java)

                        if (userRole == "keamanan") {
                            layoutUpdate.visibility = View.VISIBLE
                            val rgStatus: RadioGroup = findViewById(R.id.rgStatus)
                            val btnUpdateStatus: Button = findViewById(R.id.btnUpdateStatus)

                            when (laporan.status) {
                                "baru" -> rgStatus.check(R.id.rbBaru)
                                "ditangani" -> rgStatus.check(R.id.rbDitangani)
                                "selesai" -> rgStatus.check(R.id.rbSelesai)
                            }

                            btnUpdateStatus.setOnClickListener {
                                val selectedRadioButtonId = rgStatus.checkedRadioButtonId
                                if (selectedRadioButtonId != -1) {
                                    val selectedRadioButton = findViewById<RadioButton>(selectedRadioButtonId)
                                    val newStatus = selectedRadioButton.text.toString().lowercase()
                                    updateStatusLaporan(laporan.id, newStatus)
                                } else {
                                    Toast.makeText(this, "Pilih status baru", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    }
            }
        }
    }

    private fun updateStatusLaporan(laporanId: String?, newStatus: String) {
        if (laporanId == null) {
            Toast.makeText(this, "ID Laporan tidak ditemukan.", Toast.LENGTH_SHORT).show()
            return
        }

        db.getReference("laporan").child(laporanId).child("status")
            .setValue(newStatus)
            .addOnSuccessListener {
                Toast.makeText(this, "Status berhasil diperbarui!", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Gagal memperbarui status: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}