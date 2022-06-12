package com.sohee.finedust.network.publicdata

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Qualifier

@Module
@InstallIn(SingletonComponent::class)
object NearbyObservatoryCreator {

    private const val BASE_URL_OBSERVATORY_API =
        "http://apis.data.go.kr"
    val SERVICE_KEY =
        "M66ovFn84Is25oHoO6tQEVwPVD83anrxkIon8fsxQytUaSNJ2nRQPOMs5MJh8Cb1GfXYVi8L3t87tz1j%2FpncMQ%3D%3D"

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class NearbyObservatoryCreatorGetClient

    @Provides
    fun create(@NearbyObservatoryCreatorGetClient okHttpClient: OkHttpClient): NearbyObservatoryAPI {
        return Retrofit.Builder()
            .baseUrl(BASE_URL_OBSERVATORY_API)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(NearbyObservatoryAPI::class.java)
    }

    @NearbyObservatoryCreatorGetClient
    @Provides
    fun getClient(): OkHttpClient {
        val httpLoggingInterceptor = HttpLoggingInterceptor()
        httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY

        return OkHttpClient.Builder()
            .addInterceptor(httpLoggingInterceptor)
            .build()
    }
}




