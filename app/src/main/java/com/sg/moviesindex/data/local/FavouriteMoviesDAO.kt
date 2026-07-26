package com.sg.moviesindex.data.local

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy.Companion.REPLACE
import androidx.room.Query

/**
 * Data Access Object for the favourite movies table.
 */
@Dao
interface FavouriteMoviesDAO {
  /**
   * Inserts a movie into the database. If the movie already exists, it is replaced.
   *
   * @param movie The movie to be inserted.
   */
  @Insert(onConflict = REPLACE)
  fun insertFMovie(movie: Movie)

  /**
   * Deletes a specific movie from the database.
   *
   * @param movie The movie to be deleted.
   */
  @Delete
  fun deleteFMovie(movie: Movie)

  /**
   * Retrieves all favourite movies from the database.
   *
   * @return A LiveData list containing all favourite movies.
   */
  @Query("select * from favourite_movies")
  fun getAllFMovies(): LiveData<List<Movie>>

  /**
   * Retrieves a specific favourite movie by its title.
   *
   * @param title The title of the movie.
   * @return The [Movie] object if found, null otherwise.
   */
  @Query("select * from favourite_movies where title==:title")
  fun getMovie(title: String): Movie?
}
