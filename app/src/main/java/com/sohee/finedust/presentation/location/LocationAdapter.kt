package com.sohee.finedust.presentation.location

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sohee.finedust.App
import com.sohee.finedust.data.DetailAddress
import com.sohee.finedust.databinding.LocationItemBinding

class LocationAdapter(val listener: (DetailAddress) -> Unit) :
    RecyclerView.Adapter<LocationAdapter.ViewHolder>() {

    private val detailAddressList = mutableListOf<DetailAddress>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            LocationItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItem(detailAddressList[position])
    }

    override fun getItemCount(): Int {
        return detailAddressList.size
    }

    inner class ViewHolder(private val binding: LocationItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            itemView.setOnClickListener {
                listener(detailAddressList[adapterPosition])
            }
        }

        fun bindItem(item: DetailAddress) {
            binding.titleLocation.text = item.address
        }
    }

    fun addLocationList(list: List<DetailAddress>) {
        detailAddressList.clear()
        detailAddressList.addAll(list)
        notifyDataSetChanged()
    }
}