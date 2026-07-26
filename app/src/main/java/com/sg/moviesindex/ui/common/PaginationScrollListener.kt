package com.sg.moviesindex.ui.common

import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager

abstract class PaginationScrollListener : RecyclerView.OnScrollListener {
  private val mLayoutManager: RecyclerView.LayoutManager
  private val startingPageIndex = 0
  private var visibleThreshold = 5
  private var currentPage = 0
  private var previousTotalItemCount = 0
  private var loading = true

  constructor(layoutManager: LinearLayoutManager) {
    this.mLayoutManager = layoutManager
  }

  constructor(layoutManager: GridLayoutManager) {
    this.mLayoutManager = layoutManager
    visibleThreshold *= layoutManager.spanCount
  }

  private fun getLastVisibleItem(lastVisibleItemPositions: IntArray): Int {
    var maxSize = 0
    for (i in lastVisibleItemPositions.indices) {
      if (i == 0) {
        maxSize = lastVisibleItemPositions[i]
      } else if (lastVisibleItemPositions[i] > maxSize) {
        maxSize = lastVisibleItemPositions[i]
      }
    }
    return maxSize
  }

  override fun onScrolled(
    view: RecyclerView,
    dx: Int,
    dy: Int,
  ) {
    var lastVisibleItemPosition = 0
    val totalItemCount = mLayoutManager.itemCount

    when (mLayoutManager) {
      is StaggeredGridLayoutManager -> {
        val lastVisibleItemPositions = mLayoutManager.findLastVisibleItemPositions(null)
        lastVisibleItemPosition = getLastVisibleItem(lastVisibleItemPositions)
      }

      is GridLayoutManager -> {
        lastVisibleItemPosition = mLayoutManager.findLastVisibleItemPosition()
      }

      is LinearLayoutManager -> {
        lastVisibleItemPosition = mLayoutManager.findLastVisibleItemPosition()
      }
    }

    if (totalItemCount < previousTotalItemCount) {
      this.currentPage = this.startingPageIndex
      this.previousTotalItemCount = totalItemCount
      if (totalItemCount == 0) {
        this.loading = true
      }
    }

    if (loading && totalItemCount > previousTotalItemCount) {
      loading = false
      previousTotalItemCount = totalItemCount
    }

    if (!loading && lastVisibleItemPosition + visibleThreshold > totalItemCount) {
      currentPage++
      onLoadMore(currentPage, totalItemCount, view)
      loading = true
    }
  }

  fun resetState() {
    this.currentPage = this.startingPageIndex
    this.previousTotalItemCount = 0
    this.loading = true
  }

  abstract fun onLoadMore(
    page: Int,
    totalItemsCount: Int,
    view: RecyclerView,
  )
}
