package com.prayerreminder.app.ui.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.prayerreminder.app.databinding.ItemPrayerBinding
import com.prayerreminder.app.ui.home.model.Prayer

class PrayerAdapter(
    private val onToggleChanged: (Prayer, Boolean) -> Unit
) : ListAdapter<Prayer, PrayerAdapter.PrayerViewHolder>(PrayerDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PrayerViewHolder {
        val binding = ItemPrayerBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PrayerViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PrayerViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class PrayerViewHolder(
        private val binding: ItemPrayerBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(prayer: Prayer) {
            binding.apply {
                prayerName.text = prayer.name
                prayerTime.text = prayer.time
                prayerDescription.text = prayer.description
                reminderSwitch.isChecked = prayer.reminderEnabled

                reminderSwitch.setOnCheckedChangeListener { _, isChecked ->
                    onToggleChanged(prayer.copy(reminderEnabled = isChecked), isChecked)
                }
            }
        }
    }

    private class PrayerDiffCallback : DiffUtil.ItemCallback<Prayer>() {
        override fun areItemsTheSame(oldItem: Prayer, newItem: Prayer): Boolean {
            return oldItem.name == newItem.name
        }

        override fun areContentsTheSame(oldItem: Prayer, newItem: Prayer): Boolean {
            return oldItem == newItem
        }
    }
}
