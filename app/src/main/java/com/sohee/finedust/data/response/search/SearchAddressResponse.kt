package com.sohee.finedust.data.response.search

data class SearchAddressResponse(
    val documents: List<Document>,
    val meta: Meta
)