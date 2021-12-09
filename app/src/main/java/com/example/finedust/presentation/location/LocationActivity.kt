package com.example.finedust.presentation.location

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.finedust.R
import com.example.finedust.data.DetailAddress
import com.example.finedust.databinding.ActivityLocationBinding

class LocationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLocationBinding
    private val viewModel: LocationViewModel by viewModels()
    private val adapter: LocationAdapter by lazy {
        LocationAdapter {
            mainIntent(it)
        }
    }
    private var searchText = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_location)
        binding.locationActivity = this
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        initContactAdapter()
        searchText()
        getDetailAddress()

//        binding.locationSearch.setOnEditorActionListener { _, actionId, _ ->
//            return@setOnEditorActionListener when (actionId) {
//                EditorInfo.IME_ACTION_SEND -> {
//                    getDetailAddress()
//                    true
//                }
//                else -> false
//            }
//        }
    }

    private fun initContactAdapter() {
        binding.locationRecyclerview.adapter = adapter
        val layoutManager = LinearLayoutManager(this)
        binding.locationRecyclerview.layoutManager = layoutManager
    }

    private fun searchLocation() {
        Log.d("주소 검색어", searchText)
        viewModel.location(searchText)
    }

    private fun searchText() {
        binding.locationSearch.doOnTextChanged { text, _, _, _ ->
            searchText = text.toString()
        }
    }

    private fun getDetailAddress() {
        viewModel.detailAddressList.observe(this) {
            adapter.addLocationList(it)
            Log.d("주소 검색 리스트", "$it")
        }
        viewModel.detailAddressListNull.observe(this) {
            Toast.makeText(this, "주소를 불러오지 못했습니다. 다시 입력해주시기 바랍니다.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun mainIntent(data: DetailAddress) {
        val result = Intent().apply {
            putExtra("searchLocation", data)
        }
        setResult(RESULT_OK, result)
        if (!isFinishing) finish()
    }

    }
}