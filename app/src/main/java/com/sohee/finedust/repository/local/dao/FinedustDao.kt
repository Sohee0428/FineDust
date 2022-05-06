package com.sohee.finedust.repository.local.dao

import androidx.room.*
import com.sohee.finedust.repository.local.entity.FinedustEntity

@Dao
interface FinedustDao {

    @Query("SELECT * FROM FinedustEntity")
    fun getAll(): List<FinedustEntity>

    @Insert
    suspend fun insert(toDoEntity: FinedustEntity): Long

    @Query("DELETE FROM FinedustEntity WHERE address=:address")
    suspend fun delete(address: String)

    @Query("DELETE FROM FinedustEntity")
    suspend fun deleteAll()
}