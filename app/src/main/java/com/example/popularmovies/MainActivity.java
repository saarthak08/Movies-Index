package com.example.popularmovies;

import android.content.res.Configuration;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.example.popularmovies.adapter.MoviesAdapter;
import com.example.popularmovies.model.Movie;
import com.example.popularmovies.model.MovieDBResponse;
import com.example.popularmovies.service.MovieDataService;
import com.example.popularmovies.service.RetrofitInstance;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    private String ApiKey=BuildConfig.ApiKey;
    ArrayList<Movie> movies=new ArrayList<>();
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getData();
    }

    private void getData() {
        final MovieDataService movieDataService= RetrofitInstance.getService();
        Call<MovieDBResponse> call=movieDataService.getPopularMovies(ApiKey);
        call.enqueue(new Callback<MovieDBResponse>() {
            @Override
            public void onResponse(Call<MovieDBResponse> call, Response<MovieDBResponse> response) {
                MovieDBResponse movieDBResponse=response.body();
                if(movieDBResponse!=null&&movieDBResponse.getMovies()!=null)
                {
                    movies=(ArrayList<Movie>)movieDBResponse.getMovies();
                    showOnRecyclerView();
                }
            }

            @Override
            public void onFailure(Call<MovieDBResponse> call, Throwable t) {

            }
        });

    }

    private void showOnRecyclerView() {
        recyclerView=findViewById(R.id.recycler_view);
        MoviesAdapter moviesAdapter= new MoviesAdapter(MainActivity.this,movies);
        if(this.getResources().getConfiguration().orientation==Configuration.ORIENTATION_PORTRAIT)
        {
            recyclerView.setLayoutManager(new GridLayoutManager(MainActivity.this,2));
        }
        else
            recyclerView.setLayoutManager((new GridLayoutManager(MainActivity.this,4)));
        recyclerView.setAdapter(moviesAdapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        moviesAdapter.notifyDataSetChanged();
    }
}
