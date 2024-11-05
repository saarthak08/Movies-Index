package com.sg.moviesindex.adapter;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.cursoradapter.widget.CursorAdapter;

import com.sg.moviesindex.R;
import com.sg.moviesindex.model.tmdb.Movie;
import com.sg.moviesindex.view.MoviesInfo;

import java.util.ArrayList;


public class SearchAdapter extends CursorAdapter {
  private final LayoutInflater mLayoutInflater;
  private final Context mContext;
  private final ArrayList<Movie> movies;

  public SearchAdapter(Context context, Cursor c, boolean autoRequery, ArrayList<Movie> movies) {
    super(context, c, autoRequery);
    mContext = context;
    mLayoutInflater = LayoutInflater.from(context);
    this.movies = movies;
  }

  @Override
  public View newView(Context context, Cursor cursor, ViewGroup parent) {
    return mLayoutInflater.inflate(R.layout.search_list, parent, false);
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
    int columnIndex = cursor.getColumnIndex("text");
    if (columnIndex != -1) {
      String title = cursor.getString(columnIndex);
      TextView textView = view.findViewById(R.id.textView2);
      textView.setText(title);
      view.setOnClickListener(v -> {
        int id = (Integer) view.getTag();//here is the position
        Movie movie = movies.get(id);
        Intent i = new Intent(mContext, MoviesInfo.class);
        i.putExtra("movie", movie);
        mContext.startActivity(i);
      });
    }

  }
}
