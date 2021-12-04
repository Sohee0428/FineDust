package com.example.finedust.data.repository

import com.example.finedust.data.entity.FinedustEntity

interface LocalDataSource {

    suspend fun getRecyclerviewList(): List<FinedustEntity>
    suspend fun getItem(id: Int): FinedustEntity?
    suspend fun insertItem(finedustItem: FinedustEntity): Long
    suspend fun updateItem(finedustItem: FinedustEntity)
    suspend fun deleteAll()
    suspend fun deleteItem(finedustItem: FinedustEntity)
}