package com.example.finedust.data.repository

import com.example.finedust.App
import com.example.finedust.data.FineDustDataBase
import com.example.finedust.data.entity.FinedustEntity

class LocalDataSourceImpl : LocalDataSource {

    private val db = FineDustDataBase.getInstance(App.instance)
    private val finedustDao = db!!.finedustDao()

    override suspend fun getRecyclerviewList(): List<FinedustEntity> {
        return finedustDao.getAll()
    }

    override suspend fun insertItem(finedustItem: FinedustEntity): Long {
        return finedustDao.insert(finedustItem)
    }

    override suspend fun deleteAll() {
        finedustDao.deleteAll()
    }

    override suspend fun deleteItem(address: String) {
        finedustDao.delete(address)
    }
}