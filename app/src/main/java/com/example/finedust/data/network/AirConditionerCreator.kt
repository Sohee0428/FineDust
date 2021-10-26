package com.example.finedust.data.network

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object AirConditionerCreator {

    private const val BASE_URL_PARADIDYMIS_API =
        "http://apis.data.go.kr"
    private const val SERVICE_KEY =
        "M66ovFn84Is25oHoO6tQEVwPVD83anrxkIon8fsxQytUaSNJ2nRQPOMs5MJh8Cb1GfXYVi8L3t87tz1j%2FpncMQ%3D%3D"

    fun create(): AirConditionerAPI {

        return Retrofit.Builder()
            .baseUrl(BASE_URL_PARADIDYMIS_API)
            .client(getClient())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(AirConditionerAPI::class.java)
    }

    private fun getClient(): OkHttpClient {

//            데이터를 넘겨받아 어떤 데이터를 받았는지 검색
        val httpLoggingInterceptor = HttpLoggingInterceptor()
        httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY

        return OkHttpClient.Builder()
            .addInterceptor(getInterceptor())
            .addInterceptor(httpLoggingInterceptor)
            .build()
    }

    private fun getInterceptor(): Interceptor {

        return Interceptor {
            val request = it.request()
                .newBuilder()
                .addHeader("SERVICE_KEY", SERVICE_KEY)
                .build()
            return@Interceptor it.proceed(request)
        }
    }
}


