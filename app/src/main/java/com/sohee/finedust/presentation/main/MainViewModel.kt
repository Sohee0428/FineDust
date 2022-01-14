package com.sohee.finedust.presentation.main

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.sohee.finedust.data.DetailAddress
import com.sohee.finedust.data.DetailDust
import com.sohee.finedust.data.entity.FinedustEntity
import com.sohee.finedust.data.repository.MainRepository
import com.sohee.finedust.data.repository.MainRepositoryImpl
import com.sohee.finedust.data.response.address.Address
import com.sohee.finedust.data.response.address.AddressResponse
import com.sohee.finedust.data.response.address.RoadAddress
import com.sohee.finedust.data.response.air.AirResponse
import com.sohee.finedust.data.response.air.Item
import com.sohee.finedust.data.response.kakao.KakaoResponse
import com.sohee.finedust.data.response.observatory.Observatory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainViewModel : ViewModel() {

    lateinit var mainAddress: DetailAddress
    lateinit var detailObservatory: String
    lateinit var detailDate: String
    private val repository: MainRepository = MainRepositoryImpl()
    val detailDustList = arrayListOf<DetailDust>()

    private val _favoriteList: MutableLiveData<List<FinedustEntity>> = MutableLiveData()
    val favoriteList: LiveData<List<FinedustEntity>>
        get() = _favoriteList

    private val _preAddress: MutableLiveData<Address> = MutableLiveData()
    val preAddress: LiveData<Address>
        get() = _preAddress

    private val _newAddress: MutableLiveData<RoadAddress> = MutableLiveData()
    val newAddress: LiveData<RoadAddress>
        get() = _newAddress

    private val _addressNull: MutableLiveData<Unit> = MutableLiveData()
    val addressNull: LiveData<Unit>
        get() = _addressNull

    private val _airConditionerItems: MutableLiveData<Item> = MutableLiveData()
    val airConditionerItems: LiveData<Item>
        get() = _airConditionerItems

    private val _airConditionerItemsNull: MutableLiveData<Unit> = MutableLiveData()
    val airConditionerItemsNull: LiveData<Unit>
        get() = _airConditionerItemsNull

    private val _observatoryError: MutableLiveData<Unit> = MutableLiveData()
    val observatoryError: LiveData<Unit>
        get() = _observatoryError

    fun navigate(latitude: Double, longitude: Double) {
        val callback = object : Callback<KakaoResponse> {
            override fun onResponse(call: Call<KakaoResponse>, response: Response<KakaoResponse>) {
                val xValue = response.body()!!.documents[0].x
                val yValue = response.body()!!.documents[0].y

                Log.d("TMAddressResponse", "$xValue, $yValue")
                nearbyObservatory(xValue, yValue)
            }

            override fun onFailure(call: Call<KakaoResponse>, t: Throwable) {
                Log.e("TMAddressResponse", "에러 발생")
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
                Log.d(
                    "주소", "신주소 : ${response.body()!!.documents[0].road_address}," +
                            "구주소 : ${response.body()!!.documents[0].address}"
                )
                when {
                    response.body()!!.documents[0].road_address != null -> {
                        _newAddress.value = response.body()!!.documents[0].road_address
                    }
                    response.body()!!.documents[0].address != null -> {
                        _preAddress.value = response.body()!!.documents[0].address
                    }
                    else -> {
                        _addressNull.value = Unit
                    }
                }
            }

            override fun onFailure(call: Call<AddressResponse>, t: Throwable) {
                Log.e("addressResponse", "에러 발생")
            }
        }
        repository.getAddress(latitude, longitude, callback)
    }

    fun nearbyObservatory(xValue: Double, yValue: Double) {
        val callback = object : Callback<Observatory> {
            override fun onResponse(call: Call<Observatory>, response: Response<Observatory>) {
                val stationName = response.body()!!.response.body.items[0].stationName
                if (stationName != null) {
                    Log.d("obsercatoryReponse", stationName)
                    detailObservatory = stationName
                    airConditioner(stationName)
                } else {
                    _airConditionerItemsNull.value = Unit
                }
            }

            override fun onFailure(call: Call<Observatory>, t: Throwable) {
                Log.e("observatoryReponse", "에러 발생 ")
                _observatoryError.value = Unit
            }
        }
        repository.getObservatoryItems(xValue, yValue, callback)
    }

    fun airConditioner(nearbyObservatory: String) {
        val callback = object : Callback<AirResponse> {
            override fun onResponse(call: Call<AirResponse>, response: Response<AirResponse>) {
                if (response.body()!!.response.body.items[0] != null) {
                    Log.d("airConditioner", "${response.body()!!.response.body.items[0]}")
                    _airConditionerItems.value = response.body()!!.response.body.items[0]
                    detailDate = response.body()!!.response.body.items[0].dataTime
                    Log.d("detailDate1", detailDate)
                } else {
                    _airConditionerItemsNull.value = Unit
                }
            }

            override fun onFailure(call: Call<AirResponse>, t: Throwable) {
                Log.e("airConditionResponse", "에러 발생")
            }
        }
        repository.getAirConditionerItems(nearbyObservatory, callback)
    }

    suspend fun insertDB() {
        val finedustEntity =
            FinedustEntity(x = mainAddress.x, y = mainAddress.y, address = mainAddress.address)
        repository.insertItem(finedustEntity)
    }

    suspend fun getFavoriteAddressList() {
        CoroutineScope(Main).launch {
            _favoriteList.value = repository.getRecyclerviewList()
        }
    }

    suspend fun deleteAllFavoriteList() {
        repository.deleteAll()
    }

    suspend fun deleteFavoriteItem(data: String) {
        repository.deleteItem(data)
    }

    fun favoriteListCheck(address: String): Boolean {
        var isFavorite = false
        _favoriteList.value!!.forEach {
            if (it.address == address) {
                isFavorite = true
            }
        }
        return isFavorite
    }
}