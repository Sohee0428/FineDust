package com.example.finedust.presentation.detail

import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.finedust.R
import com.example.finedust.data.DetailDust
import com.example.finedust.data.date.main
import com.example.finedust.databinding.ActivityDetailBinding
import com.example.finedust.presentation.main.MainViewModel

class DetailActivity : AppCompatActivity() {

    lateinit var binding: ActivityDetailBinding
    var detailList = arrayListOf<DetailDust>()
    val adapter: DetailAdapter by lazy {
        DetailAdapter(this, detailList)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_detail)
        binding.lifecycleOwner = this

        binding.detailRecyclerview.adapter = adapter
        val layoutManager = LinearLayoutManager(this)
        binding.detailRecyclerview.layoutManager = layoutManager

        val dustData = intent.getSerializableExtra("data") as List<DetailDust>
        val location = intent.getStringExtra("location")
        val observatory = intent.getStringExtra("observatory")
        val date = intent.getStringExtra("date")
        binding.locationStr.text = location
        binding.observatory.text = observatory
        binding.date.text = date


        Log.d("어댑터 데이터 넘기기", "$dustData")
        adapter.addDustList(dustData)

        binding.exit.setOnClickListener {
            finish()
        }
    }
}
