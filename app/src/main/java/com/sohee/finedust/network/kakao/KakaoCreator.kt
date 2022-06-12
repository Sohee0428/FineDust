package com.sohee.finedust.network.kakao

import com.google.gson.GsonBuilder
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
object KakaoCreator {

    private const val BASE_URL_KAKAO_API =
        "https://dapi.kakao.com"
    private const val REST_API_KEY = "0a352b947957bcb5250fb9211226fc92"
    private var gson = GsonBuilder().setLenient().create()

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class KakaoCreatorGetClient

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class KakaoCreatorGetInterceptor

    @Provides
    fun create(@KakaoCreatorGetClient okHttpClient: OkHttpClient): KakaoAPI {
        return Retrofit.Builder()
            .baseUrl(BASE_URL_KAKAO_API)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(KakaoAPI::class.java)
    }

    @KakaoCreatorGetClient
    @Provides
    fun getClient(@KakaoCreatorGetInterceptor interceptor: Interceptor): OkHttpClient {
        val httpLoggingInterceptor = HttpLoggingInterceptor()
        httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY

        return OkHttpClient.Builder()
            .addInterceptor(interceptor)
            .addInterceptor(httpLoggingInterceptor)
            .build()
    }

    @KakaoCreatorGetInterceptor
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