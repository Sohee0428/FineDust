package com.sohee.finedust.data.response.search

data class Meta(
    val is_end: Boolean,
    val pageable_count: Int,
    val total_count: Int
)