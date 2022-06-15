package com.sohee.finedust.ui.location

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sohee.finedust.data.DetailAddress
import com.sohee.finedust.repository.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LocationViewModel @Inject constructor(private val repository: MainRepository): ViewModel() {

    private val _locationUiEvent = MutableSharedFlow<LocationUiEvents>()
    val locationUiEvent = _locationUiEvent.asSharedFlow()

    private val _detailAddressList = MutableStateFlow<List<DetailAddress>>(emptyList())
    val detailAddressList = _detailAddressList.asStateFlow()

    var searchLocationTxt = MutableStateFlow("")

    val isSearchLocationTxt = searchLocationTxt.map {
        it.isNotEmpty()
    }.stateIn(
        viewModelScope,
        SharingStarted.Lazily,
        false
    )

    fun clickSearchLocation() {
        viewModelScope.launch {
            _locationUiEvent.emit(LocationUiEvents.CloseKeyboard)
            if (searchLocationTxt.value.isBlank()) {
                _locationUiEvent.emit(LocationUiEvents.ShowNullMessageToast("주소를 입력해주세요"))
            } else {
                location(searchLocationTxt.value)
            }
        }
    }

    fun location(query: String) {
        viewModelScope.launch {
            runCatching {
                repository.getSearchLocation(query)
            }.onSuccess {
                val documentsList = it.documents
                val addressList = DetailAddress.convertDetailAddress(documentsList)

                if (documentsList.isEmpty()) {
                    _locationUiEvent.emit(LocationUiEvents.ShowNullMessageToast("주소를 불러오지 못했습니다. 다시 입력해주시기 바랍니다."))
                } else {
                    _detailAddressList.value = addressList
                }
            }.onFailure {
                _locationUiEvent.emit(LocationUiEvents.ShowErrorMessageToast("location 에러" + it.message))
                Log.e("airConditionResponse", "에러 발생")
            }
        }
    }

    fun clickTextDelete() {
        searchLocationTxt.value = ""
    }

    sealed class LocationUiEvents {
        object CloseKeyboard : LocationUiEvents()
        data class ShowErrorMessageToast(val message: String) : LocationUiEvents()
        data class ShowNullMessageToast(val message: String) : LocationUiEvents()
    }
}