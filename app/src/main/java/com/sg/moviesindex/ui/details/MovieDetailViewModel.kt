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
    val videos = MutableLiveData<com.sg.moviesindex.data.remote.VideosList>()
    val recommendations = MutableLiveData<com.sg.moviesindex.data.remote.MoviesList>()
    val watchProviders = MutableLiveData<com.sg.moviesindex.data.remote.WatchProvidersResponse>()
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
     * Fetches user reviews for a specific movie.
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

    /**
     * Fetches video previews and trailers for a specific movie.
     *
     * @param movieId The ID of the movie.
     */
    fun fetchVideos(movieId: Long) {
      compositeDisposable.add(
        repository
          .getMovieVideos(movieId, BuildConfigs.API_KEY)
          .subscribeOn(Schedulers.io())
          .observeOn(AndroidSchedulers.mainThread())
          .subscribeWith(
            object : DisposableObserver<com.sg.moviesindex.data.remote.VideosList>() {
              override fun onNext(videosList: com.sg.moviesindex.data.remote.VideosList) {
                videos.value = videosList
              }

              override fun onError(e: Throwable) {}

              override fun onComplete() {}
            },
          ),
      )
    }

    /**
     * Fetches recommended movies based on the specified movie.
     *
     * @param movieId The ID of the movie.
     */
    fun fetchRecommendations(movieId: Long) {
      compositeDisposable.add(
        repository
          .getRecommendations(movieId, BuildConfigs.API_KEY, 1)
          .subscribeOn(Schedulers.io())
          .observeOn(AndroidSchedulers.mainThread())
          .subscribeWith(
            object : DisposableObserver<com.sg.moviesindex.data.remote.MoviesList>() {
              override fun onNext(moviesList: com.sg.moviesindex.data.remote.MoviesList) {
                recommendations.value = moviesList
              }

              override fun onError(e: Throwable) {}

              override fun onComplete() {}
            },
          ),
      )
    }

    /**
     * Fetches streaming, rental, and buy providers for a specific movie.
     *
     * @param movieId The ID of the movie.
     */
    fun fetchWatchProviders(movieId: Long) {
      compositeDisposable.add(
        repository
          .getWatchProviders(movieId, BuildConfigs.API_KEY)
          .subscribeOn(Schedulers.io())
          .observeOn(AndroidSchedulers.mainThread())
          .subscribeWith(
            object : DisposableObserver<com.sg.moviesindex.data.remote.WatchProvidersResponse>() {
              override fun onNext(response: com.sg.moviesindex.data.remote.WatchProvidersResponse) {
                watchProviders.value = response
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
