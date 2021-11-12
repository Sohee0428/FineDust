package com.example.finedust.presentation.detail

import android.content.Context
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.finedust.R
import com.example.finedust.databinding.ActivityDetailBinding
import com.example.finedust.presentation.MainViewModel

class DetailActivity : AppCompatActivity() {

    private val viewModel: MainViewModel by viewModels()

    lateinit var binding: ActivityDetailBinding

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

            binding.so2Grade.text = pmGrade(so2Grade)
            so2GradeImage(so2Grade)

            binding.coGrade.text = pmGrade(coGrade)
            coGradeImage(coGrade)

            binding.no2Grade.text = pmGrade(no2Grade)
            no2GradeImage(no2Grade)
        }
        viewModel.airConditionerItemsNull.observe(this) {
            Toast.makeText(this, "정보를 불러오지 못했습니다.", Toast.LENGTH_SHORT).show()
        }
    }

    fun pmGrade(str: String?): String {
        return when (str) {
            "1" -> "좋음"
            "2" -> "보통"
            "3" -> "나쁨"
            "4" -> "매우 나쁨"
            else -> "오류"
        }
    }

    fun khaiGradeImage(str: String?) {

        when (str) {
            "1" -> binding.khaiStateImage.setImageResource(R.drawable.perfectbear)
            "2" -> binding.khaiStateImage.setImageResource(R.drawable.goodbear)
            "3" -> binding.khaiStateImage.setImageResource(R.drawable.sosobear)
            "4" -> binding.khaiStateImage.setImageResource(R.drawable.badbear)
            else -> binding.khaiStateImage.setImageResource(R.drawable.ic_baseline_error_24)
        }
    }

    fun pm10GradeImage(str: String?) {
        when (str) {
            "1" -> binding.pm10StateImage.setImageResource(R.drawable.perfectbear)
            "2" -> binding.pm10StateImage.setImageResource(R.drawable.goodbear)
            "3" -> binding.pm10StateImage.setImageResource(R.drawable.sosobear)
            "4" -> binding.pm10StateImage.setImageResource(R.drawable.badbear)
            else -> binding.pm10StateImage.setImageResource(R.drawable.ic_baseline_error_24)
        }
    }

    fun pm25GradeImage(str: String?) {
        when (str) {
            "1" -> binding.pm25StateImage.setImageResource(R.drawable.perfectbear)
            "2" -> binding.pm25StateImage.setImageResource(R.drawable.goodbear)
            "3" -> binding.pm25StateImage.setImageResource(R.drawable.sosobear)
            "4" -> binding.pm25StateImage.setImageResource(R.drawable.badbear)
            else -> binding.pm25StateImage.setImageResource(R.drawable.ic_baseline_error_24)
        }
    }

    fun o3GradeImage(str: String?) {
        when (str) {
            "1" -> binding.o3StateImage.setImageResource(R.drawable.perfectbear)
            "2" -> binding.o3StateImage.setImageResource(R.drawable.goodbear)
            "3" -> binding.o3StateImage.setImageResource(R.drawable.sosobear)
            "4" -> binding.o3StateImage.setImageResource(R.drawable.badbear)
            else -> binding.o3StateImage.setImageResource(R.drawable.ic_baseline_error_24)
        }
    }

    fun so2GradeImage(str: String?) {
        when (str) {
            "1" -> binding.so2StateImage.setImageResource(R.drawable.perfectbear)
            "2" -> binding.so2StateImage.setImageResource(R.drawable.goodbear)
            "3" -> binding.so2StateImage.setImageResource(R.drawable.sosobear)
            "4" -> binding.so2StateImage.setImageResource(R.drawable.badbear)
            else -> binding.so2StateImage.setImageResource(R.drawable.ic_baseline_error_24)
        }
    }

    fun coGradeImage(str: String?) {
        when (str) {
            "1" -> binding.coStateImage.setImageResource(R.drawable.perfectbear)
            "2" -> binding.coStateImage.setImageResource(R.drawable.goodbear)
            "3" -> binding.coStateImage.setImageResource(R.drawable.sosobear)
            "4" -> binding.coStateImage.setImageResource(R.drawable.badbear)
            else -> binding.coStateImage.setImageResource(R.drawable.ic_baseline_error_24)
        }
    }

    fun no2GradeImage(str: String?) {
        when (str) {
            "1" -> binding.no2StateImage.setImageResource(R.drawable.perfectbear)
            "2" -> binding.no2StateImage.setImageResource(R.drawable.goodbear)
            "3" -> binding.no2StateImage.setImageResource(R.drawable.sosobear)
            "4" -> binding.no2StateImage.setImageResource(R.drawable.badbear)
            else -> binding.no2StateImage.setImageResource(R.drawable.ic_baseline_error_24)
        }
    }


}
