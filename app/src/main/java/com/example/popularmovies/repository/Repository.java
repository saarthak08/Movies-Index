package com.example.popularmovies.repository;

import android.app.Application;

import com.example.popularmovies.adapter.db.Database;
import com.example.popularmovies.adapter.db.FavouriteMoviesDAO;
import com.example.popularmovies.model.Movie;

import java.util.List;

public class Repository {
    private List<Movie> movieList;
    private FavouriteMoviesDAO favouriteMoviesDAO;
    public Repository(Application application)
    {
        Database database=Database.getInstance(application);
        favouriteMoviesDAO=database.getFDAO();
    }

    public List<Movie> getAllFMovies()
    {
        movieList=favouriteMoviesDAO.getAllFMovies();
        return movieList;
    }

    public void AddMovie()
    {

    }

    public void DeleteMovie()
    {

    }
}
