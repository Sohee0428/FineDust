package com.example.finedust.data.repository

import com.example.finedust.data.entity.FinedustEntity
import com.example.finedust.data.response.address.AddressResponse
import com.example.finedust.data.response.air.AirResponse
import com.example.finedust.data.response.kakao.KakaoResponse
import com.example.finedust.data.response.observatory.Observatory
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

    override fun getObservatoryItems(
        xValue: Double,
        yValue: Double,
        callback: Callback<Observatory>
    ) {
        remoteDataSource.getObservatory(xValue, yValue, callback)
    }

    override fun getAirConditionerItems(
        nearbyObservatory: String,
        callback: Callback<AirResponse>
    ) {
        remoteDataSource.getAirCondition(nearbyObservatory, callback)
    }

    override fun getSearchLocation(query: String, callback: Callback<SearchAddressResponse>) {
        remoteDataSource.getSearchLocation(query, callback)
    }

    override suspend fun getRecyclerviewList(): List<FinedustEntity> {
        return localDataSource.getRecyclerviewList()
    }

    override suspend fun insertItem(finedustItem: FinedustEntity): Long {
        return localDataSource.insertItem(finedustItem)
    }

    override suspend fun deleteAll() {
        localDataSource.deleteAll()
    }

    override suspend fun deleteItem(address: String) {
        localDataSource.deleteItem(address)
    }
}