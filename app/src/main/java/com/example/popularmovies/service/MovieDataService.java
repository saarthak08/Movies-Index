package com.example.popularmovies.service;

import com.example.popularmovies.model.DiscoverDBResponse;
import com.example.popularmovies.model.GenresListDBResponse;
import com.example.popularmovies.model.MovieDBResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface MovieDataService {

    @GET("movie/popular")
    Call<MovieDBResponse> getPopularMovies(@Query("api_key")String apiKey, @Query("page")int pageIndex);

    @GET("movie/top_rated")
    Call<MovieDBResponse> getTopRatedMovies(@Query("api_key")String apiKey, @Query("page")int pageIndex);

    @GET("genre/movie/list")
    Call<GenresListDBResponse> getGenresList(@Query("api_key")String apiKey);

    @GET("discover/movie")
    Call<DiscoverDBResponse> discover(@Query("api_key")String apiKey, @Query("with_genres")String genres, @Query("include_adult")Boolean adult, @Query("include_video")Boolean video, @Query("page")int pageIndex);
}
