package com.example.popularmovies.view;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import android.view.View;
import android.widget.Toast;

import com.example.popularmovies.R;
import com.example.popularmovies.databinding.ActivityMoviesInfoBinding;
import com.example.popularmovies.model.Movie;
import com.example.popularmovies.viewmodel.MainViewModel;
import com.google.android.material.snackbar.Snackbar;
import com.varunest.sparkbutton.SparkButton;

public class MoviesInfo extends AppCompatActivity {
    private Movie movie;
    private Boolean bool;
    private ActivityMoviesInfoBinding activityMoviesInfoBinding;
    private MainViewModel mainViewModel;
    private SparkButton sparkButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movies_info);
        Toolbar toolbar=findViewById(R.id.toolbar);
        View parentlayout=findViewById(android.R.id.content);
        mainViewModel= ViewModelProviders.of(MoviesInfo.this).get(MainViewModel.class);
        activityMoviesInfoBinding= DataBindingUtil.setContentView(MoviesInfo.this,R.layout.activity_movies_info);
        Intent i=getIntent();
        if(i.hasExtra("movie")) {
            movie = i.getParcelableExtra("movie");
            bool=i.getBooleanExtra("boolean",false);
            if(MainActivity.imageup<=2) {
                Snackbar.make(parentlayout, "Swipe Image Up For More Information!", Snackbar.LENGTH_SHORT).show();
                MainActivity.imageup++;
            }
            if(mainViewModel.getMovie(movie.getTitle())!=null)
            {
                activityMoviesInfoBinding.secondaryLayout.sparkButton.setChecked(true);
                activityMoviesInfoBinding.secondaryLayout.sparkButton.setActiveImage(R.drawable.ic_heart_on);
            }
            else
            {
                activityMoviesInfoBinding.secondaryLayout.sparkButton.setChecked(false);
                activityMoviesInfoBinding.secondaryLayout.sparkButton.setInactiveImage(R.drawable.ic_heart_off);
            }
            activityMoviesInfoBinding.setMovie(movie);
        }
        activityMoviesInfoBinding.secondaryLayout.sparkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(((SparkButton)v).isChecked())
                {
                    mainViewModel.DeleteMovie(movie);
                    activityMoviesInfoBinding.secondaryLayout.sparkButton.playAnimation();
                    Snackbar.make(v,"Unmarked as Favourite",Snackbar.LENGTH_SHORT).show();
                    activityMoviesInfoBinding.secondaryLayout.sparkButton.setInactiveImage(R.drawable.ic_heart_off);
                    activityMoviesInfoBinding.secondaryLayout.sparkButton.setChecked(false);
                }
                else {
                    mainViewModel.AddMovie(movie);
                    Snackbar.make(v,"Marked as Favourite",Snackbar.LENGTH_SHORT).show();
                    activityMoviesInfoBinding.secondaryLayout.sparkButton.playAnimation();
                    activityMoviesInfoBinding.secondaryLayout.sparkButton.setInactiveImage(R.drawable.ic_heart_on);
                    activityMoviesInfoBinding.secondaryLayout.sparkButton.setChecked(true);

                }
            }
        });
    }

}
