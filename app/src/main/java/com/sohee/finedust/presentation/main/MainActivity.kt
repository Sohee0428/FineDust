package com.sohee.finedust.presentation.main

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
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
    private val mainViewModel: MainViewModel by viewModels()
    private lateinit var appUpdateManager: AppUpdateManager
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
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                mainViewModel.getLocation()
            } else {
                showDialogToAllowPermission()
            }
        }
    private var backWait: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.viewModel = mainViewModel
        binding.lifecycleOwner = this
        binding.date.text = LocalDate().str_date

        initAppUpdate()
        initContactFavoriteAdapter()
        initFavoriteList()
        initCollector()
        initPermission()
        getAirConditionData()
        getLocationResult()
    }

    private fun initAppUpdate() {
        appUpdateManager = AppUpdateManagerFactory.create(this)

        appUpdateManager?.let {
            it.appUpdateInfo.addOnSuccessListener { appUpdateInfo ->

                if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                    && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)
                ) {
                    appUpdateManager?.startUpdateFlowForResult(
                        appUpdateInfo,
                        AppUpdateType.FLEXIBLE,
                        this,
                        100
                    )
                }
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
                when (it) {
                    MainViewModel.MainUiEvents.GPSPermissionFail -> initPermission()
                    is MainViewModel.MainUiEvents.ShowErrorMessageToast -> showToast(it.message)
                    is MainViewModel.MainUiEvents.ShowNullMessageToast -> showToast(it.message)
                    MainViewModel.MainUiEvents.CheckFavoriteState -> checkFavoriteState()
                    MainViewModel.MainUiEvents.ClickAppDescription -> clickAppDescription()
                    MainViewModel.MainUiEvents.ClickDeleteAllFavoriteList -> clickDeleteAllFavoriteImage()
                    MainViewModel.MainUiEvents.ClickDetailIntent -> clickDetailIntent()
                    MainViewModel.MainUiEvents.ClickLocationIntent -> clickLocationIntent()
                    MainViewModel.MainUiEvents.ClickMenuIntent -> clickMenuIntent()
                    MainViewModel.MainUiEvents.ClickMenuUpdateLocation -> menuClose()
                    MainViewModel.MainUiEvents.CurrentLocation -> currentLocationUpdate()
                }
            }
        }
    }

    private fun currentLocationUpdate() {
        binding.locationName.text = "현위치"
        binding.locationName.visibility = View.VISIBLE
        binding.favoriteImage.visibility = View.GONE
    }

    private fun initPermission() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        } else {
            mainViewModel.getLocation()
        }
    }

    private fun showDialogToAllowPermission() {
        AlertDialog.Builder(this)
            .setTitle("권한 허용")
            .setMessage("권한 허용을 거부하시겠습니까? \n 거부를 누르면 앱이 종료됩니다.")
            .setPositiveButton("허용") { _, _ ->
                requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
            .setNegativeButton("거부") { _, _ ->
                finish()
                showToast(getString(R.string.location_permission))
            }
            .show()
    }

    private fun getAirConditionData() {
        mainViewModel.favoriteList.observe(this) {
            adapter.addList(it)
        }
    }

    private fun changeLocation() {
        val address = mainViewModel.mainAddress
        mainViewModel.changeLocation()
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

    private fun checkFavoriteState() {
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
        AlertDialog.Builder(this)
            .setTitle("즐겨찾기 추가")
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
        AlertDialog.Builder(this)
            .setTitle("즐겨찾기 삭제")
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
        AlertDialog.Builder(this)
            .setTitle("즐겨찾기 목록 삭제")
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

    private fun clickLocationIntent() {
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

    private fun clickDeleteAllFavoriteImage() {
        alertToDeleteFavoriteList()
        menuClose()
    }

    private fun clickDetailIntent() {
        val intent = Intent(this, DetailActivity::class.java)
        intent.putExtra("data", mainViewModel.detailDustList)
        intent.putExtra("observatory", mainViewModel.detailObservatory)
        intent.putExtra("date", mainViewModel.detailDate.value)
        intent.putExtra("location", binding.locationStr.text.toString())
        startActivity(intent)
    }

    private fun clickAppDescription() {
        val intent = Intent(this, AppDescriptionActivity::class.java)
        startActivity(intent)
        menuClose()
    }

    override fun onDestroy() {
        super.onDestroy()
        mainViewModel.stopUpdatingLocation()
    }

    override fun onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawers()
        } else {
            if (System.currentTimeMillis() > backWait + 2500) {
                backWait = System.currentTimeMillis()
                showToast(getString(R.string.push_backbutton_one_more_time))
                return
            } else if (System.currentTimeMillis() <= backWait + 2500) {
                finishAffinity()
            }
        }
    }
}