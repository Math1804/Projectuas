package com.pemogramanmobile2.amanbersamaapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PanicAlertAdapter(private val alertList: List<PanicAlert>) : RecyclerView.Adapter<PanicAlertAdapter.AlertViewHolder>() {

    class AlertViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvNama: TextView = itemView.findViewById(R.id.tvNamaPelaporPanik)
        val tvWaktu: TextView = itemView.findViewById(R.id.tvWaktuPanik)
        val tvLokasi: TextView = itemView.findViewById(R.id.tvLokasiPanik)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlertViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_panic_alert, parent, false)
        return AlertViewHolder(view)
    }

    override fun onBindViewHolder(holder: AlertViewHolder, position: Int) {
        val alert = alertList[position]

        alert.timestamp?.let { timestamp ->
            val date = Date(timestamp)
            val format = SimpleDateFormat("dd MMMM yyyy, HH:mm:ss", Locale("id", "ID"))
            holder.tvWaktu.text = "Waktu: ${format.format(date)}"
        }

        alert.lokasi?.let { lokasiMap ->
            val latitude = lokasiMap["latitude"]
            val longitude = lokasiMap["longitude"]
            holder.tvLokasi.text = "Lokasi: ${"%.4f".format(latitude)}, ${"%.4f".format(longitude)}"
        }

        alert.pelaporId?.let { uid ->
            FirebaseDatabase.getInstance().getReference("users").child(uid).get()
                .addOnSuccessListener { snapshot ->
                    if (snapshot.exists()) {
                        val nama = snapshot.child("nama").getValue(String::class.java) ?: "Nama Tidak Ditemukan"
                        holder.tvNama.text = "Sinyal Panik dari: $nama"
                    } else {
                        holder.tvNama.text = "Sinyal Panik dari: (Pengguna Tidak Ditemukan)"
                    }
                }.addOnFailureListener {
                    holder.tvNama.text = "Sinyal Panik dari: (Gagal Memuat Nama)"
                }
        }
    }

    override fun getItemCount() = alertList.size
}