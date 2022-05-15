package com.sohee.finedust.ui.detail

import androidx.recyclerview.widget.DiffUtil
import com.sohee.finedust.data.response.aircondition.DetailDust

object DetailDiffUtil  : DiffUtil.ItemCallback<DetailDust>() {

    override fun areItemsTheSame(oldItem: DetailDust, newItem: DetailDust): Boolean {
        return oldItem.dataTime == newItem.dataTime
    }

    override fun areContentsTheSame(oldItem: DetailDust, newItem: DetailDust): Boolean {
        return oldItem == newItem
    }
}