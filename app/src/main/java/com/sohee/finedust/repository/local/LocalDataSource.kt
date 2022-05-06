package com.sohee.finedust.repository.local

import com.sohee.finedust.repository.local.entity.FinedustEntity

interface LocalDataSource {

    suspend fun getRecyclerviewList(): List<FinedustEntity>
    suspend fun insertItem(finedustItem: FinedustEntity): Long
    suspend fun deleteAll()
    suspend fun deleteItem(address: String)
}