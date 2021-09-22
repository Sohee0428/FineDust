package com.example.finedust.data

import com.google.gson.annotations.SerializedName

data class Data(
    @SerializedName("dataTime")
    val dataTime: String = "",
    @SerializedName("mangName")
    val mangName: String = "",
    @SerializedName("pm10Value")
    val pm10Value: String = ""
)