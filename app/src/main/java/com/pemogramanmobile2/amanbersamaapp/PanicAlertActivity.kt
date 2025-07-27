package com.pemogramanmobile2.amanbersamaapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class PanicAlertActivity : AppCompatActivity() {

    private lateinit var db: FirebaseDatabase
    private lateinit var rvAlerts: RecyclerView
    private lateinit var alertAdapter: PanicAlertAdapter
    private val alertList = mutableListOf<PanicAlert>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_panic_alert)

        db = FirebaseDatabase.getInstance()
        rvAlerts = findViewById(R.id.rvPanicAlerts)
        rvAlerts.layoutManager = LinearLayoutManager(this)

        supportActionBar?.title = "Daftar Sinyal Panik"
        loadPanicAlerts()
    }

    private fun loadPanicAlerts() {
        val alertsRef = db.getReference("panic_alerts").orderByChild("timestamp")

        alertsRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                alertList.clear()
                if (snapshot.exists()){
                    for (alertSnapshot in snapshot.children){
                        val alert = alertSnapshot.getValue(PanicAlert::class.java)
                        alert?.let { alertList.add(it) }
                    }
                }
                alertList.reverse()

                alertAdapter = PanicAlertAdapter(alertList)
                rvAlerts.adapter = alertAdapter
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@PanicAlertActivity, "Gagal memuat data: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}