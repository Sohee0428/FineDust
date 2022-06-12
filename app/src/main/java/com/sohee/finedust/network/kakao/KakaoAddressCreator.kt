package com.sohee.finedust.network.kakao

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
object KakaoAddressCreator {

    private const val BASE_URL_KAKAO_API =
        "https://dapi.kakao.com"
    private const val REST_API_KEY = "0a352b947957bcb5250fb9211226fc92"

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class KakaoAddressCreatorGetClient

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class KakaoAddressCreatorGetInterceptor

    fun create(@KakaoAddressCreatorGetClient okHttpClient: OkHttpClient): KakaoAPI {
        return Retrofit.Builder()
            .baseUrl(BASE_URL_KAKAO_API)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(KakaoAPI::class.java)
    }

    @KakaoAddressCreatorGetClient
    @Provides
    fun getClient(@KakaoAddressCreatorGetInterceptor interceptor: Interceptor): OkHttpClient {
        val httpLoggingInterceptor = HttpLoggingInterceptor()
        httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY

        return OkHttpClient.Builder()
            .addInterceptor(interceptor)
            .addInterceptor(httpLoggingInterceptor)
            .build()
    }

    @KakaoAddressCreatorGetInterceptor
    @Provides
    fun getInterceptor(): Interceptor {
        return Interceptor {
            val request = it.request()
                .newBuilder()
                .header("Authorization", "KakaoAK $REST_API_KEY")
                .build()

            return@Interceptor it.proceed(request)
        }
    }
}