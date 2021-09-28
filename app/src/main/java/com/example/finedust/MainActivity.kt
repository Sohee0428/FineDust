package com.example.finedust

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.example.finedust.api.AirConditionerAPI
import com.example.finedust.api.KakaoAPI
import com.example.finedust.api.NearbyParadidymisAPI
import com.example.finedust.data.air.AirConditionerInfo
import com.example.finedust.data.kakao.Test
import com.example.finedust.data.paradidymis.Paradidymis
import com.example.finedust.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    lateinit var activityDataBinding: ActivityMainBinding

    val api1 = KakaoAPI.create()
    val api2 = NearbyParadidymisAPI.create()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        activityDataBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)

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

                navigateAPI(latitude, longitude)

            } else {
                Log.d("CheckCurrentLocation", "내 위치 실패12")
            }
        } catch (e: SecurityException) {
        }
    }

    fun navigateAPI(latitude: Double, longitude: Double) {

        api1.getNavigate(longitude, latitude).enqueue(object : Callback<Test> {
            override fun onResponse(call: Call<Test>, response: Response<Test>) {
                Log.d("jsh", response.body().toString())

                val xValue = response.body()?.documents?.get(0)?.x
                Log.d("xValue", xValue.toString())
                val yValue = response.body()?.documents?.get(0)?.y
                Log.d("yValue", yValue.toString())

                nearbyParadidymis(xValue, yValue)
            }

            override fun onFailure(call: Call<Test>, t: Throwable) {
            }
        }
        )
    }

    fun nearbyParadidymis(xValue: Double?, yValue: Double?) {

        api2.getParadidymis(xValue, yValue, NearbyParadidymisAPI.SERVICE_KEY)
            .enqueue(object : Callback<Paradidymis> {
                override fun onResponse(
                    call: Call<Paradidymis>,
                    response: Response<Paradidymis>
                ) {
                    Log.d("paradidymis", response.body().toString())

                    val nearbyParadidymis =
                        response.body()?.response?.body?.items?.get(0)?.stationName
                    Log.d("nearbyParadidymis", nearbyParadidymis.toString())

                    airConditioner(nearbyParadidymis)
                }

                override fun onFailure(
                    call: Call<Paradidymis>,
                    t: Throwable
                ) {

                }
            }
            )
    }

    fun airConditioner(nearbyParadidymis: String?) {
        val api3 = AirConditionerAPI.create()
        api3.getAirConditioner(
            nearbyParadidymis,
            NearbyParadidymisAPI.SERVICE_KEY
        )
            .enqueue(object : Callback<AirConditionerInfo> {
                override fun onResponse(
                    call: Call<AirConditionerInfo>,
                    response: Response<AirConditionerInfo>
                ) {
                    Log.d("KSH", response.body().toString())

                    activityDataBinding.dataItem = response.body()?.response?.body?.items?.get(0)

                    val pm10Grade =
                        response.body()?.response?.body?.items?.get(0)?.pm10Grade.toString()

                    fineDustGrade(pm10Grade)
                    fineDustGradeImage(pm10Grade)
                }

                override fun onFailure(
                    call: Call<AirConditionerInfo>,
                    t: Throwable
                ) {
                }
            }
            )
    }

    fun fineDustGrade(pm10Grade: String) {
        activityDataBinding.fineLevel.text = when (pm10Grade) {
            "1" -> "좋음"
            "2" -> "보통"
            "3" -> "나쁨"
            else -> "매우 나쁨"
        }
    }

    fun fineDustGradeImage(pm10Grade: String) {
        when (pm10Grade) {
            "1" -> activityDataBinding.stateImage.setImageResource(R.drawable.ic_baseline_mood_24)
            "2" -> activityDataBinding.stateImage.setImageResource(R.drawable.ic_baseline_sentiment_satisfied_alt_24)
            "3" -> activityDataBinding.stateImage.setImageResource(R.drawable.ic_baseline_sentiment_dissatisfied_24)
            "4" -> activityDataBinding.stateImage.setImageResource(R.drawable.ic_baseline_sentiment_very_dissatisfied_24)
        }
    }
}