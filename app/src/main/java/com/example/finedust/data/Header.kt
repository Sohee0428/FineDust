package com.example.finedust.data

import com.google.gson.annotations.SerializedName

data class Header(
    @SerializedName("resultCode")
    val resultCode: String = "00",
    @SerializedName("resultMsg")
    val resultMsg: String = ""
)
