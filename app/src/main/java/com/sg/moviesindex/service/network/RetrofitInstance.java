package com.sg.moviesindex.service.network;


import android.content.Context;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitInstance {

    private static Retrofit retrofit = null;
    private static Retrofit retrofitYTS = null;
    private static final String BASE_URL_TMDB = "https://api.themoviedb.org/3/";
    private static final String BASE_URL_YTS = "https://yts.mx/api/v2/";
    private static final int REQUEST_TIMEOUT = 60;
    private static OkHttpClient okHttpClientYTS;
    private static OkHttpClient okHttpClientTMDb;
    private static int cacheSize = 10 * 1024 * 1024; // 10 MB
    private static Cache cacheYTS;
    private static Cache cacheTMDb;


    public static TMDbService getTMDbService(Context context) {
        if (okHttpClientTMDb == null) {
            initOkHttpTMDb(context);
        }
        if (retrofit == null) {
            retrofit = new Retrofit.Builder().baseUrl(BASE_URL_TMDB)
                    .client(okHttpClientTMDb)
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create()).addConverterFactory(GsonConverterFactory.create()).build();
        }
        return retrofit.create(TMDbService.class);
    }


    public static YTSService getYTSService(Context context) {
        if (okHttpClientYTS == null) {
            initOkHttpYTS(context);
        }
        retrofitYTS = new Retrofit.Builder()
                .baseUrl(BASE_URL_YTS)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(okHttpClientYTS)
                .build();
        return retrofitYTS.create(YTSService.class);
    }

    private static void initOkHttpYTS(Context context) {
        cacheYTS = new Cache(context.getCacheDir(), cacheSize);
        OkHttpClient.Builder httpClient = new OkHttpClient().newBuilder()
                .connectTimeout(REQUEST_TIMEOUT, TimeUnit.SECONDS)
                .cache(cacheYTS)
                .readTimeout(REQUEST_TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(REQUEST_TIMEOUT, TimeUnit.SECONDS);
        okHttpClientYTS = httpClient.build();
    }

    private static void initOkHttpTMDb(Context context) {
        cacheTMDb = new Cache(context.getCacheDir(), cacheSize);
        OkHttpClient.Builder httpClient = new OkHttpClient().newBuilder()
                .connectTimeout(REQUEST_TIMEOUT, TimeUnit.SECONDS)
                .cache(cacheTMDb)
                .readTimeout(REQUEST_TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(REQUEST_TIMEOUT, TimeUnit.SECONDS);
        okHttpClientTMDb = httpClient.build();
    }

    public static void resetCache() throws IOException {
        if (cacheYTS != null) {
            cacheYTS.evictAll();
        }
        if (cacheTMDb != null) {
            cacheTMDb.evictAll();
        }
    }


}
