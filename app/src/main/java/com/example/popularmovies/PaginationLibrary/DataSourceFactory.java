package com.example.popularmovies.PaginationLibrary;

import android.app.Application;

import com.example.popularmovies.service.MovieDataService;

import androidx.lifecycle.MutableLiveData;

public class DataSourceFactory {
    private DataSource dataSource;
    private Application application;
    private MovieDataService movieDataService;
    private MutableLiveData<DataSource> dataSourceMutableLiveData;

    public DataSourceFactory(Application application, MovieDataService movieDataService) {
        this.application = application;
        this.movieDataService = movieDataService;
    }

    public void create()
    {
        dataSource=new DataSource(movieDataService,application);
        dataSourceMutableLiveData.postValue(dataSource);
    }

    public MutableLiveData<DataSource> getDataSourceMutableLiveData()
    {
        return dataSourceMutableLiveData;
    }
}
