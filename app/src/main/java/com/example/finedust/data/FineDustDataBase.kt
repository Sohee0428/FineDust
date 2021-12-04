package com.example.finedust.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.finedust.data.dao.FinedustDao
import com.example.finedust.data.entity.FinedustEntity

@Database(entities = [FinedustEntity::class], version = 1, exportSchema = false)
abstract class FineDustDataBase : RoomDatabase() {
    abstract fun finedustDao(): FinedustDao

    companion object {
//        싱글톤으로 구현하지 않을경우 호출 부분에서 사용하면 됨?
        private var INSTANCE: FineDustDataBase? = null

        @Synchronized
        fun getInstance(context: Context): FineDustDataBase? {
            if (INSTANCE == null) {
                synchronized(FineDustDataBase::class) {
                    INSTANCE = Room.databaseBuilder(
                        context.applicationContext,
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