package com.sohee.finedust.presentation.location

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sohee.finedust.data.DetailAddress
import com.sohee.finedust.data.repository.MainRepository
import com.sohee.finedust.data.repository.MainRepositoryImpl
import kotlinx.coroutines.launch

class LocationViewModel : ViewModel() {

    private val repository: MainRepository = MainRepositoryImpl()

    private val _detailAddressList: MutableLiveData<List<DetailAddress>> = MutableLiveData()
    val detailAddressList: LiveData<List<DetailAddress>>
        get() = _detailAddressList

    private val _detailAddressListNull: MutableLiveData<Unit> = MutableLiveData()
    val detailAddressListNull: LiveData<Unit>
        get() = _detailAddressListNull

    fun location(query: String) {
        viewModelScope.launch {
            runCatching {
                repository.getSearchLocation(query)
            }.onSuccess {
                val documentsList = it.documents
                val addressList = DetailAddress.convertDetailAddress(documentsList)

                if (documentsList.isEmpty()) {
                    _detailAddressListNull.value = Unit
                } else {
                    _detailAddressList.value = addressList
                }
            }.onFailure {
                Log.e("airConditionResponse", "에러 발생")
            }
        }
    }
}