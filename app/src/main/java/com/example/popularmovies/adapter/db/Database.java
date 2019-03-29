package com.example.popularmovies.adapter.db;

import android.content.Context;

import com.example.popularmovies.model.Movie;

import androidx.room.Room;
import androidx.room.RoomDatabase;

@androidx.room.Database(entities = {Movie.class},version = 1)
public abstract class Database extends RoomDatabase {
    public abstract FavouriteMoviesDAO getFDAO();
    private static Database instance;
    public  static  synchronized Database getInstance(Context context)
    {
        if(instance ==null)
        {
            instance= Room.databaseBuilder(context.getApplicationContext(),Database.class,"TMDB").fallbackToDestructiveMigration()
                    .build();
        }
        return instance;
    }

}
