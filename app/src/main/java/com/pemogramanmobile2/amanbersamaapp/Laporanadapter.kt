package com.pemogramanmobile2.amanbersamaapp

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.FirebaseDatabase

class LaporanAdapter(
    private val laporanList: MutableList<Laporan>,
    private val userRole: String?
) : RecyclerView.Adapter<LaporanAdapter.LaporanViewHolder>() {

    class LaporanViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvDeskripsi: TextView = itemView.findViewById(R.id.tvDeskripsiLaporan)
        val tvStatus: TextView = itemView.findViewById(R.id.tvStatusLaporan)
        val btnHapus: Button = itemView.findViewById(R.id.btnHapusLaporan)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LaporanViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_laporan, parent, false)
        return LaporanViewHolder(view)
    }

    override fun onBindViewHolder(holder: LaporanViewHolder, position: Int) {
        val laporan = laporanList[position]

        holder.tvDeskripsi.text = laporan.deskripsi
        holder.tvStatus.text = laporan.status?.replaceFirstChar { it.uppercase() }

        holder.itemView.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, DetailLaporanActivity::class.java).apply {
                putExtra("EXTRA_LAPORAN", laporan)
            }
            context.startActivity(intent)
        }

        if (userRole == "keamanan") {
            holder.btnHapus.visibility = View.VISIBLE
            holder.btnHapus.setOnClickListener {
                laporan.id?.let { id ->
                    FirebaseDatabase.getInstance().getReference("laporan").child(id).removeValue()
                }
            }
        } else {
            holder.btnHapus.visibility = View.GONE
        }
    }

    override fun getItemCount(): Int {
        return laporanList.size
    }
}