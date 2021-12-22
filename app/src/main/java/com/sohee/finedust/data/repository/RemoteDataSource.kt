package com.sohee.finedust.data.repository

import com.sohee.finedust.data.response.address.AddressResponse
import com.sohee.finedust.data.response.air.AirResponse
import com.sohee.finedust.data.response.kakao.KakaoResponse
import com.sohee.finedust.data.response.observatory.Observatory
import com.sohee.finedust.data.response.search.SearchAddressResponse
import retrofit2.Callback

interface RemoteDataSource {

    fun getAddress(latitude: Double, longitude: Double, callback: Callback<AddressResponse>)
    fun getKakaoItems(latitude: Double, longitude: Double, callback: Callback<KakaoResponse>)
    fun getObservatory(xValue: Double, yValue: Double, callback: Callback<Observatory>)
    fun getAirCondition(nearbyObservatory: String, callback: Callback<AirResponse>)
    fun getSearchLocation(query: String, callback: Callback<SearchAddressResponse>)
}