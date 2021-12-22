package com.sohee.finedust.presentation.detail

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sohee.finedust.data.DetailDust
import com.sohee.finedust.databinding.DetailItemBinding

class DetailAdapter(val context: Context, private val detailList: ArrayList<DetailDust>) :
    RecyclerView.Adapter<DetailAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            DetailItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItem(detailList[position])
    }

    override fun getItemCount(): Int {
        return detailList.size
    }

    class ViewHolder(private val binding: DetailItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bindItem(item: DetailDust) {
            binding.item = item
            binding.measure.text = item.measure
            binding.dataName.text = item.dustName
            binding.description.text = item.description
            binding.dataValue.text = item.value.first
            binding.dataGrade.text = item.value.second
        }
    }

    fun addDustList(list: List<DetailDust>) {
        detailList.addAll(list)
        notifyDataSetChanged()
    }
}