package com.sg.moviesindex.ui.favorites

import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.tabs.TabLayout
import com.sg.moviesindex.R
import com.sg.moviesindex.data.local.Movie
import com.sg.moviesindex.databinding.FragmentFavouriteMoviesBinding
import com.sg.moviesindex.ui.common.MoviesAdapter
import com.sg.moviesindex.ui.main.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

/**
 * Fragment that displays the user's library: Favourite movies and Watchlist from the local database.
 * Uses a TabLayout to switch between Favorites and Watchlist, and a grid layout to show movies.
 */
@AndroidEntryPoint
class FavouriteMoviesFragment : Fragment() {
  private lateinit var fragmentFavouriteMoviesBinding: FragmentFavouriteMoviesBinding
  private lateinit var moviesAdapter: MoviesAdapter
  private lateinit var viewModel: MainViewModel
  private var currentTab = 0

  companion object {
    @JvmStatic
    fun newInstance(): FavouriteMoviesFragment = FavouriteMoviesFragment()
  }

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?,
  ): View {
    fragmentFavouriteMoviesBinding =
      DataBindingUtil.inflate(inflater, R.layout.fragment_favourite_movies, container, false)
    return fragmentFavouriteMoviesBinding.root
  }

  override fun onViewCreated(
    view: View,
    savedInstanceState: Bundle?,
  ) {
    super.onViewCreated(view, savedInstanceState)
    requireActivity().title = getString(R.string.favourite_movies)
    viewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]

    val recyclerView = fragmentFavouriteMoviesBinding.rvF4
    moviesAdapter = MoviesAdapter(requireContext())

    val orientation = resources.configuration.orientation
    recyclerView.layoutManager =
      if (orientation == Configuration.ORIENTATION_PORTRAIT) {
        GridLayoutManager(requireContext(), 2)
      } else {
        GridLayoutManager(requireContext(), 4)
      }

    recyclerView.adapter = moviesAdapter
    recyclerView.itemAnimator = DefaultItemAnimator()

    setupTabs()
    observeCurrentTab()
  }

  private fun setupTabs() {
    val tabLayout = fragmentFavouriteMoviesBinding.tabLayoutLibrary
    tabLayout.removeAllTabs()
    tabLayout.addTab(tabLayout.newTab().setText(R.string.tab_favorites))
    tabLayout.addTab(tabLayout.newTab().setText(R.string.tab_watchlist))

    tabLayout.addOnTabSelectedListener(
      object : TabLayout.OnTabSelectedListener {
        override fun onTabSelected(tab: TabLayout.Tab?) {
          currentTab = tab?.position ?: 0
          observeCurrentTab()
        }

        override fun onTabUnselected(tab: TabLayout.Tab?) {}

        override fun onTabReselected(tab: TabLayout.Tab?) {}
      },
    )
  }

  private fun observeCurrentTab() {
    if (currentTab == 0) {
      fragmentFavouriteMoviesBinding.tvNoMovies.setText(R.string.no_favourite_movies)
      viewModel.getAllMovies().observe(viewLifecycleOwner) { movies ->
        if (currentTab == 0) {
          updateMovieList(movies)
        }
      }
    } else {
      fragmentFavouriteMoviesBinding.tvNoMovies.setText(R.string.no_watchlist_movies)
      viewModel.getAllWatchlistMovies().observe(viewLifecycleOwner) { watchlistMovies ->
        if (currentTab == 1) {
          val movies = watchlistMovies?.map { it.toMovie() }
          updateMovieList(movies)
        }
      }
    }
  }

  private fun updateMovieList(movies: List<Movie>?) {
    if (movies.isNullOrEmpty()) {
      fragmentFavouriteMoviesBinding.tvNoMovies.visibility = View.VISIBLE
    } else {
      fragmentFavouriteMoviesBinding.tvNoMovies.visibility = View.GONE
    }
    moviesAdapter.submitList(movies)
  }
}
