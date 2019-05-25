package com.example.popularmovies.paginationlibrary;

import android.app.Application;

import com.example.popularmovies.service.MovieDataService;

import androidx.lifecycle.MutableLiveData;
/* ****


 This class is the data source for the paging library for this project. But paging isn't implemented by this class in this project. Hence, this code isn't used in this project.
   But it can be used as an alternative.


**** */
public class DataSourceFactory extends DataSource.Factory{
    private DataSource dataSource;
    private Application application;
    private MovieDataService movieDataService;
    private MutableLiveData<DataSource> dataSourceMutableLiveData;

    public DataSourceFactory(MovieDataService movieDataService,Application application) {
        this.application = application;
        this.movieDataService = movieDataService;
    }

    public DataSource create()
    {
        dataSource=new DataSource(movieDataService,application);
        dataSourceMutableLiveData.postValue(dataSource);
        return null;
    }

    public MutableLiveData<DataSource> getDataSourceMutableLiveData()
    {
        return dataSourceMutableLiveData;
    }
}
