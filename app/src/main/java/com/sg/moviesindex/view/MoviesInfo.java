package com.sg.moviesindex.view;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.dd.processbutton.iml.ActionProcessButton;
import com.google.android.material.snackbar.Snackbar;
import com.sg.moviesindex.BuildConfig;
import com.sg.moviesindex.R;
import com.sg.moviesindex.adapter.CastsAdapter;
import com.sg.moviesindex.adapter.ReviewsAdapter;
import com.sg.moviesindex.databinding.ActivityMoviesInfoBinding;
import com.sg.moviesindex.model.tmdb.Cast;
import com.sg.moviesindex.model.tmdb.CastsList;
import com.sg.moviesindex.model.tmdb.Movie;
import com.sg.moviesindex.model.tmdb.Review;
import com.sg.moviesindex.model.tmdb.ReviewsList;
import com.sg.moviesindex.service.network.MovieDataService;
import com.sg.moviesindex.service.network.RetrofitInstance;
import com.sg.moviesindex.utils.PaginationScrollListener;
import com.sg.moviesindex.service.TorrentFetcherService;
import com.sg.moviesindex.viewmodel.MainViewModel;
import com.varunest.sparkbutton.SparkButton;

import java.util.ArrayList;
import java.util.Locale;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

public class MoviesInfo extends AppCompatActivity  implements TorrentFetcherService.OnCompleteListener {
    private Movie movie;
    private Boolean bool;
    private ActivityMoviesInfoBinding activityMoviesInfoBinding;
    private MainViewModel mainViewModel;
    private Observable<CastsList> castsList;
    private Observable<ReviewsList> reviewsList;
    private final MovieDataService movieDataService = RetrofitInstance.getTMDbService();
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
    private ActionProcessButton btnSignIn;
    private View parentlayout;
    final static int MY_PERMISSIONS_REQUESTS_STORAGE_PERMISSIONS=3;
    private TorrentFetcherService torrentFetcherService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movies_info);
        Toolbar toolbar = findViewById(R.id.toolbar);
        parentlayout = findViewById(android.R.id.content);
        mainViewModel = ViewModelProviders.of(MoviesInfo.this).get(MainViewModel.class);
        activityMoviesInfoBinding = DataBindingUtil.setContentView(MoviesInfo.this, R.layout.activity_movies_info);
        linearLayoutManagerReviews = new LinearLayoutManager(MoviesInfo.this);
        reviewsAdapter = new ReviewsAdapter(MoviesInfo.this, reviews);
        reviews.setResults(new ArrayList<Review>());
        casts.setCast(new ArrayList<Cast>());
        reviews.setTotalPages(1);
        btnSignIn=activityMoviesInfoBinding.secondaryLayout.btnSignIn;
        torrentFetcherService = new TorrentFetcherService(this,MoviesInfo.this);
        recyclerViewReviews = activityMoviesInfoBinding.secondaryLayout.rvReviews;
        recyclerViewReviews.setLayoutManager(linearLayoutManagerReviews);
        recyclerViewReviews.setItemAnimator(new DefaultItemAnimator());
        recyclerViewCasts = activityMoviesInfoBinding.secondaryLayout.rvCasts;
        recyclerViewCasts.setLayoutManager(new LinearLayoutManager(MoviesInfo.this, LinearLayoutManager.HORIZONTAL, false));
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
        }
        btnSignIn.setMode(ActionProcessButton.Mode.ENDLESS);
        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestStoragePermissions();
            }
        });
        getParcelableData();
        setPaginationListeners();
        getReviews(1);
        getCasts();
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
            recyclerViewCasts.setAdapter(new CastsAdapter(MoviesInfo.this, casts));
            recyclerViewReviews.setAdapter(reviewsAdapter);
        }
    }

    public void getCasts() {
        castsList = movieDataService.getCasts(movie.getId(), ApiKey).doOnError(new Consumer<Throwable>() {
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
                            recyclerViewCasts.setAdapter(new CastsAdapter(MoviesInfo.this, casts));
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
        reviewsList = movieDataService.getReviews(movie.getId(), ApiKey, pageNo).doOnError(new Consumer<Throwable>() {
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
        compositeDisposable.clear();
        super.onDestroy();
    }
    @Override
    public void onComplete() {
    }



    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == MY_PERMISSIONS_REQUESTS_STORAGE_PERMISSIONS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
                torrentFetcherService.start(btnSignIn,movie);
            } else {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
                new MaterialDialog.Builder(MoviesInfo.this).title("Permission Required")
                        .content("You need to give storage permission in order to download the torrent file.\nIf permission is denied permanently, then you need to \'Go to Settings\' and manually grant the storage permission.")
                        .negativeText("Cancel")
                        .neutralText("Allow")
                        .positiveText("Go to Settings")
                        .canceledOnTouchOutside(true)
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                Intent x = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + getPackageName()));
                                startActivity(x);
                            }
                        })
                        .onNeutral(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                ActivityCompat.requestPermissions(MoviesInfo.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUESTS_STORAGE_PERMISSIONS);
                            }
                        })
                        .show();
            }
        }
    }


    public void requestStoragePermissions() {
        if (ContextCompat.checkSelfPermission(MoviesInfo.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(MoviesInfo.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUESTS_STORAGE_PERMISSIONS);
        }
        else {
            torrentFetcherService.start(btnSignIn,movie);
        }
    }
}
