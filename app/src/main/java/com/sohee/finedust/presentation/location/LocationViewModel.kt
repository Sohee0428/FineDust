package com.sohee.finedust.presentation.location

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.sohee.finedust.data.DetailAddress
import com.sohee.finedust.data.repository.MainRepository
import com.sohee.finedust.data.repository.MainRepositoryImpl
import com.sohee.finedust.data.response.search.SearchAddressResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LocationViewModel : ViewModel() {

    private val repository: MainRepository = MainRepositoryImpl()

    private val _detailAddressList: MutableLiveData<List<DetailAddress>> = MutableLiveData()
    val detailAddressList: LiveData<List<DetailAddress>>
        get() = _detailAddressList

    private val _detailAddressListNull: MutableLiveData<Unit> = MutableLiveData()
    val detailAddressListNull: LiveData<Unit>
        get() = _detailAddressListNull

    fun location(query: String) {
        val callback = object : Callback<SearchAddressResponse> {
            override fun onResponse(
                call: Call<SearchAddressResponse>,
                response: Response<SearchAddressResponse>
            ) {
                Log.d("위치 검색", response.toString())
                Log.d("검색어", query)

                val documentsList = response.body()!!.documents
                val addressList = DetailAddress.convertDetailAddress(documentsList)

                if (documentsList.isEmpty()) {
                    _detailAddressListNull.value = Unit
                } else {
                    _detailAddressList.value = addressList
                }
            }
            override fun onFailure(call: Call<SearchAddressResponse>, t: Throwable) {
                Log.e("airConditionResponse", "에러 발생")
            }
        }
        repository.getSearchLocation(query, callback)
    }
}