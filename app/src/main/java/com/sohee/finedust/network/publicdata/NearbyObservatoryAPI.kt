package com.sohee.finedust.network.publicdata

import com.sohee.finedust.data.response.aircondition.observatory.Observatory
import retrofit2.http.GET
import retrofit2.http.Query

interface NearbyObservatoryAPI {

    @GET("/B552584/MsrstnInfoInqireSvc/getNearbyMsrstnList?returnType=json")
    suspend fun getObservatory(
        @Query("tmX") tmX: Double?,
        @Query("tmY") tmY: Double?,
        @Query("serviceKey", encoded = true) key: String
    ): Observatory
}



