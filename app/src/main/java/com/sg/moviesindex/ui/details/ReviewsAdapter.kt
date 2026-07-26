package com.sg.moviesindex.ui.details

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.sg.moviesindex.R
import com.sg.moviesindex.data.remote.Review
import com.sg.moviesindex.databinding.ReviewListItemBinding

class ReviewsAdapter : ListAdapter<Review, RecyclerView.ViewHolder>(DiffCallback()) {
  override fun onCreateViewHolder(
    viewGroup: ViewGroup,
    viewType: Int,
  ): RecyclerView.ViewHolder {
    val reviewListViewBinding: ReviewListItemBinding =
      DataBindingUtil.inflate(
        LayoutInflater.from(viewGroup.context),
        R.layout.review_list_item,
        viewGroup,
        false,
      )
    return ReviewsViewHolder(reviewListViewBinding)
  }

  override fun onBindViewHolder(
    viewHolder: RecyclerView.ViewHolder,
    i: Int,
  ) {
    if (viewHolder is ReviewsViewHolder) {
      val review = getItem(i)
      if (review != null) {
        viewHolder.reviewListViewBinding.review = review
      }
    }
  }

  class ReviewsViewHolder(
    val reviewListViewBinding: ReviewListItemBinding,
  ) : RecyclerView.ViewHolder(reviewListViewBinding.root)

  class DiffCallback : DiffUtil.ItemCallback<Review>() {
    override fun areItemsTheSame(
      oldItem: Review,
      newItem: Review,
    ): Boolean = oldItem.id == newItem.id

    override fun areContentsTheSame(
      oldItem: Review,
      newItem: Review,
    ): Boolean = oldItem == newItem
  }
}
