package com.example.finedust.data.network

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object KakaoAddressCreator {

    private const val BASE_URL_KAKAO_API =
        "https://dapi.kakao.com"
    private const val REST_API_KEY = "0a352b947957bcb5250fb9211226fc92"

    fun create(): KakaoAddressAPI {

        return Retrofit.Builder()
            .baseUrl(BASE_URL_KAKAO_API)
            .client(getClint())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(KakaoAddressAPI::class.java)
    }

    private fun getClint(): OkHttpClient {

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
                .addHeader("Authorization", "KakaoAK $REST_API_KEY")
                .build()
            return@Interceptor it.proceed(request)
        }
    }
}



