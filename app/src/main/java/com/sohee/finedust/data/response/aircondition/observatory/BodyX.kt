package com.sohee.finedust.data.response.aircondition.observatory

data class BodyX(
    val items: List<ItemX>,
    val numOfRows: Int,
    val pageNo: Int,
    val totalCount: Int
)