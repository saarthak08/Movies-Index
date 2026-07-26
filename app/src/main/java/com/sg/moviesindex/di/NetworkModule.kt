package com.sg.moviesindex.di

import android.content.Context
import com.sg.moviesindex.data.remote.TMDbService
import com.sg.moviesindex.data.remote.YTSService
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.Cache
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
  private const val BASE_URL_TMDB = "https://api.themoviedb.org/3/"
  private const val BASE_URL_YTS = "https://movies-api.accel.li/api/v2/"
  private const val CACHE_SIZE = 10L * 1024 * 1024 // 10 MB

  @Provides
  @Singleton
  fun provideMoshi(): Moshi =
    Moshi
      .Builder()
      .add(KotlinJsonAdapterFactory())
      .build()

  @Provides
  @Singleton
  @Named("TMDbCache")
  fun provideTMDbCache(
    @ApplicationContext context: Context,
  ): Cache = Cache(context.cacheDir, CACHE_SIZE)

  @Provides
  @Singleton
  @Named("YTSCache")
  fun provideYTSCache(
    @ApplicationContext context: Context,
  ): Cache = Cache(context.cacheDir, CACHE_SIZE)

  @Provides
  @Singleton
  @Named("TMDbClient")
  fun provideTMDbClient(
    @Named("TMDbCache") cache: Cache,
  ): OkHttpClient =
    OkHttpClient
      .Builder()
      .cache(cache)
      .connectTimeout(30, TimeUnit.SECONDS)
      .readTimeout(30, TimeUnit.SECONDS)
      .build()

  @Provides
  @Singleton
  @Named("YTSClient")
  fun provideYTSClient(
    @Named("YTSCache") cache: Cache,
  ): OkHttpClient =
    OkHttpClient
      .Builder()
      .cache(cache)
      .connectTimeout(30, TimeUnit.SECONDS)
      .readTimeout(30, TimeUnit.SECONDS)
      .build()

  @Provides
  @Singleton
  @Named("TMDbRetrofit")
  fun provideTMDbRetrofit(
    @Named("TMDbClient") client: OkHttpClient,
    moshi: Moshi,
  ): Retrofit =
    Retrofit
      .Builder()
      .baseUrl(BASE_URL_TMDB)
      .client(client)
      .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
      .addConverterFactory(MoshiConverterFactory.create(moshi))
      .build()

  @Provides
  @Singleton
  @Named("YTSRetrofit")
  fun provideYTSRetrofit(
    @Named("YTSClient") client: OkHttpClient,
    moshi: Moshi,
  ): Retrofit =
    Retrofit
      .Builder()
      .baseUrl(BASE_URL_YTS)
      .client(client)
      .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
      .addConverterFactory(MoshiConverterFactory.create(moshi))
      .build()

  @Provides
  @Singleton
  fun provideTMDbService(
    @Named("TMDbRetrofit") retrofit: Retrofit,
  ): TMDbService = retrofit.create(TMDbService::class.java)

  @Provides
  @Singleton
  fun provideYTSService(
    @Named("YTSRetrofit") retrofit: Retrofit,
  ): YTSService = retrofit.create(YTSService::class.java)
}
