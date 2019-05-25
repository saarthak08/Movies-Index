package com.example.popularmovies.paginationlibrary;

import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.paging.PagedListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.example.popularmovies.R;
import com.example.popularmovies.databinding.MovieListItemBinding;
import com.example.popularmovies.model.Movie;
import com.example.popularmovies.view.MoviesInfo;

/* ******

This class is the data source for the paging library for this project. But paging isn't implemented by this class in this project. Hence, this code isn't used in this project.
   But it can be used as an alternative.

******* */

public class MoviesPagedListAdapter extends PagedListAdapter<Movie,RecyclerView.ViewHolder> {
    private Context context;
    public static int VIEW_TYPE_ITEM = 0;
    public static int VIEW_TYPE_LOADING = 1;

    public MoviesPagedListAdapter(Context context) {
        super(Movie.callback);
        this.context = context;
    }



    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        if (viewType==VIEW_TYPE_ITEM) {
            MovieListItemBinding movieListItemBinding=DataBindingUtil.inflate(LayoutInflater.from(viewGroup.getContext()),
                    R.layout.movie_list_item, viewGroup, false);
            return new MoviesViewHolder(movieListItemBinding);
        }
        else {
            View view=LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.loadmore_progressbar, viewGroup, false);
            return new LoadingViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        if (viewHolder instanceof MoviesViewHolder) {
            Movie movie=getItem(i);
            ((MoviesViewHolder)viewHolder).movieListItemBinding.setMovie(movie);
        }
        else if (viewHolder instanceof LoadingViewHolder) {
        }
    }

    class MoviesViewHolder extends RecyclerView.ViewHolder {
        private MovieListItemBinding movieListItemBinding;

        public MoviesViewHolder(@NonNull final MovieListItemBinding movieListItemBinding) {
            super(movieListItemBinding.getRoot());
            this.movieListItemBinding = movieListItemBinding;
            movieListItemBinding.getRoot().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        Movie movie = getItem(position);
                        Intent i = new Intent(context, MoviesInfo.class);
                        i.putExtra("movie", movie);
                        context.startActivity(i);
                    }
                }
            });
        }
    }

    @Override
    public int getItemViewType(int position) {
        return getItem(position)==null?VIEW_TYPE_LOADING:VIEW_TYPE_ITEM;
    }

    public class LoadingViewHolder extends RecyclerView.ViewHolder {

        ProgressBar progressBar;

        public LoadingViewHolder(@NonNull View itemView) {
            super(itemView);
            progressBar = itemView.findViewById(R.id.loadmpre_progressbar);
        }
    }

}