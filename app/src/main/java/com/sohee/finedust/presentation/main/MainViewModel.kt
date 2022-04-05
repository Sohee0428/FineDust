package com.sohee.finedust.presentation.main

import android.Manifest
import android.content.pm.PackageManager
import android.os.Looper
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.location.*
import com.sohee.finedust.App
import com.sohee.finedust.data.DetailAddress
import com.sohee.finedust.data.DetailDust
import com.sohee.finedust.data.entity.FinedustEntity
import com.sohee.finedust.data.repository.MainRepository
import com.sohee.finedust.data.repository.MainRepositoryImpl
import com.sohee.finedust.data.response.air.Item
import com.sohee.finedust.logHelper
import com.sohee.finedust.showToast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {

    lateinit var mainAddress: DetailAddress
    lateinit var detailObservatory: String
    lateinit var detailDate: String
    private val repository: MainRepository = MainRepositoryImpl()
    val detailDustList = arrayListOf<DetailDust>()

    private var latitude: Double = 0.0
    private var longitude: Double = 0.0
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    lateinit var locationRequest: LocationRequest

    private val _mainUiEvent = MutableSharedFlow<MainUiEvents>()
    val mainUiEvent = _mainUiEvent.asSharedFlow()

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

    fun getLocation() {
        try {
            fusedLocationProviderClient =
                LocationServices.getFusedLocationProviderClient(App.context)

            locationRequest = LocationRequest.create().apply {
                priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            }

            updateLocationCallback()
            checkUpdateLocationSettings()

        } catch (e: SecurityException) {
            Log.e("CheckCurrentLocationE", "현 위치 에러 발생")
        }
    }

    private fun updateLocationCallback() {
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                latitude = locationResult.lastLocation.latitude
                longitude = locationResult.lastLocation.longitude

                viewModelScope.launch {
                    _mainUiEvent.emit(MainUiEvents.CurrentLocation)
                }

                navigate(latitude, longitude)
                address(latitude, longitude)
            }
        }
    }

    private fun checkUpdateLocationSettings() {
        val locationSettingRequest = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)
        val settingsClient = LocationServices.getSettingsClient(App.context)
        val task = settingsClient.checkLocationSettings(locationSettingRequest.build())
        task.addOnSuccessListener {
            updateLocation()
        }.addOnFailureListener {
            logHelper(it.toString())
        }
    }

    private fun updateLocation() {
        if (ActivityCompat.checkSelfPermission(
                App.context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            viewModelScope.launch {
                    _mainUiEvent.emit(MainUiEvents.GPSPermissionFail)
            }
        } else {
            fusedLocationProviderClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            )
        }
    }

    private fun updateCurrentLocation() {
        updateLocationCallback()
        updateLocation()
        App.context.showToast("위치를 업데이트 했습니다.")
    }

    fun navigate(latitude: Double, longitude: Double) {
        viewModelScope.launch {
            if (latitude != 0.0 && longitude != 0.0) {
                runCatching {
                    repository.getKakaoLatLon(latitude, longitude)
                }.onSuccess {
                    nearbyObservatory(it.documents[0].x, it.documents[0].y)
                }.onFailure {
                    _mainUiEvent.emit(MainUiEvents.ShowErrorMessageToast(it.message.toString()))
                    Log.e("TMAddressResponse", "에러 발생")
                }
            } else {
                _mainUiEvent.emit(MainUiEvents.ShowNullMessageToast("위치를 불러오지 못하였습니다."))
            }
        }
    }

    fun address(latitude: Double, longitude: Double) {
        viewModelScope.launch {
            runCatching {
                repository.getAddress(latitude, longitude)
            }.onSuccess {
                when {
                    it.documents[0].road_address != null -> _addressName.value =
                        it.documents[0].road_address?.address_name
                    it.documents[0].address != null -> _addressName.value =
                        it.documents[0].address?.address_name
                    else -> _mainUiEvent.emit(MainUiEvents.ShowErrorMessageToast("주소를 불러오지 못했습니다."))
                }
            }.onFailure {
                Log.e("addressResponse", "에러 발생 >> ${it.message}")
                _mainUiEvent.emit(MainUiEvents.ShowErrorMessageToast(it.message.toString()))
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
                _mainUiEvent.emit(MainUiEvents.ShowErrorMessageToast(it.message.toString()))
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
                else _mainUiEvent.emit(MainUiEvents.ShowNullMessageToast("미세먼지 정보를 불러오지 못하였습니다. 잠시후 다시 시도해주시기 바랍니다."))
            }.onFailure {
                Log.e("airConditionResponse", "에러 발생")
                _mainUiEvent.emit(MainUiEvents.ShowErrorMessageToast(it.message.toString()))
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

    fun clickLocationUpdate() {
        updateCurrentLocation()
    }

    fun checkFavoriteState() {
        viewModelScope.launch {
            _mainUiEvent.emit(MainUiEvents.CheckFavoriteState)
        }
    }

    fun clickDetailIntent() {
        viewModelScope.launch {
            _mainUiEvent.emit(MainUiEvents.ClickDetailIntent)
        }
    }

    fun clickLocationIntent() {
        viewModelScope.launch {
            _mainUiEvent.emit(MainUiEvents.ClickLocationIntent)
        }
    }

    fun clickMenuIntent() {
        viewModelScope.launch {
            _mainUiEvent.emit(MainUiEvents.ClickMenuIntent)
        }
    }

    fun clickAppDescription() {
        viewModelScope.launch {
            _mainUiEvent.emit(MainUiEvents.ClickAppDescription)
        }
    }

    fun clickMenuUpdateLocation() {
        viewModelScope.launch {
            updateCurrentLocation()
            _mainUiEvent.emit(MainUiEvents.ClickMenuUpdateLocation)
        }
    }

    fun clickDeleteAllFavoriteList() {
        viewModelScope.launch {
            _mainUiEvent.emit(MainUiEvents.ClickDeleteAllFavoriteList)
        }
    }

    fun stopUpdatingLocation() {
        fusedLocationProviderClient.removeLocationUpdates(locationCallback)
    }

    sealed class MainUiEvents {
        object GPSPermissionFail : MainUiEvents()
        object CurrentLocation : MainUiEvents()
        data class ShowErrorMessageToast(val message: String) : MainUiEvents()
        data class ShowNullMessageToast(val message: String) : MainUiEvents()
        object CheckFavoriteState : MainUiEvents()
        object ClickDetailIntent : MainUiEvents()
        object ClickLocationIntent : MainUiEvents()
        object ClickMenuIntent : MainUiEvents()
        object ClickAppDescription : MainUiEvents()
        object ClickMenuUpdateLocation : MainUiEvents()
        object ClickDeleteAllFavoriteList : MainUiEvents()
    }
}