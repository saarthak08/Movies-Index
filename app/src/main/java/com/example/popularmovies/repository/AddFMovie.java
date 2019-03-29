package com.example.popularmovies.repository;

import android.os.AsyncTask;

import com.example.popularmovies.db.FavouriteMoviesDAO;
import com.example.popularmovies.db.FavouriteMoviesEntity;
import com.example.popularmovies.model.Movie;

public class AddFMovie extends AsyncTask<Movie,Void,Void> {
    private FavouriteMoviesDAO favouriteMoviesDAO;

    public AddFMovie(FavouriteMoviesDAO favouriteMoviesDAO) {
        this.favouriteMoviesDAO = favouriteMoviesDAO;
    }

    @Override
    protected Void doInBackground(Movie... movies) {
        FavouriteMoviesEntity favouriteMoviesEntity=new FavouriteMoviesEntity();
        favouriteMoviesEntity.setTitle(movies[0].getTitle());
        favouriteMoviesEntity.setOverview(movies[0].getOverview());
        favouriteMoviesEntity.setPosterPath(movies[0].getPosterPath());
        favouriteMoviesEntity.setVoteAverage(movies[0].getVoteAverage());
        favouriteMoviesEntity.setId(movies[0].getId());
        favouriteMoviesDAO.insertFMovie(favouriteMoviesEntity);
        return null;
    }
}
