package com.example.finedust.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity
data class FinedustEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val address: String,
    val x: String,
    val y: String
) : Serializable
