package com.example.popularmovies.db;

import com.example.popularmovies.model.Movie;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

@Dao
public interface FavouriteMoviesDAO {

    @Insert
    void insertFMovie(FavouriteMoviesEntity movie);

    @Delete
    void deleteFMovie(FavouriteMoviesEntity movie);

    @Query("select * from favourite_movies")
    List<FavouriteMoviesEntity> getAllFMovies();
}
