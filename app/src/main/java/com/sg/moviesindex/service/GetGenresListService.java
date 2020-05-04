package com.sg.moviesindex.service;

import android.content.Context;
import android.view.View;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.sg.moviesindex.BuildConfig;
import com.sg.moviesindex.model.DiscoverDBResponse;
import com.sg.moviesindex.model.GenresList;
import com.sg.moviesindex.model.GenresListDBResponse;
import com.sg.moviesindex.model.MovieDBResponse;
import com.sg.moviesindex.service.network.MovieDataService;
import com.sg.moviesindex.service.network.RetrofitInstance;
import com.sg.moviesindex.view.MainActivity;

import java.util.ArrayList;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

public class GetGenresListService {
    private Observable<MovieDBResponse> observableMovie;
    private Observable<DiscoverDBResponse> observableDB;
    private Observable<GenresListDBResponse> genresListObservable;
    private Context context;
    private CompositeDisposable compositeDisposable;
    private FetchFirstTimeDataService fetchFirstTimeDataService;

    public GetGenresListService(Context context, CompositeDisposable compositeDisposable, FetchFirstTimeDataService fetchFirstTimeDataService) {
        this.context = context;
        this.compositeDisposable = compositeDisposable;
        this.fetchFirstTimeDataService = fetchFirstTimeDataService;
    }

    public void getGenresList() {
        final MovieDataService movieDataService = RetrofitInstance.getService();
        String ApiKey = BuildConfig.ApiKey;
        MainActivity.drawer = 2;
        genresListObservable = movieDataService.getGenresList(ApiKey);
        compositeDisposable.add(
                genresListObservable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableObserver<GenresListDBResponse>() {
                            @Override
                            public void onNext(GenresListDBResponse genresListDBResponse) {
                                if (genresListDBResponse != null && genresListDBResponse.getGenresLists() != null) {
                                    MainActivity.genresLists = (ArrayList<GenresList>) genresListDBResponse.getGenresLists();
                                    String[] a = new String[MainActivity.genresLists.size()];
                                    for (int i = 0; i < MainActivity.genresLists.size(); i++) {
                                        a[i] = MainActivity.genresLists.get(i).getName();
                                    }
                                    new MaterialDialog.Builder(context).title("Choose a Category").items(a).itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
                                        @Override
                                        public boolean onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                                            MainActivity.selected = which;
                                            MainActivity.genreid = MainActivity.genresLists.get(which).getId();
                                            fetchFirstTimeDataService.getFirstGenreData(MainActivity.genreid, context);
                                            return true;
                                        }
                                    }).canceledOnTouchOutside(false).cancelable(false).show();
                                }

                            }

                            @Override
                            public void onError(Throwable e) {
                                Toast.makeText(context, "Error!" + e.getMessage().trim(), Toast.LENGTH_SHORT).show();

                            }

                            @Override
                            public void onComplete() {

                            }
                        })
        );
    }

}
