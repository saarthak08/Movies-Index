package com.sg.moviesindex.fragments;


import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sg.moviesindex.R;
import com.sg.moviesindex.adapter.MoviesAdapter;
import com.sg.moviesindex.databinding.FragmentFavouriteMoviesBinding;
import com.sg.moviesindex.model.Movie;
import com.sg.moviesindex.viewmodel.MainViewModel;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FavouriteMovies#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FavouriteMovies extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    private MainViewModel viewModel;
    private ArrayList<Movie> movie;
    private Context context;
    private TextView textView;
    private MoviesAdapter moviesAdapter;
    private RecyclerView recyclerView;
    private ScrollView scrollView;
    private FragmentFavouriteMoviesBinding fragmentFavouriteMoviesBinding;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    public FavouriteMovies() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FavouriteMovies.
     */
    // TODO: Rename and change types and number of parameters
    public static FavouriteMovies newInstance(String param1, String param2) {
        FavouriteMovies fragment = new FavouriteMovies();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fragmentFavouriteMoviesBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_favourite_movies, container, false);
        View view = fragmentFavouriteMoviesBinding.getRoot();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Favourite Movies");
        context = getContext();
        viewModel = ViewModelProviders.of(getActivity()).get(MainViewModel.class);
        viewModel.getAllMovies().observe(getActivity(), new Observer<List<Movie>>() {
            @Override
            public void onChanged(List<Movie> movies) {
                movie = (ArrayList<Movie>) movies;
                showRecyclerView();
            }
        });
    }

    private void showRecyclerView() {
        textView = fragmentFavouriteMoviesBinding.tvNoMovies;
        if (movie.isEmpty()) {
            textView.setVisibility(View.VISIBLE);
        } else {
            textView.setVisibility(View.GONE);
        }
        recyclerView = fragmentFavouriteMoviesBinding.rvF4;
        moviesAdapter = new MoviesAdapter(context, movie);
        if (context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            recyclerView.setLayoutManager(new GridLayoutManager(context, 2));
        } else {
            recyclerView.setLayoutManager((new GridLayoutManager(context, 4)));
        }
        recyclerView.setAdapter(moviesAdapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        moviesAdapter.notifyDataSetChanged();
    }
}

