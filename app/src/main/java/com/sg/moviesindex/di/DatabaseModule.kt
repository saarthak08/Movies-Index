package com.sg.moviesindex.di

import android.content.Context
import androidx.room.Room
import com.sg.moviesindex.data.local.Database
import com.sg.moviesindex.data.local.FavouriteMoviesDAO
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
  @Provides
  @Singleton
  fun provideDatabase(
    @ApplicationContext context: Context,
  ): Database =
    Room
      .databaseBuilder(context, Database::class.java, "TMDB")
      .allowMainThreadQueries()
      .fallbackToDestructiveMigration(true)
      .build()

  @Provides
  @Singleton
  fun provideFavouriteMoviesDAO(database: Database): FavouriteMoviesDAO = database.getFDAO()
}
