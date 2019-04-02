package com.example.popularmovies.viewmodel;

import android.app.Application;

import com.example.popularmovies.model.Movie;
import com.example.popularmovies.repository.Repository;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

public class MainViewModel extends AndroidViewModel {
    private Repository repository;


    public MainViewModel(@NonNull Application application) {
        super(application);
        repository=new Repository(application);
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
