package com.example.popularmovies;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.BindingAdapter;
import androidx.databinding.DataBindingUtil;

import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.popularmovies.R;
import com.example.popularmovies.databinding.ActivityMoviesInfoBinding;
import com.example.popularmovies.model.Movie;

public class MoviesInfo extends AppCompatActivity {
    private Movie movie;
    private ActivityMoviesInfoBinding activityMoviesInfoBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movies_info);
        activityMoviesInfoBinding= DataBindingUtil.setContentView(MoviesInfo.this,R.layout.activity_movies_info);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("More Information");
        Intent i=getIntent();
        if(i.hasExtra("movie")) {
            movie = i.getParcelableExtra("movie");
            Toast.makeText(getApplicationContext(), "Swipe Image Up for more information!", Toast.LENGTH_LONG).show();
            getSupportActionBar().setTitle(movie.getTitle());
            activityMoviesInfoBinding.setMovie(movie);
        }
    }
}
