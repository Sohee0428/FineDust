package com.example.finedust.presentation.main

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.example.finedust.R
import com.example.finedust.data.date.LocalDate
import com.example.finedust.databinding.ActivityMainBinding
import com.gun0912.tedpermission.PermissionListener

class MainActivity : AppCompatActivity() {

    private val mainViewModel: MainViewModel by viewModels()

    lateinit var binding: ActivityMainBinding

    lateinit var pagerAdapter: FragmentAdapter


    var address = arrayListOf<AddressResponse>()
    val adapter: FinedustAdapter by lazy {
        FinedustAdapter(this, address)
    }

    val data = mutableListOf<FinedustDao>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.viewModel = mainViewModel

        binding.date.text = LocalDate().str_date
        pagerAdapter = ViewPagerAdapter(this)
        binding.viewPager.adapter = pagerAdapter


        val layoutManager = LinearLayoutManager(this)

//            리사이클러뷰에 아이템을 배치 후 더이상 보이지 않을 때 재사용성을 결정하는 것
//            불필요한 findViewById를 수행하지 않음
        activityDataBinding.recyclerView.layoutManager = layoutManager
        activityDataBinding.recyclerView.setHasFixedSize(true)

//        이거 사용하는 이유? -> TedPermission 이용함
        val requestPermissionLauncher =
            registerForActivityResult(
                ActivityResultContracts.RequestPermission()
            ) { isGranted: Boolean ->
                if (isGranted) {
                    Log.d("위치 허가", "requestPermission Success")
                    getLocation()
                } else {
                    Log.d("위치 불허", "requestPermission Fail")
                }
            }

        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED -> {
                Log.d("checkSelfPermission", "checkPermission Success")
                getLocation()
            }
            else -> {
                Log.d("checkSelfPermission", "checkPermission Fail")
                requestPermissionLauncher.launch(
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            }
        }
    }

    fun getAddressData() {
        mainViewModel.newAddress.observe(this) {
         Log.d("신주소", it.address_name)
                activityDataBinding.locationData.text = it.address_name
        }
        mainViewModel.preAddress.observe(this) {
            Log.d("구주소", it.address_name)
            activityDataBinding.locationData.text = it.address_name
        }
        mainViewModel.addressNull.observe(this) {
            Log.d("주소", "주소 값을 불러오지 못함.")
            Toast.makeText(this, "주소를 불러오지 못했습니다.", Toast.LENGTH_SHORT).show()
        }
    }
}