package com.sohee.finedust.data.repository

import com.sohee.finedust.data.entity.FinedustEntity
import com.sohee.finedust.data.response.address.AddressResponse
import com.sohee.finedust.data.response.air.AirResponse
import com.sohee.finedust.data.response.kakao.KakaoResponse
import com.sohee.finedust.data.response.observatory.Observatory
import com.sohee.finedust.data.response.search.SearchAddressResponse

interface MainRepository {

    suspend fun getAddress(latitude: Double, longitude: Double): AddressResponse
    suspend fun getKakaoLatLon(latitude: Double, longitude: Double): KakaoResponse
    suspend fun getObservatoryItems(xValue: Double, yValue: Double): Observatory
    suspend fun getAirConditionerItems(nearbyObservatory: String): AirResponse
    suspend fun getSearchLocation(query: String): SearchAddressResponse

    suspend fun getRecyclerviewList(): List<FinedustEntity>
    suspend fun insertItem(finedustItem: FinedustEntity): Long
    suspend fun deleteAll()
    suspend fun deleteItem(address: String)
}