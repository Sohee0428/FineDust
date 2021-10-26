package com.example.finedust.data.network

import com.example.finedust.data.response.air.AirResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface AirConditionerAPI {

    @GET("/B552584/ArpltnInforInqireSvc/getMsrstnAcctoRltmMesureDnsty?&dataTerm=DAILY&pageNo=1&numOfRows=100&returnType=json")
    fun getAirConditioner(
        @Query("stationName") stationName: String?,
        @Query("serviceKey", encoded = true) key: String
    ): Call<AirResponse>
}



