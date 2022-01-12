package com.sohee.finedust.data

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.sohee.finedust.App
import com.sohee.finedust.data.dao.FinedustDao
import com.sohee.finedust.data.entity.FinedustEntity

@Database(entities = [FinedustEntity::class], version = 1, exportSchema = false)
abstract class FineDustDataBase : RoomDatabase() {

    abstract fun finedustDao(): FinedustDao

    companion object {
        private var INSTANCE: FineDustDataBase? = null

        @Synchronized
        fun getInstance(): FineDustDataBase? {
            if (INSTANCE == null) {
                synchronized(FineDustDataBase::class) {
                    INSTANCE = Room.databaseBuilder(
                        App.instance,
                        FineDustDataBase::class.java, "database"
                    )
                        .allowMainThreadQueries()
                        .build()
                }
            }
            return INSTANCE
        }
    }
}