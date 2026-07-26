package com.sg.moviesindex.ui.search

import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cursoradapter.widget.CursorAdapter
import com.sg.moviesindex.R
import com.sg.moviesindex.data.local.Movie
import com.sg.moviesindex.ui.details.MovieDetailActivity

class SearchAdapter(
  private val context: Context,
  c: Cursor?,
  autoRequery: Boolean,
  private val movies: ArrayList<Movie>,
) : CursorAdapter(context, c, autoRequery) {
  override fun newView(
    context: Context,
    cursor: Cursor,
    parent: ViewGroup,
  ): View = LayoutInflater.from(context).inflate(R.layout.search_list, parent, false)

  override fun bindView(
    view: View,
    context: Context,
    cursor: Cursor,
  ) {
    val textView = view.findViewById<TextView>(R.id.textView2)
    val text = cursor.getString(cursor.getColumnIndexOrThrow("text"))
    textView.text = text
  }

  override fun getView(
    position: Int,
    convertView: View?,
    parent: ViewGroup,
  ): View {
    val view = super.getView(position, convertView, parent)
    view.setOnClickListener {
      if (position < movies.size) {
        val movie = movies[position]
        val intent =
          Intent(context, MovieDetailActivity::class.java).apply {
            putExtra("movie", movie)
          }
        context.startActivity(intent)
      }
    }
    return view
  }
}
