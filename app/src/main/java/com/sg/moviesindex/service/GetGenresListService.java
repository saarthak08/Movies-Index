package com.sg.moviesindex.service;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.sg.moviesindex.BuildConfig;
import com.sg.moviesindex.model.tmdb.DiscoversList;
import com.sg.moviesindex.model.tmdb.Genre;
import com.sg.moviesindex.model.tmdb.GenresList;
import com.sg.moviesindex.model.tmdb.MoviesList;
import com.sg.moviesindex.service.network.RetrofitInstance;
import com.sg.moviesindex.service.network.TMDbService;
import com.sg.moviesindex.view.MainActivity;

import java.util.ArrayList;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

public class GetGenresListService {
    private Observable<MoviesList> observableMovie;
    private Observable<DiscoversList> observableDB;
    private Observable<GenresList> genresListObservable;
    private Context context;
    private ProgressBar progressBar;
    private CompositeDisposable compositeDisposable;
    private FetchFirstTimeDataService fetchFirstTimeDataService;

    public GetGenresListService(Context context, CompositeDisposable compositeDisposable, FetchFirstTimeDataService fetchFirstTimeDataService, ProgressBar progressBar) {
        this.context = context;
        this.compositeDisposable = compositeDisposable;
        this.fetchFirstTimeDataService = fetchFirstTimeDataService;
        this.progressBar = progressBar;
    }

    public void getGenresList() {
        final TMDbService TMDbService = RetrofitInstance.getTMDbService();
        String ApiKey = BuildConfig.ApiKey;
        MainActivity.drawer = 2;
        genresListObservable = TMDbService.getGenresList(ApiKey);
        compositeDisposable.add(
                genresListObservable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableObserver<GenresList>() {
                            @Override
                            public void onNext(GenresList genresList) {
                                if (genresList != null && genresList.getGenres() != null) {
                                    MainActivity.genres = (ArrayList<Genre>) genresList.getGenres();
                                    String[] a = new String[MainActivity.genres.size()];
                                    for (int i = 0; i < MainActivity.genres.size(); i++) {
                                        a[i] = MainActivity.genres.get(i).getName();
                                    }
                                    Dialog dialog = new MaterialAlertDialogBuilder(context).setTitle("Choose a Category").setSingleChoiceItems(a, -1, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            MainActivity.selected = which;
                                            MainActivity.genreid = MainActivity.genres.get(which).getId();
                                            fetchFirstTimeDataService.getFirstGenreData(context);
                                            dialog.dismiss();
                                        }
                                    }).setCancelable(false).show();
                                }

                            }

                            @Override
                            public void onError(Throwable e) {
                                Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                                progressBar.setIndeterminate(false);
                            }

                            @Override
                            public void onComplete() {

                            }
                        })
        );
    }

}
