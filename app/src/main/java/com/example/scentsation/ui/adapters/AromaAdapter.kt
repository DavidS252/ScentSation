package com.example.scentsation.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.recyclerview.widget.RecyclerView
import com.example.scentsation.R

class AromaAdapter(
    private val aromaList: List<String>,
    private val selectedAromas: MutableList<String>
) : RecyclerView.Adapter<AromaAdapter.AromaViewHolder>() {

    class AromaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val aromaCheckBox: CheckBox = itemView.findViewById(R.id.aromaCheckBox)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AromaViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_aroma, parent, false)
        return AromaViewHolder(view)
    }

    override fun onBindViewHolder(holder: AromaViewHolder, position: Int) {
        val aroma = aromaList[position]
        holder.aromaCheckBox.text = aroma
        holder.aromaCheckBox.isChecked = selectedAromas.contains(aroma)

        holder.aromaCheckBox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                selectedAromas.add(aroma)
            } else {
                selectedAromas.remove(aroma)
            }
        }
    }

    override fun getItemCount() = aromaList.size
}
