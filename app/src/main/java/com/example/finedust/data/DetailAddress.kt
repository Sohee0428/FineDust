package com.example.finedust.data

import com.example.finedust.data.response.search.Document
import java.io.Serializable

data class DetailAddress(
    val address: String,
    val x: String,
    val y: String,
    val isFavorite: Boolean = false
) :Serializable {
    companion object {
        fun convertDetailAddress(input: List<Document>): List<DetailAddress> {
            val addressList = mutableListOf<DetailAddress>()

            input.forEach {
                addressList.add(DetailAddress(it.address_name, it.x, it.y))
            }
            return addressList
        }
    }
}
