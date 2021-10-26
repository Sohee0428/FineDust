package com.example.finedust.data.repository

import com.example.finedust.data.response.address.AddressResponse
import com.example.finedust.data.response.air.AirResponse
import com.example.finedust.data.response.kakao.KakaoResponse
import com.example.finedust.data.response.paradidymis.Paradidymis
import retrofit2.Callback

interface RemoteDataSource {

    fun getAddress(latitude: Double, longitude: Double, callback: Callback<AddressResponse>)
    fun getKakaoItems(latitude: Double, longitude: Double, callback: Callback<KakaoResponse>)
    fun getParadidmis(xValue: Double, yValue: Double, callback: Callback<Paradidymis>)
    fun getAirCondition(nearbyParadidymis: String, callback: Callback<AirResponse>)
}