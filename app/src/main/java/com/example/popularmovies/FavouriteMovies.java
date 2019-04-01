package com.example.popularmovies;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.res.Configuration;
import android.os.Bundle;
import android.widget.ScrollView;

import com.example.popularmovies.adapter.MoviesAdapter;
import com.example.popularmovies.databinding.ActivityFavouriteMoviesBinding;
import com.example.popularmovies.model.Movie;
import com.example.popularmovies.viewmodel.MainViewModel;

import java.util.ArrayList;
import java.util.List;

public class FavouriteMovies extends AppCompatActivity {
    private MainViewModel viewModel;
    private ArrayList<Movie> movie;
    private MoviesAdapter moviesAdapter;
    private RecyclerView recyclerView;
    private ScrollView scrollView;
    private ActivityFavouriteMoviesBinding activityFavouriteMoviesBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourite_movies);
        getSupportActionBar().setTitle("Favourite Movies");
        activityFavouriteMoviesBinding= DataBindingUtil.setContentView(FavouriteMovies.this,R.layout.activity_favourite_movies);
        scrollView=activityFavouriteMoviesBinding.scrolleViewF;
        viewModel= ViewModelProviders.of(FavouriteMovies.this).get(MainViewModel.class);
        viewModel.getAllMovies().observe(FavouriteMovies.this, new Observer<List<Movie>>() {
            @Override
            public void onChanged(List<Movie> movies) {
                movie=(ArrayList<Movie>)movies;
                showRecyclerView();
            }
        });

    }

    private void showRecyclerView() {
        recyclerView=activityFavouriteMoviesBinding.rvF;
         moviesAdapter= new MoviesAdapter(FavouriteMovies.this,movie);
        if(this.getResources().getConfiguration().orientation== Configuration.ORIENTATION_PORTRAIT)
        {
            recyclerView.setLayoutManager(new GridLayoutManager(FavouriteMovies.this,2));
        }
        else
        {recyclerView.setLayoutManager((new GridLayoutManager(FavouriteMovies.this,4)));}
        recyclerView.setAdapter(moviesAdapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
    }
}
