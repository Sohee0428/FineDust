package com.sohee.finedust.data.network

import com.sohee.finedust.data.response.observatory.Observatory
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface NearbyObservatoryAPI {

    @GET("/B552584/MsrstnInfoInqireSvc/getNearbyMsrstnList?returnType=json")
    fun getObservatory(
        @Query("tmX") tmX: Double?,
        @Query("tmY") tmY: Double?,
        @Query("serviceKey", encoded = true) key: String
    ): Call<Observatory>
}



