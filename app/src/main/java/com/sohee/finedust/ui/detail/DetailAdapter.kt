package com.sohee.finedust.ui.detail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.sohee.finedust.data.response.aircondition.DetailDust
import com.sohee.finedust.databinding.DetailItemBinding

class DetailAdapter :
    ListAdapter<DetailDust, DetailAdapter.ViewHolder>(DetailDiffUtil) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            DetailItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItem(getItem(position))
    }

    class ViewHolder(private val binding: DetailItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bindItem(item: DetailDust?) {
            binding.item = item
        }
    }
}