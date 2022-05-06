package com.sohee.finedust.repository.local

import com.sohee.finedust.data.FineDustDataBase
import com.sohee.finedust.repository.local.entity.FinedustEntity
import com.sohee.finedust.repository.local.LocalDataSource

class LocalDataSourceImpl : LocalDataSource {

    private val db = FineDustDataBase.getInstance()
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