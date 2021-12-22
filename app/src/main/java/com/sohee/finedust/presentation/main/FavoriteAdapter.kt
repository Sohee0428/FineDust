package com.sohee.finedust.presentation.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sohee.finedust.data.entity.FinedustEntity
import com.sohee.finedust.databinding.FavoriteItemBinding

class FavoriteAdapter(
    val choiceListener: (FinedustEntity) -> Unit,
    val deleteListener: (FinedustEntity) -> Unit
) : RecyclerView.Adapter<FavoriteAdapter.ViewHolder>() {

    private val favoriteList = mutableListOf<FinedustEntity>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            FavoriteItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItem(favoriteList[position])
    }

    override fun getItemCount(): Int {
        return favoriteList.size
    }

    inner class ViewHolder(private val binding: FavoriteItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            itemView.setOnClickListener {
                choiceListener(favoriteList[adapterPosition])
            }

            binding.deleteFavoriteItem.setOnClickListener {
                deleteListener.invoke(favoriteList[adapterPosition])
            }
        }

        fun bindItem(item: FinedustEntity) {
            binding.favoriteName.text = item.address
        }
    }

    fun addList(list: List<FinedustEntity>) {
        favoriteList.clear()
        favoriteList.addAll(list)
        notifyDataSetChanged()
    }
}