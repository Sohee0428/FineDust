package com.example.finedust.data.dao

import androidx.room.*
import com.example.finedust.data.entity.FinedustEntity

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