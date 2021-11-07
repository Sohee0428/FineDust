package com.example.finedust.presentation.main

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
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

    lateinit var activityDataBinding: ActivityMainBinding

    var address = arrayListOf<AddressResponse>()
    val adapter: FinedustAdapter by lazy {
        FinedustAdapter(this, address)
    }

    val data = mutableListOf<FinedustDao>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        activityDataBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        activityDataBinding.viewModel = mainViewModel

        activityDataBinding.date.text = LocalDate().str_date
        activityDataBinding.recyclerView.adapter = adapter

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
//                TedPermission.create()
//                    .setPermissionListener(permissionSuccessListener)
//                    .setDeniedMessage("권한이 거절되어 사용할 수 없습니다. [설정]으로 이동하여 권한을 허가해주어야 합니다.")
//                    .setPermissions(Manifest.permission.ACCESS_FINE_LOCATION)
//                    .check()
            }
        }

        activityDataBinding.locationUpdateImg.setOnClickListener {
            getLocation()
        }
    }

//    onCreate 함수 외부 ▽

    private fun getLocation() {
        try {
            val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
            val locationProvider = LocationManager.GPS_PROVIDER
            val locationListener = LocationListener {
            }
            locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                3000L,
                30F,
                locationListener
            )
            val currentLatLng: Location? = locationManager.getLastKnownLocation(locationProvider)
            if (currentLatLng != null) {
                val latitude = currentLatLng.latitude
                val longitude = currentLatLng.longitude
                Log.d("CheckCurrentLocation", "내 위치 $latitude, $longitude")

//                Google Play service 위치 API
//                val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
//                fusedLocationClient.lastLocation.addOnSuccessListener {
//                }

                mainViewModel.address(latitude, longitude)
                mainViewModel.navigate(latitude, longitude)

                getAddressData()
                getAirConditionData()

            } else {
                Log.d("CheckCurrentLocation", "현 위치 실패")
            }
        } catch (e: SecurityException) {
            Log.e("CheckCurrentLocationE", "현 위치 에러 발생")
        }
    }

    fun getAirConditionData(){
        mainViewModel.airConditionerItems.observe(this) {
            val address = FinedustListItemBinding.bind(View(this)).locationData.text.toString()
            val data = AddressAndFineDustData(
                address = address,
                pm10Value = it.pm10Value,
                pm10Grade = it.pm10Grade,
                pm25Value = it.pm25Value,
                pm25Grade = it.pm25Grade
            )
            val list = mutableListOf(data)
            adapter.addList(list)
            Log.d("미세먼지", "미세먼지 = ${it.pm10Value}, 초미세먼지 = ${it.pm25Value}")
            FinedustListItemBinding.bind(View(this)).pm10Value.text = it?.pm10Value
            FinedustListItemBinding.bind(View(this)).pm25Value.text = it?.pm25Value
            val pm10Grade = it.pm10Grade
            val pm25Grade = it.pm25Grade

            pm10Grade(pm10Grade)
            pm10GradeImage(pm10Grade)

            pm25Grade(pm25Grade)
            pm25GradeImage(pm25Grade)
        }
    }

    fun pm10Grade(pm10Grade: String) {
        activityDataBinding.pm10Grade.text = when (pm10Grade) {
            "1" -> "좋음"
            "2" -> "보통"
            "3" -> "나쁨"
            "4" -> "매우 나쁨"
            else -> "오류"
        }
    }

    fun pm25Grade(pm25Grade: String) {
        activityDataBinding.pm25Grade.text = when (pm25Grade) {
            "1" -> "좋음"
            "2" -> "보통"
            "3" -> "나쁨"
            "4" -> "매우 나쁨"
            else -> "오류"
        }
    }

    fun pm10GradeImage(pm10Grade: String) {
        when (pm10Grade) {
            "1" -> activityDataBinding.pm10StateImage.setImageResource(R.drawable.perfect)
            "2" -> activityDataBinding.pm10StateImage.setImageResource(R.drawable.soso)
            "3" -> FinedustListItemBinding.bind(View(this)).pm10StateImage.setImageResource(R.drawable.bad)
            "4" -> FinedustListItemBinding.bind(View(this)).pm10StateImage.setImageResource(R.drawable.verybad)
            else -> FinedustListItemBinding.bind(View(this)).pm10StateImage.setImageResource(R.drawable.ic_baseline_error_24)
        }
    }

    fun pm25GradeImage(pm25Grade: String) {
        when (pm25Grade) {
            "1" -> activityDataBinding.pm25StateImage.setImageResource(R.drawable.ic_baseline_mood_24)
            "2" -> activityDataBinding.pm25StateImage.setImageResource(R.drawable.ic_baseline_sentiment_satisfied_alt_24)
            "3" -> activityDataBinding.pm25StateImage.setImageResource(R.drawable.ic_baseline_sentiment_dissatisfied_24)
            "4" -> activityDataBinding.pm25StateImage.setImageResource(R.drawable.ic_baseline_sentiment_very_dissatisfied_24)
            else -> activityDataBinding.pm25StateImage.setImageResource(R.drawable.ic_baseline_error_24)
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