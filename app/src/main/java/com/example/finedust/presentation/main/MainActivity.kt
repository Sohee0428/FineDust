package com.example.finedust.presentation.main

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.finedust.R
import com.example.finedust.data.DetailAddress
import com.example.finedust.data.DetailDust
import com.example.finedust.data.FineDustDataBase
import com.example.finedust.data.date.LocalDate
import com.example.finedust.data.entity.FinedustEntity
import com.example.finedust.databinding.ActivityMainBinding
import com.example.finedust.presentation.AppDescriptionActivity
import com.example.finedust.presentation.FavoriteAdapter
import com.example.finedust.presentation.detail.DetailActivity
import com.example.finedust.presentation.location.LocationActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private val mainViewModel: MainViewModel by viewModels()
    lateinit var binding: ActivityMainBinding
    private var fineDustList = listOf<FinedustEntity>()
    private lateinit var getLocationResult: ActivityResultLauncher<Intent>
    lateinit var getAddressList: MutableList<DetailAddress>
    var db: FineDustDataBase? = null
    val adapter: FavoriteAdapter by lazy {
        FavoriteAdapter(this, getAddressList as ArrayList<FinedustEntity>)
        //            CoroutineScope(Dispatchers.IO).async {
//                viewModel.insertDB(it)
//            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.viewModel = mainViewModel
        binding.lifecycleOwner = this
        binding.date.text = LocalDate().str_date

        binding.favoriteList.adapter = adapter
        val layoutManager = LinearLayoutManager(this)
        binding.favoriteList.layoutManager = layoutManager

        permission()
        getAddressData()
        getAirConditionData()

        getLocationResult = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            if (it.resultCode == RESULT_OK) {
                val data = it.data?.getSerializableExtra("searchLocation") as DetailAddress
                Log.d("data", "${it.data}")
                changeLocation(data.x.toDouble(), data.y.toDouble())

                getAddressList.clear()
                getAddressList.add(data)
            }
        }

        binding.search.setOnClickListener {
            locationIntent()
        }
        binding.locationUpdateImg.setOnClickListener {
            getLocation()
            Toast.makeText(this, "위치를 업데이트 했습니다.", Toast.LENGTH_SHORT).show()
        }
        binding.background.setOnClickListener {
            detailIntent()
        }
        binding.menu.setOnClickListener {
            menuIntent()
        }
        binding.youAreHere.setOnClickListener {
            menuClose()
            getLocation()
        }
    }

    fun permission() {
        val requestPermissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
                if (isGranted) {
                    Log.d("위치 허가", "requestPermission Success")
                    getLocation()
                } else {
                    Log.d("위치 불허", "requestPermission Fail")
                    finish()
                    Toast.makeText(
                        this,
                        "위치 권한이 거절되어 사용할 수 없습니다.     [설정]으로 이동하여 권한을 허가해주어야 합니다.",
                        Toast.LENGTH_SHORT
                    ).show()
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
            }
        }
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

                binding.locationName.text = "현위치"

                mainViewModel.navigate(latitude, longitude)
                mainViewModel.address(latitude, longitude)
            } else {
                Log.d("CheckCurrentLocation", "현 위치 실패")
            }
        } catch (e: SecurityException) {
            Log.e("CheckCurrentLocationE", "현 위치 에러 발생")
        }
    }

    fun changeLocation(x: Double, y: Double) {
        mainViewModel.navigate(y, x)
        mainViewModel.address(y, x)
    }

    fun getAddressData() {
        mainViewModel.newAddress.observe(this) {
            Log.d("신주소", it.address_name)
            binding.locationStr.text = it.address_name
        }
        mainViewModel.preAddress.observe(this) {
            Log.d("구주소", it.address_name)
            binding.locationStr.text = it.address_name
        }
        mainViewModel.addressNull.observe(this) {
            Log.d("주소", "주소 값을 불러오지 못함.")
            Toast.makeText(this, "주소를 불러오지 못했습니다.", Toast.LENGTH_SHORT).show()
        }
    }

    fun getAirConditionData() {
        mainViewModel.airConditionerItems.observe(this) {
            Log.d("미세먼지", "통합 = ${it.khaiValue}, 미세먼지 = ${it.pm10Value}, 초미세먼지 = ${it.pm25Value}")

            binding.khaiValue.text = it?.khaiValue
            binding.pm10Value.text = it?.pm10Value
            binding.pm25Value.text = it?.pm25Value

            val khaiGrade = it?.khaiGrade
            val pm10Grade = pm10Grade(it?.pm10Value)
            val pm25Grade = pm25Grade(it?.pm25Value)

            Log.d("미세먼지 단계", "통합 = $khaiGrade, 미세먼지 = $pm10Grade, 초미세먼지 = $pm25Grade")

            val itemList = mutableListOf(
                it.khaiValue to it.khaiGrade,
                it.pm10Value to pm10Grade,
                it.pm25Value to pm25Grade,
                it.o3Value to it.o3Grade,
                it.so2Value to it.so2Grade,
                it.coValue to it.coGrade,
                it.no2Value to it.no2Grade
            )

            binding.pm10Grade.text = pmGrade(pm10Grade)
            binding.pm25Grade.text = pmGrade(pm25Grade)

            mainViewModel.detailDustList.clear()
            mainViewModel.detailDustList.addAll(DetailDust.getDetailDustList(itemList))
        }
        mainViewModel.airConditionerItemsNull.observe(this) {
            Toast.makeText(this, "정보를 불러오지 못했습니다.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun pm10Grade(str: String?): String {
        return when (str?.toInt()) {
            in 0..30 -> "1"
            in 31..50 -> "2"
            in 51..100 -> "3"
            null -> ""
            else -> "4"
        }
    }

    private fun pm25Grade(str: String?): String {
        return when (str?.toInt()) {
            in 0..15 -> "1"
            in 16..25 -> "2"
            in 26..50 -> "3"
            null -> ""
            else -> "4"
        }
    }

    private fun pmGrade(str: String?): String {
        return when (str) {
            "1" -> "좋음"
            "2" -> "보통"
            "3" -> "나쁨"
            "4" -> "매우 나쁨"
            else -> "오류"
        }
    }

    private fun locationIntent() {
        val intent = Intent(this, LocationActivity::class.java)
        getLocationResult.launch(intent)
    }

    private fun menuIntent() {
        binding.drawerLayout.openDrawer(GravityCompat.START)
    }

    private fun menuClose() {
        binding.drawerLayout.closeDrawer(GravityCompat.START)
    }

    private fun detailIntent() {
        Log.d("detailDate2", mainViewModel.detailDate)
        val intent = Intent(this, DetailActivity::class.java)
        intent.putExtra("data", mainViewModel.detailDustList)
        intent.putExtra("observatory", mainViewModel.detailObservatory)
        intent.putExtra("date", mainViewModel.detailDate)
        intent.putExtra("location", binding.locationStr.text.toString())
        startActivity(intent)
    }

    private fun appDescription() {
        val intent = Intent(this, AppDescriptionActivity::class.java)
        startActivity(intent)
    }

}