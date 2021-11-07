package com.example.finedust.presentation.recyclerview

import android.content.Context
import android.location.Address
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.finedust.R
import com.example.finedust.data.AddressAndFineDustData
import com.example.finedust.data.response.address.AddressResponse
import com.example.finedust.data.response.air.AirResponse
import com.example.finedust.databinding.FinedustListItemBinding

class FinedustAdapter(val context: Context, val address: ArrayList<AddressAndFineDustData>) : RecyclerView.Adapter<FinedustAdapter.ViewHolder>() {

    lateinit var airResponse: ArrayList<AirResponse>

    fun addList(list: List<AddressAndFineDustData>) {
        address.addAll(list)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): FinedustAdapter.ViewHolder {
        val rawItemBinding =
            FinedustListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(rawItemBinding)
    }

    override fun onBindViewHolder(holder: FinedustAdapter.ViewHolder, position: Int) {
        holder.bindAddress(position)
    }

    override fun getItemCount(): Int = address.size

    inner class ViewHolder(private val binding: FinedustListItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bindAddress(position: Int) {

            Log.d("adapter address", address[position].documents[0].address.address_name)

            binding.locationData.text = address[position].address

        }

        val pm10GradeStr = airResponse[0].response.body.items[0].pm10Grade
        val pm25GradeStr = airResponse[0].response.body.items[0].pm25Grade

        fun bindAirCondition(position: Int) {
            binding.pm10Value.text = address[position].pm10Value
            binding.pm25Value.text = address[position].pm25Value
            binding.pm10Grade.text = convertDustStatus(pm10GradeStr)
            binding.pm25Grade.text = convertDustStatus(pm25GradeStr)
      }

        private fun convertDustStatus(str: String): String {
             return when (str) {
                "1" -> "좋음"
                "2" -> "보통"
                "3" -> "나쁨"
                "4" -> "매우 나쁨"
                else -> "오류"
            }
        }

        fun pm10GradeImage(pm10GradeStr: String) {
            when (pm10GradeStr) {
                "1" -> binding.pm10StateImage.setImageResource(R.drawable.perfect)
                "2" -> binding.pm10StateImage.setImageResource(R.drawable.soso)
                "3" -> binding.pm10StateImage.setImageResource(R.drawable.bad)
                "4" -> FinedustListItemBinding.pm10StateImage.setImageResource(R.drawable.verybad)
                else -> FinedustListItemBinding.bind(View(this)).pm10StateImage.setImageResource(R.drawable.ic_baseline_error_24)
            }
        }

        fun pm25GradeImage(pm25GradeStr: String) {
            when (pm25GradeStr) {
                "1" -> binding.pm25StateImage.setImageResource(R.drawable.perfect)
                "2" -> FinedustListItemBinding.bind(View(this)).pm25StateImage.setImageResource(R.drawable.soso)
                "3" -> FinedustListItemBinding.bind(View(this)).pm25StateImage.setImageResource(R.drawable.bad)
                "4" -> FinedustListItemBinding.bind(View(this)).pm25StateImage.setImageResource(R.drawable.verybad)
                else -> FinedustListItemBinding.bind(View(this)).pm25StateImage.setImageResource(R.drawable.ic_baseline_error_24)
            }
    }

    }
}