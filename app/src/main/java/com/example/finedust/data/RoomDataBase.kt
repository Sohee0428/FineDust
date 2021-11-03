package com.example.finedust.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.finedust.data.dao.FinedustDao
import com.example.finedust.data.entity.FinedustEntity

@Database(entities = [FinedustEntity::class], version = 1, exportSchema = false)
abstract class RoomDataBase: RoomDatabase() {
    abstract fun finedustDao(): FinedustDao

    companion object {
        private var INSTANCE : RoomDataBase? = null

        fun getInstance(context: Context): RoomDataBase? {
            if (INSTANCE == null) {
//                여러 쓰레드를 접근 못하게 하기위해
                synchronized(RoomDataBase::class) {
                    INSTANCE = Room.databaseBuilder(context.applicationContext,
                    RoomDataBase::class.java, "contact")
//                            데이터 베이스가 갱신될 때 기존의 테이블을 버리고 새로 사용
                        .fallbackToDestructiveMigration()
                        .build()
                }
            }
            return INSTANCE
        }
    }
}