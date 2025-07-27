package com.pemogramanmobile2.amanbersamaapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseDatabase
    private lateinit var tvWelcome: TextView
    private lateinit var btnLogout: Button
    private lateinit var btnProfile: ImageButton
    private lateinit var fabBuatLaporan: FloatingActionButton
    private lateinit var securityButtonsLayout: LinearLayout
    private lateinit var rvLaporan: RecyclerView
    private lateinit var laporanAdapter: LaporanAdapter
    private val laporanList = mutableListOf<Laporan>()
    private lateinit var rvPengumuman: RecyclerView
    private lateinit var pengumumanAdapter: PengumumanAdapter
    private val pengumumanList = mutableListOf<Pengumuman>()
    private var userRole: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initViews()
        initFirebase()

        if (auth.currentUser == null) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        setupListeners()
        loadUserData()
        loadPengumuman()
    }

    private fun initFirebase() {
        auth = FirebaseAuth.getInstance()
        db = FirebaseDatabase.getInstance()
    }

    private fun initViews() {
        tvWelcome = findViewById(R.id.tvWelcome)
        btnLogout = findViewById(R.id.btnLogout)
        btnProfile = findViewById(R.id.btnProfile)
        fabBuatLaporan = findViewById(R.id.fabBuatLaporan)
        securityButtonsLayout = findViewById(R.id.security_buttons_layout)
        rvLaporan = findViewById(R.id.rvLaporan)
        rvLaporan.layoutManager = LinearLayoutManager(this)
        rvPengumuman = findViewById(R.id.rvPengumuman)
        rvPengumuman.layoutManager = LinearLayoutManager(this)
    }

    private fun setupListeners() {
        btnLogout.setOnClickListener {
            auth.signOut()
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
        btnProfile.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }
        fabBuatLaporan.setOnClickListener {
            startActivity(Intent(this, BuatLaporanActivity::class.java))
        }
        val btnLihatPanic: Button = findViewById(R.id.btnLihatPanic)
        val btnBuatPengumuman: Button = findViewById(R.id.btnBuatPengumuman)
        btnLihatPanic.setOnClickListener {
            startActivity(Intent(this, PanicAlertActivity::class.java))
        }
        btnBuatPengumuman.setOnClickListener {
            startActivity(Intent(this, BuatPengumumanActivity::class.java))
        }
    }

    private fun loadUserData() {
        val user = auth.currentUser
        user?.let {
            db.getReference("users").child(it.uid).get()
                .addOnSuccessListener { snapshot ->
                    if (snapshot.exists()) {
                        val nama = snapshot.child("nama").getValue(String::class.java)
                        userRole = snapshot.child("role").getValue(String::class.java)
                        updateUiBasedOnRole(nama)
                        loadLaporan()
                    }
                }
        }
    }

    private fun updateUiBasedOnRole(nama: String?) {
        tvWelcome.text = "Selamat Datang, $nama"
        if (userRole == "keamanan") {
            tvWelcome.text = "Selamat Bertugas, $nama"
            fabBuatLaporan.visibility = View.GONE
            securityButtonsLayout.visibility = View.VISIBLE
        } else {
            fabBuatLaporan.visibility = View.VISIBLE
            securityButtonsLayout.visibility = View.GONE
        }
    }

    private fun loadLaporan() {
        laporanAdapter = LaporanAdapter(laporanList, userRole)
        rvLaporan.adapter = laporanAdapter
        val laporanRef = db.getReference("laporan").orderByChild("timestamp")
        laporanRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                laporanList.clear()
                if (snapshot.exists()) {
                    for (laporanSnapshot in snapshot.children) {
                        val laporan = laporanSnapshot.getValue(Laporan::class.java)
                        laporan?.id = laporanSnapshot.key
                        laporan?.let { laporanList.add(it) }
                    }
                }
                laporanList.reverse()
                laporanAdapter.notifyDataSetChanged()
            }
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@MainActivity, "Gagal memuat laporan", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun loadPengumuman() {
        pengumumanAdapter = PengumumanAdapter(pengumumanList)
        rvPengumuman.adapter = pengumumanAdapter
        val pengumumanRef = db.getReference("pengumuman").orderByChild("timestamp")
        pengumumanRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                pengumumanList.clear()
                if (snapshot.exists()) {
                    for (data in snapshot.children) {
                        val pengumuman = data.getValue(Pengumuman::class.java)
                        pengumuman?.let { pengumumanList.add(it) }
                    }
                }
                pengumumanList.reverse()
                pengumumanAdapter.notifyDataSetChanged()
            }
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@MainActivity, "Gagal memuat pengumuman", Toast.LENGTH_SHORT).show()
            }
        })
    }
}