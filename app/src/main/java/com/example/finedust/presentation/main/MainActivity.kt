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
import com.example.finedust.databinding.ActivityMainBinding
import com.google.android.gms.location.LocationServices

class MainActivity : AppCompatActivity() {

    private val mainViewModel: MainViewModel by viewModels()

    private lateinit var fusedLocationClient: FusedLocationProviderClient


    lateinit var activityDataBinding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        activityDataBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        activityDataBinding.viewModel = mainViewModel

        val requestPermissionLauncher =
            registerForActivityResult(
                ActivityResultContracts.RequestPermission()
            ) { isGranted: Boolean ->
                if (isGranted) {
                    Log.d("jsh", "test3")
                    getLocation()
                } else {
                    Log.d("jsh", "test4")
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

        mainViewModel.address.observe(this) {
            activityDataBinding.location.text = it.address_name
        }

        mainViewModel.airConditionerItems.observe(this) {
            Log.d("미세먼지", it.pm10Value)
            val pm10Grade = it.pm10Grade
            fineDustGrade(pm10Grade)
            fineDustGradeImage(pm10Grade)
        }
    }
//    onCreate 함수 외부 ▽

    private fun getLocation() {
        try {

            fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
            fusedLocationClient.lastLocation.addOnSuccessListener { location : Location? ->

            }

            fun createLocationRequest() {
                val locationRequest = LocationRequest.create()?.apply {
                    interval = 10000
                    fastestInterval = 5000
                    priority = LocationRequest.PRIORITY_HIGH_ACCURACY
                }
            }

            val builder = LocationSettingsRequest.Builder().addAllLocationRequests(locationre)


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

                mainViewModel.navigate(latitude, longitude)
            } else {
                Log.d("CheckCurrentLocation", "내 위치 실패12")
            }
        } catch (e: SecurityException) {
        }
    }

    fun fineDustGrade(pm10Grade: String) {
        activityDataBinding.fineLevel.text = when (pm10Grade) {
            "1" -> "좋음"
            "2" -> "보통"
            "3" -> "나쁨"
            "4" -> "매우 나쁨"
            else -> "오류"
        }
    }

    fun fineDustGradeImage(pm10Grade: String) {
        when (pm10Grade) {
            "1" -> activityDataBinding.stateImage.setImageResource(R.drawable.ic_baseline_mood_24)
            "2" -> activityDataBinding.stateImage.setImageResource(R.drawable.ic_baseline_sentiment_satisfied_alt_24)
            "3" -> activityDataBinding.stateImage.setImageResource(R.drawable.ic_baseline_sentiment_dissatisfied_24)
            "4" -> activityDataBinding.stateImage.setImageResource(R.drawable.ic_baseline_sentiment_very_dissatisfied_24)
            else -> activityDataBinding.stateImage.setImageResource(R.drawable.ic_baseline_error_24)
        }
    }
}