package com.example.finedust.data.repository

import com.example.finedust.data.entity.FinedustEntity
import com.example.finedust.data.response.address.AddressResponse
import com.example.finedust.data.response.air.AirResponse
import com.example.finedust.data.response.kakao.KakaoResponse
import com.example.finedust.data.response.observatory.Observatory
import com.example.finedust.data.response.search.SearchAddressResponse
import retrofit2.Callback

interface MainRepository {

    fun getAddress(latitude: Double, longitude: Double, callback: Callback<AddressResponse>)
    fun getKakaoLatLon(latitude: Double, longitude: Double, callback: Callback<KakaoResponse>)
    fun getObservatoryItems(xValue: Double, yValue: Double, callback: Callback<Observatory>)
    fun getAirConditionerItems(nearbyObservatory: String, callback: Callback<AirResponse>)
    fun getSearchLocation(query: String, callback: Callback<SearchAddressResponse>)

    suspend fun getRecyclerviewList(): List<FinedustEntity>
    suspend fun getItem(id: Int): FinedustEntity?
    suspend fun insertItem(finedustItem: FinedustEntity): Long
    suspend fun updateItem(finedustItem: FinedustEntity)
    suspend fun deleteAll()
    suspend fun deleteItem(finedustItem: FinedustEntity)
}