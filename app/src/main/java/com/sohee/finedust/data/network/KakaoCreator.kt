package com.sohee.finedust.data.network

import com.google.gson.GsonBuilder
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object KakaoCreator {

    private const val BASE_URL_KAKAO_API =
        "https://dapi.kakao.com"
    private const val REST_API_KEY = "0a352b947957bcb5250fb9211226fc92"
    private var gson = GsonBuilder().setLenient().create()

    fun create(): KakaoAPI {
        return Retrofit.Builder()
            .baseUrl(BASE_URL_KAKAO_API)
            .client(getClient())
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(KakaoAPI::class.java)
    }

    private fun getClient(): OkHttpClient {
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
                .header("Authorization", "KakaoAK $REST_API_KEY")
                .build()

            return@Interceptor it.proceed(request)
        }
    }
}