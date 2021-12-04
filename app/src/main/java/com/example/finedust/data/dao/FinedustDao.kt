package com.example.finedust.data.dao

import androidx.room.*
import com.example.finedust.data.entity.FinedustEntity

@Dao
interface FinedustDao {

    @Query("SELECT * FROM FinedustEntity")
    fun getAll(): List<FinedustEntity>

    @Query("SELECT * FROM FinedustEntity WHERE id=:id")
    suspend fun getById(id: Int): FinedustEntity?

    @Insert
    suspend fun insert(toDoEntity: FinedustEntity): Long

    @Delete
    suspend fun delete(toDoEntity: FinedustEntity)

    @Query("DELETE FROM FinedustEntity")
    suspend fun deleteAll()

    @Update
    suspend fun update(toDoEntity: FinedustEntity)
}