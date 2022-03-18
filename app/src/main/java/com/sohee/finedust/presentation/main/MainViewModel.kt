package com.sohee.finedust.presentation.main

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
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

    private val _mainUiEvent = MutableStateFlow<MainUiEvents?>(null)
    val mainUiEvent = _mainUiEvent.asStateFlow()

    private val _favoriteList: MutableLiveData<List<FinedustEntity>> = MutableLiveData()
    val favoriteList: LiveData<List<FinedustEntity>>
        get() = _favoriteList

    private val _addressName = MutableStateFlow<String?>(null)
    val addressName = _addressName.asStateFlow()

    private val _airConditionerItems = MutableStateFlow<Item?>(null)
    val airConditionerItems = _airConditionerItems.asStateFlow()

    private val _pm10Grade = MutableStateFlow<String?>(null)
    val pm10Grade = _pm10Grade.asStateFlow()

    private val _pm25Grade = MutableStateFlow<String?>(null)
    val pm25Grade = _pm25Grade.asStateFlow()

    fun navigate(latitude: Double, longitude: Double) {
        viewModelScope.launch {
            runCatching {
                repository.getKakaoLatLon(latitude, longitude)
            }.onSuccess {
                nearbyObservatory(it.documents[0].x, it.documents[0].y)
            }.onFailure {
                _mainUiEvent.value = MainUiEvents.ShowErrorMessageToast(it.message.toString())
                Log.e("TMAddressResponse", "에러 발생")
            }
        }
    }

    fun address(latitude: Double, longitude: Double) {
        viewModelScope.launch {
            runCatching {
                repository.getAddress(latitude, longitude)
            }.onSuccess {
                when {
                    it.documents[0].road_address.address_name.isNotEmpty() -> _addressName.value =
                        it.documents[0].road_address.address_name
                    it.documents[0].address.address_name.isNotEmpty() -> _addressName.value =
                        it.documents[0].address.address_name
                    else -> _mainUiEvent.value =
                        MainUiEvents.ShowErrorMessageToast("주소를 불러오지 못했습니다.")
                }
            }.onFailure {
                Log.e("addressResponse", "에러 발생 >> ${it.message}")
                _mainUiEvent.value = MainUiEvents.ShowErrorMessageToast(it.message.toString())
            }
        }
    }

    private fun nearbyObservatory(xValue: Double, yValue: Double) {
        viewModelScope.launch {
            runCatching {
                repository.getObservatoryItems(xValue, yValue)
            }.onSuccess {
                val stationName =  it.response.body.items[0].stationName
                detailObservatory = stationName
                airConditioner(stationName)
            }.onFailure {
                Log.e("observatoryReponse", "에러 발생 ")
                _mainUiEvent.value = MainUiEvents.ShowErrorMessageToast(it.message.toString())
            }
        }
    }

    private fun airConditioner(nearbyObservatory: String) {
        viewModelScope.launch {
            runCatching {
                repository.getAirConditionerItems(nearbyObservatory)
            }.onSuccess {
                if (it.response.body.items[0] != null){
                    _airConditionerItems.value = it.response.body.items[0]
                    detailDate = it.response.body.items[0].dataTime
                    setAirConditionData(it.response.body.items[0])
                }
                else _mainUiEvent.value = MainUiEvents.ShowNullMessageToast("미세먼지 정보를 불러오지 못하였습니다. 잠시후 다시 시도해주시기 바랍니다.")
            }.onFailure {
                Log.e("airConditionResponse", "에러 발생")
                _mainUiEvent.value = MainUiEvents.ShowErrorMessageToast(it.message.toString())
            }
        }
    }

    private fun setAirConditionData(airConditionData: Item) {
        _pm10Grade.value = pm10Grade(airConditionData.pm10Value)
        _pm25Grade.value = pm10Grade(airConditionData.pm25Value)

        val itemList = mutableListOf(
            airConditionData.khaiValue to airConditionData.khaiGrade,
            airConditionData.pm10Value to pm10Grade(pm10Grade.value),
            airConditionData.pm25Value to pm25Grade(pm25Grade.value),
            airConditionData.o3Value to airConditionData.o3Grade,
            airConditionData.so2Value to airConditionData.so2Grade,
            airConditionData.coValue to airConditionData.coGrade,
            airConditionData.no2Value to airConditionData.no2Grade
        )

        detailDustList.clear()
        detailDustList.addAll(DetailDust.getDetailDustList(itemList))
    }

    private fun pm10Grade(str: String?): String {
        return when (str?.toIntOrNull()) {
            in 0..30 -> "1"
            in 31..50 -> "2"
            in 51..100 -> "3"
            null -> ""
            else -> "4"
        }
    }

    private fun pm25Grade(str: String?): String {
        return when (str?.toIntOrNull()) {
            in 0..15 -> "1"
            in 16..25 -> "2"
            in 26..50 -> "3"
            null -> ""
            else -> "4"
        }
    }

    fun pmGrade(str: String?): String {
        return when (str) {
            "1" -> "좋음"
            "2" -> "보통"
            "3" -> "나쁨"
            "4" -> "매우 나쁨"
            "" -> "로딩 중"
            else -> "오류"
        }
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

    sealed class MainUiEvents {
        object GPSPermissionSuccess : MainUiEvents()
        object GPSPermissionFail : MainUiEvents()
        data class ShowErrorMessageToast(val message: String) : MainUiEvents()
        data class ShowNullMessageToast(val message: String) : MainUiEvents()

    }
}