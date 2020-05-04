package com.sg.moviesindex.paginationlibrary;

import android.app.Application;

import androidx.lifecycle.MutableLiveData;

import com.sg.moviesindex.service.network.MovieDataService;

/* ****


 This class is the data source for the paging library for this project. But paging isn't implemented by this class in this project. Hence, this code isn't used in this project.
   But it can be used as an alternative.


**** */
public class DataSourceFactory extends DataSource.Factory {
    private DataSource dataSource;
    private Application application;
    private MovieDataService movieDataService;
    private MutableLiveData<DataSource> dataSourceMutableLiveData;

    public DataSourceFactory(MovieDataService movieDataService, Application application) {
        this.application = application;
        this.movieDataService = movieDataService;
    }

    public DataSource create() {
        dataSource = new DataSource(movieDataService, application);
        dataSourceMutableLiveData.postValue(dataSource);
        return null;
    }

    public MutableLiveData<DataSource> getDataSourceMutableLiveData() {
        return dataSourceMutableLiveData;
    }
}
