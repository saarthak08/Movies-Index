package com.sg.moviesindex.data.repository

import androidx.lifecycle.LiveData
import com.sg.moviesindex.data.local.FavouriteMoviesDAO
import com.sg.moviesindex.data.local.Movie
import com.sg.moviesindex.data.local.WatchlistMovie
import com.sg.moviesindex.data.local.WatchlistMoviesDAO
import com.sg.moviesindex.data.remote.CastsList
import com.sg.moviesindex.data.remote.DiscoversList
import com.sg.moviesindex.data.remote.GenresList
import com.sg.moviesindex.data.remote.MoviesList
import com.sg.moviesindex.data.remote.ReviewsList
import com.sg.moviesindex.data.remote.TMDbService
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository class that acts as a single source of truth for all data operations.
 * It coordinates data fetching between the local database ([FavouriteMoviesDAO], [WatchlistMoviesDAO])
 * and the remote network APIs ([TMDbService]).
 */
@Singleton
class Repository
  @Inject
  constructor(
    private val favouriteMoviesDAO: FavouriteMoviesDAO,
    private val tmDbService: TMDbService,
    private val watchlistMoviesDAO: WatchlistMoviesDAO,
  ) {
    /**
     * Retrieves all favourite movies from the local database.
     */
    fun getAllFMovies(): LiveData<List<Movie>> = favouriteMoviesDAO.getAllFMovies()

    /**
     * Retrieves a specific favourite movie by title/id from the local database.
     */
    fun getMovie(id: String): Movie? = favouriteMoviesDAO.getMovie(id)

    /**
     * Checks whether a movie is marked as favourite by its ID.
     */
    fun isFavorite(movieId: Long): LiveData<Boolean> = favouriteMoviesDAO.isFavoriteMovie(movieId)

    /**
     * Adds a movie to the local favourite movies database asynchronously.
     */
    fun addMovie(movie: Movie) {
      Completable
        .fromAction { favouriteMoviesDAO.insertFMovie(movie) }
        .subscribeOn(Schedulers.io())
        .subscribe()
    }

    /**
     * Deletes a movie from the local favourite movies database asynchronously.
     */
    fun deleteMovie(movie: Movie) {
      Completable
        .fromAction { favouriteMoviesDAO.deleteFMovie(movie) }
        .subscribeOn(Schedulers.io())
        .subscribe()
    }

    /**
     * Retrieves all watchlist movies from the local database.
     */
    fun getAllWatchlistMovies(): LiveData<List<WatchlistMovie>> =
      watchlistMoviesDAO.getAllWatchlistMovies()

    /**
     * Checks whether a movie is in the watchlist by its ID.
     */
    fun isWatchlistMovie(movieId: Long): LiveData<Boolean> =
      watchlistMoviesDAO.isWatchlistMovie(movieId)

    /**
     * Adds a movie to the local watchlist asynchronously.
     */
    fun addWatchlistMovie(movie: WatchlistMovie) {
      Completable
        .fromAction { watchlistMoviesDAO.insertWatchlistMovie(movie) }
        .subscribeOn(Schedulers.io())
        .subscribe()
    }

    /**
     * Deletes a movie from the watchlist by its ID asynchronously.
     */
    fun removeWatchlistMovie(movieId: Long) {
      Completable
        .fromAction { watchlistMoviesDAO.deleteWatchlistMovieById(movieId) }
        .subscribeOn(Schedulers.io())
        .subscribe()
    }

    /**
     * Fetches a list of popular movies from the remote API.
     */
    fun getPopularMovies(
      apiKey: String,
      page: Int,
    ): Observable<MoviesList> = tmDbService.getPopularMoviesWithRx(apiKey, page)

    /**
     * Fetches a list of top-rated movies from the remote API.
     */
    fun getTopRatedMovies(
      apiKey: String,
      page: Int,
    ): Observable<MoviesList> = tmDbService.getTopRatedMoviesWithRx(apiKey, page)

    /**
     * Fetches a list of upcoming movies from the remote API.
     */
    fun getUpcomingMovies(
      apiKey: String,
      page: Int,
      region: String?,
    ): Observable<MoviesList> = tmDbService.getUpcomingMoviesWithRx(apiKey, page, region ?: "")

    /**
     * Fetches a list of now playing movies from the remote API.
     */
    fun getNowPlayingMovies(
      apiKey: String,
      page: Int,
      region: String?,
    ): Observable<MoviesList> = tmDbService.getNowPlayingWithRx(apiKey, page, region ?: "")

    /**
     * Discovers movies from the remote API based on the specified genre.
     */
    fun discoverMovies(
      apiKey: String,
      genreId: String,
      page: Int,
    ): Observable<DiscoversList> =
      tmDbService.discover(
        apiKey,
        genreId,
        adult = false,
        video = false,
        pageIndex = page,
        sortBy = "popularity.desc",
        region = null,
        language = null,
        releaseDateGTE = null,
        releaseDateLTE = null,
      )

    /**
     * Fetches the list of official movie genres from the remote API.
     */
    fun getGenresList(apiKey: String): Observable<GenresList> = tmDbService.getGenresList(apiKey)

    /**
     * Searches for movies on the remote API using a text query.
     */
    fun searchMovies(
      apiKey: String,
      query: String,
      page: Int,
    ): Observable<DiscoversList> = tmDbService.search(apiKey, false, query, page)

    /**
     * Fetches full details for a specific movie by its ID from the remote API.
     */
    fun getFullMovieInformation(
      movieId: Long,
      apiKey: String,
    ): Observable<Movie> = tmDbService.getFullMovieInformation(movieId, apiKey)

    /**
     * Fetches the cast and crew information for a specific movie by its ID.
     */
    fun getCasts(
      movieId: Long,
      apiKey: String,
    ): Observable<CastsList> = tmDbService.getCasts(movieId, apiKey)

    /**
     * Fetches user reviews for a specific movie by its ID.
     */
    fun getReviews(
      movieId: Long,
      apiKey: String,
      page: Int,
    ): Observable<ReviewsList> = tmDbService.getReviews(movieId, apiKey, page)

    /**
     * Fetches video previews and trailers for a specific movie by its ID.
     */
    fun getMovieVideos(
      movieId: Long,
      apiKey: String,
    ): Observable<com.sg.moviesindex.data.remote.VideosList> =
      tmDbService.getMovieVideos(movieId, apiKey)

    /**
     * Fetches recommended movies for a specific movie by its ID.
     */
    fun getRecommendations(
      movieId: Long,
      apiKey: String,
      page: Int,
    ): Observable<MoviesList> = tmDbService.getRecommendations(movieId, apiKey, page)

    /**
     * Fetches streaming, rental, and purchase providers for a specific movie by its ID.
     */
    fun getWatchProviders(
      movieId: Long,
      apiKey: String,
    ): Observable<com.sg.moviesindex.data.remote.WatchProvidersResponse> =
      tmDbService.getWatchProviders(movieId, apiKey)

    /**
     * Fetches biography and detail information for a person/actor by their ID.
     */
    fun getPersonDetails(
      personId: Long,
      apiKey: String,
    ): Observable<com.sg.moviesindex.data.remote.Person> =
      tmDbService.getPersonDetails(personId, apiKey)

    /**
     * Fetches movie credits (filmography) for a person/actor by their ID.
     */
    fun getPersonMovieCredits(
      personId: Long,
      apiKey: String,
    ): Observable<com.sg.moviesindex.data.remote.PersonMovieCredits> =
      tmDbService.getPersonMovieCredits(personId, apiKey)
  }
