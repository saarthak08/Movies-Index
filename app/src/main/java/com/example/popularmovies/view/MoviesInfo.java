package com.example.popularmovies.view;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.popularmovies.R;
import com.example.popularmovies.model.Movie;

public class MoviesInfo extends AppCompatActivity {
    private Movie movie;
    private ImageView movieImage;
    private String image;
    private TextView movieTitle, movieSynopsis, movieRating, movieReleaseDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movies_info);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("More Information");
        movieTitle = findViewById(R.id.tvMovieTitle);
        movieSynopsis =findViewById(R.id.tvPlotsynopsis);
        movieRating =findViewById(R.id.tvMovieRating);
        movieReleaseDate = findViewById(R.id.tvReleaseDate);
        movieImage=findViewById(R.id.ivMovieLarge);
        Intent i=getIntent();
        if(i.hasExtra("movie")) {
            movie = i.getParcelableExtra("movie");
            Toast.makeText(getApplicationContext(), "Swipe Image Up for more information!", Toast.LENGTH_LONG).show();

            image = movie.getPosterPath();

            String path = "https://image.tmdb.org/t/p/w500" + image;

            Glide.with(this)
                    .load(path)
                    .placeholder(R.drawable.loading)
                    .into(movieImage);

            getSupportActionBar().setTitle(movie.getTitle());
            movieTitle.setText(movie.getTitle());
            movieSynopsis.setText("Synopsis:\n"+movie.getOverview());
            movieRating.setText("Rating: "+Double.toString(movie.getVoteAverage()));
            movieReleaseDate.setText("Release Date: "+ movie.getReleaseDate());
        }
    }

}
