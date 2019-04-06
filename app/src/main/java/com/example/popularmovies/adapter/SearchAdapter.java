package com.example.popularmovies.adapter;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.popularmovies.R;
import com.example.popularmovies.fragments.Movies;
import com.example.popularmovies.model.Movie;

import java.util.ArrayList;

import androidx.appcompat.widget.SearchView;
import androidx.cursoradapter.widget.CursorAdapter;


public class SearchAdapter extends CursorAdapter {
    private LayoutInflater mLayoutInflater;
    private Context mContext;
    private SearchView searchView;
    public SearchAdapter(Context context, Cursor c, boolean autoRequery, SearchView searchView, ArrayList<Movie> movies) {
        super(context, c, autoRequery);
        mContext = context;
        this.searchView = searchView;
        mLayoutInflater = LayoutInflater.from(context);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View v = mLayoutInflater.inflate(R.layout.search_list, parent, false);
        return v;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        String title=cursor.getString(cursor.getColumnIndex("text"));
        TextView textView=view.findViewById(R.id.textView2);
        textView.setText(title);

    }
}
