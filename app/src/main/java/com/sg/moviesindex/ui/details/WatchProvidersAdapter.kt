package com.sg.moviesindex.ui.details

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.sg.moviesindex.R
import com.sg.moviesindex.data.remote.WatchProvider
import com.sg.moviesindex.databinding.WatchProviderItemBinding

class WatchProvidersAdapter(
  private val onItemClick: ((WatchProvider) -> Unit)? = null,
) : ListAdapter<WatchProvider, WatchProvidersAdapter.WatchProviderViewHolder>(DiffCallback) {
  companion object DiffCallback : DiffUtil.ItemCallback<WatchProvider>() {
    override fun areItemsTheSame(
      oldItem: WatchProvider,
      newItem: WatchProvider,
    ): Boolean = oldItem.providerId == newItem.providerId

    override fun areContentsTheSame(
      oldItem: WatchProvider,
      newItem: WatchProvider,
    ): Boolean = oldItem == newItem
  }

  override fun onCreateViewHolder(
    parent: ViewGroup,
    viewType: Int,
  ): WatchProviderViewHolder {
    val binding: WatchProviderItemBinding =
      DataBindingUtil.inflate(
        LayoutInflater.from(parent.context),
        R.layout.watch_provider_item,
        parent,
        false,
      )
    return WatchProviderViewHolder(binding)
  }

  override fun onBindViewHolder(
    holder: WatchProviderViewHolder,
    position: Int,
  ) {
    val provider = getItem(position)
    holder.bind(provider)
  }

  inner class WatchProviderViewHolder(
    private val binding: WatchProviderItemBinding,
  ) : RecyclerView.ViewHolder(binding.root) {
    fun bind(provider: WatchProvider) {
      binding.provider = provider
      binding.root.setOnClickListener {
        onItemClick?.invoke(provider)
      }
      binding.executePendingBindings()
    }
  }
}
