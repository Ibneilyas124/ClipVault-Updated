package com.sarfrazqureshi.clipvault.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sarfrazqureshi.clipvault.databinding.ItemClipBinding
import com.sarfrazqureshi.clipvault.db.ClipItem
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ClipAdapter(
    private var items: List<ClipItem> = emptyList(),
    private val onClick: (ClipItem) -> Unit,
    private val onLongClick: (ClipItem) -> Unit
) : RecyclerView.Adapter<ClipAdapter.ClipViewHolder>() {

    private val dateFormat = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault())

    inner class ClipViewHolder(val binding: ItemClipBinding) : RecyclerView.ViewHolder(binding.root)

    fun submitList(newItems: List<ClipItem>) {
        items = newItems
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClipViewHolder {
        val binding = ItemClipBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ClipViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ClipViewHolder, position: Int) {
        val item = items[position]
        holder.binding.textContent.text = if (item.isPinned) "\uD83D\uDCCC " + item.content else item.content
        holder.binding.textTimestamp.text = dateFormat.format(Date(item.timestamp))
        holder.itemView.setOnClickListener { onClick(item) }
        holder.itemView.setOnLongClickListener {
            onLongClick(item)
            true
        }
    }

    override fun getItemCount(): Int = items.size
}
