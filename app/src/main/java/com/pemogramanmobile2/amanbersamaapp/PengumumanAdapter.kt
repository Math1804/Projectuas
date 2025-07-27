package com.pemogramanmobile2.amanbersamaapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PengumumanAdapter(private val pengumumanList: List<Pengumuman>) : RecyclerView.Adapter<PengumumanAdapter.PengumumanViewHolder>() {

    class PengumumanViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvJudul: TextView = itemView.findViewById(R.id.tvJudulPengumuman)
        val tvIsi: TextView = itemView.findViewById(R.id.tvIsiPengumuman)
        val tvWaktu: TextView = itemView.findViewById(R.id.tvWaktuPengumuman)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PengumumanViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_pengumuman, parent, false)
        return PengumumanViewHolder(view)
    }

    override fun onBindViewHolder(holder: PengumumanViewHolder, position: Int) {
        val pengumuman = pengumumanList[position]
        holder.tvJudul.text = pengumuman.judul
        holder.tvIsi.text = pengumuman.isi
        pengumuman.timestamp?.let {
            val date = Date(it)
            val format = SimpleDateFormat("dd MMMM yyyy", Locale("id", "ID"))
            holder.tvWaktu.text = format.format(date)
        }
    }

    override fun getItemCount() = pengumumanList.size
}