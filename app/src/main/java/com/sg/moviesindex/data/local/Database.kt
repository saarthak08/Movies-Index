package com.sg.moviesindex.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.sg.moviesindex.data.local.typeconverters.CastTypeConverter
import com.sg.moviesindex.data.local.typeconverters.GenreTypeConverter
import com.sg.moviesindex.data.local.typeconverters.ReviewTypeConverter

/**
 * The Room database for this app.
 * Contains the [Movie] entity and provides access to [FavouriteMoviesDAO].
 */
@Database(
  entities = [Movie::class, WatchlistMovie::class],
  version = 5,
)
@TypeConverters(CastTypeConverter::class, ReviewTypeConverter::class, GenreTypeConverter::class)
abstract class Database : RoomDatabase() {
  /**
   * Retrieves the DAO for favourite movies.
   *
   * @return The [FavouriteMoviesDAO] instance.
   */
  abstract fun getFDAO(): FavouriteMoviesDAO

  /**
   * Retrieves the DAO for watchlist movies.
   *
   * @return The [WatchlistMoviesDAO] instance.
   */
  abstract fun getWatchlistMoviesDAO(): WatchlistMoviesDAO

  companion object {
    private val callback =
      object : Callback() {
      }

    @Volatile
    private var instance: com.sg.moviesindex.data.local.Database? = null

    /**
     * Gets the singleton instance of the database.
     *
     * @param context The application context.
     * @return The singleton [Database] instance.
     */
    @Synchronized
    fun getInstance(context: Context): com.sg.moviesindex.data.local.Database =
      instance ?: synchronized(this) {
        instance ?: Room
          .databaseBuilder(
            context.applicationContext,
            com.sg.moviesindex.data.local.Database::class.java,
            "TMDB",
          ).addCallback(callback)
          .fallbackToDestructiveMigration(true)
          .build()
          .also { instance = it }
      }
  }
}
