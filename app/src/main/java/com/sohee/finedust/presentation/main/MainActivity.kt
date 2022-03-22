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
import androidx.lifecycle.lifecycleScope
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
import com.sohee.finedust.showToast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
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
        binding.lifecycleOwner = this
        binding.date.text = LocalDate().str_date

        initContactFavoriteAdapter()
        initFavoriteList()
        initCollector()
        permission()
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

    private fun initCollector() {
        lifecycleScope.launch {
            mainViewModel.mainUiEvent.collect {
                when(it) {
                    MainViewModel.MainUiEvents.GPSPermissionFail -> TODO()
                    MainViewModel.MainUiEvents.GPSPermissionSuccess -> TODO()
                    is MainViewModel.MainUiEvents.ShowErrorMessageToast -> showToast(it.message)
                    is MainViewModel.MainUiEvents.ShowNullMessageToast -> showToast(it.message)
                    MainViewModel.MainUiEvents.CheckFavoriteState -> checkFavoriteState()
                    MainViewModel.MainUiEvents.ClickAppDescription -> clickAppDescription()
                    MainViewModel.MainUiEvents.ClickDeleteAllFavoriteList -> clickDeleteAllFavoriteImage()
                    MainViewModel.MainUiEvents.ClickDetailIntent -> clickDetailIntent()
                    MainViewModel.MainUiEvents.ClickLocationIntent -> clickLocationIntent()
                    MainViewModel.MainUiEvents.ClickLocationUpdate -> clickLocationUpdateImg()
                    MainViewModel.MainUiEvents.ClickMenuIntent -> clickMenuIntent()
                    MainViewModel.MainUiEvents.ClickYouAreHere -> clickYouAreHere()
                }
            }
        }
    }

    private fun permission() {
        val requestPermissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
                if (isGranted) {
                    getLocation()
                } else {
                    finish()
                    Toast.makeText(
                        this,
                        getString(R.string.location_permission),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED -> {
                getLocation()
            }
            else -> {
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
            }
        } catch (e: SecurityException) {
            Log.e("CheckCurrentLocationE", "현 위치 에러 발생")
        }
    }

    private fun getAirConditionData() {
        mainViewModel.favoriteList.observe(this) {
            adapter.addList(it)
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