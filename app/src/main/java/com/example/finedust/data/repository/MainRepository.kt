package com.example.finedust.data.repository

import com.example.finedust.data.response.address.AddressResponse
import com.example.finedust.data.response.air.AirResponse
import com.example.finedust.data.response.kakao.KakaoResponse
import com.example.finedust.data.response.paradidymis.Paradidymis
import com.example.finedust.data.response.search.SearchAddressResponse
import retrofit2.Callback

interface MainRepository {

    fun getAddress(latitude: Double, longitude: Double, callback: Callback<AddressResponse>)
    fun getKakaoLatLon(latitude: Double, longitude: Double, callback: Callback<KakaoResponse>)
    fun getParadidymisItems(xValue: Double, yValue: Double, callback: Callback<Paradidymis>)
    fun getAirConditionerItems(nearbyParadidymis: String, callback: Callback<AirResponse>)
    fun getSearchLocation(query: String, callback: Callback<SearchAddressResponse>)
}