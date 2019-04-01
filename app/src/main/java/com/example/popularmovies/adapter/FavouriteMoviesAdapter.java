package com.example.popularmovies.adapter;

import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.databinding.BindingAdapter;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.popularmovies.R;
import com.example.popularmovies.databinding.MovieListItemBinding;
import com.example.popularmovies.model.Movie;
import com.example.popularmovies.MoviesInfo;

import java.util.ArrayList;

public class FavouriteMoviesAdapter extends RecyclerView.Adapter<FavouriteMoviesAdapter.FMoviesViewHolder> {
    private Context context;
    private ArrayList<Movie> movies;

    public FavouriteMoviesAdapter(Context context, ArrayList<Movie> movies) {
        this.context = context;
        this.movies = movies;
    }

    @BindingAdapter({"bind:imageUrl"})
    public static void loadImage(ImageView view, String url) {
        Glide.with(view.getContext())
                .load(url)
                .placeholder(R.drawable.loading)
                .into(view);
    }


    @NonNull
    @Override
    public FMoviesViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        MovieListItemBinding movieListItemBinding = DataBindingUtil.inflate(LayoutInflater.from(viewGroup.getContext()),
                R.layout.movie_list_item, viewGroup, false);
        return new FMoviesViewHolder(movieListItemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull FMoviesViewHolder moviesViewHolder, int i) {
        Movie movie = movies.get(i);
        moviesViewHolder.movieListItemBinding.setMovie(movie);
    }


    @Override
    public int getItemCount() {
        return movies.size();
    }

    class FMoviesViewHolder extends RecyclerView.ViewHolder {
        private MovieListItemBinding movieListItemBinding;

        public FMoviesViewHolder(@NonNull final MovieListItemBinding movieListItemBinding) {
            super(movieListItemBinding.getRoot());
            this.movieListItemBinding = movieListItemBinding;
            movieListItemBinding.getRoot().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        Movie movie = movies.get(position);
                        Intent i = new Intent(context, MoviesInfo.class);
                        i.putExtra("movie", movie);
                        i.putExtra("boolean",movie.getF_enabled());
                        context.startActivity(i);
                    }
                }
            });
        }
    }
}