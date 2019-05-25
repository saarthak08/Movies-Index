package com.example.popularmovies.adapter;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.SearchView;
import android.widget.TextView;

import com.example.popularmovies.view.MoviesInfo;
import com.example.popularmovies.R;
import com.example.popularmovies.model.Movie;

import java.util.ArrayList;


public class SearchAdapter extends CursorAdapter {
    private LayoutInflater mLayoutInflater;
    private Context mContext;
    private android.widget.SearchView searchView;
    private ArrayList<Movie> movies;
    public SearchAdapter(Context context, Cursor c, boolean autoRequery, SearchView searchView, ArrayList<Movie> movies) {
        super(context, c, autoRequery);
        mContext = context;
        this.searchView = searchView;
        mLayoutInflater = LayoutInflater.from(context);
        this.movies=movies;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View v = mLayoutInflater.inflate(R.layout.search_list, parent, false);
        return v;
    }

    @Override
    public View getView(int position, View convertview, ViewGroup arg2) {
        if (convertview == null) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertview = inflater.inflate(R.layout.search_list,
                    null);
        }
        convertview.setTag(position);
        return super.getView(position, convertview, arg2);
    }

    @Override
    public void bindView(final View view, Context context, final Cursor cursor) {
        String title=cursor.getString(cursor.getColumnIndex("text"));
        TextView textView=view.findViewById(R.id.textView2);
        textView.setText(title);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //String id=cursor.getString(cursor.getColumnIndex("_id"));
                int id=(Integer) view.getTag();//here is the position
                Movie movie=movies.get(id);
                Intent i = new Intent(mContext, MoviesInfo.class);
                i.putExtra("movie", movie);
                mContext.startActivity(i);
            }
        });

    }
}
