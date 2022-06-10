package com.sohee.finedust.ui.detail

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.sohee.finedust.R
import com.sohee.finedust.data.DetailIntentData
import com.sohee.finedust.databinding.ActivityDetailBinding
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding
    private val viewModel: DetailViewModel by viewModels()
    private val adapter: DetailAdapter by lazy {
        DetailAdapter()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_detail)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        initContactAdapter()
        initCollector()
        setData()
    }

    private fun initContactAdapter() {
        binding.detailRecyclerview.adapter = adapter
        val layoutManager = LinearLayoutManager(this)
        binding.detailRecyclerview.layoutManager = layoutManager
    }

    private fun initCollector() {
        lifecycleScope.launch {
            viewModel.detailDustData.collect {
                if (it != null) {
                    adapter.submitList(viewModel.detailDustData.value!!.data)
                }
            }
        }
    }

    private fun setData() {
        viewModel.detailDustData.value =
            intent.getSerializableExtra("detailDustData") as DetailIntentData
    }

    fun close(view: View) {
        finish()
    }
}
