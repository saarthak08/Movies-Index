package com.sg.moviesindex.data.repository

import androidx.lifecycle.MutableLiveData
import com.sg.moviesindex.InstantExecutorExtension
import com.sg.moviesindex.RxImmediateSchedulerRule
import com.sg.moviesindex.data.local.FavouriteMoviesDAO
import com.sg.moviesindex.data.local.Movie
import com.sg.moviesindex.data.remote.CastsList
import com.sg.moviesindex.data.remote.DiscoversList
import com.sg.moviesindex.data.remote.GenresList
import com.sg.moviesindex.data.remote.MoviesList
import com.sg.moviesindex.data.remote.ReviewsList
import com.sg.moviesindex.data.remote.TMDbService
import io.mockk.Runs
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.just
import io.reactivex.Observable
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito.verify

@ExtendWith(InstantExecutorExtension::class, RxImmediateSchedulerRule::class, MockKExtension::class)
class RepositoryTest {
  @MockK
  private lateinit var favouriteMoviesDAO: FavouriteMoviesDAO

  @MockK
  private lateinit var tmDbService: TMDbService

  private lateinit var repository: Repository

  @BeforeEach
  fun setUp() {
    repository = Repository(favouriteMoviesDAO, tmDbService)
  }

  @Test
  fun `getAllFMovies returns LiveData from DAO`() {
    val liveData = MutableLiveData<List<Movie>>()
    every { favouriteMoviesDAO.getAllFMovies() } returns liveData

    val result = repository.getAllFMovies()

    assertEquals(liveData, result)
    verify { favouriteMoviesDAO.getAllFMovies() }
  }

  @Test
  fun `getMovie returns movie when found in DAO`() {
    val movie = Movie(id = 1, title = "Test")
    every { favouriteMoviesDAO.getMovie("1") } returns movie

    val result = repository.getMovie("1")

    assertEquals(movie, result)
    verify { favouriteMoviesDAO.getMovie("1") }
  }

  @Test
  fun `getMovie returns null when not found`() {
    every { favouriteMoviesDAO.getMovie("99") } returns null

    val result = repository.getMovie("99")

    assertNull(result)
    verify { favouriteMoviesDAO.getMovie("99") }
  }

  @Test
  fun `addMovie inserts movie into DAO`() {
    val movie = Movie(id = 1)
    every { favouriteMoviesDAO.insertFMovie(movie) } just Runs

    repository.addMovie(movie)

    verify { favouriteMoviesDAO.insertFMovie(movie) }
  }

  @Test
  fun `deleteMovie removes movie from DAO`() {
    val movie = Movie(id = 1)
    every { favouriteMoviesDAO.deleteFMovie(movie) } just Runs

    repository.deleteMovie(movie)

    verify { favouriteMoviesDAO.deleteFMovie(movie) }
  }

  @Test
  fun `getPopularMovies delegates to service`() {
    val response = MoviesList(page = 1)
    every { tmDbService.getPopularMoviesWithRx("key", 1) } returns Observable.just(response)

    val observer = repository.getPopularMovies("key", 1).test()

    observer.assertValue(response)
    verify { tmDbService.getPopularMoviesWithRx("key", 1) }
  }

  @Test
  fun `getTopRatedMovies delegates to service`() {
    val response = MoviesList(page = 1)
    every { tmDbService.getTopRatedMoviesWithRx("key", 1) } returns Observable.just(response)

    val observer = repository.getTopRatedMovies("key", 1).test()

    observer.assertValue(response)
    verify { tmDbService.getTopRatedMoviesWithRx("key", 1) }
  }

  @Test
  fun `getUpcomingMovies with region delegates correctly`() {
    val response = MoviesList()
    every { tmDbService.getUpcomingMoviesWithRx("key", 1, "US") } returns
      Observable.just(response)

    val observer = repository.getUpcomingMovies("key", 1, "US").test()

    observer.assertValue(response)
    verify { tmDbService.getUpcomingMoviesWithRx("key", 1, "US") }
  }

  @Test
  fun `getUpcomingMovies with null region passes empty string`() {
    val response = MoviesList()
    every { tmDbService.getUpcomingMoviesWithRx("key", 1, "") } returns
      Observable.just(response)

    val observer = repository.getUpcomingMovies("key", 1, null).test()

    observer.assertValue(response)
    verify { tmDbService.getUpcomingMoviesWithRx("key", 1, "") }
  }

  @Test
  fun `getNowPlayingMovies delegates to service`() {
    val response = MoviesList()
    every { tmDbService.getNowPlayingWithRx("key", 1, "US") } returns Observable.just(response)

    val observer = repository.getNowPlayingMovies("key", 1, "US").test()

    observer.assertValue(response)
    verify { tmDbService.getNowPlayingWithRx("key", 1, "US") }
  }

  @Test
  fun `discoverMovies passes correct defaults`() {
    val response = DiscoversList()
    every {
      tmDbService.discover(
        "key",
        "28",
        false,
        false,
        1,
        "popularity.desc",
        null,
        null,
        null,
        null,
      )
    } returns Observable.just(response)

    val observer = repository.discoverMovies("key", "28", 1).test()

    observer.assertValue(response)
    verify {
      tmDbService.discover(
        "key",
        "28",
        false,
        false,
        1,
        "popularity.desc",
        null,
        null,
        null,
        null,
      )
    }
  }

  @Test
  fun `getGenresList delegates to service`() {
    val response = GenresList()
    every { tmDbService.getGenresList("key") } returns Observable.just(response)

    val observer = repository.getGenresList("key").test()

    observer.assertValue(response)
    verify { tmDbService.getGenresList("key") }
  }

  @Test
  fun `searchMovies delegates to service with defaults`() {
    val response = DiscoversList()
    every { tmDbService.search("key", false, "query", 1) } returns Observable.just(response)

    val observer = repository.searchMovies("key", "query", 1).test()

    observer.assertValue(response)
    verify { tmDbService.search("key", false, "query", 1) }
  }

  @Test
  fun `getFullMovieInformation delegates to service`() {
    val movie = Movie(id = 1)
    every { tmDbService.getFullMovieInformation(1L, "key") } returns Observable.just(movie)

    val observer = repository.getFullMovieInformation(1L, "key").test()

    observer.assertValue(movie)
    verify { tmDbService.getFullMovieInformation(1L, "key") }
  }

  @Test
  fun `getCasts delegates to service`() {
    val response = CastsList()
    every { tmDbService.getCasts(1L, "key") } returns Observable.just(response)

    val observer = repository.getCasts(1L, "key").test()

    observer.assertValue(response)
    verify { tmDbService.getCasts(1L, "key") }
  }

  @Test
  fun `getReviews delegates to service`() {
    val response = ReviewsList()
    every { tmDbService.getReviews(1L, "key", 1) } returns Observable.just(response)

    val observer = repository.getReviews(1L, "key", 1).test()

    observer.assertValue(response)
    verify { tmDbService.getReviews(1L, "key", 1) }
  }
}
