package com.sohee.finedust.presentation.main

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.View
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
import com.google.android.gms.location.*
import com.sohee.finedust.R
import com.sohee.finedust.data.DetailAddress
import com.sohee.finedust.data.date.LocalDate
import com.sohee.finedust.databinding.ActivityMainBinding
import com.sohee.finedust.logHelper
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
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private var latitude: Double = 0.0
    private var longitude: Double = 0.0
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
                when (it) {
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
                    MainViewModel.MainUiEvents.ClickMenuUpdateLocation -> clickMenuUpdateLocation()
                }
            }
        }
    }

    private fun permission() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        val requestPermissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
                if (isGranted) {
                    fusedLocationProviderClient.lastLocation.addOnSuccessListener { location: Location? ->
                        if (location != null) {
                            latitude = location.latitude
                            longitude = location.longitude
                            Log.d(
                                "Test", "GPS Location Latitude: $latitude" +
                                        ", Longitude: $longitude"
                            )
                            getLocation()
                        }
                    }
                } else {
                    finish()
                    showToast(getString(R.string.location_permission))
                }
            }

        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED -> {
                fusedLocationProviderClient.lastLocation.addOnSuccessListener { location: Location? ->
                    if (location != null) {
                        latitude = location.latitude
                        longitude = location.longitude
                        Log.d(
                            "Test", "GPS Location Latitude: $latitude" +
                                    ", Longitude: $longitude"
                        )
                        getLocation()
                    }
                }
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
            val locationRequest = LocationRequest.create()
            locationCallback = object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult) {
                    if (locationResult == null) {
                        showToast("현위치를 불러오지 못하였습니다.")
                        return
                    }
                    for (location in locationResult.locations) {
                        if (location != null) {
                            latitude = location.latitude
                            longitude = location.longitude
                            Log.d(
                                "Test111", "GPS Location changed, Latitude: $latitude" +
                                        ", Longitude: $longitude"
                            )

                            binding.locationName.text = "현위치"
                            binding.locationName.visibility = View.VISIBLE
                            binding.favoriteImage.visibility = View.GONE

                            mainViewModel.navigate(latitude, longitude)
                            mainViewModel.address(latitude, longitude)
                        }
                    }
                }
            }
            locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            fusedLocationProviderClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            )
            logHelper("latitude : $latitude / longitude : $longitude")
            showToast("$latitude, $longitude")

//            val builder = LocationSettingsRequest.Builder()
//                .addLocationRequest(locationRequest!!)
//            val client: SettingsClient = LocationServices.getSettingsClient(this)
//            val task: Task<LocationSettingsResponse> = client.checkLocationSettings(builder.build())
//
//            task.addOnSuccessListener { locationSettingsResponse ->
//                val latitude = locationSettingsResponse.locationSettingsStates.isLocationPresent
//            }
//
//            task.addOnFailureListener { exception ->
//                if (exception is ResolvableApiException) {
//                    // Location settings are not satisfied, but this can be fixed
//                    // by showing the user a dialog.
//                    try {
//                        // Show the dialog by calling startResolutionForResult(),
//                        // and check the result in onActivityResult().
//                        exception.startResolutionForResult(
//                            this@MainActivity,
//                            0
//                        )
//                    } catch (sendEx: IntentSender.SendIntentException) {
//                        // Ignore the error.
//                    }
//                }
//            }

//            if (currentLatLng != null) {
//                val latitude = currentLatLng.latitude
//                val longitude = currentLatLng.longitude
//                Log.d("CheckCurrentLocation", "내 위치 $latitude, $longitude")
//
//                binding.locationName.text = "현위치"
//                binding.locationName.visibility = View.VISIBLE
//                binding.favoriteImage.visibility = View.GONE
//
//                mainViewModel.navigate(latitude, longitude)
//                mainViewModel.address(latitude, longitude)
//            } else {
//                showToast("현위치를 불러오지 못하였습니다.")
//            }
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
                showToast("즐겨찾기에 추가되었습니다.")
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
                showToast("즐겨찾기에 삭제되었습니다.")
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
                showToast("즐겨찾기 목록이 모두 삭제되었습니다.")
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

    private fun clickMenuIntent() {
        binding.drawerLayout.openDrawer(GravityCompat.START)
        CoroutineScope(Dispatchers.Main).launch {
            mainViewModel.getFavoriteAddressList()
        }

    }

    private fun clickLocationUpdateImg() {
        getLocation()
        showToast("위치를 업데이트 했습니다.")
    }

    private fun clickDeleteAllFavoriteImage() {
        alertToDeleteFavoriteList()
        menuClose()
    }

    private fun clickDetailIntent() {
        Log.d("detailDate2", mainViewModel.detailDate)
        val intent = Intent(this, DetailActivity::class.java)
        intent.putExtra("data", mainViewModel.detailDustList)
        intent.putExtra("observatory", mainViewModel.detailObservatory)
        intent.putExtra("date", mainViewModel.detailDate)
        intent.putExtra("location", binding.locationStr.text.toString())
        startActivity(intent)
    }

    private fun clickAppDescription() {
        val intent = Intent(this, AppDescriptionActivity::class.java)
        startActivity(intent)
        menuClose()
    }

    private fun clickMenuUpdateLocation() {
        menuClose()
        getLocation()
        showToast("위치를 업데이트 했습니다.")
    }

    override fun onDestroy() {
        super.onDestroy()
        fusedLocationProviderClient.removeLocationUpdates(locationCallback)
    }

    override fun onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawers()
        } else {
            if (System.currentTimeMillis() > backWait + 2500) {
                backWait = System.currentTimeMillis()
                showToast("종료하시려면 뒤로가기를 한번 더 눌러주세요.")
                return
            }
            else if (System.currentTimeMillis() <= backWait + 2500) {
                finishAffinity()
            }
        }
    }
}