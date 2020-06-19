package com.sg.moviesindex.service;

import android.content.Context;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.sg.moviesindex.BuildConfig;
import com.sg.moviesindex.adapter.MoviesAdapter;
import com.sg.moviesindex.model.tmdb.Discover;
import com.sg.moviesindex.model.tmdb.DiscoverDBResponse;
import com.sg.moviesindex.model.tmdb.Movie;
import com.sg.moviesindex.model.tmdb.MovieDBResponse;
import com.sg.moviesindex.service.network.MovieDataService;
import com.sg.moviesindex.service.network.RetrofitInstance;
import com.sg.moviesindex.utils.DiscoverToMovie;
import com.sg.moviesindex.view.MainActivity;

import java.util.ArrayList;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

public class FetchMoreDataService {
    private Observable<MovieDBResponse> observableMovie;
    private Observable<DiscoverDBResponse> observableDB;
    private RecyclerView recyclerView;
    private ArrayList<Movie> movieList;
    private int totalPages;
    private int totalPagesGenre;
    private ArrayList<Discover> discovers = new ArrayList<>();
    private CompositeDisposable compositeDisposable;
    private MoviesAdapter moviesAdapter;
    private Context context;

    public FetchMoreDataService(RecyclerView recyclerView, ArrayList<Movie> movieList, CompositeDisposable compositeDisposable, Context context, MoviesAdapter moviesAdapter) {
        this.recyclerView = recyclerView;
        this.movieList = movieList;
        this.compositeDisposable = compositeDisposable;
        this.context = context;
        this.moviesAdapter = moviesAdapter;
    }

    public void loadMore(int a, final int pages) {
        final MovieDataService movieDataService = RetrofitInstance.getTMDbService();
        String ApiKey = BuildConfig.ApiKey;
        if (a == 0) {
            observableMovie = movieDataService.getPopularMoviesWithRx(ApiKey, pages);
        } else if (a == 1) {
            observableMovie = movieDataService.getTopRatedMoviesWithRx(ApiKey, pages);
        } else if (a == 4) {
            observableMovie = movieDataService.getUpcomingMoviesWithRx(ApiKey, pages, MainActivity.region);
        } else if (a == 5) {
            observableMovie = movieDataService.getNowPlayingWithRx(ApiKey, pages, MainActivity.region);
        }
        compositeDisposable.add(observableMovie.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableObserver<MovieDBResponse>() {
                    @Override
                    public void onNext(MovieDBResponse movieDBResponse) {
                        if (movieDBResponse != null && movieDBResponse.getMovies() != null) {
                            if (pages == 1) {
                                movieList = (ArrayList<Movie>) movieDBResponse.getMovies();
                                totalPages = movieDBResponse.getTotalPages();
                                recyclerView.setAdapter(moviesAdapter);
                            } else {
                                ArrayList<Movie> movies = (ArrayList<Movie>) movieDBResponse.getMovies();
                                for (Movie movie : movies) {
                                    movieList.add(movie);
                                    moviesAdapter.notifyItemInserted(movieList.size() - 1);
                                }
                            }
                        }

                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(context, "Error! Check your internet connection.", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onComplete() {

                    }
                }));
    }


    public void loadMoreGenres(final int pages) {
        final MovieDataService movieDataService = RetrofitInstance.getTMDbService();
        String ApiKey = BuildConfig.ApiKey;
        observableDB = movieDataService.discover(ApiKey, Integer.toString(MainActivity.genreid), false, false, pages, "popularity.desc");
        compositeDisposable.add(
                observableDB.subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableObserver<DiscoverDBResponse>() {
                            @Override
                            public void onNext(DiscoverDBResponse discoverDBResponse) {
                                if (discoverDBResponse != null && discoverDBResponse.getResults() != null) {
                                    if (pages == 1) {
                                        discovers = (ArrayList<Discover>) discoverDBResponse.getResults();
                                        totalPagesGenre = discoverDBResponse.getTotalPages();
                                        recyclerView.setAdapter(moviesAdapter);
                                    } else {
                                        ArrayList<Discover> discovers = (ArrayList<Discover>) discoverDBResponse.getResults();
                                        DiscoverToMovie discoverToMovie = new DiscoverToMovie(discovers);
                                        ArrayList<Movie> movies = discoverToMovie.getMovies();
                                        for (Movie movie : movies) {
                                            movieList.add(movie);
                                            moviesAdapter.notifyItemInserted(movieList.size() - 1);
                                        }
                                    }
                                }
                            }

                            @Override
                            public void onError(Throwable e) {
                                Toast.makeText(context, "Error! Check your internet connection.", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onComplete() {

                            }
                        })
        );
    }

}
