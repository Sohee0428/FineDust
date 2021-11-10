package com.example.finedust.presentation.main

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.example.finedust.R
import com.example.finedust.data.date.LocalDate
import com.example.finedust.databinding.ActivityMainBinding
import com.gun0912.tedpermission.PermissionListener

class MainActivity : AppCompatActivity() {

    private val mainViewModel: MainViewModel by viewModels()

    lateinit var binding: ActivityMainBinding

    lateinit var pagerAdapter: FragmentAdapter

    private val MIN_SCALE = 0.85f
    private val MIN_ALPHA = 0.5f

    var address = arrayListOf<AddressResponse>()
    val adapter: FinedustAdapter by lazy {
        FinedustAdapter(this, address)
    }

    val data = mutableListOf<FinedustDao>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.viewModel = mainViewModel

        binding.date.text = LocalDate().str_date
        pagerAdapter = ViewPagerAdapter(this)
        binding.viewPager.adapter = pagerAdapter
        binding.viewPager.setPageTransformer(ZoomOutPageTransformer())

        finedustFragment = FinedustFragment()
        pagerAdapter.addFragment(finedustFragment)


        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            val text = position + 1
            tab.text = "$text"
        }.attach()
    }

    inner class ZoomOutPageTransformer : ViewPager2.PageTransformer {
        override fun transformPage(view: View, position: Float) {
            view.apply {
                val pageWidth = width
                val pageHeight = height
                when {
                    position < -1 -> { // [-Infinity,-1)
                        // This page is way off-screen to the left.
                        alpha = 0f
                    }
                    position <= 1 -> { // [-1,1]
                        // Modify the default slide transition to shrink the page as well
                        val scaleFactor = Math.max(MIN_SCALE, 1 - Math.abs(position))
                        val vertMargin = pageHeight * (1 - scaleFactor) / 2
                        val horzMargin = pageWidth * (1 - scaleFactor) / 2
                        translationX = if (position < 0) {
                            horzMargin - vertMargin / 2
                        } else {
                            horzMargin + vertMargin / 2
                        }

                        // Scale the page down (between MIN_SCALE and 1)
                        scaleX = scaleFactor
                        scaleY = scaleFactor

                        // Fade the page relative to its size.
                        alpha = (MIN_ALPHA +
                                (((scaleFactor - MIN_SCALE) / (1 - MIN_SCALE)) * (1 - MIN_ALPHA)))
                    }
                    else -> { // (1,+Infinity]
                        // This page is way off-screen to the right.
                        alpha = 0f
                    }
                }
            }
        }
    }

    fun permission() {
        val requestPermissionLauncher =
            registerForActivityResult(
                ActivityResultContracts.RequestPermission()
            ) { isGranted: Boolean ->
                if (isGranted) {
                    Log.d("위치 허가", "requestPermission Success")
                    getLocation()
                } else {
                    Log.d("위치 불허", "requestPermission Fail")
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

    fun getAddressData() {
        mainViewModel.newAddress.observe(this) {
         Log.d("신주소", it.address_name)
                activityDataBinding.locationData.text = it.address_name
        }
        mainViewModel.preAddress.observe(this) {
            Log.d("구주소", it.address_name)
            activityDataBinding.locationData.text = it.address_name
        }
        mainViewModel.addressNull.observe(this) {
            Log.d("주소", "주소 값을 불러오지 못함.")
            Toast.makeText(this, "주소를 불러오지 못했습니다.", Toast.LENGTH_SHORT).show()
        }
    }
}