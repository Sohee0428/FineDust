package com.sohee.finedust.network.publicdata

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Qualifier

@Module
@InstallIn(SingletonComponent::class)
object AirConditionerCreator {

    private const val BASE_URL_OBSERVATORY_API =
        "http://apis.data.go.kr"
    private const val SERVICE_KEY =
        "M66ovFn84Is25oHoO6tQEVwPVD83anrxkIon8fsxQytUaSNJ2nRQPOMs5MJh8Cb1GfXYVi8L3t87tz1j%2FpncMQ%3D%3D"

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class AirConditionerCreatorGetClient

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class AirConditionerCreatorGetInterceptor

    @Provides
    fun create(
        @AirConditionerCreatorGetClient okhttp: OkHttpClient
    ): AirConditionerAPI {
        return Retrofit.Builder()
            .baseUrl(BASE_URL_OBSERVATORY_API)
            .client(okhttp)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(AirConditionerAPI::class.java)
    }

    @AirConditionerCreatorGetClient
    @Provides
    fun getClient(@AirConditionerCreatorGetInterceptor interceptor: Interceptor): OkHttpClient {
        val httpLoggingInterceptor = HttpLoggingInterceptor()
        httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY

        return OkHttpClient.Builder()
            .addInterceptor(interceptor)
            .addInterceptor(httpLoggingInterceptor)
            .build()
    }

    @AirConditionerCreatorGetInterceptor
    @Provides
    fun getInterceptor(): Interceptor {
        return Interceptor {
            val request = it.request()
                .newBuilder()
                .header("SERVICE_KEY", SERVICE_KEY)
                .build()

            return@Interceptor it.proceed(request)
        }
    }
}


