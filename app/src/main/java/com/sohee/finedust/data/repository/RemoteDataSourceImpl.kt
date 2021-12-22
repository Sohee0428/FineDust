package com.sohee.finedust.data.repository

import com.sohee.finedust.data.network.*
import com.sohee.finedust.data.response.address.AddressResponse
import com.sohee.finedust.data.response.air.AirResponse
import com.sohee.finedust.data.response.kakao.KakaoResponse
import com.sohee.finedust.data.response.observatory.Observatory
import com.sohee.finedust.data.response.search.SearchAddressResponse
import retrofit2.Callback

class RemoteDataSourceImpl : RemoteDataSource {

    private val addressService = KakaoAddressCreator.create()
    private val kakaoService = KakaoCreator.create()
    private val nearbyObservatoryService = NearbyObservatoryCreator.create()
    private val airConditionService = AirConditionerCreator.create()
    private val searchAddressService = SearchAddressCreator.create()

    override fun getAddress(
        latitude: Double,
        longitude: Double,
        callback: Callback<AddressResponse>
    ) {
        addressService.getAddress(longitude, latitude).enqueue(callback)
    }

    override fun getKakaoItems(
        latitude: Double,
        longitude: Double,
        callback: Callback<KakaoResponse>
    ) {
        kakaoService.getNavigate(longitude, latitude).enqueue(callback)
    }

    override fun getObservatory(xValue: Double, yValue: Double, callback: Callback<Observatory>) {
        nearbyObservatoryService.getObservatory(
            xValue,
            yValue,
            NearbyObservatoryCreator.SERVICE_KEY
        ).enqueue(callback)
    }

    override fun getAirCondition(nearbyObservatory: String, callback: Callback<AirResponse>) {
        airConditionService.getAirConditioner(
            nearbyObservatory,
            NearbyObservatoryCreator.SERVICE_KEY
        ).enqueue(callback)
    }

    override fun getSearchLocation(query: String, callback: Callback<SearchAddressResponse>) {
        searchAddressService.getLocation(
            query
        ).enqueue(callback)
    }
}