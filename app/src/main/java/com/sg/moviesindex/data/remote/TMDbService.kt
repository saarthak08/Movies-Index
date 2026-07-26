package com.sg.moviesindex.data.remote

import com.sg.moviesindex.data.local.Movie
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Retrofit interface for the TMDb (The Movie Database) API.
 * Defines the endpoints and their required parameters for fetching movie-related data.
 */
interface TMDbService {
  @GET("movie/popular")
  fun getPopularMoviesWithRx(
    @Query("api_key") apiKey: String,
    @Query("page") pageIndex: Int,
  ): Observable<MoviesList>

  @GET("movie/top_rated")
  fun getTopRatedMoviesWithRx(
    @Query("api_key") apiKey: String,
    @Query("page") pageIndex: Int,
  ): Observable<MoviesList>

  @GET("movie/upcoming")
  fun getUpcomingMoviesWithRx(
    @Query("api_key") apiKey: String,
    @Query("page") pageIndex: Int,
    @Query("region") region: String?,
  ): Observable<MoviesList>

  @GET("movie/now_playing")
  fun getNowPlayingWithRx(
    @Query("api_key") apiKey: String,
    @Query("page") pageIndex: Int,
    @Query("region") region: String?,
  ): Observable<MoviesList>

  @GET("genre/movie/list")
  fun getGenresList(
    @Query("api_key") apiKey: String,
  ): Observable<GenresList>

  @GET("movie/{movieId}/reviews")
  fun getReviews(
    @Path("movieId") movieId: Long,
    @Query("api_key") apiKey: String,
    @Query("page") pageIndex: Int,
  ): Observable<ReviewsList>

  @GET("movie/{movieId}/credits")
  fun getCasts(
    @Path("movieId") movieId: Long,
    @Query("api_key") apiKey: String,
  ): Observable<CastsList>

  @GET("discover/movie")
  fun discover(
    @Query("api_key") apiKey: String,
    @Query("with_genres") genres: String?,
    @Query("include_adult") adult: Boolean?,
    @Query("include_video") video: Boolean?,
    @Query("page") pageIndex: Int,
    @Query("sort_by") sortBy: String?,
    @Query("region") region: String?,
    @Query("with_original_language") language: String?,
    @Query("release_date.gte") releaseDateGTE: String?,
    @Query("release_date.lte") releaseDateLTE: String?,
  ): Observable<DiscoversList>

  @GET("search/movie")
  fun search(
    @Query("api_key") apiKey: String,
    @Query("include_adult") adult: Boolean?,
    @Query("query") query: String,
    @Query("page") page: Int,
  ): Observable<DiscoversList>

  @GET("movie/{id}")
  fun getFullMovieInformation(
    @Path("id") id: Long,
    @Query("api_key") apiKey: String,
  ): Observable<Movie>
}
