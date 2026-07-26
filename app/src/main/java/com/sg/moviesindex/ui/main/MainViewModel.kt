package com.sg.moviesindex.ui.main

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.sg.moviesindex.R
import com.sg.moviesindex.data.local.Movie
import com.sg.moviesindex.data.remote.Discover
import com.sg.moviesindex.data.remote.DiscoversList
import com.sg.moviesindex.data.remote.Genre
import com.sg.moviesindex.data.remote.GenresList
import com.sg.moviesindex.data.remote.MoviesList
import com.sg.moviesindex.data.repository.Repository
import com.sg.moviesindex.util.BuildConfigs
import com.sg.moviesindex.util.DiscoverToMovie
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

/**
 * ViewModel for [MainActivity].
 * Handles fetching movies, searching, discovering by genres, and managing the state of the UI.
 * Uses [Repository] to fetch data and exposes it via [LiveData].
 */
@HiltViewModel
class MainViewModel
  @Inject
  constructor(
    application: Application,
    private val repository: Repository,
  ) : AndroidViewModel(application) {
    private val compositeDisposable = CompositeDisposable()

    val movieList = MutableLiveData<ArrayList<Movie>>(ArrayList())
    val totalPages = MutableLiveData(0)
    val totalPagesGenres = MutableLiveData(0)
    val drawer = MutableLiveData(0)
    val genreId = MutableLiveData(0L)
    val region = MutableLiveData("")
    val selectedGenreIndex = MutableLiveData(0)
    val genres = MutableLiveData<ArrayList<Genre>>(ArrayList())
    val searchQuery = MutableLiveData("")
    val isLoading = MutableLiveData(false)
    val errorMessage = MutableLiveData<String?>(null)

    /**
     * Fetches movies based on the specified category type.
     *
     * @param type The type of movies to fetch (0: Popular, 1: Now Playing, 2: Upcoming, 3: Top Rated).
     * @param page The page number for pagination.
     */
    fun fetchMovies(
      type: Int,
      page: Int,
    ) {
      isLoading.value = true
      errorMessage.value = null
      val apiKey = BuildConfigs.API_KEY
      val observable: Observable<MoviesList> =
        when (type) {
          0 -> {
            repository.getPopularMovies(apiKey, page)
          }

          3 -> {
            repository.getTopRatedMovies(apiKey, page)
          }

          2 -> {
            repository.getUpcomingMovies(apiKey, page, region.value)
          }

          1 -> {
            repository.getNowPlayingMovies(apiKey, page, region.value)
          }

          else -> {
            isLoading.value = false
            return
          }
        }

      compositeDisposable.add(
        observable
          .subscribeOn(Schedulers.io())
          .observeOn(AndroidSchedulers.mainThread())
          .subscribeWith(
            object : DisposableObserver<MoviesList>() {
              override fun onNext(moviesList: MoviesList) {
                if (moviesList.movies != null) {
                  if (page == 1) {
                    moviesList.movies?.let { nonNullMovies ->
                      movieList.value = ArrayList(nonNullMovies)
                      totalPages.value = moviesList.totalPages
                    }
                  } else {
                    movieList.value?.addAll(moviesList.movies!!)
                    movieList.value = movieList.value // Trigger observer
                  }
                }
              }

              override fun onError(e: Throwable) {
                isLoading.value = false
                errorMessage.value =
                  getApplication<Application>().getString(
                    R.string.error_internet_connection,
                  )
              }

              override fun onComplete() {
                isLoading.value = false
              }
            },
          ),
      )
    }

    /**
     * Fetches movies discovered by a specific genre ID.
     *
     * @param page The page number for pagination.
     */
    fun fetchGenreMovies(page: Int) {
      isLoading.value = true
      errorMessage.value = null
      val apiKey = BuildConfigs.API_KEY

      compositeDisposable.add(
        repository
          .discoverMovies(apiKey, genreId.value.toString(), page)
          .subscribeOn(Schedulers.io())
          .observeOn(AndroidSchedulers.mainThread())
          .subscribeWith(
            object : DisposableObserver<DiscoversList>() {
              override fun onNext(discoversList: DiscoversList) {
                if (discoversList.results != null) {
                  val discovers = discoversList.results as ArrayList<Discover>
                  val discoverToMovie = DiscoverToMovie(discovers)
                  val movies = discoverToMovie.movies

                  if (page == 1) {
                    movieList.value = movies
                    totalPagesGenres.value = discoversList.totalPages
                  } else {
                    movieList.value?.addAll(movies)
                    movieList.value = movieList.value
                  }
                }
              }

              override fun onError(e: Throwable) {
                isLoading.value = false
                errorMessage.value =
                  getApplication<Application>().getString(
                    R.string.error_internet_connection,
                  )
              }

              override fun onComplete() {
                isLoading.value = false
              }
            },
          ),
      )
    }

    /**
     * Fetches the list of official genres from the API.
     */
    fun fetchGenres() {
      isLoading.value = true
      errorMessage.value = null
      val apiKey = BuildConfigs.API_KEY

      compositeDisposable.add(
        repository
          .getGenresList(apiKey)
          .subscribeOn(Schedulers.io())
          .observeOn(AndroidSchedulers.mainThread())
          .subscribeWith(
            object : DisposableObserver<GenresList>() {
              override fun onNext(genresList: GenresList) {
                genresList.genres?.let { nonNullGenres ->
                  genres.value = ArrayList(nonNullGenres)
                }
              }

              override fun onError(e: Throwable) {
                isLoading.value = false
                errorMessage.value =
                  getApplication<Application>().getString(R.string.error_fetching_genres)
              }

              override fun onComplete() {
                isLoading.value = false
              }
            },
          ),
      )
    }

    /**
     * Searches for movies based on a text query.
     *
     * @param query The search text query.
     * @param page The page number for pagination.
     */
    fun searchMovies(
      query: String,
      page: Int,
    ) {
      isLoading.value = true
      errorMessage.value = null
      searchQuery.value = query
      val apiKey = BuildConfigs.API_KEY

      compositeDisposable.add(
        repository
          .searchMovies(apiKey, query, page)
          .subscribeOn(Schedulers.io())
          .observeOn(AndroidSchedulers.mainThread())
          .subscribeWith(
            object : DisposableObserver<DiscoversList>() {
              override fun onNext(discoversList: DiscoversList) {
                if (discoversList.results != null) {
                  val discovers = discoversList.results as ArrayList<Discover>
                  val discoverToMovie = DiscoverToMovie(discovers)
                  val movies = discoverToMovie.movies

                  if (page == 1) {
                    movieList.value = movies
                    totalPages.value = discoversList.totalPages
                  } else {
                    movieList.value?.addAll(movies)
                    movieList.value = movieList.value
                  }
                }
              }

              override fun onError(e: Throwable) {
                isLoading.value = false
                errorMessage.value =
                  getApplication<Application>().getString(R.string.error_searching_movies)
              }

              override fun onComplete() {
                isLoading.value = false
              }
            },
          ),
      )
    }

    fun getSearchSuggestions(query: String): Observable<DiscoversList> =
      repository.searchMovies(BuildConfigs.API_KEY, query, 1)

    fun getMovie(id: String): Movie? = repository.getMovie(id)

    fun getAllMovies(): LiveData<List<Movie>> = repository.getAllFMovies()

    fun addMovie(movie: Movie) {
      repository.addMovie(movie)
    }

    fun deleteMovie(movie: Movie) {
      repository.deleteMovie(movie)
    }

    override fun onCleared() {
      compositeDisposable.clear()
    }
  }
