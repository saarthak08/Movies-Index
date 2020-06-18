package com.sg.moviesindex.fragments;


import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.sg.moviesindex.R;
import com.sg.moviesindex.adapter.MoviesAdapter;
import com.sg.moviesindex.databinding.FragmentMoviesBinding;
import com.sg.moviesindex.model.tmdb.Movie;
import com.sg.moviesindex.service.FetchFirstTimeDataService;
import com.sg.moviesindex.service.FetchMoreDataService;
import com.sg.moviesindex.utils.PaginationScrollListener;
import com.sg.moviesindex.utils.SearchUtil;
import com.sg.moviesindex.view.MainActivity;
import com.sg.moviesindex.viewmodel.MainViewModel;

import java.util.ArrayList;

import io.reactivex.disposables.CompositeDisposable;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Movies #newInstance} factory method to
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
    private ArrayList<Movie> movieList = new ArrayList<>();
    private RecyclerView recyclerView;
    private MoviesAdapter moviesAdapter;
    private Context context;
    private MainViewModel viewModel;
    private SwipeRefreshLayout swipeRefreshLayout;
    private FragmentMoviesBinding fragmentMoviesBinding;
    private int selectedItem = 0;
    private PaginationScrollListener paginationScrollListener;
    GridLayoutManager gridLayoutManager;
    private static CompositeDisposable compositeDisposable = new CompositeDisposable();
    private static int genreid = MainActivity.genreid;
    private FetchMoreDataService fetchMoreDataService;
    private SearchUtil searchUtil;

    private FetchFirstTimeDataService firstTimeData;

    public Movies(FetchFirstTimeDataService fetchFirstTimeDataService, SearchUtil searchUtil) {
        firstTimeData = fetchFirstTimeDataService;
        this.searchUtil = searchUtil;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_fragment, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        String[] a = new String[MainActivity.genresLists.size()];
        for (int i = 0; i < MainActivity.genresLists.size(); i++) {
            a[i] = MainActivity.genresLists.get(i).getName();
        }
        selectedItem = MainActivity.selected;
        new AlertDialog.Builder(getContext()).setSingleChoiceItems(a, selectedItem, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                genreid = MainActivity.genresLists.get(which).getId();
                firstTimeData.getFirstGenreData(genreid, getContext());
                selectedItem = which;
                dialog.dismiss();
                for (int i = 0; i <= getActivity().getSupportFragmentManager().getBackStackEntryCount(); i++) {
                    getActivity().getSupportFragmentManager().popBackStack();
                }

            }
        }).show();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fragmentMoviesBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_movies, container, false);
        View view = fragmentMoviesBinding.getRoot();
        return view;
    }

    @SuppressLint("WrongConstant")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = ViewModelProviders.of(getActivity()).get(MainViewModel.class);
        recyclerView = fragmentMoviesBinding.rv2;
        swipeRefreshLayout = fragmentMoviesBinding.swiperefresh2;
        if (MainActivity.drawer == 0) {
            getActivity().setTitle("Popular Movies");
        } else if (MainActivity.drawer == 1) {
            getActivity().setTitle("Top Rated Movies");
        } else if (MainActivity.drawer == 2) {
            if (!MainActivity.genresLists.isEmpty()) {
                getActivity().setTitle("Genre: " + MainActivity.genresLists.get(MainActivity.selected).getName());
            }
        } else if (MainActivity.drawer == 3) {
            getActivity().setTitle("Search Results: " + MainActivity.queryM);
        } else if (MainActivity.drawer == 4) {
            getActivity().setTitle("Upcoming Movies: " + MainActivity.region);
        } else if (MainActivity.drawer == 5) {
            getActivity().setTitle("Now Playing: " + MainActivity.region);
        }
        context = getContext();
        movieList = MainActivity.movieList;
        swipeRefreshLayout.setColorSchemeColors(Color.BLUE, Color.DKGRAY, Color.RED, Color.GREEN, Color.MAGENTA, Color.BLACK, Color.CYAN);
        moviesAdapter = new MoviesAdapter(context, movieList);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(false);
                        gridLayoutManager.scrollToPosition(0);
                        gridLayoutManager.scrollToPositionWithOffset(0, 0);
                    }
                }, 4000);
            }
        });
        gridLayoutManager = new GridLayoutManager(getContext(), 2);
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                switch (moviesAdapter.getItemViewType(position)) {
                    case 0:
                        return 1;
                    case 1:
                        return 2;
                    default:
                        return -1;
                }
            }
        });
        gridLayoutManager.scrollToPosition(0);
        gridLayoutManager.scrollToPositionWithOffset(0, 0);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setAdapter(moviesAdapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        moviesAdapter.notifyDataSetChanged();
        fetchMoreDataService = new FetchMoreDataService(recyclerView, movieList, compositeDisposable, getActivity(), moviesAdapter);
        paginationScrollListener = new PaginationScrollListener(gridLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                if (MainActivity.drawer == 0 || MainActivity.drawer == 1 || MainActivity.drawer == 4 || MainActivity.drawer == 5) {
                    if ((page + 1) <= MainActivity.totalPages) {
                        fetchMoreDataService.loadMore(MainActivity.drawer, page + 1);
                    }
                } else if (MainActivity.drawer == 2) {
                    if ((page + 1) <= MainActivity.totalPagesGenres) {
                        fetchMoreDataService.loadMoreGenres(page + 1);
                    }
                } else if (MainActivity.drawer == 3) {
                    if ((page + 1) <= MainActivity.totalPages) {
                        searchUtil.loadMoreSearches(page + 1, MainActivity.queryM);
                    }
                }
            }
        };
        recyclerView.addOnScrollListener(paginationScrollListener);
    }


    @Override
    public void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }
}

