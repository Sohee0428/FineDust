package com.sohee.finedust.data.dao

import androidx.room.*
import com.sohee.finedust.data.entity.FinedustEntity

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