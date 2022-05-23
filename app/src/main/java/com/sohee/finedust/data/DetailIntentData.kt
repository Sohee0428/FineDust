package com.sohee.finedust.data

import com.sohee.finedust.data.response.aircondition.DetailDust
import java.io.Serializable

data class DetailIntentData(
    val data: ArrayList<DetailDust>,
    val observatory: String,
    val date: String,
    val location: String
) : Serializable
