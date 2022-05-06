package com.sohee.finedust.network.publicdata

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object NearbyObservatoryCreator {

    private const val BASE_URL_OBSERVATORY_API =
        "http://apis.data.go.kr"
    val SERVICE_KEY =
        "M66ovFn84Is25oHoO6tQEVwPVD83anrxkIon8fsxQytUaSNJ2nRQPOMs5MJh8Cb1GfXYVi8L3t87tz1j%2FpncMQ%3D%3D"

    fun create(): NearbyObservatoryAPI {
        return Retrofit.Builder()
            .baseUrl(BASE_URL_OBSERVATORY_API)
            .client(getClient())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(NearbyObservatoryAPI::class.java)
    }

    private fun getClient(): OkHttpClient {
        val httpLoggingInterceptor = HttpLoggingInterceptor()
        httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY

        return OkHttpClient.Builder()
            .addInterceptor(httpLoggingInterceptor)
            .build()
    }
}




