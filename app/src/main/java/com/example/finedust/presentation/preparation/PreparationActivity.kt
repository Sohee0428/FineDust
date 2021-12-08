package com.example.finedust.presentation.preparation

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.viewpager2.widget.ViewPager2
import com.example.finedust.R
import com.example.finedust.databinding.ActivityPreparationBinding
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class PreparationActivity : AppCompatActivity() {

    lateinit var binding: ActivityPreparationBinding
    lateinit var viewPager: ViewPager2
    lateinit var tabLayout: TabLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_preparation)

        binding.viewPager.adapter = ViewPagerAdapter(getList())
        binding.viewPager.orientation = ViewPager2.ORIENTATION_HORIZONTAL

        TabLayoutMediator(tabLayout, viewPager){ tab, position ->
            when (position) {
                0 -> "고농도 미세먼지 7가지 대응요령"
                1 -> "고농도 미세먼지 단계별 대응요령"
            }
        }.attach()

        binding.cancelImage.setOnClickListener {
            finish()
        }
    }

    private fun getList(): ArrayList<Int> {
        return arrayListOf(R.layout.preparation_item)
//        뭘 넣을지
    }
}