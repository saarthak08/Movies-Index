package com.sg.moviesindex.service.network;

import com.sg.moviesindex.model.tmdb.MovieDBResponse;
import com.sg.moviesindex.model.yts.APIResponse;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface YTSService {
    @GET("list_movies.json")
    Observable<APIResponse> getMoviesList(@Query("page") int pageIndex, @Query("query_term")String query_term);
}
