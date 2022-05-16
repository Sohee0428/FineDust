package com.sohee.finedust.data.response.aircondition

import com.sohee.finedust.ui.App
import com.sohee.finedust.R
import java.io.Serializable

data class DetailDust(
    val dataTime: String,
    val dustName: String,
    val value: Pair<String?, String?>,
    val measure: String,
    val description: String
) : Serializable {
    companion object {

        val dustNameList = listOf(
            "통합대기환경지수 (CAI)",
            "미세먼지 (PM10)",
            "초미세먼지 (PM25)",
            "오존 (O3)",
            "아황산가스 (SO2)",
            "일산화탄소 (CO)",
            "이산화질소 (NO2)"
        )

        val descriptionList = listOf(
            App.instance.getString(R.string.cai_description),
            App.instance.getString(R.string.pm10_description),
            App.instance.getString(R.string.pm25_description),
            App.instance.getString(R.string.o3_description),
            App.instance.getString(R.string.so2_description),
            App.instance.getString(R.string.co_description),
            App.instance.getString(R.string.no2_description)
        )

        val measureList = listOf(
            "", "㎍/㎥", "㎍/㎥", "ppm", "ppm", "ppm", "ppm"
        )

        fun getDetailDustList(item: List<Pair<String?, String?>>,dataTime: String?): List<DetailDust> {
            val detailDustList = mutableListOf<DetailDust>()

            dustNameList.forEachIndexed { index, name ->
                detailDustList.add(
                    DetailDust(
                        dataTime = dataTime ?: "-",
                        dustName = name,
                        value = item[index],
                        measure = measureList[index],
                        description = descriptionList[index]
                    )
                )
            }
            return detailDustList
        }
    }
}