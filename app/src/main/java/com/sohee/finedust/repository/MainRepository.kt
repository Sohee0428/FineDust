package com.sohee.finedust.repository

import com.sohee.finedust.data.response.address.AddressResponse
import com.sohee.finedust.data.response.aircondition.air.AirResponse
import com.sohee.finedust.data.response.aircondition.observatory.Observatory
import com.sohee.finedust.data.response.kakao.KakaoResponse
import com.sohee.finedust.data.response.search.SearchAddressResponse
import com.sohee.finedust.repository.local.entity.FinedustEntity

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