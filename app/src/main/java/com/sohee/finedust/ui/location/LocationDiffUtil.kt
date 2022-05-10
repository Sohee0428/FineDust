package com.sohee.finedust.ui.location

import androidx.recyclerview.widget.DiffUtil
import com.sohee.finedust.data.DetailAddress

object LocationDiffUtil  : DiffUtil.ItemCallback<DetailAddress>() {

    override fun areItemsTheSame(oldItem: DetailAddress, newItem: DetailAddress): Boolean {
        return oldItem.address == newItem.address
    }

    override fun areContentsTheSame(oldItem: DetailAddress, newItem: DetailAddress): Boolean {
        return oldItem == newItem
    }
}