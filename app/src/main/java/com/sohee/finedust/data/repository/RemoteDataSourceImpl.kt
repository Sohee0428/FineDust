package com.sohee.finedust.data.repository

import com.sohee.finedust.data.network.*
import com.sohee.finedust.data.response.address.AddressResponse
import com.sohee.finedust.data.response.air.AirResponse
import com.sohee.finedust.data.response.kakao.KakaoResponse
import com.sohee.finedust.data.response.observatory.Observatory
import com.sohee.finedust.data.response.search.SearchAddressResponse

class RemoteDataSourceImpl : RemoteDataSource {

    private val addressService = KakaoAddressCreator.create()
    private val kakaoService = KakaoCreator.create()
    private val nearbyObservatoryService = NearbyObservatoryCreator.create()
    private val airConditionService = AirConditionerCreator.create()
    private val searchAddressService = SearchAddressCreator.create()

    override suspend fun getAddress(
        latitude: Double,
        longitude: Double
    ): AddressResponse =
        addressService.getAddress(longitude, latitude)


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
        searchAddressService.getLocation(
            query
        )

}