package com.example.finedust.data.network

import com.example.finedust.data.response.paradidymis.Paradidymis
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface NearbyParadidymisAPI {

    @GET("/B552584/MsrstnInfoInqireSvc/getNearbyMsrstnList?returnType=json")
    fun getParadidymis(
        @Query("tmX") tmX: Double?,
        @Query("tmY") tmY: Double?,
        @Query("serviceKey", encoded = true) key: String
    ): Call<Paradidymis>
}



