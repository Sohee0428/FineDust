package com.sohee.finedust.presentation.detail

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.sohee.finedust.R
import com.sohee.finedust.data.DetailDust
import com.sohee.finedust.databinding.ActivityDetailBinding

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
        val dustData = intent.getSerializableExtra("data") as List<DetailDust>?
        val location = intent.getStringExtra("location")
        val observatory = intent.getStringExtra("observatory")
        val date = intent.getStringExtra("date")

        binding.locationStr.text = location ?: "위치를... 불러오지 못하였습니다. ㅠㅠ"
        binding.observatory.text = observatory ?: " - "
        binding.date.text = date

        Log.d("어댑터 데이터 넘기기", "$dustData")
        adapter.addDustList(dustData)
    }

    fun close(view: View) {
        finish()
    }
}
