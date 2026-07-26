package com.sg.moviesindex.data.remote

import io.reactivex.Observable
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Streaming
import retrofit2.http.Url

/**
 * Retrofit interface for the YTS API.
 * Used primarily for searching torrents and downloading torrent files.
 */
interface YTSService {
  @GET("list_movies.json")
  fun getMoviesList(
    @Query("query_term") queryTerm: String,
  ): Observable<APIResponse>

  @GET
  @Streaming
  fun downloadFileWithDynamicUrlSync(
    @Url fileUrl: String,
  ): Call<ResponseBody>
}
