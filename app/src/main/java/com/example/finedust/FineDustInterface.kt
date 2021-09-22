package com.example.finedust

import android.telecom.Call
import com.example.finedust.data.Data
import retrofit2.http.GET
import retrofit2.http.Query

interface FineDustInterface {
    @GET("getMsrstnAcctoRltmMesureDnsty?stationName=$stationName&dataTerm=month&pageNo=1&numOfRows=100&returnType=xml&serviceKey=M66ovFn84Is25oHoO6tQEVwPVD83anrxkIon8fsxQytUaSNJ2nRQPOMs5MJh8Cb1GfXYVi8L3t87tz1j%2FpncMQ%3D%3D")

    fun GetFineDust(
        @Query("query") query: String
    ): Call<Data>
}