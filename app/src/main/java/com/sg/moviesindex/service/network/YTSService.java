package com.sg.moviesindex.service.network;

import com.sg.moviesindex.model.yts.APIResponse;

import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

public interface YTSService {
    @GET("list_movies.json")
    Observable<APIResponse> getMoviesList(@Query("query_term") String query_term);

    @GET
    @Streaming
    Call<ResponseBody> downloadFileWithDynamicUrlSync(@Url String fileUrl);
}
