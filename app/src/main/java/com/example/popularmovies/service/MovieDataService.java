package com.example.popularmovies.service;

import com.example.popularmovies.model.Movie;
import com.example.popularmovies.model.MovieDBResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface MovieDataService {

    @GET("movie/popular")
    Call<MovieDBResponse> getPopularMovies(@Query("api_key")String apiKey);

    @GET("movie/top_rated")
    Call<MovieDBResponse> getTopRatedMovies(@Query("api_key")String apiKey);
}
