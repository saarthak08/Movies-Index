package com.sg.moviesindex.fragments;


import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
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
import com.sg.moviesindex.service.GetGenresListService;
import com.sg.moviesindex.service.network.RetrofitInstance;
import com.sg.moviesindex.utils.PaginationScrollListener;
import com.sg.moviesindex.utils.SearchUtil;
import com.sg.moviesindex.view.MainActivity;
import com.sg.moviesindex.viewmodel.MainViewModel;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
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

    public Movies() {
        //Required Constructor
    }

    private MoviesAdapter moviesAdapter;
    private Context context;
    private SwipeRefreshLayout swipeRefreshLayout;
    private FragmentMoviesBinding fragmentMoviesBinding;
    private PaginationScrollListener paginationScrollListener;
    GridLayoutManager gridLayoutManager;
    private static CompositeDisposable compositeDisposable = new CompositeDisposable();
    private static long genreid = MainActivity.genreid;
    private FetchMoreDataService fetchMoreDataService;
    private SearchUtil searchUtil;
    private GetGenresListService getGenresListService;

    private FetchFirstTimeDataService firstTimeData;

    public Movies(FetchFirstTimeDataService fetchFirstTimeDataService, SearchUtil searchUtil) {
        firstTimeData = fetchFirstTimeDataService;
        this.searchUtil = searchUtil;
        this.getGenresListService = MainActivity.genresList;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(false);
        if (getArguments() != null) {
            // TODO: Rename and change types of parameters
            String mParam1 = getArguments().getString(ARG_PARAM1);
            String mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fragmentMoviesBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_movies, container, false);
        return fragmentMoviesBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        MainViewModel viewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
        RecyclerView recyclerView = fragmentMoviesBinding.rv2;
        swipeRefreshLayout = fragmentMoviesBinding.swiperefresh2;
        if (MainActivity.drawer == 0) {
            getActivity().setTitle("Popular Movies");
        } else if (MainActivity.drawer == 1) {
            getActivity().setTitle("Top Rated Movies");
        } else if (MainActivity.drawer == 2) {
            if (!MainActivity.genres.isEmpty()) {
                getActivity().setTitle("Genre: " + MainActivity.genres.get(MainActivity.selected).getName());
            }
        } else if (MainActivity.drawer == 3) {
            getActivity().setTitle("Search Results: " + MainActivity.queryM);
        } else if (MainActivity.drawer == 4) {
            getActivity().setTitle("Upcoming Movies: " + MainActivity.region);
        } else if (MainActivity.drawer == 5) {
            getActivity().setTitle("Now Playing: " + MainActivity.region);
        }
        context = getContext();
        ArrayList<Movie> movieList = MainActivity.movieList;
        swipeRefreshLayout.setColorSchemeColors(Color.BLUE, Color.DKGRAY, Color.RED, Color.GREEN, Color.MAGENTA, Color.BLACK, Color.CYAN);
        moviesAdapter = new MoviesAdapter(context, movieList);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                try {
                    RetrofitInstance.resetCache();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                for (int i = 0; i <= getActivity().getSupportFragmentManager().getBackStackEntryCount(); i++) {
                    getActivity().getSupportFragmentManager().popBackStack();
                }
                if (MainActivity.drawer != 2) {
                    firstTimeData.getDataFirst(MainActivity.drawer, context);
                } else {
                    getGenresListService.getGenresList();
                }
                swipeRefreshLayout.setRefreshing(false);
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
        try {
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
        } catch (Error e) {
            // Toast.makeText(getContext(),"Error in Loading Movies!",Toast.LENGTH_SHORT).show();
        }
        recyclerView.addOnScrollListener(paginationScrollListener);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        compositeDisposable.clear();
    }
}

