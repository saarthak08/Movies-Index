package com.example.popularmovies.view;

import android.content.Context;
import android.database.MatrixCursor;
import android.os.Bundle;
import com.afollestad.materialdialogs.MaterialDialog;
import com.example.popularmovies.BuildConfig;
import com.example.popularmovies.R;
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
import com.example.popularmovies.utils.DiscoverToMovie;
import com.google.android.material.navigation.NavigationView;
import com.jakewharton.rxbinding3.widget.RxSearchView;
import com.jakewharton.rxbinding3.widget.RxTextView;
import com.jakewharton.rxbinding3.widget.SearchViewQueryTextEvent;

import androidx.appcompat.widget.SearchView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static ProgressBar progressBar;
    public static ArrayList<Movie> movieList=new ArrayList<>();
    public static int category=0;
    private NavigationView navigationView;
    public static FragmentTransaction fragmentTransaction;
    public static Movies movies=new Movies();
    public static FragmentManager fragmentManager;
    public static int totalPages;
    public static int totalPagesGenres;
    public static int drawer=0;
    public static int imageup=0;
    public static int genreid;
    private static Observable<MovieDBResponse> observableMovie;
    private static Observable<DiscoverDBResponse> observableDB;
    private static Observable<GenresListDBResponse> genresListObservable;
    public static int selected;
    public static ArrayList<Discover> discovers;
    public static ArrayList<GenresList> genresLists;
    public static ArrayList<Discover> search;
    public static ArrayList<Movie> moviesearch;
    public static String queryM;
    private MatrixCursor cursor;
    private static CompositeDisposable compositeDisposable=new CompositeDisposable();

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
        //getDataFirst(category,MainActivity.this);
        getDataFirst(category,MainActivity.this);
    }

    public static void getDataFirst(int a, final Context context) {
        final MovieDataService movieDataService= RetrofitInstance.getService();
        String ApiKey= BuildConfig.ApiKey;

        if(a==0) {
            observableMovie= movieDataService.getPopularMoviesWithRx(ApiKey,1);
        }else {
            observableMovie = movieDataService.getTopRatedMoviesWithRx(ApiKey,1);
        }
        compositeDisposable.add(
        observableMovie.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableObserver<MovieDBResponse>() {
                    @Override
                    public void onNext(MovieDBResponse movieDBResponse) {
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
                    public void onError(Throwable e) {
                        Toast.makeText(context, "Error!" + e.getMessage().trim(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onComplete() {

                    }
        }));
    }
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
        else {
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
            for(int i=0;i<=getSupportFragmentManager().getBackStackEntryCount();i++) {
                getSupportFragmentManager().popBackStack();
            }
            fragmentTransaction1=getSupportFragmentManager().beginTransaction();
            fragmentTransaction1.addToBackStack(null);
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
        drawer=2;
        genresListObservable= movieDataService.getGenresList(ApiKey);
        compositeDisposable.add(
                genresListObservable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableObserver<GenresListDBResponse>() {
                    @Override
                    public void onNext(GenresListDBResponse genresListDBResponse) {
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
                                    return true;
                                }
                            }).canceledOnTouchOutside(false).cancelable(false).show();
                        }

                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(MainActivity.this, "Error!" + e.getMessage().trim(), Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onComplete() {

                    }
                })
        );
    }

    public static void getFirstGenreData(int genreid, final Context context){
        final MovieDataService movieDataService= RetrofitInstance.getService();
        String ApiKey= BuildConfig.ApiKey;
        observableDB=movieDataService.discover(ApiKey,Integer.toString(genreid),false,false,1);
        compositeDisposable.add(
        observableDB.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableObserver<DiscoverDBResponse>() {
                    @Override
                    public void onNext(DiscoverDBResponse discoverDBResponse) {
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
                    public void onError(Throwable e) {
                        Toast.makeText(context, "Error!" + e.getMessage().trim(), Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onComplete() {

                    }
                })
        );
    }


    public void search(final SearchView searchView) {
       /* compositeDisposable.add(
        RxSearchView.queryTextChanges(searchView).debounce(400,TimeUnit.MILLISECONDS)
                .map(new Function<CharSequence, String>() {
                    @Override
                    public String apply(CharSequence charSequence) throws Exception {
                        return charSequence.toString().trim();
                    }
                })
                .filter(new Predicate<String>() {
                    @Override
                    public boolean test(String s) throws Exception {
                        return !s.isEmpty();
                    }
                })
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(Schedulers.io())
                .distinctUntilChanged()
                .switchMap(new Function<String, ObservableSource<DiscoverDBResponse>>() {

                     @Override
                    public ObservableSource<DiscoverDBResponse> apply(String charSequence) throws Exception {
                         queryM=charSequence;
                         final MovieDataService movieDataService = RetrofitInstance.getService();
                    String ApiKey = BuildConfig.ApiKey;
                    observableDB = movieDataService.search(ApiKey, false, charSequence);
                    return observableDB;
                  }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableObserver<DiscoverDBResponse>() {
                    @Override
                    public void onNext(DiscoverDBResponse discoverDBResponse) {
                        if (discoverDBResponse != null && discoverDBResponse.getResults() != null) {
                            search = (ArrayList<Discover>) discoverDBResponse.getResults();
                            DiscoverToMovie discoverToMovie = new DiscoverToMovie(search);
                            moviesearch = discoverToMovie.getMovies();
                            String a[] = new String[moviesearch.size()];
                            for (int i = 0; i < a.length; i++) {
                                a[i] = moviesearch.get(i).getTitle();
                            }
                            ArrayAdapter<String> Adapter = new ArrayAdapter<String>(MainActivity.this, R.layout.search_list, a);
                            String[] columnNames = {"_id", "text"};
                           cursor = new MatrixCursor(columnNames);
                            String[] temp = new String[2];
                            int id = 0;
                            for (String item : a) {
                                temp[0] = Integer.toString(id++);
                                temp[1] = item;
                                cursor.addRow(temp);

                            }
                            SearchAdapter searchAdapter = new SearchAdapter(MainActivity.this, cursor, true, searchView, moviesearch);
                            searchView.setSuggestionsAdapter(searchAdapter);
                        }
                    }
                    @Override
                    public void onError(Throwable e) {
                        Log.d("SearchError",e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                }));*/

       searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
           @Override
           public boolean onQueryTextSubmit(String query) {
               for (int i = 0; i <= getSupportFragmentManager().getBackStackEntryCount(); i++) {
                   getSupportFragmentManager().popBackStack();
               }
               if (!query.isEmpty()) {
                   searchView.setQuery("",false);
                   searchView.clearFocus();
                   searchView.setIconified(true);
                   final MovieDataService movieDataService = RetrofitInstance.getService();
                   final String ApiKey = BuildConfig.ApiKey;
                   queryM = query;
                   drawer = 3;
                   observableDB = movieDataService.search(ApiKey, false, query);
                   compositeDisposable.add(observableDB
                           .subscribeOn(Schedulers.io())
                           .observeOn(AndroidSchedulers.mainThread())
                           .subscribeWith(new DisposableObserver<DiscoverDBResponse>() {
                               @Override
                               public void onNext(DiscoverDBResponse discoverDBResponse) {
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
                               public void onError(Throwable e) {
                                   Toast.makeText(MainActivity.this, "Error!", Toast.LENGTH_SHORT).show();

                               }

                               @Override
                               public void onComplete() {

                               }
                           }));
               }
               return true;
           }

           @Override
           public boolean onQueryTextChange(String newText) {
               final MovieDataService movieDataService = RetrofitInstance.getService();
               final String ApiKey = BuildConfig.ApiKey;
               queryM = newText;
               drawer = 3;
               observableDB = movieDataService.search(ApiKey, false, queryM);
               compositeDisposable.add(observableDB
                     //  .debounce(400,TimeUnit.MILLISECONDS)
                       .subscribeOn(Schedulers.io())
                       .observeOn(AndroidSchedulers.mainThread())
                       .subscribeWith(new DisposableObserver<DiscoverDBResponse>() {
                           @Override
                           public void onNext(DiscoverDBResponse discoverDBResponse) {
                               if (discoverDBResponse != null && discoverDBResponse.getResults() != null) {
                                   search = (ArrayList<Discover>) discoverDBResponse.getResults();
                                   DiscoverToMovie discoverToMovie = new DiscoverToMovie(search);
                                   moviesearch = discoverToMovie.getMovies();
                                   String a[] = new String[moviesearch.size()];
                                   for (int i = 0; i < a.length; i++) {
                                       a[i] = moviesearch.get(i).getTitle();
                                   }
                                   ArrayAdapter<String> Adapter = new ArrayAdapter<String>(MainActivity.this, R.layout.search_list, a);
                                   String[] columnNames = {"_id", "text"};
                                   cursor = new MatrixCursor(columnNames);
                                   String[] temp = new String[2];
                                   int id = 0;
                                   for (String item : a) {
                                       temp[0] = Integer.toString(id++);
                                       temp[1] = item;
                                       cursor.addRow(temp);

                                   }
                                   SearchAdapter searchAdapter=new SearchAdapter(MainActivity.this, cursor,true,searchView,moviesearch);
                                   searchView.setSuggestionsAdapter(searchAdapter);
                               }
                           }

                           @Override
                           public void onError(Throwable e) {

                           }

                           @Override
                           public void onComplete() {

                           }
                       }));
           return true;
           }
    });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_view,menu);
        MenuItem menuItem = menu.findItem(R.id.app_bar_search);
        SearchView searchView = (SearchView) menuItem.getActionView();
        search(searchView);
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        compositeDisposable.clear();
    }


     /* public static void getDataFirst(int a, final Context context) {
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
    }*/
}
