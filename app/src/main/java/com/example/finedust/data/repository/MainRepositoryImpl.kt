package com.example.finedust.data.repository

import com.example.finedust.data.response.address.AddressResponse
import com.example.finedust.data.response.air.AirResponse
import com.example.finedust.data.response.kakao.KakaoResponse
import com.example.finedust.data.response.paradidymis.Paradidymis
import com.example.finedust.data.response.search.SearchAddressResponse
import retrofit2.Callback

class MainRepositoryImpl : MainRepository {

    private val localDataSource: LocalDataSource = LocalDataSourceImpl()
    private val remoteDataSource: RemoteDataSource = RemoteDataSourceImpl()

    override fun getAddress(
        latitude: Double,
        longitude: Double,
        callback: Callback<AddressResponse>
    ) {
        remoteDataSource.getAddress(latitude, longitude, callback)
    }

    override fun getKakaoLatLon(
        latitude: Double,
        longitude: Double,
        callback: Callback<KakaoResponse>
    ) {
        remoteDataSource.getKakaoItems(latitude, longitude, callback)
    }

    override fun getParadidymisItems(
        xValue: Double,
        yValue: Double,
        callback: Callback<Paradidymis>
    ) {
        remoteDataSource.getParadidmis(xValue, yValue, callback)
    }

    override fun getAirConditionerItems(
        nearbyParadidymis: String,
        callback: Callback<AirResponse>
    ) {
        remoteDataSource.getAirCondition(nearbyParadidymis, callback)
    }

    override fun getSearchLocation(query: String, callback: Callback<SearchAddressResponse>) {
        remoteDataSource.getSearchLocation(query, callback)
    }
}