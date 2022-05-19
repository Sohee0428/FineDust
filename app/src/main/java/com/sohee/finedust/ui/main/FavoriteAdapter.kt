package com.sohee.finedust.ui.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.sohee.finedust.repository.local.entity.FinedustEntity
import com.sohee.finedust.databinding.FavoriteItemBinding

class FavoriteAdapter(
    val choiceListener: (FinedustEntity) -> Unit,
    val deleteListener: (FinedustEntity) -> Unit
) : ListAdapter<FinedustEntity, FavoriteAdapter.ViewHolder>(FavoriteDiffUtil) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            FavoriteItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItem(getItem(position))
    }

    inner class ViewHolder(private val binding: FavoriteItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            itemView.setOnClickListener {
                choiceListener(getItem(adapterPosition))
            }

            binding.deleteFavoriteItem.setOnClickListener {
                deleteListener.invoke(getItem(adapterPosition))
            }
        }

        fun bindItem(item: FinedustEntity) {
            binding.item = item
        }
    }
}