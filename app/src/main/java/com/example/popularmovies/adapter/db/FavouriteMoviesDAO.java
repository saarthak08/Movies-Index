package com.example.popularmovies.adapter.db;

import android.view.LayoutInflater;

import com.example.popularmovies.model.Movie;

import java.util.ArrayList;
import java.util.List;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

@Dao
public interface FavouriteMoviesDAO {

    @Insert
    void insertFMovie(Movie movie);

    @Delete
    void deleteFMovie(Movie movie);

    @Query("select * from favourite_movies")
    List<Movie> getAllFMovies();
}
