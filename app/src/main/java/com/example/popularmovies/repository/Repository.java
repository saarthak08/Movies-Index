package com.example.popularmovies.repository;

import android.app.Application;

import com.example.popularmovies.db.Database;
import com.example.popularmovies.db.FavouriteMoviesDAO;
import com.example.popularmovies.model.Movie;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import androidx.lifecycle.LiveData;

public class Repository {
    private FavouriteMoviesDAO favouriteMoviesDAO;
    public Repository(Application application)
    {
        Database database=Database.getInstance(application);
        favouriteMoviesDAO=database.getFDAO();

    }

    public LiveData<List<Movie>> getAllFMovies()
    {
        return favouriteMoviesDAO.getAllFMovies();
    }

    public LiveData<Movie> getMovie(String id)
    {
        return favouriteMoviesDAO.getMovie(id);
    }
    public void AddMovie(Movie movie)
    {

        new AddFMovie(favouriteMoviesDAO).execute(movie);
    }

    public void DeleteMovie(Movie movie)
    {
            new DeleteFMovie(favouriteMoviesDAO).execute(movie);
    }
}
