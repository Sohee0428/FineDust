package com.example.finedust.data

import com.google.gson.annotations.SerializedName

data class Item(
    @SerializedName("dataTime")
    val dataTime: String = "",
    @SerializedName("mangName")
    val mangName: String = "",
    @SerializedName("pm10Value")
    val pm10Value: String = "",
    @SerializedName("pm25Value")
    val pm25Value: String = "",
    @SerializedName("khaiValue")
    val khaiValue: String = "",
    @SerializedName("khaiGrade")
    val khaiGrade: String = "",
    @SerializedName("pm10Grade")
    val pm10Grade: String = "",
    @SerializedName("pm10Grade1h")
    val pm10Grade1h: String = "",
    @SerializedName("pm25Grade")
    val pm25Grade: String = "",
    @SerializedName("pm25Grade1h")
    val pm25Grade1h: String = ""
)