package com.sg.moviesindex.model.yts;


import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class Data implements Serializable, Parcelable {

    @SerializedName("movie_count")
    @Expose
    private Long movieCount;
    @SerializedName("limit")
    @Expose
    private Long limit;
    @SerializedName("page_number")
    @Expose
    private Long pageNumber;
    @SerializedName("movies")
    @Expose
    private List<Movie> movies = null;
    public final static Parcelable.Creator<Data> CREATOR = new Creator<Data>() {


        @SuppressWarnings({
                "unchecked"
        })
        public Data createFromParcel(Parcel in) {
            return new Data(in);
        }

        public Data[] newArray(int size) {
            return (new Data[size]);
        }

    };
    private final static long serialVersionUID = 8675435186511918158L;

    protected Data(Parcel in) {
        this.movieCount = ((Long) in.readValue((Long.class.getClassLoader())));
        this.limit = ((Long) in.readValue((Long.class.getClassLoader())));
        this.pageNumber = ((Long) in.readValue((Long.class.getClassLoader())));
        in.readList(this.movies, (com.sg.moviesindex.model.yts.Movie.class.getClassLoader()));
    }

    /**
     * No args constructor for use in serialization
     */
    public Data() {
    }

    /**
     * @param movies
     * @param pageNumber
     * @param movieCount
     * @param limit
     */
    public Data(Long movieCount, Long limit, Long pageNumber, List<Movie> movies) {
        super();
        this.movieCount = movieCount;
        this.limit = limit;
        this.pageNumber = pageNumber;
        this.movies = movies;
    }

    public Long getMovieCount() {
        return movieCount;
    }

    public void setMovieCount(Long movieCount) {
        this.movieCount = movieCount;
    }

    public Long getLimit() {
        return limit;
    }

    public void setLimit(Long limit) {
        this.limit = limit;
    }

    public Long getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(Long pageNumber) {
        this.pageNumber = pageNumber;
    }

    public List<Movie> getMovies() {
        return movies;
    }

    public void setMovies(List<Movie> movies) {
        this.movies = movies;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(movieCount);
        dest.writeValue(limit);
        dest.writeValue(pageNumber);
        dest.writeList(movies);
    }

    public int describeContents() {
        return 0;
    }

}
