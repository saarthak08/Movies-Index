package com.example.popularmovies.db;

import com.example.popularmovies.model.Movie;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import static androidx.room.OnConflictStrategy.REPLACE;

@Dao
public interface FavouriteMoviesDAO {

    @Insert(onConflict = REPLACE)
    void insertFMovie(Movie movie);

    @Delete
    void deleteFMovie(Movie movie);

    @Query("select * from favourite_movies")
    List<Movie> getAllFMovies();
}
