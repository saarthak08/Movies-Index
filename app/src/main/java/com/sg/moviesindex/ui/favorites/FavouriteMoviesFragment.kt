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
import com.sg.moviesindex.R
import com.sg.moviesindex.databinding.FragmentFavouriteMoviesBinding
import com.sg.moviesindex.ui.common.MoviesAdapter
import com.sg.moviesindex.ui.main.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

/**
 * Fragment that displays the user's favourite movies from the local database.
 * Uses a grid layout manager to show movies.
 */
@AndroidEntryPoint
class FavouriteMoviesFragment : Fragment() {
  private lateinit var fragmentFavouriteMoviesBinding: FragmentFavouriteMoviesBinding
  private lateinit var moviesAdapter: MoviesAdapter

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
    val viewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]

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

    viewModel.getAllMovies().observe(viewLifecycleOwner) { movies ->
      if (movies.isNullOrEmpty()) {
        fragmentFavouriteMoviesBinding.tvNoMovies.visibility = View.VISIBLE
      } else {
        fragmentFavouriteMoviesBinding.tvNoMovies.visibility = View.GONE
      }
      moviesAdapter.submitList(movies)
    }
  }
}
