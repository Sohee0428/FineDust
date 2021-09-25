package com.example.finedust

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.finedust.API.AirConditionerAPI
import com.example.finedust.API.KakaoAPI
import com.example.finedust.API.NearbyParadidymisAPI
import com.example.finedust.data.Air.AirConditionerInfo
import com.example.finedust.data.Kakao.Test
import com.example.finedust.data.Paradidymis.Paradidymis
import com.google.android.gms.location.LocationServices
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {

    private val data: TextView by lazy {
        findViewById(R.id.date)
    }

    private val location: TextView by lazy {
        findViewById(R.id.location)
    }

    private val stateImage: ImageView by lazy {
        findViewById(R.id.stateImage)
    }

    private val fineConcentration: TextView by lazy {
        findViewById(R.id.fineConcentration)
    }

    private val fineLevel: TextView by lazy {
        findViewById(R.id.fineLevel)
    }

    private val retrofit = Retrofit.Builder()
        .baseUrl("http://apis.data.go.kr/B552584/ArpltnInforInqireSvc/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()


    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
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


    private fun getLocation() {
        try {
            val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

            val locationProvider = LocationManager.GPS_PROVIDER
            val locationListener = LocationListener { }
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

                val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
                fusedLocationClient.lastLocation.addOnSuccessListener {
                }

                val api1 = KakaoAPI.create()
                api1.getNavigate(longitude, latitude).enqueue(object : Callback<Test> {
                    override fun onResponse(call: Call<Test>, response: Response<Test>) {
                        Log.d("jsh", response.body().toString())

                        val xValue = response.body()?.documents?.get(0)?.x
                        Log.d("xValue", xValue.toString())
                        val yValue = response.body()?.documents?.get(0)?.y
                        Log.d("yValue", yValue.toString())

                        val api2 = NearbyParadidymisAPI.create()
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

                                                val pm10Grade =
                                                    response.body()?.response?.body?.items?.get(0)?.pm10Grade.toString()

                                                data.text =
                                                    response.body()?.response?.body?.items?.get(0)?.dataTime
                                                fineConcentration.text =
                                                    response.body()?.response?.body?.items?.get(0)?.pm10Value + "㎍/㎥"
                                                fineLevel.text = when (pm10Grade) {
                                                    "1" -> {
                                                        "좋음"
                                                    }
                                                    "2" -> {
                                                        "보통"
                                                    }
                                                    "3" -> {
                                                        "나쁨"
                                                    }
                                                    else -> {
                                                        "매우 나쁨"
                                                    }
                                                }

                                                when (pm10Grade) {
                                                    "1" -> stateImage.setImageResource(R.drawable.ic_baseline_mood_24)
                                                    "2" -> stateImage.setImageResource(R.drawable.ic_baseline_sentiment_satisfied_alt_24)
                                                    "3" -> stateImage.setImageResource(R.drawable.ic_baseline_sentiment_dissatisfied_24)
                                                    "4" -> stateImage.setImageResource(R.drawable.ic_baseline_sentiment_very_dissatisfied_24)

                                                }

                                            }

                                            override fun onFailure(
                                                call: Call<AirConditionerInfo>,
                                                t: Throwable
                                            ) {
                                            }
                                        }
                                        )
                                }

                                override fun onFailure(
                                    call: Call<Paradidymis>,
                                    t: Throwable
                                ) {
                                }
                            }
                            )
                    }

                    override fun onFailure(call: Call<Test>, t: Throwable) {
                    }
                }
                )
            } else {
                Log.d("CheckCurrentLocation", "내 위치 실패12")
            }
        } catch (e: SecurityException) {
        }
    }
}