package com.sohee.finedust.data.network

import com.sohee.finedust.data.response.air.AirResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface AirConditionerAPI {

    @GET("/B552584/ArpltnInforInqireSvc/getMsrstnAcctoRltmMesureDnsty?&dataTerm=DAILY&pageNo=1&numOfRows=100&returnType=json&ver=1.3")
    fun getAirConditioner(
        @Query("stationName") stationName: String?,
        @Query("serviceKey", encoded = true) key: String
    ): Call<AirResponse>
}



