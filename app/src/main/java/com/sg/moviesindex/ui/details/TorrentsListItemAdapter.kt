package com.sg.moviesindex.ui.details

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.github.leandroborgesferreira.loadingbutton.customViews.CircularProgressButton
import com.sg.moviesindex.R
import com.sg.moviesindex.data.remote.Torrent
import com.sg.moviesindex.databinding.TorrentListItemsLayoutBinding
import com.sg.moviesindex.service.TorrentFetcherService

class TorrentsListItemAdapter(
  private val torrentList: List<Torrent>?,
  private val button: CircularProgressButton,
  private val completeListener: TorrentFetcherService.OnCompleteListener,
) : RecyclerView.Adapter<TorrentsListItemAdapter.TorrentListItemViewHolder>() {
  companion object;

  override fun onCreateViewHolder(
    parent: ViewGroup,
    viewType: Int,
  ): TorrentListItemViewHolder {
    val torrentListItemsLayoutBinding: TorrentListItemsLayoutBinding =
      DataBindingUtil.inflate(
        LayoutInflater.from(parent.context),
        R.layout.torrent_list_items_layout,
        parent,
        false,
      )
    return TorrentListItemViewHolder(torrentListItemsLayoutBinding, button, completeListener)
  }

  override fun onBindViewHolder(
    holder: TorrentListItemViewHolder,
    position: Int,
  ) {
    val torrent = torrentList?.get(position)
    holder.torrentListItemsLayoutBinding.torrent = torrent
  }

  override fun getItemCount(): Int = torrentList?.size ?: 0

  class TorrentListItemViewHolder(
    val torrentListItemsLayoutBinding: TorrentListItemsLayoutBinding,
    button: CircularProgressButton,
    completeListener: TorrentFetcherService.OnCompleteListener,
  ) : RecyclerView.ViewHolder(torrentListItemsLayoutBinding.root) {
    init {
      torrentListItemsLayoutBinding.ivDownloadButton.setOnClickListener {
        completeListener.onComplete(false, torrentListItemsLayoutBinding.torrent)
        button.revertAnimation()
        button.stopAnimation()
      }
    }
  }
}
