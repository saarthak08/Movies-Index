package com.example.popularmovies.repository;

import android.app.Application;
import android.os.Build;

import com.example.popularmovies.BuildConfig;
import com.example.popularmovies.model.Movie;
import com.example.popularmovies.model.MovieDBResponse;
import com.example.popularmovies.service.MovieDataService;
import com.example.popularmovies.service.RetrofitInstance;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.paging.PageKeyedDataSource;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/* This class is the data source for the paging library for this project. But paging isn't implemented by this class in this project. Hence, this code isn't used in this project.
   But it can be used as an alternative. */

public class DataSource extends PageKeyedDataSource<Long, Movie> {
    private MovieDataService movieDataService;
    private Application application;
    private String ApiKey = BuildConfig.ApiKey;


    public DataSource(MovieDataService movieDataService, Application application) {
        this.movieDataService = movieDataService;
        this.application = application;
    }

    @Override
    public void loadInitial(@NonNull LoadInitialParams<Long> params, @NonNull final LoadInitialCallback<Long, Movie> callback) {
        movieDataService= RetrofitInstance.getService();
        Call<MovieDBResponse> call=movieDataService.getPopularMovies(ApiKey,1);
        call.enqueue(new Callback<MovieDBResponse>() {
            @Override
            public void onResponse(Call<MovieDBResponse> call, Response<MovieDBResponse> response) {
                MovieDBResponse movieDBResponse = response.body();
                ArrayList<Movie> movies = new ArrayList<>();
                if (movieDBResponse != null & movieDBResponse.getMovies() != null) {
                    movies = (ArrayList<Movie>) movieDBResponse.getMovies();
                    callback.onResult(movies, null, (long) 2);
                }
            }
            @Override
            public void onFailure(Call<MovieDBResponse> call, Throwable t) {

            }
        });

    }

    @Override
    public void loadBefore(@NonNull LoadParams<Long> params, @NonNull LoadCallback<Long, Movie> callback) {

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void loadAfter(@NonNull final LoadParams<Long> params, @NonNull final LoadCallback<Long, Movie> callback) {
        movieDataService= RetrofitInstance.getService();
        Call<MovieDBResponse> call=movieDataService.getPopularMovies(ApiKey, Math.toIntExact(params.key));
        call.enqueue(new Callback<MovieDBResponse>() {
            @Override
            public void onResponse(Call<MovieDBResponse> call, Response<MovieDBResponse> response) {
                MovieDBResponse movieDBResponse=response.body();
                ArrayList<Movie> movies=new ArrayList<>();
                if(movieDBResponse!=null&&movieDBResponse.getMovies()!=null) {
                    movies = (ArrayList<Movie>) movieDBResponse.getMovies();
                    callback.onResult(movies, params.key + 1);
                }
            }

            @Override
            public void onFailure(Call<MovieDBResponse> call, Throwable t) {

            }
        });

    }
}
