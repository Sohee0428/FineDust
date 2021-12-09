package com.example.finedust.data.repository

import com.example.finedust.data.response.address.AddressResponse
import com.example.finedust.data.response.air.AirResponse
import com.example.finedust.data.response.kakao.KakaoResponse
import com.example.finedust.data.response.observatory.Observatory
import com.example.finedust.data.response.search.SearchAddressResponse
import retrofit2.Callback

interface RemoteDataSource {

    fun getAddress(latitude: Double, longitude: Double, callback: Callback<AddressResponse>)
    fun getKakaoItems(latitude: Double, longitude: Double, callback: Callback<KakaoResponse>)
    fun getObservatory(xValue: Double, yValue: Double, callback: Callback<Observatory>)
    fun getAirCondition(nearbyObservatory: String, callback: Callback<AirResponse>)
    fun getSearchLocation(query: String, callback: Callback<SearchAddressResponse>)
}