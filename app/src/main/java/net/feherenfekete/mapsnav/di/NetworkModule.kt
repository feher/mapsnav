package net.feherenfekete.mapsnav.di

import dagger.Module
import dagger.Provides
import net.feherenfekete.mapsnav.repository.directions.gmaps.GmapsDirectionsService
import net.feherenfekete.mapsnav.repository.poi.wikipedia.WikiPoiService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton


@Module
class NetworkModule {

    @Provides
    @Singleton
    fun okHttpClient(): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()
    }

    @Provides
    @Singleton
    fun wikiPoiService(okHttpClient: OkHttpClient): WikiPoiService {
        return Retrofit.Builder()
            .baseUrl("https://en.wikipedia.org/w/")
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
            .create(WikiPoiService::class.java)
    }

    @Provides
    @Singleton
    fun gmapsDirectionsService(okHttpClient: OkHttpClient): GmapsDirectionsService {
        return Retrofit.Builder()
            .baseUrl("https://maps.googleapis.com/maps/api/directions/")
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
            .create(GmapsDirectionsService::class.java)
    }

}
