package com.example.finedust.presentation.fragment

import android.content.Context
import android.content.Intent
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.finedust.R
import com.example.finedust.data.DetailDust
import com.example.finedust.databinding.FragmentFinedustBinding
import com.example.finedust.presentation.MainViewModel
import com.example.finedust.presentation.detail.DetailActivity

class FinedustFragment : Fragment() {

    lateinit var binding: FragmentFinedustBinding
    private val viewModel: MainViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFinedustBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getAirConditionData()
        getAddressData()

        binding.locationUpdateImg.setOnClickListener {
            getLocation()
            Toast.makeText(context, "위치를 업데이트 했습니다.", Toast.LENGTH_SHORT).show()
        }

        binding.background.setOnClickListener {
            detailInformation()
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        getLocation()
    }

    fun getLocation() {
        try {
            if (activity == null) return

            val locationManager =
                requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager
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

                viewModel.navigate(latitude, longitude)
                viewModel.address(latitude, longitude)
            } else {
                Log.d("CheckCurrentLocation", "현 위치 실패")
            }
        } catch (e: SecurityException) {
            Log.e("CheckCurrentLocationE", "현 위치 에러 발생")
        }
    }

    fun getAirConditionData() {
        viewModel.airConditionerItems.observe(viewLifecycleOwner) {
            Log.d("미세먼지", "통합 = ${it.khaiValue}, 미세먼지 = ${it.pm10Value}, 초미세먼지 = ${it.pm25Value}")
            Log.d(
                "미세먼지 단계",
                "통합 = ${it.khaiGrade}, 미세먼지 = ${it.pm10Grade}, 초미세먼지 = ${it.pm25Grade}"
            )

            val itemList = mutableListOf(
                it.khaiValue,
                it.pm10Value,
                it.pm25Value,
                it.o3Value,
                it.so2Value,
                it.coValue,
                it.no2Value
            )
            val list = DetailDust.getDetailDustList(itemList)

            binding.khaiValue.text = it?.khaiValue
            binding.pm10Value.text = it?.pm10Value
            binding.pm25Value.text = it?.pm25Value

            val pm10Grade = it?.pm10Grade
            val pm25Grade = it?.pm25Grade
            val khaiGrade = it?.khaiGrade

            binding.khaiGrade.text = pmGrade(khaiGrade)
            changekhaiTextColor(khaiGrade)

            binding.pm10Grade.text = pmGrade(pm10Grade)
            changepm10TextColor(pm10Grade)

            binding.pm25Grade.text = pmGrade(pm25Grade)
            changepm25TextColor(pm25Grade)
        }
        viewModel.airConditionerItemsNull.observe(viewLifecycleOwner) {
            Toast.makeText(context, "정보를 불러오지 못했습니다.", Toast.LENGTH_SHORT).show()
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

    fun changekhaiTextColor(str: String?) {
        when (str) {
            "1" -> binding.khaiGrade.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.blueTxt
                )
            )
            "2" -> binding.khaiGrade.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.greenTxt
                )
            )
            "3" -> binding.khaiGrade.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.orangeTxt
                )
            )
            "4" -> binding.khaiGrade.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.redTxt
                )
            )
            else -> return
        }
    }

    fun changepm10TextColor(str: String?) {
        when (str) {
            "1" -> binding.pm10Grade.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.blueTxt
                )
            )
            "2" -> binding.pm10Grade.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.greenTxt
                )
            )
            "3" -> binding.pm10Grade.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.orangeTxt
                )
            )
            "4" -> binding.pm10Grade.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.redTxt
                )
            )
            else -> return
        }
    }

    fun changepm25TextColor(str: String?) {
        when (str) {
            "1" -> binding.pm25Grade.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.blueTxt
                )
            )
            "2" -> binding.pm25Grade.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.greenTxt
                )
            )
            "3" -> binding.pm25Grade.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.orangeTxt
                )
            )
            "4" -> binding.pm25Grade.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.redTxt
                )
            )
            else -> return
        }
    }

    fun getAddressData() {
        viewModel.newAddress.observe(viewLifecycleOwner) {
            Log.d("신주소", it.address_name)
            binding.locationStr.text = it.address_name
        }
        viewModel.preAddress.observe(viewLifecycleOwner) {
            Log.d("구주소", it.address_name)
            binding.locationStr.text = it.address_name
        }
        viewModel.addressNull.observe(viewLifecycleOwner) {
            Log.d("주소", "주소 값을 불러오지 못함.")
            Toast.makeText(context, "주소를 불러오지 못했습니다.", Toast.LENGTH_SHORT).show()
        }
    }

//    fun getBackground(str: String) {
//        when (str) {
//            "1" -> binding.background.setBackgroundResource(R.drawable.perfect_background)
//            "2" -> binding.background.setBackgroundResource(R.drawable.good_background)
//            "3" -> binding.background.setBackgroundResource(R.drawable.soso_background)
//            "4" -> binding.background.setBackgroundResource(R.drawable.bad_background)
//            else -> binding.background.setBackgroundResource(R.drawable.fragment_background)
//        }
//    }

    override fun onDestroy() {
        super.onDestroy()
        binding
    }

    fun detailInformation() {
        val intent = Intent(context, DetailActivity::class.java)
        startActivity(intent)
    }
}