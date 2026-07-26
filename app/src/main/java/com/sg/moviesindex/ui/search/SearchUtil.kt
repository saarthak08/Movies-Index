package com.sg.moviesindex.ui.search

import android.content.Context
import android.database.MatrixCursor
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.FragmentManager
import com.sg.moviesindex.R
import com.sg.moviesindex.data.remote.Discover
import com.sg.moviesindex.data.remote.DiscoversList
import com.sg.moviesindex.ui.main.MainViewModel
import com.sg.moviesindex.ui.movies.MoviesFragment
import com.sg.moviesindex.util.DiscoverToMovie
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit

/**
 * Utility class to handle search view queries and display search suggestions.
 * Integrates with [SearchView] to trigger API calls via [MainViewModel] and updates the UI.
 */
class SearchUtil(
  private val compositeDisposable: CompositeDisposable,
  private val fragmentManager: FragmentManager,
  private val context: Context,
  private val viewModel: MainViewModel,
) {
  /**
   * Sets up the search query listener on the provided [SearchView].
   * Submits search queries to fetch results and provides autocomplete suggestions while typing.
   *
   * @param searchView The view instance used for searching movies.
   */
  fun search(searchView: SearchView) {
    searchView.setOnQueryTextListener(
      object : SearchView.OnQueryTextListener {
        override fun onQueryTextSubmit(query: String): Boolean {
          fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
          if (query.isNotEmpty()) {
            searchView.setQuery("", false)
            searchView.clearFocus()
            searchView.isIconified = true

            viewModel.drawer.value = 6
            viewModel.searchMovies(query, 1)

            fragmentManager
              .beginTransaction()
              .addToBackStack(null)
              .replace(R.id.frame_layout, MoviesFragment.newInstance())
              .commit()
          }
          return true
        }

        override fun onQueryTextChange(newText: String): Boolean {
          if (newText.isEmpty()) return true

          compositeDisposable.add(
            viewModel
              .getSearchSuggestions(newText)
              .debounce(400, TimeUnit.MILLISECONDS)
              .subscribeOn(Schedulers.io())
              .observeOn(AndroidSchedulers.mainThread())
              .subscribeWith(
                object : DisposableObserver<DiscoversList>() {
                  override fun onNext(discoversList: DiscoversList) {
                    if (discoversList.results != null) {
                      val searchResults = discoversList.results as ArrayList<Discover>
                      val movies = DiscoverToMovie(searchResults).movies

                      val columnNames = arrayOf("_id", "text")
                      val cursor = MatrixCursor(columnNames)
                      movies.forEachIndexed { index, movie ->
                        cursor.addRow(arrayOf(index.toString(), movie.title))
                      }

                      searchView.suggestionsAdapter =
                        SearchAdapter(context, cursor, true, movies)
                    }
                  }

                  override fun onError(e: Throwable) {}

                  override fun onComplete() {}
                },
              ),
          )
          return true
        }
      },
    )
  }
}
