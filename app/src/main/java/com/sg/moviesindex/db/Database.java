package com.sg.moviesindex.db;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.sg.moviesindex.db.typeconverters.CastsTypeConverter;
import com.sg.moviesindex.db.typeconverters.ReviewsTypeConverter;
import com.sg.moviesindex.model.tmdb.Movie;

@androidx.room.Database(entities = Movie.class, version = 1)
@TypeConverters({CastsTypeConverter.class, ReviewsTypeConverter.class})
public abstract class Database extends RoomDatabase {
    public abstract FavouriteMoviesDAO getFDAO();

    private static Database instance;

    public static synchronized Database getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(), Database.class, "TMDB").addCallback(callback).allowMainThreadQueries().fallbackToDestructiveMigration()
                    .build();
        }
        return instance;
    }

    private static RoomDatabase.Callback callback = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
        }
    };
}
