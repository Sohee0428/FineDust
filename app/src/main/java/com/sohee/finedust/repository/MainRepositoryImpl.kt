package com.sohee.finedust.repository

import com.sohee.finedust.data.response.address.AddressResponse
import com.sohee.finedust.data.response.aircondition.air.AirResponse
import com.sohee.finedust.data.response.aircondition.observatory.Observatory
import com.sohee.finedust.data.response.kakao.KakaoResponse
import com.sohee.finedust.data.response.search.SearchAddressResponse
import com.sohee.finedust.repository.local.LocalDataSource
import com.sohee.finedust.repository.local.entity.FinedustEntity
import com.sohee.finedust.repository.remote.RemoteDataSource
import javax.inject.Inject

class MainRepositoryImpl @Inject constructor(
    private val localDataSource: LocalDataSource,
    private val remoteDataSource: RemoteDataSource
) : MainRepository {

    override suspend fun getAddress(
        latitude: Double,
        longitude: Double
    ): AddressResponse =
        remoteDataSource.getAddress(latitude, longitude)

    override suspend fun getKakaoLatLon(
        latitude: Double,
        longitude: Double
    ): KakaoResponse =
        remoteDataSource.getKakaoItems(latitude, longitude)

    override suspend fun getObservatoryItems(
        xValue: Double,
        yValue: Double
    ): Observatory =
        remoteDataSource.getObservatory(xValue, yValue)

    override suspend fun getAirConditionerItems(
        nearbyObservatory: String
    ): AirResponse =
        remoteDataSource.getAirCondition(nearbyObservatory)

    override suspend fun getSearchLocation(query: String): SearchAddressResponse =
        remoteDataSource.getSearchLocation(query)

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