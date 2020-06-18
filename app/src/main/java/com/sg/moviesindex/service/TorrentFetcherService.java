package com.sg.moviesindex.service;


import com.dd.processbutton.ProcessButton;
import com.sg.moviesindex.model.tmdb.DiscoverDBResponse;
import com.sg.moviesindex.model.tmdb.Movie;
import com.sg.moviesindex.model.yts.APIResponse;
import com.sg.moviesindex.service.network.MovieDataService;
import com.sg.moviesindex.service.network.RetrofitInstance;
import com.sg.moviesindex.service.network.YTSService;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import io.reactivex.Observable;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;


public class TorrentFetcherService {

    private Observable<APIResponse> observableAPIResponse;
    private CompositeDisposable compositeDisposable;
    private APIResponse response=new APIResponse();
    private Context context;
    private int totalPages;
    private int currentPage=1;

    public interface OnCompleteListener {
        public void onComplete();
    }

    private OnCompleteListener mListener;

    public TorrentFetcherService(OnCompleteListener listener, Context context) {
        mListener = listener;
        compositeDisposable=new CompositeDisposable();
        this.context=context;
    }


    public void start(final ProcessButton button, Movie movieTMDb) {
        final Handler handler = new Handler();
        button.setProgress(50);
        final YTSService ytsService = RetrofitInstance.getYTSService();
        String searchQuery=movieTMDb.getTitle().toLowerCase().replace(' ','_');
        observableAPIResponse=ytsService.getMoviesList(currentPage,searchQuery);
        compositeDisposable.add(observableAPIResponse.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
        .subscribeWith(new DisposableObserver<APIResponse>() {
            @Override
            public void onNext(APIResponse apiResponse) {
                response=apiResponse;
                Log.e("Torrent Fetch",apiResponse.getStatus());
            }

            @Override
            public void onError(Throwable e) {
                Log.e("Torrent Fetch",e.toString());
                Toast.makeText(context, "Error in fetching torrent files! " + e.toString(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onComplete() {
                if(response.getData().getMovieCount().intValue()>response.getData().getLimit().intValue()) {
                    totalPages = (response.getData().getMovieCount().intValue() / response.getData().getLimit().intValue());
                    if((response.getData().getMovieCount().intValue() % response.getData().getLimit().intValue()!=0)) {
                        totalPages=totalPages+1;
                    }
                }
                else {
                    totalPages=1;
                }
                Toast.makeText(context, totalPages+"", Toast.LENGTH_SHORT).show();
                if(totalPages>1) {
                    fetchAllResults(currentPage+1,searchQuery,button);
                }
                else {
                    button.setProgress(100);
                    mListener.onComplete();
                }
            }
        }));
    }

    private void fetchAllResults(int pageNo,String searchQuery,final ProcessButton button) {
        if(pageNo<=5) {
            final YTSService ytsService = RetrofitInstance.getYTSService();
            observableAPIResponse = ytsService.getMoviesList(currentPage,searchQuery);
            compositeDisposable.add(observableAPIResponse.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(new DisposableObserver<APIResponse>() {
                        @Override
                        public void onNext(APIResponse apiResponse) {
                            if(apiResponse!=null) {
                                response.getData().getMovies().addAll(apiResponse.getData().getMovies());
                            }
                        }
                        @Override
                        public void onError(Throwable e) {
                            Log.e("Torrent Fetch",e.toString()+" Page No: "+pageNo);
                        }

                        @Override
                        public void onComplete() {
                            fetchAllResults(pageNo+1,searchQuery,button);
                        }
                    }));
        }
        else {
            mListener.onComplete();
            button.setProgress(100);
        }
    }


}
