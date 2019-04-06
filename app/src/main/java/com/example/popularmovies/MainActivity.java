package com.example.popularmovies;

import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.graphics.Color;
import android.os.Bundle;
import com.afollestad.materialdialogs.MaterialDialog;
import com.example.popularmovies.adapter.SearchAdapter;
import com.example.popularmovies.fragments.FavouriteMovies;
import com.example.popularmovies.fragments.Movies;
import com.example.popularmovies.model.Discover;
import com.example.popularmovies.model.DiscoverDBResponse;
import com.example.popularmovies.model.GenresList;
import com.example.popularmovies.model.GenresListDBResponse;
import com.example.popularmovies.model.Movie;
import com.example.popularmovies.model.MovieDBResponse;
import com.example.popularmovies.service.MovieDataService;
import com.example.popularmovies.service.RetrofitInstance;
import android.os.Handler;
import com.example.popularmovies.utils.DiscoverToMovie;
import com.google.android.material.navigation.NavigationView;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.GravityCompat;
import androidx.core.view.MenuItemCompat;
import androidx.cursoradapter.widget.CursorAdapter;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Toast;
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static ProgressBar progressBar;
    public static ArrayList<Movie> movieList=new ArrayList<>();
    public boolean doubleBackToExitPressedOnce=false;
    public static int category=0;
    private NavigationView navigationView;
    public static FragmentTransaction fragmentTransaction;
    public static Movies movies=new Movies();
    public static FragmentManager fragmentManager;
    public static int totalPages;
    public static int totalPagesGenres;
    public static int drawer=0;
    public static int genreid;
    private ArrayAdapter<String> arrayAdapter;
    public static int selected;
    public static ArrayList<Discover> discovers;
    public static ArrayList<GenresList> genresLists;
    public SearchView searchView;
    public ArrayAdapter<String> adapter;
    public static ArrayList<Discover> search;
    public static ArrayList<Movie> moviesearch;
    public static String queryM;


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_view,menu);
        MenuItem menuItem = menu.findItem(R.id.app_bar_search);
        SearchView searchView = (SearchView) menuItem.getActionView();
        search(searchView);
        return true;
    }

    public void search(final SearchView searchView)
    {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                for(int i=0;i<=getSupportFragmentManager().getBackStackEntryCount();i++) {
                    getSupportFragmentManager().popBackStack();
                }
                final MovieDataService movieDataService = RetrofitInstance.getService();
                String ApiKey = BuildConfig.ApiKey;
                Call<DiscoverDBResponse> call;
                queryM=query;
                drawer=3;
                call = movieDataService.search(ApiKey,false,query);
                call.enqueue(new Callback<DiscoverDBResponse>() {
                    @Override
                    public void onResponse(Call<DiscoverDBResponse> call, Response<DiscoverDBResponse> response) {
                        DiscoverDBResponse discoverDBResponse = response.body();
                        if (discoverDBResponse != null && discoverDBResponse.getResults() != null) {
                            discovers = (ArrayList<Discover>) discoverDBResponse.getResults();
                            totalPagesGenres = discoverDBResponse.getTotalPages();
                            DiscoverToMovie discoverToMovie = new DiscoverToMovie(discovers);
                            movieList = discoverToMovie.getMovies();
                            if (progressBar != null) {
                                progressBar.setIndeterminate(false);
                            }
                            fragmentTransaction = fragmentManager.beginTransaction();
                            fragmentTransaction.addToBackStack(null);
                            fragmentTransaction.add(R.id.frame_layout, movies).commitAllowingStateLoss();
                        }
                    }

                    @Override
                    public void onFailure(Call<DiscoverDBResponse> call, Throwable t) {
                        Toast.makeText(MainActivity.this, "Error!" + t.getMessage().trim(), Toast.LENGTH_SHORT).show();
                    }
                });
                return true;
            }

            @Override
            public boolean onQueryTextChange(final String newText) {
                final MovieDataService movieDataService= RetrofitInstance.getService();
                String ApiKey= BuildConfig.ApiKey;
                Call<DiscoverDBResponse> call;
                call=movieDataService.search(ApiKey,false,newText);
                call.enqueue(new Callback<DiscoverDBResponse>() {
                    @Override
                    public void onResponse(Call<DiscoverDBResponse> call, Response<DiscoverDBResponse> response) {
                        DiscoverDBResponse discoverDBResponse=response.body();
                        if(discoverDBResponse!=null&&discoverDBResponse.getResults()!=null)
                        {
                            search= (ArrayList<Discover>) discoverDBResponse.getResults();
                            DiscoverToMovie discoverToMovie=new DiscoverToMovie(search);
                            moviesearch=discoverToMovie.getMovies();
                            String a[]=new String[moviesearch.size()];
                            for(int i=0;i<a.length;i++)
                            {
                                a[i]=moviesearch.get(i).getTitle();
                            }
                            ArrayAdapter<String>  Adapter = new ArrayAdapter<String>(MainActivity.this,R.layout.search_list, a);
                            String[] columnNames = {"_id","text"};
                            MatrixCursor cursor = new MatrixCursor(columnNames);
                            String[] temp = new String[2];
                            int id = 0;
                            for(String item : a){
                                temp[0] = Integer.toString(id++);
                                temp[1] = item;
                                cursor.addRow(temp);
                            }
                            SearchAdapter searchAdapter=new SearchAdapter(MainActivity.this,cursor,true,searchView,moviesearch);
                            searchView.setSuggestionsAdapter(searchAdapter);

                        }
                    }

                    @Override
                    public void onFailure(Call<DiscoverDBResponse> call, Throwable t) {
                        Toast.makeText(MainActivity.this, "Error!" + t.getMessage().trim(), Toast.LENGTH_SHORT).show();
                    }
                });
                return true;
            }
        });
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(BuildConfig.ApiKey.isEmpty())
        {
            Toast.makeText(MainActivity.this,"Please get the API key first",Toast.LENGTH_SHORT).show();
        }
        fragmentManager=getSupportFragmentManager();
        navigationView= (NavigationView) findViewById(R.id.nav_view);
        progressBar=findViewById(R.id.progressBar);
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
        getDataFirst(category,MainActivity.this);
    }
        public static void getDataFirst(int a, final Context context) {
        final MovieDataService movieDataService= RetrofitInstance.getService();
        String ApiKey= BuildConfig.ApiKey;
        Call<MovieDBResponse> call;
        if(a==0) {
            call= movieDataService.getPopularMovies(ApiKey,1);
        }else {
            call = movieDataService.getTopRatedMovies(ApiKey,1);
        }

        call.enqueue(new Callback<MovieDBResponse>() {
            @Override
            public void onResponse(Call<MovieDBResponse> call, Response<MovieDBResponse> response) {
                MovieDBResponse movieDBResponse = response.body();
                if (movieDBResponse != null && movieDBResponse.getMovies() != null) {
                    movieList = (ArrayList<Movie>) movieDBResponse.getMovies();
                    totalPages=movieDBResponse.getTotalPages();
                    if(progressBar!=null) {
                        progressBar.setIndeterminate(false);
                    }
                        if(fragmentManager.getFragments().isEmpty()) {
                            fragmentTransaction=fragmentManager.beginTransaction();
                            fragmentTransaction.addToBackStack(null);
                            fragmentTransaction.add(R.id.frame_layout, movies).commitAllowingStateLoss();
                        }
                        else
                        {
                            fragmentTransaction=fragmentManager.beginTransaction();
                            fragmentTransaction.addToBackStack(null);
                            fragmentTransaction.replace(R.id.frame_layout,movies).commitAllowingStateLoss();
                        }
              }
            }
            @Override
            public void onFailure(Call<MovieDBResponse> call, Throwable t) {
                Toast.makeText(context, "Error!" + t.getMessage().trim(), Toast.LENGTH_SHORT).show();
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

        if (id == R.id.movies) {
            drawer=0;
            for(int i=0;i<=getSupportFragmentManager().getBackStackEntryCount();i++) {
                getSupportFragmentManager().popBackStack();
            }
            getDataFirst(drawer,MainActivity.this);
        } else if (id == R.id.favmovies) {
            FragmentTransaction fragmentTransaction1;
            fragmentTransaction1=getSupportFragmentManager().beginTransaction();
            fragmentTransaction1.replace(R.id.frame_layout,new FavouriteMovies()).commitAllowingStateLoss();
        }
        else if (id == R.id.toprated) {
            drawer=1;
            for(int i=0;i<=getSupportFragmentManager().getBackStackEntryCount();i++) {
                getSupportFragmentManager().popBackStack();
            }
            getDataFirst(drawer,MainActivity.this);
        }
        else if (id == R.id.genres) {
            drawer=2;
            for(int i=0;i<=getSupportFragmentManager().getBackStackEntryCount();i++) {
                getSupportFragmentManager().popBackStack();
            }
            getGenresList();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void getGenresList()
    {
        final MovieDataService movieDataService= RetrofitInstance.getService();
        String ApiKey= BuildConfig.ApiKey;
        Call<GenresListDBResponse> call;
        call= movieDataService.getGenresList(ApiKey);
        call.enqueue(new Callback<GenresListDBResponse>() {
            @Override
            public void onResponse(Call<GenresListDBResponse> call, Response<GenresListDBResponse> response) {
                GenresListDBResponse genresListDBResponse=response.body();
                if(genresListDBResponse!=null&&genresListDBResponse.getGenresLists()!=null)
                {
                    genresLists=(ArrayList<GenresList>) genresListDBResponse.getGenresLists();
                    String[] a=new String[genresLists.size()];
                    for (int i=0;i<genresLists.size();i++)
                    {
                        a[i]=genresLists.get(i).getName();
                    }
                    new MaterialDialog.Builder(MainActivity.this).title("Choose A Category").items(a).itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
                        @Override
                        public boolean onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {

                            selected=which;
                            genreid=genresLists.get(which).getId();
                            getFirstGenreData(genreid,MainActivity.this);
                            return false;
                        }
                    }).canceledOnTouchOutside(false).cancelable(false).show();
                }
            }

            @Override
            public void onFailure(Call<GenresListDBResponse> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Error!" + t.getMessage().trim(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static void getFirstGenreData(int genreid, final Context context){
        final MovieDataService movieDataService= RetrofitInstance.getService();
        String ApiKey= BuildConfig.ApiKey;
        Call<DiscoverDBResponse> call;
        call=movieDataService.discover(ApiKey,Integer.toString(genreid),false,false,1);
        call.enqueue(new Callback<DiscoverDBResponse>() {
            @Override
            public void onResponse(Call<DiscoverDBResponse> call, Response<DiscoverDBResponse> response) {
               DiscoverDBResponse discoverDBResponse = response.body();
                if (discoverDBResponse != null && discoverDBResponse.getResults() != null) {
                    discovers = (ArrayList<Discover>) discoverDBResponse.getResults();
                    totalPagesGenres = discoverDBResponse.getTotalPages();
                    DiscoverToMovie discoverToMovie=new DiscoverToMovie(discovers);
                    movieList=discoverToMovie.getMovies();
                    if (progressBar != null) {
                        progressBar.setIndeterminate(false);
                    }
                        fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.addToBackStack(null);
                        fragmentTransaction.add(R.id.frame_layout, movies).commitAllowingStateLoss();
                }
            }
            @Override
            public void onFailure(Call<DiscoverDBResponse> call, Throwable t) {
                Toast.makeText(context, "Error!" + t.getMessage().trim(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}
