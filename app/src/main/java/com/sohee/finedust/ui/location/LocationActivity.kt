package com.sohee.finedust.ui.location

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.sohee.finedust.R
import com.sohee.finedust.data.DetailAddress
import com.sohee.finedust.databinding.ActivityLocationBinding
import com.sohee.finedust.util.showToast
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class LocationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLocationBinding
    private val viewModel: LocationViewModel by viewModels()
    private val adapter: LocationAdapter by lazy {
        LocationAdapter {
            mainIntent(it)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_location)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        initContactAdapter()
        initCollector()
        clickKeyboardSearchBtn()
    }

    private fun initContactAdapter() {
        binding.locationRecyclerview.adapter = adapter
        val layoutManager = LinearLayoutManager(this)
        binding.locationRecyclerview.layoutManager = layoutManager
    }

    private fun initCollector() {
        lifecycleScope.launch {
            launch {
                viewModel.locationUiEvent.collect {
                    when (it) {
                        LocationViewModel.LocationUiEvents.CloseKeyboard -> closeKeyboard()
                        is LocationViewModel.LocationUiEvents.ShowErrorMessageToast -> showToast(it.message)
                        is LocationViewModel.LocationUiEvents.ShowNullMessageToast -> showToast(it.message)
                    }
                }
            }
            launch {
                viewModel.detailAddressList.collect {
                    getDetailAddress(it)
                }
            }
        }
    }

    private fun clickKeyboardSearchBtn() {
        binding.locationSearch.setOnEditorActionListener { _, actionId, _ ->
            when (actionId) {
                EditorInfo.IME_ACTION_SEARCH -> {
                    viewModel.clickSearchLocation()
                    true
                }
                else -> false
            }
        }
    }

    private fun getDetailAddress(list: List<DetailAddress>) {
        adapter.addLocationList(list)
        Log.d("주소 검색 리스트", "$list")
    }

    private fun mainIntent(data: DetailAddress) {
        val result = Intent().apply {
            putExtra("searchLocation", data)
        }
        setResult(RESULT_OK, result)
        if (!isFinishing) finish()
    }

    private fun closeKeyboard() {
        val imm = this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.locationSearch.windowToken, 0)
    }
}