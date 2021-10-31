package com.example.finedust.presentation.main

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        activityDataBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        activityDataBinding.viewModel = mainViewModel

        activityDataBinding.date.text = LocalDate().str_date

        val permissionSuccessListener = object : PermissionListener {
            override fun onPermissionGranted() {
                getLocation()
            }
            override fun onPermissionDenied(deniedPermissions: MutableList<String>?) {
                onDestroy()
            }
        }

//        이거 사용하는 이유? -> TedPermission 이용함
        val requestPermissionLauncher =
            registerForActivityResult(
                ActivityResultContracts.RequestPermission()
            ) { isGranted: Boolean ->
                if (isGranted) {
                    Log.d("위치 허가", "requestPermission Success")
                    getLocation()
                } else {
                    Log.d("위치 허가", "requestPermission Fail")
                    // Explain to the user that the feature is unavailable because the
                    // features requires a permission that the user has denied. At the
                    // same time, respect the user's decision. Don't link to system
                    // settings in an effort to convince the user to change their
                    // decision.
                }
            }

        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED -> {
                Log.d("jsh", "test1")
                getLocation()
            }
            else -> {
                // You can directly ask for the permission.
                // The registered ActivityResultCallback gets the result of this request.
                Log.d("jsh", "test2")
                requestPermissionLauncher.launch(
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            }
        }
//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
////            권한이 있는 경우
//                    ActivityCompat.requestPermissions(this, Array<String>)
//            } else if (shouldShowRequestPermissionRationale)

        Log.d("주소",mainViewModel.address.toString())

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
                Log.d("CheckCurrentLocation", "내 위치 실패12")
            }
        } catch (e: SecurityException) {
        }
    }

    fun getAirConditionData(){
        mainViewModel.airConditionerItems.observe(this) {
            Log.d("미세먼지", "미세먼지 = ${it.pm10Value}, 초미세먼지 = ${it.pm25Value}")
            activityDataBinding.pm10Value.text = it?.pm10Value
            activityDataBinding.pm25Value.text = it?.pm25Value
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
            "1" -> activityDataBinding.pm10StateImage.setImageResource(R.drawable.ic_baseline_mood_24)
            "2" -> activityDataBinding.pm10StateImage.setImageResource(R.drawable.ic_baseline_sentiment_satisfied_alt_24)
            "3" -> activityDataBinding.pm10StateImage.setImageResource(R.drawable.ic_baseline_sentiment_dissatisfied_24)
            "4" -> activityDataBinding.pm10StateImage.setImageResource(R.drawable.ic_baseline_sentiment_very_dissatisfied_24)
            else -> activityDataBinding.pm10StateImage.setImageResource(R.drawable.ic_baseline_error_24)
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
        mainViewModel.preAddress.observe(this) {
            activityDataBinding.locationData.text = it.address_name
//            if (it.road_address.address_name!!.isNullOrEmpty()) {
//                Log.d("주소", "신 주소 = ${it.road_address.address_name}")
//                activityDataBinding.location.text = it.road_address.address_name
//            }
//            if (it.road_address.address_name.isNullOrEmpty() && it.address.address_name!!.isNullOrEmpty()){
//                Log.d("주소", "구 주소 = ${it.address.address_name}")
//                activityDataBinding.location.text = it.address.address_name
//            } else {
//                Toast.makeText(this, "주소를 불러오지 못했습니다.", Toast.LENGTH_SHORT).show()
//            }
        }
    }
}