package com.example.finedust.presentation.main

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.finedust.data.repository.MainRepository
import com.example.finedust.data.repository.MainRepositoryImpl
import com.example.finedust.data.response.address.Address
import com.example.finedust.data.response.address.AddressResponse
import com.example.finedust.data.response.air.AirResponse
import com.example.finedust.data.response.air.Item
import com.example.finedust.data.response.kakao.KakaoResponse
import com.example.finedust.data.response.paradidymis.Paradidymis
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainViewModel() : ViewModel() {

    private val repository: MainRepository = MainRepositoryImpl()

    private val _airConditionerItems: MutableLiveData<Item> = MutableLiveData()
    val airConditionerItems: LiveData<Item>
        get() = _airConditionerItems

    private val _address: MutableLiveData<Address> = MutableLiveData()
    val address: LiveData<Address>
        get() = _address

    fun navigate(latitude: Double, longitude: Double) {

        val callback = object : Callback<KakaoResponse> {
            override fun onResponse(call: Call<KakaoResponse>, response: Response<KakaoResponse>) {
                val xValue = response.body()!!.documents[0].x
                val yValue = response.body()!!.documents[0].y

                Log.d("tm 주소", "$xValue, $yValue")
                nearbyParadidymis(xValue, yValue)
            }

            override fun onFailure(call: Call<KakaoResponse>, t: Throwable) {
            }
        }
        repository.getKakaoLatLon(latitude, longitude, callback)
    }

    fun address(latitude: Double, longitude: Double) {

        val callback = object : Callback<AddressResponse> {
            override fun onResponse(
                call: Call<AddressResponse>,
                response: Response<AddressResponse>
            ) {
                _address.value = response.body()!!.documents[0].address
            }
            override fun onFailure(call: Call<AddressResponse>, t: Throwable) {
            }
        }
        repository.getAddress(latitude, longitude, callback)
    }

    fun nearbyParadidymis(xValue: Double, yValue: Double) {

        val callback = object : Callback<Paradidymis> {
            override fun onResponse(call: Call<Paradidymis>, response: Response<Paradidymis>) {
                val stationName = response.body()!!.response.body.items[0].stationName
                Log.d("관측소", stationName)
                airConditioner(stationName)
            }
            override fun onFailure(call: Call<Paradidymis>, t: Throwable) {
            }
        }
        repository.getParadidymisItems(xValue, yValue, callback)
    }

    fun airConditioner(nearbyParadidymis: String) {

        val callback = object : Callback<AirResponse> {
            override fun onResponse(call: Call<AirResponse>, response: Response<AirResponse>) {
                _airConditionerItems.value = response.body()!!.response.body.items[0]
            }
            override fun onFailure(call: Call<AirResponse>, t: Throwable) {
            }
        }
        repository.getAirConditionerItems(nearbyParadidymis, callback)
    }
}