package com.sg.moviesindex.view;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.navigation.NavigationView;
import com.sg.moviesindex.BuildConfig;
import com.sg.moviesindex.R;
import com.sg.moviesindex.fragments.FavouriteMovies;
import com.sg.moviesindex.model.Discover;
import com.sg.moviesindex.model.GenresList;
import com.sg.moviesindex.model.Movie;
import com.sg.moviesindex.service.FetchFirstTimeDataService;
import com.sg.moviesindex.service.GetGenresListService;
import com.sg.moviesindex.utils.SearchUtil;

import java.util.ArrayList;

import io.reactivex.disposables.CompositeDisposable;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private ProgressBar progressBar;
    public static ArrayList<Movie> movieList = new ArrayList<>();
    private NavigationView navigationView;
    private FragmentManager fragmentManager;
    public static int totalPages;
    public static int totalPagesGenres;
    public static int drawer = 0;
    public static int imageup = 0;
    public static int genreid;
    public static String region = "";
    public static int selected;
    public static ArrayList<Discover> discovers;
    public static ArrayList<GenresList> genresLists;
    public static ArrayList<Discover> search;
    public static ArrayList<Movie> moviesearch;
    public static String queryM;
    private GetGenresListService genresList;
    private FetchFirstTimeDataService fetchFirstTimeDataService;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (BuildConfig.ApiKey.isEmpty()) {
            Toast.makeText(MainActivity.this, "Please get the API key first", Toast.LENGTH_SHORT).show();
        }
        fragmentManager = getSupportFragmentManager();
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        progressBar = findViewById(R.id.progressBar);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        progressBar.animate().alpha(1).setDuration(500);
        progressBar.setIndeterminate(true);
        navigationView.getMenu().getItem(0).setChecked(true);
        //getDataFirst(category,MainActivity.this);
        fetchFirstTimeDataService = new FetchFirstTimeDataService(progressBar, compositeDisposable, fragmentManager);
        fetchFirstTimeDataService.getDataFirst(drawer, MainActivity.this);
        genresList = new GetGenresListService(MainActivity.this, compositeDisposable, fetchFirstTimeDataService, progressBar);
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            int fragments = getSupportFragmentManager().getBackStackEntryCount();
            if (fragments == 1) {
                finish();
            } else if (getFragmentManager().getBackStackEntryCount() > 1) {
                getFragmentManager().popBackStack();
            } else {
                super.onBackPressed();
            }
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.movies) {
            drawer = 0;
            for (int i = 0; i <= getSupportFragmentManager().getBackStackEntryCount(); i++) {
                getSupportFragmentManager().popBackStack();
            }
            fetchFirstTimeDataService.getDataFirst(drawer, MainActivity.this);
        } else if (id == R.id.favmovies) {
            FragmentTransaction fragmentTransaction1;
            for (int i = 0; i <= getSupportFragmentManager().getBackStackEntryCount(); i++) {
                getSupportFragmentManager().popBackStack();
            }
            fragmentTransaction1 = getSupportFragmentManager().beginTransaction();
            fragmentTransaction1.addToBackStack(null);
            fragmentTransaction1.replace(R.id.frame_layout, new FavouriteMovies()).commitAllowingStateLoss();
        } else if (id == R.id.toprated) {
            drawer = 1;
            for (int i = 0; i <= getSupportFragmentManager().getBackStackEntryCount(); i++) {
                getSupportFragmentManager().popBackStack();
            }
            fetchFirstTimeDataService.getDataFirst(drawer, MainActivity.this);
        } else if (id == R.id.genres) {
            drawer = 2;
            for (int i = 0; i <= getSupportFragmentManager().getBackStackEntryCount(); i++) {
                getSupportFragmentManager().popBackStack();
            }
            genresList.getGenresList();
        } else if (id == R.id.upcoming_movies) {
            drawer = 4;
            for (int i = 0; i <= getSupportFragmentManager().getBackStackEntryCount(); i++) {
                getSupportFragmentManager().popBackStack();
            }
            fetchFirstTimeDataService.getDataFirst(drawer, MainActivity.this);
        } else if (id == R.id.now_playing) {
            drawer = 5;
            for (int i = 0; i <= getSupportFragmentManager().getBackStackEntryCount(); i++) {
                getSupportFragmentManager().popBackStack();
            }
            fetchFirstTimeDataService.getDataFirst(drawer, MainActivity.this);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_view, menu);
        MenuItem menuItem = menu.findItem(R.id.app_bar_search);
        SearchView searchView = (SearchView) menuItem.getActionView();
        SearchUtil searchUtil = new SearchUtil(compositeDisposable, fragmentManager, MainActivity.this, progressBar);
        searchUtil.search(searchView);
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        compositeDisposable.clear();
    }
}
