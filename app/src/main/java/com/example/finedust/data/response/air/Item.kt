package com.example.finedust.data.response.air

data class Item(
    val khaiValue: String?,
    val khaiGrade: String?,
    val pm10Value: String?,
    val pm10Value24: String,
    val pm10Grade: String?,
    val pm10Grade1h: String,
    val pm25Value: String?,
    val pm25Value24: String,
    val pm25Grade: String?,
    val pm25Grade1h: String,
    val coGrade: String?,
    val coValue: String?,
    val mangName: String,
    val no2Grade: String?,
    val no2Value: String,
    val o3Grade: String?,
    val o3Value: String?,
    val so2Grade: String?,
    val so2Value: String?,
    val dataTime: String
)
