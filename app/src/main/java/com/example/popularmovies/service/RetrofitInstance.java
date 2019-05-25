package com.example.popularmovies.service;

import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitInstance {

    private static Retrofit retrofit=null;
    private static final String BASE_URL="https://api.themoviedb.org/3/";
    private static final int REQUEST_TIMEOUT = 60;
    private static OkHttpClient okHttpClient;
    public static MovieDataService getService()
    {
            if (okHttpClient == null)
            initOkHttp();
            if(retrofit==null)
            {
                retrofit=new Retrofit.Builder().baseUrl(BASE_URL)
                        .client(okHttpClient)
                        .addCallAdapterFactory(RxJava2CallAdapterFactory.create()).addConverterFactory(GsonConverterFactory.create()).build();
            }
            return retrofit.create(MovieDataService.class);
    }

    private static void initOkHttp() {
        OkHttpClient.Builder httpClient = new OkHttpClient().newBuilder()
                .connectTimeout(REQUEST_TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(REQUEST_TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(REQUEST_TIMEOUT, TimeUnit.SECONDS);
        okHttpClient = httpClient.build();
    }

}
