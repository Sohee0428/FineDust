package com.example.finedust.presentation.preparation

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.finedust.R

class ViewPagerAdapter(list: ArrayList<Int>) : RecyclerView.Adapter<ViewPagerAdapter.PagerViewHolder>() {
    var item = list
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ) = PagerViewHolder(parent)

    override fun onBindViewHolder(holder: ViewPagerAdapter.PagerViewHolder, position: Int) {
        holder.preparation
    }

    override fun getItemCount(): Int = item.size

    inner class PagerViewHolder(parent: ViewGroup): RecyclerView.ViewHolder
        (LayoutInflater.from(parent.context).inflate(R.layout.preparation_item, parent, false)) {

            val preparation = itemView
    }
}