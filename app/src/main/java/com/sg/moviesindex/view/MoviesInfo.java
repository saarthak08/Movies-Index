package com.sg.moviesindex.view;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.sg.moviesindex.BuildConfig;
import com.sg.moviesindex.R;
import com.sg.moviesindex.adapter.CastsAdapter;
import com.sg.moviesindex.adapter.ReviewsAdapter;
import com.sg.moviesindex.databinding.ActivityMoviesInfoBinding;
import com.sg.moviesindex.model.tmdb.Cast;
import com.sg.moviesindex.model.tmdb.CastsList;
import com.sg.moviesindex.model.tmdb.Genre;
import com.sg.moviesindex.model.tmdb.Movie;
import com.sg.moviesindex.model.tmdb.Review;
import com.sg.moviesindex.model.tmdb.ReviewsList;
import com.sg.moviesindex.service.TorrentDownloaderService;
import com.sg.moviesindex.service.TorrentFetcherService;
import com.sg.moviesindex.service.network.TMDbService;
import com.sg.moviesindex.service.network.RetrofitInstance;
import com.sg.moviesindex.utils.PaginationScrollListener;
import com.sg.moviesindex.viewmodel.MainViewModel;
import com.varunest.sparkbutton.SparkButton;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import antonkozyriatskyi.circularprogressindicator.CircularProgressIndicator;
import br.com.simplepass.loadingbutton.customViews.CircularProgressButton;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

