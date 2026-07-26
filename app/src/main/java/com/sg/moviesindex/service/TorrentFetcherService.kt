package com.sg.moviesindex.service

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.list.customListAdapter
import com.github.leandroborgesferreira.loadingbutton.customViews.CircularProgressButton
import com.sg.moviesindex.R
import com.sg.moviesindex.data.local.Movie
import com.sg.moviesindex.data.remote.APIResponse
import com.sg.moviesindex.data.remote.Torrent
import com.sg.moviesindex.data.remote.YTSService
import com.sg.moviesindex.ui.details.TorrentsListItemAdapter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers

/**
 * Service responsible for fetching available torrent files for a given movie using the YTS API.
 * Uses RxJava to make asynchronous API requests.
 */
class TorrentFetcherService(
  private val mListener: OnCompleteListener,
  private val context: Context,
  private val ytsService: YTSService,
) {
  private val compositeDisposable = CompositeDisposable()
  private var response = APIResponse()
  private var resultantMovie: com.sg.moviesindex.data.remote.Movie? = null

  /**
   * Starts fetching torrents for the specified movie.
   * Modifies the UI of the download button to indicate loading.
   *
   * @param button The circular progress button that initiated the action.
   * @param movieTMDb The TMDb movie object containing the IMDb ID needed for the API call.
   */
  fun start(
    button: CircularProgressButton,
    movieTMDb: Movie,
  ) {
    button.startAnimation()
    val movieId = movieTMDb.imdbId
    if (movieId.isNullOrEmpty()) {
      Toast
        .makeText(
          context,
          context.getString(R.string.no_torrents_found),
          Toast.LENGTH_SHORT,
        ).show()
      mListener.onComplete(true, null)
      button.revertAnimation()
      return
    }

    compositeDisposable.add(
      ytsService
        .getMoviesList(movieId)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribeWith(
          object : DisposableObserver<APIResponse>() {
            override fun onNext(apiResponse: APIResponse) {
              response = apiResponse
              Log.e("Torrent Fetch", apiResponse.status ?: "Unknown")
            }

            override fun onError(e: Throwable) {
              Log.e("Torrent Fetch", e.toString())
              Toast
                .makeText(
                  context,
                  context.getString(R.string.error_fetching_torrents),
                  Toast.LENGTH_SHORT,
                ).show()
              mListener.onComplete(true, null)
              button.revertAnimation()
            }

            override fun onComplete() {
              button.revertAnimation()
              if (response.data != null) {
                if (response.data!!.movieCount == 0L) {
                  Toast
                    .makeText(
                      context,
                      context.getString(R.string.no_torrents_found),
                      Toast.LENGTH_SHORT,
                    ).show()
                } else {
                  if (!response.data!!.movies.isNullOrEmpty()) {
                    resultantMovie = response.data!!.movies!![0]
                  }
                  showMaterialDialog(button)
                }
              }
            }
          },
        ),
    )
  }

  /**
   * Displays a material dialog with the fetched torrent options.
   *
   * @param button The circular progress button to revert animation after selection.
   */
  private fun showMaterialDialog(button: CircularProgressButton) {
    resultantMovie?.let { movie ->
      try {
        val dialog = MaterialDialog(context)
        dialog.title(res = R.string.torrent_files_dialog_title)
        dialog.message(res = R.string.torrent_dialog_message)

        val adapter = TorrentsListItemAdapter(movie.torrents, button, mListener)
        dialog.customListAdapter(adapter, LinearLayoutManager(context))

        dialog.show()
      } catch (e: Exception) {
        Log.e("MaterialDialogException", e.toString())
      }
    }
  }

  /**
   * Interface definition for a callback to be invoked when the fetching is complete.
   */
  interface OnCompleteListener {
    /**
     * Called when the fetch operation completes.
     *
     * @param error true if an error occurred, false otherwise.
     * @param torrent The selected torrent object if successful, null otherwise.
     */
    fun onComplete(
      error: Boolean,
      torrent: Torrent?,
    )
  }
}
