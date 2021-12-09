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

    override suspend fun getItem(id: Int): FinedustEntity? {
        return finedustDao.getById(id)
    }

    override suspend fun insertItem(finedustItem: FinedustEntity): Long {
        return finedustDao.insert(finedustItem)
    }

    override suspend fun updateItem(finedustItem: FinedustEntity) {
        finedustDao.update(finedustItem)
    }

    override suspend fun deleteAll() {
        finedustDao.deleteAll()
    }

    override suspend fun deleteItem(finedustItem: FinedustEntity) {
        finedustDao.delete(finedustItem)
    }
}