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
import com.example.finedust.API.KakaoAPI
import com.example.finedust.data.Test
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

    private val alertLevel: TextView by lazy {
        findViewById(R.id.alertLevel)
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

    private val ultrafineConcentration: TextView by lazy {
        findViewById(R.id.ultrafineConcentration)
    }

    private val ultrafineLevel: TextView by lazy {
        findViewById(R.id.ultrafineLevel)
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
                    Log.d("jsh","test3")
                    getLocation()
                } else {
                    Log.d("jsh","test4")
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
                Log.d("jsh","test1")
                getLocation()


            }
            else -> {
                // You can directly ask for the permission.
                // The registered ActivityResultCallback gets the result of this request.
                Log.d("jsh","test2")
                requestPermissionLauncher.launch(
                    Manifest.permission.ACCESS_FINE_LOCATION)
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
            val locationListener = LocationListener {  }
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

        val request = Request.Builder().url(url).build()
        val client = OkHttpClient()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {

                    override fun onFailure(call: Call<Test>, t: Throwable) {
                    }

                })

            } else {
                Log.d("CheckCurrentLocation", "내 위치 실패12")
            }
        } catch (e: SecurityException) {
        }
    }


}