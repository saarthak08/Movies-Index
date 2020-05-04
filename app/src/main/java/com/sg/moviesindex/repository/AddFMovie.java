package com.sg.moviesindex.repository;

import android.os.AsyncTask;

import com.sg.moviesindex.db.FavouriteMoviesDAO;
import com.sg.moviesindex.model.Movie;

public class AddFMovie extends AsyncTask<Movie, Void, Void> {
    private FavouriteMoviesDAO favouriteMoviesDAO;

    public AddFMovie(FavouriteMoviesDAO favouriteMoviesDAO) {
        this.favouriteMoviesDAO = favouriteMoviesDAO;
    }

    @Override
    protected Void doInBackground(Movie... movies) {
        favouriteMoviesDAO.insertFMovie(movies[0]);
        return null;
    }
}
