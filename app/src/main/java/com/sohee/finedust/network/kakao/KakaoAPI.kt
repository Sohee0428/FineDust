package com.sohee.finedust.network.kakao

import com.sohee.finedust.data.response.address.AddressResponse
import com.sohee.finedust.data.response.kakao.KakaoResponse
import com.sohee.finedust.data.response.search.SearchAddressResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface KakaoAPI {

    @GET("/v2/local/geo/transcoord.json?input_coord=WGS84&output_coord=TM")
    suspend fun getNavigate(
        @Query("x") x: Double,
        @Query("y") y: Double
    ): KakaoResponse

    @GET("/v2/local/geo/coord2address.json?input_coord=WGS84")
    suspend fun getAddress(
        @Query("x") x: Double,
        @Query("y") y: Double
    ): AddressResponse

    @GET("/v2/local/search/address.json")
    suspend fun getLocation(
        @Query("query") query: String
    ): SearchAddressResponse
}