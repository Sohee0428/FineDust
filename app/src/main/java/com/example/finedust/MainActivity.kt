package com.example.finedust

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.CancellationTokenSource
import okhttp3.*
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException

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

//    위치 정보 가져오기기
   private var cancellationTokenSource: CancellationTokenSource? = null
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


//        위치 정보 가져오기
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        cancellationTokenSource = CancellationTokenSource()
        fusedLocationProviderClient.getCurrentLocation(
            LocationRequest.PRIORITY_HIGH_ACCURACY,
            cancellationTokenSource!!.token
        ).addOnSuccessListener { location ->
            Log.d("위치 정보", "${location.latitude}, ${location.longitude}")
        }

        fetJson()
    }

    fun fetJson(){
        val key = "M66ovFn84Is25oHoO6tQEVwPVD83anrxkIon8fsxQytUaSNJ2nRQPOMs5MJh8Cb1GfXYVi8L3t87tz1j%2FpncMQ%3D%3D"
        val url = "http://apis.data.go.kr/B552584/ArpltnInforInqireSvc + $key + &returnType=json"

        val request = Request.Builder().url(url).build()
        val client = OkHttpClient()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {

            }

            override fun onResponse(call: Call, response: Response) {
            }

        })
    }

}