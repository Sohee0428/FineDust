package com.sohee.finedust.ui.main

import androidx.recyclerview.widget.DiffUtil
import com.sohee.finedust.repository.local.entity.FinedustEntity

object FavoriteDiffUtil  : DiffUtil.ItemCallback<FinedustEntity>() {

    override fun areItemsTheSame(oldItem: FinedustEntity, newItem: FinedustEntity): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: FinedustEntity, newItem: FinedustEntity): Boolean {
        return oldItem == newItem
    }
}