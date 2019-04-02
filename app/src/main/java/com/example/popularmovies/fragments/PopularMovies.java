package com.example.popularmovies.fragments;


import android.content.Context;
import android.content.res.Configuration;
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

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.popularmovies.MainActivity;
import com.example.popularmovies.R;
import com.example.popularmovies.adapter.MoviesAdapter;
import com.example.popularmovies.databinding.FragmentPopularMoviesBinding;
import com.example.popularmovies.model.Movie;
import com.example.popularmovies.viewmodel.MainViewModel;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PopularMovies#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PopularMovies extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    ArrayList<Movie> movieList=new ArrayList<>();
    private RecyclerView recyclerView;
    private Context context;
    private MainViewModel viewModel;
    private SwipeRefreshLayout swipeRefreshLayout;
    private FragmentPopularMoviesBinding fragmentPopularMoviesBinding;

    public PopularMovies()
    {

    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PopularMovies.
     */
    // TODO: Rename and change types and number of parameters
    public static PopularMovies newInstance(String param1, String param2) {
        PopularMovies fragment = new PopularMovies();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fragmentPopularMoviesBinding= DataBindingUtil.inflate(inflater,R.layout.fragment_popular_movies,container,false);
        View view=fragmentPopularMoviesBinding.getRoot();
        return view;
    }

   @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel= ViewModelProviders.of(getActivity()).get(MainViewModel.class);
        recyclerView=fragmentPopularMoviesBinding.rv2;
        getActivity().setTitle("Popular Movies");
        context=getContext();
        swipeRefreshLayout=fragmentPopularMoviesBinding.swiperefresh2;
        movieList= MainActivity.movieList;
        swipeRefreshLayout.setColorSchemeColors(Color.BLUE, Color.DKGRAY, Color.RED,Color.GREEN,Color.MAGENTA,Color.BLACK,Color.CYAN);
        MoviesAdapter moviesAdapter= new MoviesAdapter(context,movieList);
       swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
           @Override
           public void onRefresh() {
               new Handler().postDelayed(new Runnable() {
                   @Override
                   public void run() {
                       getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout,new PopularMovies()).commitAllowingStateLoss();
                   }
               },5000);
           }
       });
        if(context.getResources().getConfiguration().orientation== Configuration.ORIENTATION_PORTRAIT)
        {
            recyclerView.setLayoutManager(new GridLayoutManager(context,2));
        }
        else
        {recyclerView.setLayoutManager((new GridLayoutManager(context,4)));}
        recyclerView.setAdapter(moviesAdapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        moviesAdapter.notifyDataSetChanged();
        swipeRefreshLayout.setRefreshing(false);

    }

}
