package com.sohee.finedust.ui.main

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
import com.sohee.finedust.R
import com.sohee.finedust.data.DetailAddress
import com.sohee.finedust.data.DetailIntentData
import com.sohee.finedust.data.response.aircondition.DetailDust
import com.sohee.finedust.data.response.aircondition.air.Item
import com.sohee.finedust.repository.MainRepository
import com.sohee.finedust.repository.local.entity.FinedustEntity
import com.sohee.finedust.util.LocalDate
import com.sohee.finedust.util.showToast
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: MainRepository,
    private val application: Application
) : ViewModel() {

    lateinit var mainAddress: DetailAddress
    lateinit var detailObservatory: String
    lateinit var detailDate: String
    val detailDustList = arrayListOf<DetailDust>()
    val mainIntentDetailDustData = MutableStateFlow<DetailIntentData?>(null)
    val isShowProgressBar = MutableStateFlow(true)
    val isClickable = MutableStateFlow(false)

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

    private val _currentDate = MutableStateFlow("")
    val currentDate = _currentDate.asStateFlow()

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
            viewModelScope.launch {
                _mainUiEvent.emit(
                    MainUiEvents.ShowErrorMessageToast(
                        "getLocation 에러" + App.instance.getString(
                            R.string.fail_location
                        )
                    )
                )
            }
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
        }.addOnFailureListener { exception ->
            logHelper(exception.toString())
            viewModelScope.launch {
                _mainUiEvent.emit(MainUiEvents.ShowErrorMessageToast("checkUpdateLocationSettings 에러 -> $exception"))
            }
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
        App.context.showToast(App.instance.getString(R.string.update_location))
    }

    fun navigate(latitude: Double, longitude: Double) {
        viewModelScope.launch {
            if (latitude != 0.0 && longitude != 0.0) {
                runCatching {
                    repository.getKakaoLatLon(latitude, longitude)
                }.onSuccess {
                    nearbyObservatory(it.documents[0].x, it.documents[0].y)
                    isShowProgressBar.value = false
                }.onFailure {
                    _mainUiEvent.emit(MainUiEvents.ShowErrorMessageToast(it.message.toString()))
                    Log.e("TMAddressResponse", it.message.toString())
                }
            } else {
                _mainUiEvent.emit(
                    MainUiEvents.ShowNullMessageToast(
                        "navigate 서버 오류" + App.instance.getString(
                            R.string.fail_location
                        )
                    )
                )
            }

            _currentDate.value = LocalDate().str_date_time
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
                    else -> _mainUiEvent.emit(
                        MainUiEvents.ShowNullMessageToast(
                            "address 값 null" + App.instance.getString(
                                R.string.fail_address
                            )
                        )
                    )
                }
                isShowProgressBar.value = false
            }.onFailure {
                Log.e("addressResponse", "에러 발생 >> ${it.message}")
                _mainUiEvent.emit(MainUiEvents.ShowErrorMessageToast("address 서버 오류" + it.message.toString()))
            }
        }
    }

    private fun nearbyObservatory(xValue: Double, yValue: Double) {
        viewModelScope.launch {
            runCatching {
                repository.getObservatoryItems(xValue, yValue)
            }.onSuccess {
                val stationName = it.response.body.items[0].stationName
                detailObservatory = stationName
                airConditioner(stationName)
            }.onFailure {
                Log.e("observatoryResponse", "에러 발생 ")
                _mainUiEvent.emit(MainUiEvents.ShowErrorMessageToast("nearByObservatory 서버 오류" + it.message.toString()))
            }
        }
    }

    private fun airConditioner(nearbyObservatory: String) {
        viewModelScope.launch {
            runCatching {
                repository.getAirConditionerItems(nearbyObservatory)
            }.onSuccess {
                if (it.response.body.items[0] != null) {
                    _airConditionerItems.value = it.response.body.items[0]
                    detailDate = it.response.body.items[0].dataTime
                    setAirConditionData(it.response.body.items[0])
                    isClickable.value = true
                } else {
                    _mainUiEvent.emit(MainUiEvents.ShowNullMessageToast(App.instance.getString(R.string.fail_get_finedust_data)))
                }
            }.onFailure {
                Log.e("airConditionResponse", "에러 발생" + it.message)
                _mainUiEvent.emit(MainUiEvents.ShowErrorMessageToast("airConditioner 서버 오류" + it.message))
            }
        }
    }

    private fun setAirConditionData(airConditionData: Item) {
        _pm10Grade.value = pm10Grade(airConditionData.pm10Value)
        _pm25Grade.value = pm25Grade(airConditionData.pm25Value)

        val itemList = mutableListOf(
            airConditionData.khaiValue to airConditionData.khaiGrade,
            airConditionData.pm10Value to _pm10Grade.value,
            airConditionData.pm25Value to _pm25Grade.value,
            airConditionData.o3Value to airConditionData.o3Grade,
            airConditionData.so2Value to airConditionData.so2Grade,
            airConditionData.coValue to airConditionData.coGrade,
            airConditionData.no2Value to airConditionData.no2Grade
        )

        detailDustList.clear()
        detailDustList.addAll(DetailDust.getDetailDustList(itemList, airConditionData.dataTime))
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
            null -> "로딩 중"
            else -> "오류"
        }
    }

    suspend fun insertDB() {
        val finedustEntity =
            FinedustEntity(x = mainAddress.x, y = mainAddress.y, address = mainAddress.address)
        repository.insertItem(finedustEntity)
    }

    suspend fun getFavoriteAddressList() {
        viewModelScope.launch {
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
            mainIntentDetailDustData.value = DetailIntentData(
                data = detailDustList,
                observatory = detailObservatory,
                date = detailDate,
                location = addressName.value!!
            )
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

    fun changeLocation() {
        navigate(mainAddress.x.toDouble(), mainAddress.y.toDouble())
        _addressName.value = mainAddress.address
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