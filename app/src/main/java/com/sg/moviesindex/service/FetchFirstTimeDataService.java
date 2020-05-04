package com.sg.moviesindex.service;

import android.content.Context;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.afollestad.materialdialogs.MaterialDialog;
import com.sg.moviesindex.BuildConfig;
import com.sg.moviesindex.R;
import com.sg.moviesindex.fragments.Movies;
import com.sg.moviesindex.model.Discover;
import com.sg.moviesindex.model.DiscoverDBResponse;
import com.sg.moviesindex.model.Movie;
import com.sg.moviesindex.model.MovieDBResponse;
import com.sg.moviesindex.service.network.MovieDataService;
import com.sg.moviesindex.service.network.RetrofitInstance;
import com.sg.moviesindex.utils.DiscoverToMovie;
import com.sg.moviesindex.view.MainActivity;

import java.util.ArrayList;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

public class FetchFirstTimeDataService {
    private ProgressBar progressBar;
    private Observable<MovieDBResponse> observableMovie;
    private Observable<DiscoverDBResponse> observableDB;
    private CompositeDisposable compositeDisposable;
    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;


    public FetchFirstTimeDataService(ProgressBar progressBar, CompositeDisposable compositeDisposable, FragmentManager fragmentManager) {
        this.progressBar = progressBar;
        this.compositeDisposable = compositeDisposable;
        this.fragmentManager = fragmentManager;
        this.fragmentTransaction = fragmentManager.beginTransaction();
    }

    public void getDataFirst(int a, final Context context) {
        final MovieDataService movieDataService = RetrofitInstance.getService();
        String ApiKey = BuildConfig.ApiKey;
        if (a == 0 || a == 1) {
            if (a == 0) {
                observableMovie = movieDataService.getPopularMoviesWithRx(ApiKey, 1);

            } else if (a == 1) {
                observableMovie = movieDataService.getTopRatedMoviesWithRx(ApiKey, 1);
            }
            fetchData(context);
        } else if (a == 4 || a == 5) {
            String[] x = {"All", "India", "USA", "UK"};
            new MaterialDialog.Builder(context).title("Choose a Region").items(x).itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
                @Override
                public boolean onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                    if (which == 0) {
                        MainActivity.region = "";
                    } else if (which == 1) {
                        MainActivity.region = "IN";
                    } else if (which == 2) {
                        MainActivity.region = "US";
                    } else {
                        MainActivity.region = "GB";
                    }
                    if (a == 4) {
                        observableMovie = movieDataService.getUpcomingMoviesWithRx(ApiKey, 1, MainActivity.region);
                    } else if (a == 5) {
                        observableMovie = movieDataService.getNowPlayingWithRx(ApiKey, 1, MainActivity.region);
                    }
                    fetchData(context);
                    return true;
                }
            }).canceledOnTouchOutside(false).cancelable(false).show();
        }


    }

    public void fetchData(final Context context) {
        compositeDisposable.add(
                observableMovie.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableObserver<MovieDBResponse>() {
                            @Override
                            public void onNext(MovieDBResponse movieDBResponse) {
                                if (movieDBResponse != null && movieDBResponse.getMovies() != null) {
                                    MainActivity.movieList = (ArrayList<Movie>) movieDBResponse.getMovies();
                                    MainActivity.totalPages = movieDBResponse.getTotalPages();
                                    if (progressBar != null) {
                                        progressBar.setIndeterminate(false);
                                    }
                                    if (fragmentManager.getFragments().isEmpty()) {
                                        fragmentTransaction = fragmentManager.beginTransaction();
                                        fragmentTransaction.addToBackStack(null);
                                        fragmentTransaction.add(R.id.frame_layout, new Movies(FetchFirstTimeDataService.this)).commitAllowingStateLoss();
                                    } else {
                                        fragmentTransaction = fragmentManager.beginTransaction();
                                        fragmentTransaction.addToBackStack(null);
                                        fragmentTransaction.replace(R.id.frame_layout, new Movies(FetchFirstTimeDataService.this)).commitAllowingStateLoss();
                                    }
                                }

                            }

                            @Override
                            public void onError(Throwable e) {
                                Toast.makeText(context, "Error! " + e.getMessage().trim(), Toast.LENGTH_SHORT).show();
                                progressBar.setIndeterminate(false);
                            }

                            @Override
                            public void onComplete() {

                            }
                        }));
    }


    public void getFirstGenreData(int genreid, final Context context) {
        final MovieDataService movieDataService = RetrofitInstance.getService();
        String ApiKey = BuildConfig.ApiKey;
        observableDB = movieDataService.discover(ApiKey, Integer.toString(genreid), false, false, 1, "popularity.desc").doOnError(new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                progressBar.setIndeterminate(false);
            }
        });
        compositeDisposable.add(
                observableDB.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableObserver<DiscoverDBResponse>() {
                            @Override
                            public void onNext(DiscoverDBResponse discoverDBResponse) {
                                if (discoverDBResponse != null && discoverDBResponse.getResults() != null) {
                                    MainActivity.discovers = (ArrayList<Discover>) discoverDBResponse.getResults();
                                    MainActivity.totalPagesGenres = discoverDBResponse.getTotalPages();
                                    DiscoverToMovie discoverToMovie = new DiscoverToMovie(MainActivity.discovers);
                                    MainActivity.movieList = discoverToMovie.getMovies();
                                    if (progressBar != null) {
                                        progressBar.setIndeterminate(false);
                                    }
                                    fragmentTransaction = fragmentManager.beginTransaction();
                                    fragmentTransaction.addToBackStack(null);
                                    fragmentTransaction.add(R.id.frame_layout, new Movies(FetchFirstTimeDataService.this)).commitAllowingStateLoss();
                                }

                            }

                            @Override
                            public void onError(Throwable e) {
                                Toast.makeText(context, "Error! " + e.getMessage().trim(), Toast.LENGTH_SHORT).show();
                                progressBar.setIndeterminate(false);
                            }

                            @Override
                            public void onComplete() {

                            }
                        })
        );
    }

}
