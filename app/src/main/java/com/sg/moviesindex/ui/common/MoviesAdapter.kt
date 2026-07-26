package com.sg.moviesindex.ui.common

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.sg.moviesindex.R
import com.sg.moviesindex.data.local.Movie
import com.sg.moviesindex.databinding.MovieListItemBinding
import com.sg.moviesindex.ui.details.MovieDetailActivity
import java.text.SimpleDateFormat
import java.util.Locale

class MoviesAdapter(
  private val context: Context,
) : ListAdapter<Movie, RecyclerView.ViewHolder>(DiffCallback()) {
  companion object {
    private const val VIEW_TYPE_ITEM = 0
    private const val VIEW_TYPE_LOADING = 1
  }

  override fun onCreateViewHolder(
    viewGroup: ViewGroup,
    viewType: Int,
  ): RecyclerView.ViewHolder =
    if (viewType == VIEW_TYPE_ITEM) {
      val movieListItemBinding: MovieListItemBinding =
        DataBindingUtil.inflate(
          LayoutInflater.from(viewGroup.context),
          R.layout.movie_list_item,
          viewGroup,
          false,
        )
      MoviesViewHolder(movieListItemBinding)
    } else {
      val view =
        LayoutInflater
          .from(viewGroup.context)
          .inflate(R.layout.loadmore_progressbar, viewGroup, false)
      LoadingViewHolder(view)
    }

  override fun onBindViewHolder(
    viewHolder: RecyclerView.ViewHolder,
    i: Int,
  ) {
    if (viewHolder is MoviesViewHolder) {
      val movie = getItem(i)
      if (movie != null) {
        try {
          val releaseDate = movie.releaseDate
          if ((releaseDate != null) && !releaseDate.contains(",")) {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
            val date = inputFormat.parse(releaseDate)
            if (date != null) {
              val outputFormat = SimpleDateFormat("MMM d, yyyy", Locale.US)
              movie.releaseDate = outputFormat.format(date)
            }
          }
        } catch (e: Exception) {
          Log.e("MoviesAdapter", "Error parsing date", e)
        }
        viewHolder.movieListItemBinding.movie = movie
      }
    }
  }

  override fun getItemViewType(position: Int): Int =
    if (getItem(position) == null) VIEW_TYPE_LOADING else VIEW_TYPE_ITEM

  class LoadingViewHolder(
    itemView: View,
  ) : RecyclerView.ViewHolder(itemView) {
    // No specific implementation needed here
  }

  inner class MoviesViewHolder(
    val movieListItemBinding: MovieListItemBinding,
  ) : RecyclerView.ViewHolder(movieListItemBinding.root) {
    init {
      movieListItemBinding.root.setOnClickListener {
        val position = absoluteAdapterPosition
        if (position != RecyclerView.NO_POSITION) {
          val movie = getItem(position)
          if (movie != null) {
            val intent =
              Intent(context, MovieDetailActivity::class.java).apply {
                putExtra("movie", movie)
              }
            context.startActivity(intent)
          }
        }
      }
    }
  }

  private class DiffCallback : DiffUtil.ItemCallback<Movie>() {
    override fun areItemsTheSame(
      oldItem: Movie,
      newItem: Movie,
    ): Boolean = oldItem.id == newItem.id

    override fun areContentsTheSame(
      oldItem: Movie,
      newItem: Movie,
    ): Boolean = oldItem == newItem
  }
}
