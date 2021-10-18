package com.example.finedust.data.response.paradidymis

data class BodyX(
    val items: List<ItemX>,
    val numOfRows: Int,
    val pageNo: Int,
    val totalCount: Int
)