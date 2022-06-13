package com.sohee.finedust.repository.remote

import com.sohee.finedust.data.response.address.AddressResponse
import com.sohee.finedust.data.response.aircondition.air.AirResponse
import com.sohee.finedust.data.response.aircondition.observatory.Observatory
import com.sohee.finedust.data.response.kakao.KakaoResponse
import com.sohee.finedust.data.response.search.SearchAddressResponse
import com.sohee.finedust.network.kakao.KakaoAPI
import com.sohee.finedust.network.publicdata.AirConditionerAPI
import com.sohee.finedust.network.publicdata.NearbyObservatoryAPI
import com.sohee.finedust.network.publicdata.NearbyObservatoryCreator
import javax.inject.Inject

class RemoteDataSourceImpl @Inject constructor(
    private val airConditionService: AirConditionerAPI,
    private val nearbyObservatoryService: NearbyObservatoryAPI,
    private val kakaoService: KakaoAPI
) : RemoteDataSource {

    override suspend fun getAddress(
        latitude: Double,
        longitude: Double
    ): AddressResponse =
        kakaoService.getAddress(longitude, latitude)

    override suspend fun getKakaoItems(
        latitude: Double,
        longitude: Double
    ): KakaoResponse =
        kakaoService.getNavigate(longitude, latitude)

    override suspend fun getObservatory(xValue: Double, yValue: Double): Observatory =
        nearbyObservatoryService.getObservatory(
            xValue,
            yValue,
            NearbyObservatoryCreator.SERVICE_KEY
        )

    override suspend fun getAirCondition(nearbyObservatory: String): AirResponse =
        airConditionService.getAirConditioner(
            nearbyObservatory,
            NearbyObservatoryCreator.SERVICE_KEY
        )

    override suspend fun getSearchLocation(query: String): SearchAddressResponse =
        kakaoService.getLocation(
            query
        )
}