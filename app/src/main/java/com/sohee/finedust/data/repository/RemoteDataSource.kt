package com.sohee.finedust.data.repository

import com.sohee.finedust.data.response.address.AddressResponse
import com.sohee.finedust.data.response.air.AirResponse
import com.sohee.finedust.data.response.kakao.KakaoResponse
import com.sohee.finedust.data.response.observatory.Observatory
import com.sohee.finedust.data.response.search.SearchAddressResponse

interface RemoteDataSource {

    suspend fun getAddress(latitude: Double, longitude: Double): AddressResponse
    suspend fun getKakaoItems(latitude: Double, longitude: Double): KakaoResponse
    suspend fun getObservatory(xValue: Double, yValue: Double): Observatory
    suspend fun getAirCondition(nearbyObservatory: String): AirResponse
    suspend fun getSearchLocation(query: String): SearchAddressResponse
}