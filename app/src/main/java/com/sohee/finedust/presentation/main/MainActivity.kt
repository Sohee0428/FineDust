package com.sohee.finedust.presentation.main

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.View
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
import com.sohee.finedust.R
import com.sohee.finedust.data.DetailAddress
import com.sohee.finedust.data.DetailDust
import com.sohee.finedust.data.date.LocalDate
import com.sohee.finedust.data.entity.FinedustEntity
import com.sohee.finedust.databinding.ActivityMainBinding
import com.sohee.finedust.presentation.AppDescriptionActivity
import com.sohee.finedust.presentation.detail.DetailActivity
import com.sohee.finedust.presentation.location.LocationActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var getLocationResultData: ActivityResultLauncher<Intent>
    private var locationListener: LocationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
            val lat = location.latitude
            val lng = location.longitude
            Log.d("Gps", "Lat: $lat, lon: $lng")
        }

        override fun onProviderDisabled(provider: String) {}
        override fun onProviderEnabled(provider: String) {}
        override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
    }
    private val mainViewModel: MainViewModel by viewModels()
    private val adapter: FavoriteAdapter by lazy {
        FavoriteAdapter({
            mainViewModel.mainAddress = DetailAddress(it.address, it.x, it.y, true)
            changeLocation()
            isCheckFavoriteImage(true)
            menuClose()
        },
            {
                deleteFavoriteItem(it.address)
                menuClose()
            })
    }
    private var backWait: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.viewModel = mainViewModel
        binding.mainActivity = this
        binding.lifecycleOwner = this
        binding.date.text = LocalDate().str_date

        initContactFavoriteAdapter()
        initFavoriteList()
        permission()
        getAddressData()
        getAirConditionData()
        getLocationResult()
    }

    override fun onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawers()
        } else {
            if (System.currentTimeMillis() > backWait + 2500) {
                backWait = System.currentTimeMillis()
                Toast.makeText(this, "종료하시려면 뒤로가기를 한번 더 눌러주세요.", Toast.LENGTH_SHORT).show()
                return
            }
            else if (System.currentTimeMillis() <= backWait + 2500) {
                finishAffinity()
            }
        }
    }

    private fun initContactFavoriteAdapter() {
        binding.favoriteList.adapter = adapter
        val layoutManager = LinearLayoutManager(this)
        binding.favoriteList.layoutManager = layoutManager
    }

    private fun initFavoriteList() {
        CoroutineScope(Dispatchers.Main).launch {
            mainViewModel.getFavoriteAddressList()
        }
    }

    private fun permission() {
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
                        "위치 권한이 거절되어 사용할 수 없습니다.     [설정]으로 이동하여 권한 허용을 해주어야 합니다.",
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

    private fun getLocation() {
        try {
            val locationManager =
                this.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            val locationProvider = LocationManager.GPS_PROVIDER
            val currentLatLng: Location? = locationManager.getLastKnownLocation(locationProvider)

            locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                3000L,
                30F,
                locationListener
            )

            if (currentLatLng != null) {
                val latitude = currentLatLng.latitude
                val longitude = currentLatLng.longitude
                Log.d("CheckCurrentLocation", "내 위치 $latitude, $longitude")

                binding.locationName.text = "현위치"
                binding.locationName.visibility = View.VISIBLE
                binding.favoriteImage.visibility = View.GONE

                mainViewModel.navigate(latitude, longitude)
                mainViewModel.address(latitude, longitude)
            } else {
                Toast.makeText(this, "현위치를 불러오지 못하였습니다.", Toast.LENGTH_SHORT).show()
                Log.d("CheckCurrentLocation", "현 위치 실패")
            }
        } catch (e: SecurityException) {
            Log.e("CheckCurrentLocationE", "현 위치 에러 발생")
        }
    }

    private fun getAddressData() {
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

    private fun getAirConditionData() {
        mainViewModel.airConditionerItems.observe(this) {
            Log.d("미세먼지", "통합 = ${it.khaiValue}, 미세먼지 = ${it.pm10Value}, 초미세먼지 = ${it.pm25Value}")

            binding.khaiValue.text = it?.khaiValue
            binding.pm10Value.text = it?.pm10Value
            binding.pm25Value.text = it?.pm25Value

            val khaiGrade = it?.khaiGrade
            val pm10Grade = pm10Grade(it?.pm10Value)
            val pm25Grade = pm25Grade(it?.pm25Value)
            Log.d("미세먼지 단계", "통합 = $khaiGrade, 미세먼지 = $pm10Grade, 초미세먼지 = $pm25Grade")

            binding.pm10Grade.text = pmGrade(pm10Grade)
            binding.pm25Grade.text = pmGrade(pm25Grade)

            val itemList = mutableListOf(
                it.khaiValue to it.khaiGrade,
                it.pm10Value to pm10Grade,
                it.pm25Value to pm25Grade,
                it.o3Value to it.o3Grade,
                it.so2Value to it.so2Grade,
                it.coValue to it.coGrade,
                it.no2Value to it.no2Grade
            )

            mainViewModel.detailDustList.clear()
            mainViewModel.detailDustList.addAll(DetailDust.getDetailDustList(itemList))
        }
        mainViewModel.airConditionerItemsNull.observe(this) {
            Toast.makeText(this, "정보를 불러오지 못했습니다.", Toast.LENGTH_SHORT).show()
        }
        mainViewModel.observatoryError.observe(this) {
            Toast.makeText(
                this,
                "미세먼지 정보를 불러오지 못하였습니다. 잠시후 다시 시도해주시기 바랍니다.",
                Toast.LENGTH_SHORT
            ).show()
        }
        mainViewModel.favoriteList.observe(this) {
            adapter.addList(it)
        }
    }

    private fun pm10Grade(str: String?): String {
        return when (str?.toIntOrNull()) {
            in 0..30 -> "1"
            in 31..50 -> "2"
            in 51..100 -> "3"
            null -> ""
            else -> "4"
        }
    }

    private fun pm25Grade(str: String?): String {
        return when (str?.toIntOrNull()) {
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
            "" -> "로딩 중"
            else -> "오류"
        }
    }

    private fun changeLocation() {
        val address = mainViewModel.mainAddress
        mainViewModel.navigate(address.y.toDouble(), address.x.toDouble())
        binding.locationStr.text = address.address
        binding.locationName.visibility = View.GONE
        binding.favoriteImage.visibility = View.VISIBLE
        isCheckFavoriteImage(address.isFavorite)
    }

    private fun addLocation() {
        val addedFavoriteItem = mainViewModel.mainAddress.copy(isFavorite = true)
        mainViewModel.mainAddress = addedFavoriteItem
        CoroutineScope(Dispatchers.IO).launch {
            mainViewModel.insertDB()
        }
    }

    private fun getLocationResult() {
        getLocationResultData = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            if (it.resultCode == RESULT_OK) {
                val data = it.data?.getSerializableExtra("searchLocation") as DetailAddress
                Log.d("getLocationResult", "${it.data}")

                if (mainViewModel.favoriteListCheck(data.address)) {
                    val addedFavoriteItem = data.copy(isFavorite = true)
                    mainViewModel.mainAddress = addedFavoriteItem
                } else {
                    mainViewModel.mainAddress = data
                }
                changeLocation()
            }
        }
    }

    fun checkFavoriteState() {
        if (mainViewModel.mainAddress.isFavorite) {
            alertToDeleteLocation(mainViewModel.mainAddress.address)
        } else {
            alertToAddLocation()
        }
    }

    private fun isCheckFavoriteImage(checkFavoriteList: Boolean) {
        if (checkFavoriteList) {
            binding.favoriteImage.setImageResource(R.drawable.ic_baseline_star_24)
        } else {
            binding.favoriteImage.setImageResource(R.drawable.ic_baseline_star_border_24)
        }
    }

    private fun deleteFavoriteItem(data: String) {
        val addedFavoriteItem = mainViewModel.mainAddress.copy(isFavorite = false)
        mainViewModel.mainAddress = addedFavoriteItem
        CoroutineScope(Dispatchers.IO).launch {
            mainViewModel.deleteFavoriteItem(data)
        }
    }

    private fun alertToAddLocation() {
        val addLocationDust = AlertDialog.Builder(this)
        addLocationDust.setTitle("즐겨찾기 추가")
            .setMessage("즐겨찾기 목록에 장소를 추가하시겠습니까?")
            .setPositiveButton("추가") { _, _ ->
                addLocation()
                Toast.makeText(this, "즐겨찾기에 추가되었습니다.", Toast.LENGTH_SHORT).show()
                isCheckFavoriteImage(true)
            }
            .setNegativeButton("취소", null)
            .show()
    }

    private fun alertToDeleteLocation(data: String) {
        val addLocationDust = AlertDialog.Builder(this)
        addLocationDust.setTitle("즐겨찾기 삭제")
            .setMessage("즐겨찾기 목록에서 장소를 삭제하시겠습니까?")
            .setPositiveButton("삭제") { _, _ ->
                deleteFavoriteItem(data)
                Toast.makeText(this, "즐겨찾기에 삭제되었습니다.", Toast.LENGTH_SHORT).show()
                isCheckFavoriteImage(false)
            }
            .setNegativeButton("취소", null)
            .show()
    }

    private fun alertToDeleteFavoriteList() {
        val addLocationDust = AlertDialog.Builder(this)
        addLocationDust.setTitle("즐겨찾기 목록 삭제")
            .setMessage("즐겨찾기 목록에 추가된 장소를 모두 삭제하시겠습니까?")
            .setPositiveButton("삭제") { _, _ ->
                CoroutineScope(Dispatchers.IO).launch {
                    mainViewModel.deleteAllFavoriteList()
                }
                menuClose()
                Toast.makeText(this, "즐겨찾기 목록이 모두 삭제되었습니다.", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("취소", null)
            .show()
    }

    fun clickLocationIntent() {
        val intent = Intent(this, LocationActivity::class.java)
        getLocationResultData.launch(intent)
    }

    private fun menuClose() {
        binding.drawerLayout.closeDrawer(GravityCompat.START)
    }

    fun clickMenuIntent() {
        binding.drawerLayout.openDrawer(GravityCompat.START)
        CoroutineScope(Dispatchers.Main).launch {
            mainViewModel.getFavoriteAddressList()
        }

    }

    fun clickLocationUpdateImg() {
        getLocation()
        Toast.makeText(this, "위치를 업데이트 했습니다.", Toast.LENGTH_SHORT).show()
    }

    fun clickDeleteAllFavoriteImage() {
        alertToDeleteFavoriteList()
        menuClose()
    }

    fun clickDetailIntent() {
        Log.d("detailDate2", mainViewModel.detailDate)
        val intent = Intent(this, DetailActivity::class.java)
        intent.putExtra("data", mainViewModel.detailDustList)
        intent.putExtra("observatory", mainViewModel.detailObservatory)
        intent.putExtra("date", mainViewModel.detailDate)
        intent.putExtra("location", binding.locationStr.text.toString())
        startActivity(intent)
    }

    fun clickAppDescription() {
        val intent = Intent(this, AppDescriptionActivity::class.java)
        startActivity(intent)
        menuClose()
    }

    fun clickYouAreHere() {
        menuClose()
        getLocation()
    }
}