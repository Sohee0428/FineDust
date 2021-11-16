package com.example.finedust.data.repository

import com.example.finedust.data.network.*
import com.example.finedust.data.response.address.AddressResponse
import com.example.finedust.data.response.air.AirResponse
import com.example.finedust.data.response.kakao.KakaoResponse
import com.example.finedust.data.response.paradidymis.Paradidymis
import com.example.finedust.data.response.search.SearchAddressResponse
import retrofit2.Callback

class RemoteDataSourceImpl() : RemoteDataSource {

    private val addressService = KakaoAddressCreator.create()
    private val kakaoService = KakaoCreator.create()
    private val nearbyParadidymisService = NearbyParadidymisCreator.create()
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

    override fun getParadidmis(xValue: Double, yValue: Double, callback: Callback<Paradidymis>) {
        nearbyParadidymisService.getParadidymis(
            xValue,
            yValue,
            NearbyParadidymisCreator.SERVICE_KEY
        ).enqueue(callback)
    }

    override fun getAirCondition(nearbyParadidymis: String, callback: Callback<AirResponse>) {
        airConditionService.getAirConditioner(
            nearbyParadidymis,
            NearbyParadidymisCreator.SERVICE_KEY
        ).enqueue(callback)
    }

    override fun getSearchLocation(query: String, callback: Callback<SearchAddressResponse>) {
        searchAddressService.getLocation(
            query
        ).enqueue(callback)
    }
}