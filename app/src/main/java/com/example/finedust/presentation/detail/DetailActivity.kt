package com.example.finedust.presentation.detail

import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.finedust.R
import com.example.finedust.data.DetailDust
import com.example.finedust.databinding.ActivityDetailBinding

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding
    private val detailList = arrayListOf<DetailDust>()
    private val adapter: DetailAdapter by lazy {
        DetailAdapter(this, detailList)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_detail)
        binding.lifecycleOwner = this

        initContactAdapter()
        setData()
    }

    private fun initContactAdapter() {
        binding.detailRecyclerview.adapter = adapter
        val layoutManager = LinearLayoutManager(this)
        binding.detailRecyclerview.layoutManager = layoutManager
    }

    private fun setData() {
        val dustData = intent.getSerializableExtra("data") as List<DetailDust>
        val location = intent.getStringExtra("location")
        val observatory = intent.getStringExtra("observatory")
        val date = intent.getStringExtra("date")

        binding.locationStr.text = location
        binding.observatory.text = observatory
        binding.date.text = date

        Log.d("어댑터 데이터 넘기기", "$dustData")
        adapter.addDustList(dustData)
    }

        binding.exit.setOnClickListener {
            finish()
        }
    }
}
