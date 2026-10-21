package com.sg.moviesindex.data.local

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface WatchlistMoviesDAO {
  @Insert(onConflict = OnConflictStrategy.REPLACE)
  fun insertWatchlistMovie(watchlistMovie: WatchlistMovie): Long

  @Delete
  fun deleteWatchlistMovie(watchlistMovie: WatchlistMovie): Int

  @Query("DELETE FROM watchlist_movies WHERE id = :movieId")
  fun deleteWatchlistMovieById(movieId: Long): Int

  @Query("SELECT * FROM watchlist_movies ORDER BY added_at DESC")
  fun getAllWatchlistMovies(): LiveData<List<WatchlistMovie>>

  @Query("SELECT EXISTS(SELECT 1 FROM watchlist_movies WHERE id = :movieId)")
  fun isWatchlistMovie(movieId: Long): LiveData<Boolean>
}
