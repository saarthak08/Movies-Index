package com.sg.moviesindex.model;

import java.util.List;
import android.os.Parcel;
import android.os.Parcelable;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class GenresListDBResponse implements Parcelable{

    @SerializedName("genres")
    @Expose
    private List<GenresList> GenresLists = null;
    public final static Parcelable.Creator<GenresListDBResponse> CREATOR = new Creator<GenresListDBResponse>() {


        @SuppressWarnings({
                "unchecked"
        })
        public GenresListDBResponse createFromParcel(Parcel in) {
            return new GenresListDBResponse(in);
        }

        public GenresListDBResponse[] newArray(int size) {
            return (new GenresListDBResponse[size]);
        }

    }
            ;

    protected GenresListDBResponse(Parcel in) {
        in.readList(this.GenresLists, (GenresList.class.getClassLoader()));
    }

    public GenresListDBResponse() {
    }

    public List<GenresList> getGenresLists() {
        return GenresLists;
    }

    public void setGenresLists(List<GenresList> GenresLists) {
        this.GenresLists = GenresLists;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeList(GenresLists);
    }

    public int describeContents() {
        return 0;
    }

}