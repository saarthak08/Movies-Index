package com.sg.moviesindex.ui.details

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.sg.moviesindex.R
import com.sg.moviesindex.data.remote.Cast
import com.sg.moviesindex.databinding.CastListItemBinding

class CastsAdapter : ListAdapter<Cast, RecyclerView.ViewHolder>(DiffCallback()) {
  companion object {
    private const val VIEW_TYPE_ITEM = 0
    private const val VIEW_TYPE_LOADING = 1
  }

  override fun onCreateViewHolder(
    viewGroup: ViewGroup,
    viewType: Int,
  ): RecyclerView.ViewHolder =
    if (viewType == VIEW_TYPE_ITEM) {
      val castListItemBinding: CastListItemBinding =
        DataBindingUtil.inflate(
          LayoutInflater.from(viewGroup.context),
          R.layout.cast_list_item,
          viewGroup,
          false,
        )
      CastsViewHolder(castListItemBinding)
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
    if (viewHolder is CastsViewHolder) {
      val cast = getItem(i)
      if (cast != null) {
        viewHolder.castListItemBinding.cast = cast
        viewHolder.castListItemBinding.gender =
          when (cast.gender) {
            1 -> "Female"
            2 -> "Male"
            else -> "Unknown"
          }
      }
    }
  }

  override fun getItemViewType(position: Int): Int =
    if (getItem(position) == null) VIEW_TYPE_LOADING else VIEW_TYPE_ITEM

  class CastsViewHolder(
    val castListItemBinding: CastListItemBinding,
  ) : RecyclerView.ViewHolder(castListItemBinding.root)

  class LoadingViewHolder(
    itemView: View,
  ) : RecyclerView.ViewHolder(itemView) {
    val progressBar: ProgressBar = itemView.findViewById(R.id.loadmpre_progressbar)
  }

  class DiffCallback : DiffUtil.ItemCallback<Cast>() {
    override fun areItemsTheSame(
      oldItem: Cast,
      newItem: Cast,
    ): Boolean = oldItem.id == newItem.id

    override fun areContentsTheSame(
      oldItem: Cast,
      newItem: Cast,
    ): Boolean = oldItem == newItem
  }
}
