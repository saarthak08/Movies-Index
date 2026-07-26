package com.sg.moviesindex.ui.movies

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sg.moviesindex.R
import com.sg.moviesindex.databinding.FragmentMoviesBinding
import com.sg.moviesindex.ui.common.MoviesAdapter
import com.sg.moviesindex.ui.common.PaginationScrollListener
import com.sg.moviesindex.ui.main.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.disposables.CompositeDisposable

/**
 * Fragment that displays a grid list of movies based on the selected category or search query.
 * Observes data from [MainViewModel] and uses a paginated grid layout.
 */
@AndroidEntryPoint
class MoviesFragment : Fragment() {
  private val compositeDisposable = CompositeDisposable()
  private lateinit var gridLayoutManager: GridLayoutManager
  private lateinit var moviesAdapter: MoviesAdapter
  private lateinit var fragmentMoviesBinding: FragmentMoviesBinding
  private lateinit var viewModel: MainViewModel

  companion object {
    @JvmStatic
    fun newInstance(): MoviesFragment = MoviesFragment()
  }

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?,
  ): View {
    fragmentMoviesBinding =
      DataBindingUtil.inflate(inflater, R.layout.fragment_movies, container, false)
    return fragmentMoviesBinding.root
  }

  override fun onViewCreated(
    view: View,
    savedInstanceState: Bundle?,
  ) {
    super.onViewCreated(view, savedInstanceState)
    viewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]
    val recyclerView = fragmentMoviesBinding.rv2
    val swipeRefreshLayout = fragmentMoviesBinding.swiperefresh2

    viewModel.drawer.observe(viewLifecycleOwner) { updateTitle() }
    viewModel.region.observe(viewLifecycleOwner) { updateTitle() }
    viewModel.searchQuery.observe(viewLifecycleOwner) { updateTitle() }
    viewModel.selectedGenreIndex.observe(viewLifecycleOwner) { updateTitle() }
    viewModel.genres.observe(viewLifecycleOwner) { updateTitle() }

    moviesAdapter = MoviesAdapter(requireContext())
    viewModel.movieList.observe(viewLifecycleOwner) { movies ->
      moviesAdapter.submitList(ArrayList(movies))
    }

    swipeRefreshLayout.setColorSchemeColors(
      Color.BLUE,
      Color.DKGRAY,
      Color.RED,
      Color.GREEN,
      Color.MAGENTA,
      Color.BLACK,
      Color.CYAN,
    )
    swipeRefreshLayout.setOnRefreshListener {
      requireActivity().supportFragmentManager.popBackStack(
        null,
        androidx.fragment.app.FragmentManager.POP_BACK_STACK_INCLUSIVE,
      )

      val drawer = viewModel.drawer.value ?: 0
      if (drawer != 4 && drawer != 6) {
        viewModel.fetchMovies(drawer, 1)
      } else if (drawer == 4) {
        viewModel.fetchGenres()
      } else {
        viewModel.drawer.value = 0
        viewModel.fetchMovies(0, 1)
      }
      swipeRefreshLayout.isRefreshing = false
    }

    gridLayoutManager = GridLayoutManager(context, 2)
    gridLayoutManager.spanSizeLookup =
      object : GridLayoutManager.SpanSizeLookup() {
        override fun getSpanSize(position: Int): Int =
          when (moviesAdapter.getItemViewType(position)) {
            0 -> 1
            1 -> 2
            else -> -1
          }
      }

    recyclerView.layoutManager = gridLayoutManager
    recyclerView.adapter = moviesAdapter
    recyclerView.itemAnimator = DefaultItemAnimator()

    val paginationScrollListener =
      object : PaginationScrollListener(gridLayoutManager) {
        override fun onLoadMore(
          page: Int,
          totalItemsCount: Int,
          view: RecyclerView,
        ) {
          val drawer = viewModel.drawer.value ?: return

          if (drawer in 0..3) {
            val totalPages = viewModel.totalPages.value ?: 0
            if (page + 1 <= totalPages) {
              viewModel.fetchMovies(drawer, page + 1)
            }
          } else if (drawer == 4) {
            val totalPagesGenres = viewModel.totalPagesGenres.value ?: 0
            if (page + 1 <= totalPagesGenres) {
              viewModel.fetchGenreMovies(page + 1)
            }
          } else if (drawer == 6) {
            val totalPages = viewModel.totalPages.value ?: 0
            if (page + 1 <= totalPages) {
              viewModel.searchMovies(viewModel.searchQuery.value ?: "", page + 1)
            }
          }
        }
      }
    recyclerView.addOnScrollListener(paginationScrollListener)
  }

  /**
   * Updates the activity title based on the currently selected navigation drawer item,
   * search query, or region.
   */
  private fun updateTitle() {
    val drawer = viewModel.drawer.value ?: 0
    when (drawer) {
      0 -> {
        requireActivity().title = getString(R.string.popular_movies)
      }

      3 -> {
        requireActivity().title = getString(R.string.top_rated_movies)
      }

      4 -> {
        val genres = viewModel.genres.value
        val selected = viewModel.selectedGenreIndex.value
        if (!genres.isNullOrEmpty() && selected != null && selected < genres.size) {
          requireActivity().title = getString(R.string.genre_format, genres[selected].name)
        } else {
          requireActivity().title = getString(R.string.genres)
        }
      }

      6 -> {
        requireActivity().title =
          getString(R.string.search_results_format, viewModel.searchQuery.value)
      }

      2 -> {
        requireActivity().title =
          getString(R.string.upcoming_movies_format, viewModel.region.value)
      }

      1 -> {
        requireActivity().title =
          getString(R.string.now_playing_format, viewModel.region.value)
      }
    }
  }

  override fun onDestroy() {
    super.onDestroy()
    compositeDisposable.clear()
  }
}
