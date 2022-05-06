package com.sohee.finedust.network.publicdata

import com.sohee.finedust.data.response.aircondition.air.AirResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface AirConditionerAPI {

    @GET("/B552584/ArpltnInforInqireSvc/getMsrstnAcctoRltmMesureDnsty?&dataTerm=DAILY&pageNo=1&numOfRows=100&returnType=json&ver=1.3")
    suspend fun getAirConditioner(
        @Query("stationName") stationName: String?,
        @Query("serviceKey", encoded = true) key: String
    ): AirResponse
}