package com.sg.moviesindex.ui.details

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.sg.moviesindex.R
import com.sg.moviesindex.data.local.Movie
import com.sg.moviesindex.databinding.RecommendationListItemBinding

/**
 * Adapter for the horizontal "More Like This" recommendations carousel.
 * Renders compact 130dp movie cards.
 */
class RecommendationsAdapter(
  private val context: Context,
) : ListAdapter<Movie, RecommendationsAdapter.RecommendationViewHolder>(DiffCallback) {
  companion object DiffCallback : DiffUtil.ItemCallback<Movie>() {
    override fun areItemsTheSame(
      oldItem: Movie,
      newItem: Movie,
    ): Boolean = oldItem.id == newItem.id

    override fun areContentsTheSame(
      oldItem: Movie,
      newItem: Movie,
    ): Boolean = oldItem == newItem
  }

  override fun onCreateViewHolder(
    parent: ViewGroup,
    viewType: Int,
  ): RecommendationViewHolder {
    val binding: RecommendationListItemBinding =
      DataBindingUtil.inflate(
        LayoutInflater.from(parent.context),
        R.layout.recommendation_list_item,
        parent,
        false,
      )
    return RecommendationViewHolder(binding)
  }

  override fun onBindViewHolder(
    holder: RecommendationViewHolder,
    position: Int,
  ) {
    val movie = getItem(position)
    holder.bind(movie)
  }

  inner class RecommendationViewHolder(
    private val binding: RecommendationListItemBinding,
  ) : RecyclerView.ViewHolder(binding.root) {
    fun bind(movie: Movie) {
      binding.movie = movie
      binding.root.setOnClickListener {
        val intent =
          Intent(context, MovieDetailActivity::class.java).apply {
            putExtra("movie", movie)
          }
        context.startActivity(intent)
      }
      binding.executePendingBindings()
    }
  }
}