public class MoviesInfo extends AppCompatActivity implements TorrentFetcherService.OnCompleteListener {
    private Movie movie;
    private Boolean bool;
    private ActivityMoviesInfoBinding activityMoviesInfoBinding;
    private MainViewModel mainViewModel;
    private Observable<CastsList> castsList;
    private Observable<ReviewsList> reviewsList;
    public static final String PROGRESS_UPDATE = "progress_update";
    private final TMDbService TMDbService = RetrofitInstance.getTMDbService();
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private String ApiKey = BuildConfig.ApiKey;
    private ReviewsAdapter reviewsAdapter;
    private ReviewsList reviews = new ReviewsList();
    private CastsList casts = new CastsList();
    private SparkButton sparkButton;
    private LinearLayoutManager linearLayoutManagerReviews;
    private PaginationScrollListener paginationScrollListenerReviews;
    private RecyclerView recyclerViewReviews;
    private LinearLayoutManager linearLayoutManagerCasts;
    private RecyclerView recyclerViewCasts;
    private CircularProgressButton btnSignIn;
    private ChipGroup chipGroup;
    private View parentlayout;
    private Observable<Movie> movieObservable;
    final static int MY_PERMISSIONS_REQUESTS_STORAGE_PERMISSIONS = 3;
    private TorrentFetcherService torrentFetcherService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movies_info);
        Toolbar toolbar = findViewById(R.id.toolbar);
        parentlayout = findViewById(android.R.id.content);
        mainViewModel= new ViewModelProvider(this).get(MainViewModel.class);
        activityMoviesInfoBinding = DataBindingUtil.setContentView(MoviesInfo.this, R.layout.activity_movies_info);
        linearLayoutManagerReviews = new LinearLayoutManager(MoviesInfo.this);
        reviewsAdapter = new ReviewsAdapter(MoviesInfo.this, reviews);
        reviews.setResults(new ArrayList<Review>());
        casts.setCast(new ArrayList<Cast>());
        reviews.setTotalPages(1);
        btnSignIn = activityMoviesInfoBinding.secondaryLayout.btnId;
        torrentFetcherService = new TorrentFetcherService(this, MoviesInfo.this);
        recyclerViewReviews = activityMoviesInfoBinding.secondaryLayout.rvReviews;
        recyclerViewReviews.setLayoutManager(linearLayoutManagerReviews);
        recyclerViewReviews.setItemAnimator(new DefaultItemAnimator());
        recyclerViewCasts = activityMoviesInfoBinding.secondaryLayout.rvCasts;
        linearLayoutManagerCasts = new LinearLayoutManager(MoviesInfo.this, LinearLayoutManager.HORIZONTAL, false);
        recyclerViewCasts.setLayoutManager(linearLayoutManagerCasts);
        recyclerViewCasts.setItemAnimator(new DefaultItemAnimator());
        Intent i = getIntent();
        if (i.hasExtra("movie")) {
            movie = i.getParcelableExtra("movie");
            bool = i.getBooleanExtra("boolean", false);
            if (MainActivity.imageup <= 2) {
                Snackbar.make(parentlayout, "Swipe Image Up For More Information!", Snackbar.LENGTH_SHORT).show();
                MainActivity.imageup++;
            }
            if (mainViewModel.getMovie(movie.getTitle()) != null) {
                activityMoviesInfoBinding.secondaryLayout.sparkButton.setChecked(true);
                activityMoviesInfoBinding.secondaryLayout.sparkButton.setActiveImage(R.drawable.ic_heart_on);
            } else {
                activityMoviesInfoBinding.secondaryLayout.sparkButton.setChecked(false);
                activityMoviesInfoBinding.secondaryLayout.sparkButton.setInactiveImage(R.drawable.ic_heart_off);
            }
            activityMoviesInfoBinding.setMovie(movie);
            activityMoviesInfoBinding.secondaryLayout.setLocale(new Locale(movie.getOriginalLanguage()).getDisplayLanguage(Locale.ENGLISH));
            chipGroup = activityMoviesInfoBinding.secondaryLayout.chipGroup;
        }
        getFullInformation();
        getParcelableData();
        setPaginationListeners();
        setProgressBar();
        getReviews(1);
        getCasts();
        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerReceiver();
                requestStoragePermissions();
            }
        });

        activityMoviesInfoBinding.secondaryLayout.sparkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (((SparkButton) v).isChecked()) {
                    mainViewModel.DeleteMovie(movie);
                    activityMoviesInfoBinding.secondaryLayout.sparkButton.playAnimation();
                    Snackbar.make(v, "Unmarked as Favourite", Snackbar.LENGTH_SHORT).show();
                    activityMoviesInfoBinding.secondaryLayout.sparkButton.setInactiveImage(R.drawable.ic_heart_off);
                    activityMoviesInfoBinding.secondaryLayout.sparkButton.setChecked(false);
                } else {
                    ArrayList<Cast> arrCasts = new ArrayList<Cast>(casts.getCast());
                    movie.setCastsList(arrCasts);
                    ArrayList<Review> arrReviews = new ArrayList<Review>(reviews.getResults());
                    movie.setReviewsList(arrReviews);
                    mainViewModel.AddMovie(movie);
                    Snackbar.make(v, "Marked as Favourite", Snackbar.LENGTH_SHORT).show();
                    activityMoviesInfoBinding.secondaryLayout.sparkButton.playAnimation();
                    activityMoviesInfoBinding.secondaryLayout.sparkButton.setInactiveImage(R.drawable.ic_heart_on);
                    activityMoviesInfoBinding.secondaryLayout.sparkButton.setChecked(true);

                }
            }
        });
    }

    public void setProgressBar() {
        CircularProgressIndicator circleProgressBar = activityMoviesInfoBinding.secondaryLayout.circularProgress;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                circleProgressBar.setProgress(movie.getVoteAverage(), 10.0);
            }
        }, 1500);
    }

    public void getFullInformation() {
        movieObservable = TMDbService.getFullMovieInformation(movie.getId(), BuildConfig.ApiKey);
        compositeDisposable.add(movieObservable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribeWith(new DisposableObserver<Movie>() {
            @Override
            public void onNext(Movie moviex) {
                if (moviex != null) {
                    movie = moviex;
                    Date date1 = null;
                    try {
                        date1 = new SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(movie.getReleaseDate());
                        DateFormat format = new SimpleDateFormat("MMM d, yyyy", Locale.US);
                        movie.setReleaseDate(format.format(date1));

                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    activityMoviesInfoBinding.setMovie(movie);
                    for (Genre x : movie.getGenres()) {
                        Chip chip = (Chip) getLayoutInflater().inflate(R.layout.chip_layout_item, chipGroup, false);
                        chip.setText(x.getName());
                        chipGroup.addView(chip);
                    }
                }
            }

            @Override
            public void onError(Throwable e) {
            }

            @Override
            public void onComplete() {
            }
        }));
    }

    public void setPaginationListeners() {
        paginationScrollListenerReviews = new PaginationScrollListener(linearLayoutManagerReviews) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                if ((page + 1) <= reviews.getTotalPages()) {
                    getReviews(page + 1);
                }
            }
        };
    }

    public void getParcelableData() {
        Intent i = getIntent();
        if (i.hasExtra("movie")) {
            movie = i.getParcelableExtra("movie");
            if (movie.getCastsList() != null) {
                casts.setCast(movie.getCastsList());
            }
            if (movie.getReviewsList() != null) {
                reviews.setResults(movie.getReviewsList());
            }
            recyclerViewReviews.setAdapter(reviewsAdapter);
            CastsAdapter castsAdapter = new CastsAdapter(MoviesInfo.this, casts);
            recyclerViewCasts.setAdapter(castsAdapter);
        }
    }

    public void getCasts() {
        castsList = TMDbService.getCasts(movie.getId(), ApiKey).doOnError(new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                getParcelableData();
            }
        });
        compositeDisposable.add(castsList.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribeWith(
                new DisposableObserver<CastsList>() {
                    @Override
                    public void onNext(CastsList castsList) {
                        if (castsList != null && castsList.getCast() != null) {
                            casts = castsList;
                            CastsAdapter castsAdapter = new CastsAdapter(MoviesInfo.this, casts);
                            recyclerViewCasts.setAdapter(castsAdapter);
                        }

                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onComplete() {

                    }
                }
        ));
    }

    public void getReviews(int pageNo) {
        reviewsList = TMDbService.getReviews(movie.getId(), ApiKey, pageNo).doOnError(new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                getParcelableData();
            }
        });
        recyclerViewReviews.setAdapter(reviewsAdapter);
        recyclerViewReviews.addOnScrollListener(paginationScrollListenerReviews);
        compositeDisposable.add(reviewsList.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableObserver<ReviewsList>() {
                    @Override
                    public void onNext(ReviewsList reviewsList) {
                        if (reviewsList != null && reviewsList.getResults() != null) {
                            reviews.setTotalPages(reviewsList.getTotalPages());
                            reviews.setPage(reviewsList.getPage());
                            reviews.setId(reviewsList.getId());
                            reviews.setTotalResults(reviewsList.getTotalResults());
                            for (Review review : reviewsList.getResults()) {
                                reviews.getResults().add(review);
                                reviewsAdapter.notifyItemInserted(reviews.getResults().size() - 1);
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onComplete() {

                    }
                }));

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        compositeDisposable.clear();
        btnSignIn.dispose();

    }

    @Override
    public void onComplete(boolean error) {
        if (!error) {
            startTorrentDownload();
        }
    }


    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_PERMISSIONS_REQUESTS_STORAGE_PERMISSIONS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
                torrentFetcherService.start(btnSignIn, movie);
            } else {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
                new MaterialAlertDialogBuilder(MoviesInfo.this).setTitle("Permission Required")
                        .setMessage("You need to give storage permission in order to download the torrent file.\nIf permission is denied permanently, then you need to \'Go to Settings\' and manually grant the storage permission.")
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .setNeutralButton("Allow", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ActivityCompat.requestPermissions(MoviesInfo.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUESTS_STORAGE_PERMISSIONS);
                            }
                        })
                        .setPositiveButton("Go to Settings", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent x = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + getPackageName()));
                                startActivity(x);
                            }
                        })
                        .setCancelable(false)
                        .show();
            }
        }
    }


    public void requestStoragePermissions() {
        if (ContextCompat.checkSelfPermission(MoviesInfo.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MoviesInfo.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUESTS_STORAGE_PERMISSIONS);
        } else {
            torrentFetcherService.start(btnSignIn, movie);
        }
    }

    private void registerReceiver() {
        LocalBroadcastManager bManager = LocalBroadcastManager.getInstance(this);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(PROGRESS_UPDATE);
        bManager.registerReceiver(mBroadcastReceiver, intentFilter);
    }

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent.getAction().equals(PROGRESS_UPDATE)) {

                boolean downloadComplete = intent.getBooleanExtra("downloadComplete", false);
                if (downloadComplete) {
                    Toast.makeText(getApplicationContext(), "File download completed", Toast.LENGTH_LONG).show();
                }
            }
        }
    };

    private void startTorrentDownload() {
        Intent intent = new Intent(this, TorrentDownloaderService.class);
        startService(intent);
    }

}
