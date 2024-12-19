package com.example.if570_lab_uts_willy_00000079845

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class MyAdapter(private val dataList: List<MyData>) : RecyclerView.Adapter<MyAdapter.MyViewHolder>() {

    // ViewHolder untuk menahan referensi ke tampilan
    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleTextView: TextView = itemView.findViewById(R.id.titleTextView)
    }

    // Inflate layout untuk item di RecyclerView
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_data, parent, false)
        return MyViewHolder(view)
    }

    // Mengikat data ke tampilan di ViewHolder
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = dataList[position]
        holder.titleTextView.text = currentItem.title
    }

    // Jumlah item yang akan ditampilkan
    override fun getItemCount(): Int {
        return dataList.size
    }
}