package com.sohee.finedust.data.response.air

data class Item(
    val khaiValue: String? = "01",
    val khaiGrade: String?,
    val pm10Value: String? = "01",
    val pm10Value24: String = "01",
    val pm10Grade: String?,
    val pm10Grade1h: String,
    val pm25Value: String? = "01",
    val pm25Value24: String = "01",
    val pm25Grade: String?,
    val pm25Grade1h: String,
    val coValue: String? = "01",
    val coGrade: String?,
    val no2Value: String = "01",
    val no2Grade: String?,
    val o3Value: String? = "01",
    val o3Grade: String?,
    val so2Value: String? = "01",
    val so2Grade: String?,
    val mangName: String,
    val dataTime: String
)
