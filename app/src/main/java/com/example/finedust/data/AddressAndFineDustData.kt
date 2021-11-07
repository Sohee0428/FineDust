package com.example.finedust.data

import com.example.finedust.data.response.address.AddressResponse

data class AddressAndFineDustData(
    val address: String,
    val pm10Value: String,
    val pm10Grade: String,
    val pm25Value: String,
    val pm25Grade: String
)
