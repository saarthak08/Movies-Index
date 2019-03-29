package com.example.popularmovies.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.popularmovies.R;
import com.example.popularmovies.model.Movie;

import java.util.ArrayList;

public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MoviesViewHolder> {
    private Context context;
    private ArrayList<Movie> movies;

    public MoviesAdapter(Context context, ArrayList<Movie> movies) {
        this.context = context;
        this.movies = movies;
    }

    @NonNull
    @Override
    public MoviesViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view=LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.movie_list_item,viewGroup,false);
        return new MoviesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MoviesViewHolder moviesViewHolder, int i) {
        moviesViewHolder.movieTitle.setText(movies.get(i).getTitle());
        moviesViewHolder.rating.setText(Double.toString(movies.get(i).getVoteAverage()));
        String imagePath="https://image.tmdb.org/t/p/w500"+movies.get(i).getPosterPath();

        Glide.with(context)
                .load(imagePath)
                .placeholder(R.drawable.loading)
                .into(moviesViewHolder.movieImage);

    }

    @Override
    public int getItemCount() {
        return movies.size();
    }

    class MoviesViewHolder extends RecyclerView.ViewHolder{
        private TextView movieTitle,rating;
        private ImageView movieImage;

        public MoviesViewHolder(@NonNull View itemView) {
            super(itemView);
            movieTitle=itemView.findViewById(R.id.tvTitle);
            rating=itemView.findViewById(R.id.tvRating);
            movieImage=itemView.findViewById(R.id.ivMovie);
        }
    }
}
