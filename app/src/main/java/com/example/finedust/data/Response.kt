package com.example.finedust.data

import com.google.gson.annotations.SerializedName

data class Response(
    @SerializedName("header")
    val header: List<Header>,
    @SerializedName("body")
    val body: List<Body>
)
