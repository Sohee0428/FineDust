package com.example.finedust

import android.annotation.SuppressLint
import android.content.IntentSender
import android.location.Location
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.tasks.Task

class GoogleApi : AppCompatActivity() {

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


    //    통합 위치 정보 제공자
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    //     위치 요청을 만들고 매개면수 설정
    private fun createLocationRequest() {
        val locationRequest = LocationRequest.create()?.apply {
//            수신 간격
            interval = 10000
//            빠른 업데이트 간격격
            fastestInterval = 5000
//            정확한 위치 요청
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
        //    현재 위치 요청
        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)
        val client: SettingsClient = LocationServices.getSettingsClient(this)
        val task: Task<LocationSettingsResponse> = client.checkLocationSettings(builder.build())
//    상태 코드 위로 위치 설정을 확인 가능

        task.addOnSuccessListener { locationSettingsResponse ->
        }
        task.addOnFailureListener { exception ->
            if (exception is ResolvableApiException) {
                try {
                    exception.startResolutionForResult(this@GoogleApi, REQUEST_CHECK_SETTINGS)
                } catch (sendEx: IntentSender.SendIntentException) {
                }
            }
        }
    }


    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
//        마지막 위치 요청 및 응답 처리
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
            }

    }

    override fun onResume() {
        super.onResume()
        if (requestingLocationUpdates)
    }
}
