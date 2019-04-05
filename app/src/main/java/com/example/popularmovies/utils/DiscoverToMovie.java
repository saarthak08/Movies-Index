package com.example.popularmovies.utils;

import com.example.popularmovies.model.Discover;
import com.example.popularmovies.model.Movie;

import java.util.ArrayList;

public class DiscoverToMovie {
    private ArrayList<Discover> discovers;
    private ArrayList<Movie> movies;

    public DiscoverToMovie(ArrayList<Discover> discovers) {
        this.discovers=discovers;
        movies=new ArrayList<>(discovers.size());
    }

    public ArrayList<Movie> getMovies()
    {
        for(int i=0;i<discovers.size();i++)
        {
            movies.add(new Movie());
            movies.get(i).setTitle(discovers.get(i).getTitle());
            movies.get(i).setAdult(discovers.get(i).getAdult());
            movies.get(i).setBackdropPath((String)discovers.get(i).getBackdropPath());
            movies.get(i).setPopularity(discovers.get(i).getPopularity());
            movies.get(i).setId(discovers.get(i).getId());
            movies.get(i).setOriginalLanguage(discovers.get(i).getOriginalLanguage());
            movies.get(i).setOriginalTitle(discovers.get(i).getOriginalTitle());
            movies.get(i).setOverview(discovers.get(i).getOverview());
            movies.get(i).setPosterPath(discovers.get(i).getPosterPath());
            movies.get(i).setReleaseDate(discovers.get(i).getReleaseDate());
            movies.get(i).setVideo(discovers.get(i).getVideo());
            movies.get(i).setVoteAverage(discovers.get(i).getVoteAverage());
            movies.get(i).setVoteCount(discovers.get(i).getVoteCount());
        }
        return movies;
    }
}
