package com.sohee.finedust.data.repository

import com.sohee.finedust.data.entity.FinedustEntity
import com.sohee.finedust.data.response.address.AddressResponse
import com.sohee.finedust.data.response.air.AirResponse
import com.sohee.finedust.data.response.kakao.KakaoResponse
import com.sohee.finedust.data.response.observatory.Observatory
import com.sohee.finedust.data.response.search.SearchAddressResponse
import retrofit2.Callback

interface MainRepository {

    fun getAddress(latitude: Double, longitude: Double, callback: Callback<AddressResponse>)
    fun getKakaoLatLon(latitude: Double, longitude: Double, callback: Callback<KakaoResponse>)
    fun getObservatoryItems(xValue: Double, yValue: Double, callback: Callback<Observatory>)
    fun getAirConditionerItems(nearbyObservatory: String, callback: Callback<AirResponse>)
    fun getSearchLocation(query: String, callback: Callback<SearchAddressResponse>)

    suspend fun getRecyclerviewList(): List<FinedustEntity>
    suspend fun insertItem(finedustItem: FinedustEntity): Long
    suspend fun deleteAll()
    suspend fun deleteItem(address: String)
}