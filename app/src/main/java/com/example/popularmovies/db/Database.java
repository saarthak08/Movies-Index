package com.example.popularmovies.db;

import android.content.Context;

import com.example.popularmovies.model.Movie;

import androidx.annotation.NonNull;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

@androidx.room.Database(entities = FavouriteMoviesEntity.class,version = 1)
public abstract class Database extends RoomDatabase {
    public abstract FavouriteMoviesDAO getFDAO();
    private static Database instance;
    public  static  synchronized Database getInstance(Context context)
    {
        if(instance ==null)
        {
            instance= Room.databaseBuilder(context.getApplicationContext(),Database.class,"TMDB").addCallback(callback).fallbackToDestructiveMigration()
                    .build();
        }
        return instance;
    }
    private static RoomDatabase.Callback callback=new RoomDatabase.Callback(){
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
        }
    };
}
