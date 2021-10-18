package com.example.finedust.data.network

import com.example.finedust.data.response.kakao.KakaoResponse
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface KakaoAPI {
    @GET("/v2/local/geo/transcoord.json?input_coord=WGS84&output_coord=TM")
    fun getNavigate(
        @Query("x") x: Double,
        @Query("y") y: Double
    ): Call<KakaoResponse>

    companion object {
        private const val BASE_URL_KAKAO_API =
            "https://dapi.kakao.com"
        private const val REST_API_KEY = "0a352b947957bcb5250fb9211226fc92"

        fun create(): KakaoAPI {
//            데이터를 넘겨받아 어떤 데이터를 받았는지 검색
            val httpLoggingInterceptor = HttpLoggingInterceptor()
            httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY

//            말그대로 정보를 중간에 가져오는 역할
            val headerInterceptor = Interceptor {
                val request = it.request()
                    .newBuilder()
                    .addHeader("Authorization", "KakaoAK $REST_API_KEY")
                    .build()
                return@Interceptor it.proceed(request)
            }

            val client = OkHttpClient.Builder()
                .addInterceptor(headerInterceptor)
                .addInterceptor(httpLoggingInterceptor)
                .build()

            return Retrofit.Builder()
                .baseUrl(BASE_URL_KAKAO_API)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(KakaoAPI::class.java)

        }
    }
}



