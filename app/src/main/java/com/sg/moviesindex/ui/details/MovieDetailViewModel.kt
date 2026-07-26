package com.sg.moviesindex.ui.details

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.sg.moviesindex.data.local.Movie
import com.sg.moviesindex.data.remote.CastsList
import com.sg.moviesindex.data.remote.ReviewsList
import com.sg.moviesindex.data.repository.Repository
import com.sg.moviesindex.util.BuildConfigs
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

/**
 * ViewModel for [MovieDetailActivity].
 * Handles fetching full movie details, cast members, and user reviews.
 */
@HiltViewModel
class MovieDetailViewModel
  @Inject
  constructor(
    application: Application,
    private val repository: Repository,
  ) : AndroidViewModel(application) {
    private val compositeDisposable = CompositeDisposable()

    val fullMovieInfo = MutableLiveData<Movie>()
    val casts = MutableLiveData<CastsList>()
    val reviews = MutableLiveData<ReviewsList>()
    val isLoading = MutableLiveData(false)

    /**
     * Fetches the complete detailed information for a specific movie.
     *
     * @param movieId The ID of the movie to fetch details for.
     */
    fun fetchFullInformation(movieId: Long) {
      isLoading.value = true
      compositeDisposable.add(
        repository
          .getFullMovieInformation(movieId, BuildConfigs.API_KEY)
          .subscribeOn(Schedulers.io())
          .observeOn(AndroidSchedulers.mainThread())
          .subscribeWith(
            object : DisposableObserver<Movie>() {
              override fun onNext(movie: Movie) {
                fullMovieInfo.value = movie
              }

              override fun onError(e: Throwable) {
                isLoading.value = false
              }

              override fun onComplete() {
                isLoading.value = false
              }
            },
          ),
      )
    }

    /**
     * Fetches the cast and crew information for a specific movie.
     *
     * @param movieId The ID of the movie to fetch casts for.
     */
    fun fetchCasts(movieId: Long) {
      compositeDisposable.add(
        repository
          .getCasts(movieId, BuildConfigs.API_KEY)
          .subscribeOn(Schedulers.io())
          .observeOn(AndroidSchedulers.mainThread())
          .subscribeWith(
            object : DisposableObserver<CastsList>() {
              override fun onNext(castsList: CastsList) {
                casts.value = castsList
              }

              override fun onError(e: Throwable) {}

              override fun onComplete() {}
            },
          ),
      )
    }

    /**
     * Fetches the user reviews for a specific movie.
     *
     * @param movieId The ID of the movie to fetch reviews for.
     * @param page The page number for pagination.
     */
    fun fetchReviews(
      movieId: Long,
      page: Int,
    ) {
      compositeDisposable.add(
        repository
          .getReviews(movieId, BuildConfigs.API_KEY, page)
          .subscribeOn(Schedulers.io())
          .observeOn(AndroidSchedulers.mainThread())
          .subscribeWith(
            object : DisposableObserver<ReviewsList>() {
              override fun onNext(reviewsList: ReviewsList) {
                reviews.value = reviewsList
              }

              override fun onError(e: Throwable) {}

              override fun onComplete() {}
            },
          ),
      )
    }

    override fun onCleared() {
      compositeDisposable.clear()
    }
  }
