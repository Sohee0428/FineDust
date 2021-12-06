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
    private val viewModel: LocationViewModel by viewModels()
    lateinit var binding: ActivityLocationBinding
    val adapter: LocationAdapter by lazy {
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

        val layoutManager = LinearLayoutManager(this)
        binding.locationRecyclerview.layoutManager = layoutManager

        binding.locationSearch.doOnTextChanged { text, _, _, _ ->
            searchText = text.toString()
        }

        binding.searchImg.setOnClickListener {
            if (searchText.isBlank()) {
                Toast.makeText(this, "주소를 입력해주세요.", Toast.LENGTH_SHORT).show()
            } else {
                searchLocation()
            }
            val imm = this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(binding.locationSearch.windowToken, 0)
        }

        viewModel.detailAddressList.observe(this) {
            adapter.addLocationList(it)
            Log.d("주소 검색 리스트", "$it")
        }
        viewModel.detailAddressListNull.observe(this) {
            Toast.makeText(this, "주소를 불러오지 못했습니다. 다시 입력해주시기 바랍니다.", Toast.LENGTH_SHORT).show()
        }
    }

    fun searchLocation() {
        Log.d("주소 검색어", searchText)
        viewModel.location(searchText)
    }

    fun mainIntent(data: DetailAddress) {
        val result = Intent().apply {
            putExtra("searchLocation", data)
        }
        setResult(RESULT_OK, result)
        if (!isFinishing) finish()

    }
}