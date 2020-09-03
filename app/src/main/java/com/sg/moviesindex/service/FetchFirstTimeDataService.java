package com.sg.moviesindex.service;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.sg.moviesindex.BuildConfig;
import com.sg.moviesindex.R;
import com.sg.moviesindex.fragments.Movies;
import com.sg.moviesindex.model.tmdb.Discover;
import com.sg.moviesindex.model.tmdb.DiscoversList;
import com.sg.moviesindex.model.tmdb.Movie;
import com.sg.moviesindex.model.tmdb.MoviesList;
import com.sg.moviesindex.service.network.RetrofitInstance;
import com.sg.moviesindex.service.network.TMDbService;
import com.sg.moviesindex.utils.DiscoverToMovie;
import com.sg.moviesindex.utils.SearchUtil;
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
    private Observable<MoviesList> observableMovie;
    private Observable<DiscoversList> observableDB;
    private CompositeDisposable compositeDisposable;
    private FragmentManager fragmentManager;
    private LinearLayout linearLayoutError;
    private Button refreshButtonError;
    private FragmentTransaction fragmentTransaction;


    public FetchFirstTimeDataService(LinearLayout linearLayout, Button button, ProgressBar progressBar, CompositeDisposable compositeDisposable, FragmentManager fragmentManager) {
        this.progressBar = progressBar;
        this.linearLayoutError = linearLayout;
        this.refreshButtonError = button;
        this.compositeDisposable = compositeDisposable;
        this.fragmentManager = fragmentManager;
        this.fragmentTransaction = fragmentManager.beginTransaction();
    }

    public void getDataFirst(int a, final Context context) {
        final TMDbService TMDbService = RetrofitInstance.getTMDbService(context);
        String ApiKey = BuildConfig.ApiKey;
        if (a == 0 || a == 1) {
            if (a == 0) {
                observableMovie = TMDbService.getPopularMoviesWithRx(ApiKey, 1);
            } else if (a == 1) {
                observableMovie = TMDbService.getTopRatedMoviesWithRx(ApiKey, 1);
            }
            fetchData(context, a);
        } else if (a == 4 || a == 5) {
            String[] x = {"All", "India", "USA", "UK"};
            Dialog dialog = new MaterialAlertDialogBuilder(context).setTitle("Choose a Region").setSingleChoiceItems(x, -1, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
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
                        observableMovie = TMDbService.getUpcomingMoviesWithRx(ApiKey, 1, MainActivity.region);
                    } else if (a == 5) {
                        observableMovie = TMDbService.getNowPlayingWithRx(ApiKey, 1, MainActivity.region);
                    }
                    fetchData(context, a);
                    dialog.dismiss();
                }
            }).setCancelable(false).show();
        }


    }

    private void fetchData(final Context context, int a) {
        compositeDisposable.add(
                observableMovie.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableObserver<MoviesList>() {
                            @Override
                            public void onNext(MoviesList moviesList) {
                                if (moviesList != null && moviesList.getMovies() != null) {
                                    MainActivity.movieList = (ArrayList<Movie>) moviesList.getMovies();
                                    MainActivity.totalPages = moviesList.getTotalPages();
                                    if (progressBar != null) {
                                        progressBar.setIndeterminate(false);
                                    }
                                    if (fragmentManager.getFragments().isEmpty()) {
                                        fragmentTransaction = fragmentManager.beginTransaction();
                                        fragmentTransaction.addToBackStack(null);
                                        fragmentTransaction.add(R.id.frame_layout, new Movies(FetchFirstTimeDataService.this, new SearchUtil(linearLayoutError, refreshButtonError, compositeDisposable, fragmentManager, context, progressBar))).commitAllowingStateLoss();
                                    } else {
                                        fragmentTransaction = fragmentManager.beginTransaction();
                                        fragmentTransaction.addToBackStack(null);
                                        fragmentTransaction.replace(R.id.frame_layout, new Movies(FetchFirstTimeDataService.this, new SearchUtil(linearLayoutError, refreshButtonError, compositeDisposable, fragmentManager, context, progressBar))).commitAllowingStateLoss();
                                    }
                                }

                            }

                            @Override
                            public void onError(Throwable e) {
                                progressBar.setIndeterminate(false);
                                progressBar.setVisibility(View.GONE);
                                linearLayoutError.setVisibility(View.VISIBLE);
                                refreshButtonError.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        progressBar.setIndeterminate(true);
                                        progressBar.setVisibility(View.VISIBLE);
                                        linearLayoutError.setVisibility(View.GONE);
                                        getDataFirst(a, context);
                                    }
                                });
                                Log.d("Check Your Internet", e.getMessage());
                            }

                            @Override
                            public void onComplete() {
                            }
                        }));
    }


    public void getFirstGenreData(final Context context) {
        final TMDbService TMDbService = RetrofitInstance.getTMDbService(context);
        String ApiKey = BuildConfig.ApiKey;
        observableDB = TMDbService.discover(ApiKey, Long.toString(MainActivity.genreid), false, false, 1, "popularity.desc").doOnError(new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                progressBar.setIndeterminate(false);
            }
        });
        compositeDisposable.add(
                observableDB.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableObserver<DiscoversList>() {
                            @Override
                            public void onNext(DiscoversList discoversList) {
                                if (discoversList != null && discoversList.getResults() != null) {
                                    MainActivity.discovers = (ArrayList<Discover>) discoversList.getResults();
                                    MainActivity.totalPagesGenres = discoversList.getTotalPages();
                                    DiscoverToMovie discoverToMovie = new DiscoverToMovie(MainActivity.discovers);
                                    MainActivity.movieList = discoverToMovie.getMovies();
                                    if (progressBar != null) {
                                        progressBar.setIndeterminate(false);
                                    }
                                    fragmentTransaction = fragmentManager.beginTransaction();
                                    fragmentTransaction.addToBackStack(null);
                                    fragmentTransaction.add(R.id.frame_layout, new Movies(FetchFirstTimeDataService.this, new SearchUtil(linearLayoutError, refreshButtonError, compositeDisposable, fragmentManager, context, progressBar))).commitAllowingStateLoss();
                                }

                            }

                            @Override
                            public void onError(Throwable e) {
                                Toast.makeText(context, "Error! Check your internet connection.", Toast.LENGTH_SHORT).show();
                                progressBar.setIndeterminate(false);
                            }

                            @Override
                            public void onComplete() {

                            }
                        })
        );
    }

}
