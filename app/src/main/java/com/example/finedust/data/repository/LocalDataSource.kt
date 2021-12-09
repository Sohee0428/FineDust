package com.example.finedust.data.repository

import com.example.finedust.data.entity.FinedustEntity

interface LocalDataSource {

    suspend fun getRecyclerviewList(): List<FinedustEntity>
    suspend fun insertItem(finedustItem: FinedustEntity): Long
    suspend fun deleteAll()
    suspend fun deleteItem(address: String)
}