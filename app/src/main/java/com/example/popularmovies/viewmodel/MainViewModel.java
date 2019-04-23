package com.example.popularmovies.viewmodel;

import android.app.Application;

import com.example.popularmovies.PaginationLibrary.DataSource;
import com.example.popularmovies.PaginationLibrary.DataSourceFactory;
import com.example.popularmovies.model.Movie;
import com.example.popularmovies.repository.Repository;
import com.example.popularmovies.service.MovieDataService;
import com.example.popularmovies.service.RetrofitInstance;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import androidx.paging.LivePagedListBuilder;
import androidx.paging.PagedList;

public class MainViewModel extends AndroidViewModel {
    private Repository repository;
    private LiveData<DataSource> dataSourceLiveData;
    private Executor executor;
    private LiveData<PagedList<Movie>> pagedListLiveData;
    public MainViewModel(@NonNull Application application) {
        super(application);
        repository=new Repository(application);
        MovieDataService movieDataService= RetrofitInstance.getService();
        DataSourceFactory dataSourceFactory=new DataSourceFactory(movieDataService,application);
        dataSourceLiveData=dataSourceFactory.getDataSourceMutableLiveData();
        PagedList.Config config=(new PagedList.Config.Builder())
                .setEnablePlaceholders(true)
                .setInitialLoadSizeHint(10)
                .setPageSize(20)
                .setPrefetchDistance(4)
                .build();
        executor=Executors.newFixedThreadPool(5);
        pagedListLiveData=(new LivePagedListBuilder<Long,Movie>(dataSourceFactory,config)).setFetchExecutor(executor).build();
    }


    public LiveData<PagedList<Movie>> getPagedListLiveData() {
        return pagedListLiveData;
    }

    public Movie getMovie(String id)
    {
        return repository.getMovie(id);
    }

    public LiveData<List<Movie>> getAllMovies()
    {
        return repository.getAllFMovies();
    }

    public void AddMovie(Movie movie)
    {

        repository.AddMovie(movie);
    }

    public void DeleteMovie(Movie movie)
    {
        repository.DeleteMovie(movie);
    }

}
