package com.example.popularmovies.adapter;

import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.popularmovies.R;
import com.example.popularmovies.model.Movie;
import com.example.popularmovies.view.MoviesInfo;
import com.varunest.sparkbutton.SparkButton;
import com.varunest.sparkbutton.SparkButtonBuilder;

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

        public MoviesViewHolder(@NonNull final View itemView) {
            super(itemView);
            movieTitle=itemView.findViewById(R.id.tvTitle);
            rating=itemView.findViewById(R.id.tvRating);
            movieImage=itemView.findViewById(R.id.ivMovie);
            SparkButton sparkButton=new SparkButtonBuilder(context)
                    .setSecondaryColor(ContextCompat.getColor(context,R.color.heart_secondary_color))
                    .setActiveImage(R.drawable.ic_heart_on)
                    .setInactiveImage(R.drawable.ic_heart_off)
                    .setActiveImage(ContextCompat.getColor(context,R.color.heart_primary_color)).build();
            sparkButton=itemView.findViewById(R.id.heart_button);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position=getAdapterPosition();
                    if(position!=RecyclerView.NO_POSITION) {
                        Movie movie = movies.get(position);
                        Intent i = new Intent(context, MoviesInfo.class);
                        i.putExtra("movie", movie);
                        context.startActivity(i);
                    }
                }
            });
        }
    }
}
