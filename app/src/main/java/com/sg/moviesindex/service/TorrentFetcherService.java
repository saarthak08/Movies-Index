package com.sg.moviesindex.service;


import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.Glide;
import com.dd.processbutton.ProcessButton;
import com.sg.moviesindex.model.tmdb.DiscoverDBResponse;
import com.sg.moviesindex.model.tmdb.Movie;
import com.sg.moviesindex.model.yts.APIResponse;
import com.sg.moviesindex.model.yts.Torrent;
import com.sg.moviesindex.service.network.MovieDataService;
import com.sg.moviesindex.service.network.RetrofitInstance;
import com.sg.moviesindex.service.network.YTSService;
import com.sg.moviesindex.utils.TorrentSaver;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;

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
    private String searchQuery;
    private com.sg.moviesindex.model.yts.Movie resultantMovie;

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
        searchQuery=movieTMDb.getTitle().toLowerCase().replace(' ','_');
        searchQuery = searchQuery.replaceAll("[^a-zA-Z0-9_]", "");
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
                if(totalPages>1) {
                    fetchAllResults(currentPage+1,searchQuery,button,movieTMDb);
                }
                else {
                    showMaterialDialog(movieTMDb);
                    button.setProgress(100);
                    mListener.onComplete();
                }
            }
        }));
    }

    private void fetchAllResults(int pageNo,String searchQuery,final ProcessButton button,Movie movie) {
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
                            fetchAllResults(pageNo+1,searchQuery,button,movie);
                        }
                    }));
        }
        else {
            showMaterialDialog(movie);
            mListener.onComplete();
            button.setProgress(100);
        }
    }

    private void showMaterialDialog(Movie movie) {
        filterResults(movie);
        if(resultantMovie!=null) {
            ArrayList<String> torrentsName = new ArrayList<>();
            int l=1;
            for (Torrent x:resultantMovie.getTorrents()) {
                torrentsName.add(l+") " + "Quality: "+x.getQuality()+"\nSize: "+x.getSize()+"\nType: "+x.getType()+"\nSeeds: "+x.getSeeds()+"\t|\tPeers: "+x.getPeers());
                l++;
            }
            new MaterialDialog.Builder(context).items(torrentsName).title("Torrent Files").content("Note! If the torrent file isn't working, try using a VPN or check the torrent's seeds.").itemsCallback(new MaterialDialog.ListCallback() {

                @Override
                public void onSelection(MaterialDialog dialog, View itemView, int position, CharSequence text) {
                    if(resultantMovie!=null||resultantMovie.getTorrents()!=null) {
                        TorrentSaver torrentSaver = new TorrentSaver(context, resultantMovie.getTorrents().get(position));
                        torrentSaver.downloadTorrent();
                    }

                }
            }).show();
        }
    }

    private void filterResults(Movie movie) {
        try {
            Long movieReleaseYear = Long.parseLong(movie.getReleaseDate().substring(0, 4));
            for (com.sg.moviesindex.model.yts.Movie p : response.getData().getMovies()) {
                if (p.getTitle().equals(movie.getTitle()) && p.getYear().equals(movieReleaseYear)) {
                    resultantMovie = p;
                }
            }
            if(resultantMovie==null) {
                Toast.makeText(context, "No Torrents Found!", Toast.LENGTH_SHORT).show();
            }
        }
        catch (Exception e) {
            Toast.makeText(context, "No Torrents Found!", Toast.LENGTH_SHORT).show();
        }
    }

}
