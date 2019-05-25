package com.example.popularmovies.fragments;


import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.popularmovies.BuildConfig;
import com.example.popularmovies.view.MainActivity;
import com.example.popularmovies.R;
import com.example.popularmovies.adapter.MoviesAdapter;
import com.example.popularmovies.databinding.FragmentMoviesBinding;
import com.example.popularmovies.model.Discover;
import com.example.popularmovies.model.DiscoverDBResponse;
import com.example.popularmovies.model.Movie;
import com.example.popularmovies.model.MovieDBResponse;
import com.example.popularmovies.service.MovieDataService;
import com.example.popularmovies.service.RetrofitInstance;
import com.example.popularmovies.utils.DiscoverToMovie;
import com.example.popularmovies.utils.PaginationScrollListener;
import com.example.popularmovies.viewmodel.MainViewModel;

import java.util.ArrayList;



/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Movies#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Movies extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private ArrayList<Movie> movieList=new ArrayList<>();
    private RecyclerView recyclerView;
    private MoviesAdapter moviesAdapter;
    private Context context;
    private static Observable<MovieDBResponse> observableMovie;
    private static Observable<DiscoverDBResponse> observableDB;
    private MainViewModel viewModel;
    private SwipeRefreshLayout swipeRefreshLayout;
    private FragmentMoviesBinding fragmentMoviesBinding;
    private int selectedItem=0;
    private PaginationScrollListener paginationScrollListener;
    GridLayoutManager gridLayoutManager;
    private static CompositeDisposable compositeDisposable=new CompositeDisposable();
    private int totalPages;
    private int totalPagesGenre;
    private ArrayList<Discover> discovers=new ArrayList<>();
    public static int genreid=MainActivity.genreid;




    public Movies()
    {

    }


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Movies.
     */
    // TODO: Rename and change types and number of parameters
    public static Movies newInstance(String param1, String param2) {
        Movies fragment = new Movies();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       /* if(MainActivity.drawer==2) {
            setHasOptionsMenu(true);
        }
        else
        {
            setHasOptionsMenu(false);
        }
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }*/
    }
   @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_fragment,menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        String[] a=new String[MainActivity.genresLists.size()];
        for (int i=0;i<MainActivity.genresLists.size();i++)
        {
            a[i]=MainActivity.genresLists.get(i).getName();
        }
        selectedItem=MainActivity.selected;
       new AlertDialog.Builder(getContext()).setSingleChoiceItems(a, selectedItem, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                genreid=MainActivity.genresLists.get(which).getId();
                MainActivity.getFirstGenreData(genreid,getContext());
                selectedItem=which;
                dialog.dismiss();
                for(int i=0;i<=getActivity().getSupportFragmentManager().getBackStackEntryCount();i++) {
                    getActivity().getSupportFragmentManager().popBackStack();
                }

            }
        }).show();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fragmentMoviesBinding= DataBindingUtil.inflate(inflater,R.layout.fragment_movies,container,false);
        View view=fragmentMoviesBinding.getRoot();
        return view;
    }

   @SuppressLint("WrongConstant")
   @Override
    public void onViewCreated(@NonNull View view, @Nullable final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel= ViewModelProviders.of(getActivity()).get(MainViewModel.class);
        recyclerView=fragmentMoviesBinding.rv2;
       swipeRefreshLayout=fragmentMoviesBinding.swiperefresh2;
       if(MainActivity.drawer==0) {
            getActivity().setTitle("Popular Movies");
        }
        else if(MainActivity.drawer==1)
        {
            getActivity().setTitle("Top Rated Movies");
        }
        else if(MainActivity.drawer==2)
       {
          if(!MainActivity.genresLists.isEmpty()) {
              getActivity().setTitle("Genre: " + MainActivity.genresLists.get(MainActivity.selected).getName());
          }

       }
       else if(MainActivity.drawer==3)
       {
           getActivity().setTitle("Search Results: "+MainActivity.queryM);
       }
        context=getContext();
        movieList= MainActivity.movieList;
        swipeRefreshLayout.setColorSchemeColors(Color.BLUE, Color.DKGRAY, Color.RED,Color.GREEN,Color.MAGENTA,Color.BLACK,Color.CYAN);
        moviesAdapter= new MoviesAdapter(context,movieList);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                            swipeRefreshLayout.setRefreshing(false);
                    }
                },4000);
            }
        });
        gridLayoutManager=new GridLayoutManager(getContext(),2);
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
           @Override
           public int getSpanSize(int position) {
               switch(moviesAdapter.getItemViewType(position)){
                   case 0:
                       return 1;
                   case 1:
                       return 2;
                   default:
                       return -1;
               }
           }
       });
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setAdapter(moviesAdapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        moviesAdapter.notifyDataSetChanged();
        paginationScrollListener = new PaginationScrollListener(gridLayoutManager) {
           @Override
           public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
               if (MainActivity.drawer != 2) {
                   if ((page + 1) < MainActivity.totalPages) {
                       loadMore(MainActivity.category, page + 1);
                   }
               } else {
                   if ((page + 1) < MainActivity.totalPagesGenres) {
                       loadMoreGenres(page + 1);
                   }
               }
           }
       };
       recyclerView.addOnScrollListener(paginationScrollListener);
    }

    private void loadMore(int a, final int pages)
    {
        final MovieDataService movieDataService= RetrofitInstance.getService();
        String ApiKey=BuildConfig.ApiKey;
        if(a==0) {
            observableMovie=movieDataService.getPopularMoviesWithRx(ApiKey,pages);
        }else {
            observableMovie=movieDataService.getTopRatedMoviesWithRx(ApiKey,pages);
        }
        compositeDisposable.add(observableMovie.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
        .subscribeWith(new DisposableObserver<MovieDBResponse>() {
            @Override
            public void onNext(MovieDBResponse movieDBResponse) {
                if (movieDBResponse != null&&movieDBResponse.getMovies()!=null) {
                    if(pages==1) {
                        movieList=(ArrayList<Movie>) movieDBResponse.getMovies();
                        totalPages = movieDBResponse.getTotalPages();
                        recyclerView.setAdapter(moviesAdapter);
                    } else {
                        ArrayList<Movie> movies=(ArrayList<Movie>)movieDBResponse.getMovies();
                        for (Movie movie : movies) {
                            movieList.add(movie);
                            moviesAdapter.notifyItemInserted(movieList.size() - 1);
                        }
                    }
                }

            }

            @Override
            public void onError(Throwable e) {
                Toast.makeText(getActivity(), "Error!" + e.getMessage().trim(), Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onComplete() {

            }
        }));
    }

    private void loadMoreGenres(final int pages)
    {
        final MovieDataService movieDataService= RetrofitInstance.getService();
        String ApiKey= BuildConfig.ApiKey;
        observableDB=movieDataService.discover(ApiKey,Integer.toString(MainActivity.genreid),false,false,pages);
        compositeDisposable.add(
                observableDB.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableObserver<DiscoverDBResponse>() {
                    @Override
                    public void onNext(DiscoverDBResponse discoverDBResponse) {
                        if (discoverDBResponse != null && discoverDBResponse.getResults() != null) {
                            if(pages==1) {
                                discovers=(ArrayList<Discover>)discoverDBResponse.getResults();
                                totalPagesGenre =discoverDBResponse.getTotalPages();
                                recyclerView.setAdapter(moviesAdapter);
                            } else {
                                ArrayList<Discover> discovers =(ArrayList<Discover>)discoverDBResponse.getResults();
                                DiscoverToMovie discoverToMovie= new DiscoverToMovie(discovers);
                                ArrayList<Movie> movies=discoverToMovie.getMovies();
                                for (Movie movie : movies) {
                                    movieList.add(movie);
                                    moviesAdapter.notifyItemInserted(movieList.size() - 1);
                                }
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(getActivity(), "Error!" + e.getMessage().trim(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onComplete() {

                    }
                })
        );
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        compositeDisposable.clear();
    }
}

