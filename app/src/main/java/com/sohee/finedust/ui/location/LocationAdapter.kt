package com.sohee.finedust.ui.location

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.sohee.finedust.data.DetailAddress
import com.sohee.finedust.databinding.LocationItemBinding

class LocationAdapter(val listener: (DetailAddress) -> Unit) :
   ListAdapter<DetailAddress, LocationAdapter.ViewHolder>(LocationDiffUtil) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            LocationItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItem(getItem(position))
    }

    inner class ViewHolder(private val binding: LocationItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            itemView.setOnClickListener {
                listener(getItem(adapterPosition))
            }
        }

        fun bindItem(item: DetailAddress) {
            binding.item = item
        }
    }
}