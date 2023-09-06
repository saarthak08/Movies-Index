package com.sg.moviesindex.repository;

import android.os.AsyncTask;

import com.sg.moviesindex.db.FavouriteMoviesDAO;
import com.sg.moviesindex.model.tmdb.Movie;

public class DeleteFavouriteMovie extends AsyncTask<Movie, Void, Void> {
    private final FavouriteMoviesDAO favouriteMoviesDAO;

    public DeleteFavouriteMovie(FavouriteMoviesDAO favouriteMoviesDAO) {
        this.favouriteMoviesDAO = favouriteMoviesDAO;
    }

    @Override
    protected Void doInBackground(Movie... movies) {
        favouriteMoviesDAO.deleteFMovie(movies[0]);
        return null;
    }
}
