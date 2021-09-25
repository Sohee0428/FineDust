package com.example.finedust.API

import com.example.finedust.data.Air.AirConditionerInfo
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface AirConditionerAPI {
    @GET("/B552584/ArpltnInforInqireSvc/getMsrstnAcctoRltmMesureDnsty?&dataTerm=DAILY&pageNo=1&numOfRows=100&returnType=json")
    fun getAirConditioner(
        @Query("stationName") stationName: String?,
        @Query("serviceKey", encoded = true) key: String
    ): Call<AirConditionerInfo>
        companion object {
            private const val BASE_URL_PARADIDYMIS_API =
                "http://apis.data.go.kr"
            val SERVICE_KEY = "M66ovFn84Is25oHoO6tQEVwPVD83anrxkIon8fsxQytUaSNJ2nRQPOMs5MJh8Cb1GfXYVi8L3t87tz1j%2FpncMQ%3D%3D"

            fun create(): AirConditionerAPI {
//            데이터를 넘겨받아 어떤 데이터를 받았는지 검색
                val httpLoggingInterceptor = HttpLoggingInterceptor()
                httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY

//            말그대로 정보를 중간에 가져오는 역할
                val headerInterceptor = Interceptor {
                    val request = it.request()
                        .newBuilder()
                        .addHeader("SERVICE_KEY", SERVICE_KEY)
                        .build()
                    return@Interceptor it.proceed(request)
                }

                val client = OkHttpClient.Builder()
                    .addInterceptor(headerInterceptor)
                    .addInterceptor(httpLoggingInterceptor)
                    .build()

                return Retrofit.Builder()
                    .baseUrl(BASE_URL_PARADIDYMIS_API)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                    .create(AirConditionerAPI::class.java)

            }

        }
    }



