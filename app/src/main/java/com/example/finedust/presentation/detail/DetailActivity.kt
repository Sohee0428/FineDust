package com.example.finedust.presentation.detail

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.finedust.R
import com.example.finedust.data.DetailDust
import com.example.finedust.databinding.ActivityDetailBinding

class DetailActivity : AppCompatActivity() {

    lateinit var binding: ActivityDetailBinding
    var detailList = arrayListOf<DetailDust>()
    val adapter: DetailAdapter by lazy {
        DetailAdapter(this, detailList)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_detail)

        getLocation()
        getAddressData()
        getInformation()

    }

    fun getLocation() {
        try {
            val locationManager =
                this.getSystemService(Context.LOCATION_SERVICE) as LocationManager

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
                viewModel.navigate(latitude, longitude)
                viewModel.address(latitude, longitude)

            } else {
                Log.d("CheckCurrentLocation", "현 위치 실패")
            }
        } catch (e: SecurityException) {
            Log.e("CheckCurrentLocationE", "현 위치 에러 발생")
        }
    }

    fun getAddressData() {
        viewModel.newAddress.observe(this) {
            Log.d("신주소", it.address_name)
            binding.locationStr.text = it.address_name
        }
        viewModel.preAddress.observe(this) {
            Log.d("구주소", it.address_name)
            binding.locationStr.text = it.address_name
        }
        viewModel.addressNull.observe(this) {
            Log.d("주소", "주소 값을 불러오지 못함.")
            Toast.makeText(this, "주소를 불러오지 못했습니다.", Toast.LENGTH_SHORT).show()
        }
    }

    fun getInformation() {
        viewModel.airConditionerItems.observe(this) {

            Log.d(
                "미세먼지",
                "통합 = ${it.khaiValue}, 미세먼지 = ${it.pm10Value}, 초미세먼지 = ${it.pm25Value}, 오존 = ${it.o3Value}, 이황 = ${it.so2Value}, 일산 = ${it.coValue}, 이산 = ${it.no2Value}"
            )
            Log.d(
                "미세먼지 단계",
                "통합 = ${it.khaiGrade}, 미세먼지 = ${it.pm10Grade}, 초미세먼지 = ${it.pm25Grade}, 오존 = ${it.o3Grade}, 이황 = ${it.so2Grade}, 일산 = ${it.coGrade}, 이산 = ${it.no2Grade}"
            )

            binding.khaiValue.text = it?.khaiValue
            binding.pm10Value.text = it?.pm10Value
            binding.pm25Value.text = it?.pm25Value
            binding.o3Value.text = it?.o3Value
            binding.so2Value.text = it?.so2Value
            binding.coValue.text = it?.coValue
            binding.no2Value.text = it?.no2Value

            val khaiGrade = it?.khaiGrade
            val pm10Grade = it?.pm10Grade
            val pm25Grade = it?.pm25Grade
            val o3Grade = it?.o3Grade
            val so2Grade = it?.so2Grade
            val coGrade = it?.coGrade
            val no2Grade = it?.no2Grade

            binding.khaiGrade.text = pmGrade(khaiGrade)
            khaiGradeImage(khaiGrade)

            binding.pm10Grade.text = pmGrade(pm10Grade)
            pm10GradeImage(pm10Grade)

            binding.pm25Grade.text = pmGrade(pm25Grade)
            pm25GradeImage(pm25Grade)

            binding.o3Grade.text = pmGrade(o3Grade)
            o3GradeImage(o3Grade)

        binding.detailRecyclerview.adapter = adapter
        val layoutManager = LinearLayoutManager(this)
        binding.detailRecyclerview.layoutManager = layoutManager

        val dustData = intent.getSerializableExtra("data") as List<DetailDust>
        val location = intent.getStringExtra("location")
        binding.locationStr.text = location
        Log.d("어댑터 데이터 넘기기", "$dustData")
        adapter.addDustList(dustData)

        binding.exit.setOnClickListener {
            finish()
        }
    }
}
