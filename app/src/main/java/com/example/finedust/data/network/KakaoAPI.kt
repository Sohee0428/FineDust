package com.example.finedust.data.network

import com.example.finedust.data.response.address.AddressResponse
import com.example.finedust.data.response.kakao.KakaoResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface KakaoAPI {

    @GET("/v2/local/geo/transcoord.json?input_coord=WGS84&output_coord=TM")
    fun getNavigate(
        @Query("x") x: Double,
        @Query("y") y: Double
    ): Call<KakaoResponse>
}

interface KakaoAddressAPI {

    @GET("/v2/local/geo/coord2address.json?")
    fun getNavigate(
        @Query("x") x: Double,
        @Query("y") y: Double
    ): Call<AddressResponse>
}



