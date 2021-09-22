package com.example.finedust.data

import com.google.gson.annotations.SerializedName

data class Body(
    @SerializedName("items")
    val items: List<Items>
)
