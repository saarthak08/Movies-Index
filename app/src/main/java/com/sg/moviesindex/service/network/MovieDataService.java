package com.sg.moviesindex.service.network;

import com.sg.moviesindex.model.CastsList;
import com.sg.moviesindex.model.DiscoverDBResponse;
import com.sg.moviesindex.model.GenresListDBResponse;
import com.sg.moviesindex.model.MovieDBResponse;
import com.sg.moviesindex.model.ReviewsList;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface MovieDataService {

    //@GET("movie/popular")
    //Call<MovieDBResponse> getPopularMovies(@Query("api_key")String apiKey, @Query("page")int pageIndex);

    @GET("movie/popular")
    Observable<MovieDBResponse> getPopularMoviesWithRx(@Query("api_key") String apiKey, @Query("page") int pageIndex);

    @GET("movie/top_rated")
    Observable<MovieDBResponse> getTopRatedMoviesWithRx(@Query("api_key") String apiKey, @Query("page") int pageIndex);

    @GET("movie/upcoming")
    Observable<MovieDBResponse> getUpcomingMoviesWithRx(@Query("api_key") String apiKey, @Query("page") int pageIndex, @Query("region") String region);

    @GET("movie/now_playing")
    Observable<MovieDBResponse> getNowPlayingWithRx(@Query("api_key") String apiKey, @Query("page") int pageIndex, @Query("region") String region);

    @GET("genre/movie/list")
    Observable<GenresListDBResponse> getGenresList(@Query("api_key") String apiKey);

    @GET("movie/{movieId}/reviews")
    Observable<ReviewsList> getReviews(@Path("movieId") Integer movieId, @Query("api_key") String apiKey, @Query("page") int pageIndex);

    @GET("movie/{movieId}/credits")
    Observable<CastsList> getCasts(@Path("movieId") Integer movieId, @Query("api_key") String apiKey, @Query("page") int pageIndex);

    @GET("discover/movie")
    Observable<DiscoverDBResponse> discover(@Query("api_key") String apiKey, @Query("with_genres") String genres, @Query("include_adult") Boolean adult, @Query("include_video") Boolean video, @Query("page") int pageIndex, @Query("sort_by") String sortBy);

    @GET("search/movie")
    Observable<DiscoverDBResponse> search(@Query("api_key") String apiKey, @Query("include_adult") Boolean adult, @Query("query") String query);
}
