package com.example.popularmovies;

import android.os.Bundle;

import com.example.popularmovies.fragments.FavouriteMovies;
import com.example.popularmovies.fragments.PopularMovies;
import com.example.popularmovies.model.Movie;
import com.example.popularmovies.model.MovieDBResponse;
import com.example.popularmovies.service.MovieDataService;
import com.example.popularmovies.service.RetrofitInstance;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import android.os.Handler;
import android.view.View;

import com.google.android.material.navigation.NavigationView;

import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentTransaction;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    ProgressBar progressBar;
    private String ApiKey= BuildConfig.ApiKey;
    public static ArrayList<Movie> movieList=new ArrayList<>();
    public boolean doubleBackToExitPressedOnce=false;
    FragmentTransaction fragmentTransaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        progressBar=findViewById(R.id.progressBar);
        fragmentTransaction = getSupportFragmentManager().beginTransaction();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer =findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        progressBar.animate().alpha(1).setDuration(500);
        progressBar.setIndeterminate(true);
        navigationView.getMenu().getItem(0).setChecked(true);
        getData();
    }

    private void getData() {
        final MovieDataService movieDataService= RetrofitInstance.getService();
        Call<MovieDBResponse> call=movieDataService.getPopularMovies(ApiKey);
        call.enqueue(new Callback<MovieDBResponse>() {
            @Override
            public void onResponse(Call<MovieDBResponse> call, Response<MovieDBResponse> response) {
                MovieDBResponse movieDBResponse = response.body();
                if (movieDBResponse != null && movieDBResponse.getMovies() != null) {
                    movieList = (ArrayList<Movie>) movieDBResponse.getMovies();
                    progressBar.setIndeterminate(false);
                    fragmentTransaction.add(R.id.frame_layout,new PopularMovies()).commitAllowingStateLoss();
                }
            }
            @Override
            public void onFailure(Call<MovieDBResponse> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Error!" + t.getMessage().trim(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    @Override
    public void onBackPressed() {
        DrawerLayout drawer =findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (doubleBackToExitPressedOnce) {
                super.onBackPressed();
                return;
            }
            doubleBackToExitPressedOnce = true;
            Toast.makeText(MainActivity.this,"Press \'BACK\' again to exit",Toast.LENGTH_SHORT).show();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    doubleBackToExitPressedOnce = false;
                }
            },2000);
        }
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.popmovies) {
            FragmentTransaction fragmentTransaction1;
            fragmentTransaction1=getSupportFragmentManager().beginTransaction();
            fragmentTransaction1.replace(R.id.frame_layout,new PopularMovies()).commitAllowingStateLoss();
        } else if (id == R.id.favmovies) {
            FragmentTransaction fragmentTransaction1;
            fragmentTransaction1=getSupportFragmentManager().beginTransaction();
            fragmentTransaction1.replace(R.id.frame_layout,new FavouriteMovies()).commit();

        } else if (id == R.id.poptvshows) {

        } else if (id == R.id.favtvshows) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
