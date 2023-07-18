package com.demo.rpi4

import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView

class SettingsAdapter(val settings: Array<String>) :
    RecyclerView.Adapter<SettingsAdapter.SettingsViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SettingsViewHolder {
        val view = View.inflate(parent.context, R.layout.layout_settings_item, null)
        return SettingsViewHolder(view)
    }

    override fun getItemCount(): Int {
        return settings.size
    }

    override fun onBindViewHolder(holder: SettingsViewHolder, position: Int) {
        holder.tvSettingsName.text = settings[position]
    }

    class SettingsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvSettingsName = itemView.findViewById<TextView>(R.id.tvSettingsName)
    }
}