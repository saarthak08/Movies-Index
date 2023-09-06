package com.sg.moviesindex.repository;

import android.os.AsyncTask;

import com.sg.moviesindex.db.FavouriteMoviesDAO;
import com.sg.moviesindex.model.tmdb.Movie;

public class AddFavouriteMovie extends AsyncTask<Movie, Void, Void> {
    private final FavouriteMoviesDAO favouriteMoviesDAO;

    public AddFavouriteMovie(FavouriteMoviesDAO favouriteMoviesDAO) {
        this.favouriteMoviesDAO = favouriteMoviesDAO;
    }

    @Override
    protected Void doInBackground(Movie... movies) {
        favouriteMoviesDAO.insertFMovie(movies[0]);
        return null;
    }
}
