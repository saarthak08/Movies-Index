package com.sg.moviesindex.data.repository

import androidx.lifecycle.MutableLiveData
import com.sg.moviesindex.data.local.FavouriteMoviesDAO
import com.sg.moviesindex.data.local.Movie
import com.sg.moviesindex.data.remote.TMDbService
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.schedulers.Schedulers
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class RepositoryTest {
  private lateinit var repository: Repository
  private val dao: FavouriteMoviesDAO = mockk()
  private val tmdbService: TMDbService = mockk()

  @BeforeEach
  fun setup() {
    RxJavaPlugins.setIoSchedulerHandler { Schedulers.trampoline() }
    repository = Repository(dao, tmdbService)
  }

  @AfterEach
  fun tearDown() {
    RxJavaPlugins.reset()
  }

  @Test
  fun `test getAllFMovies calls dao`() {
    val liveData = MutableLiveData<List<Movie>>()
    every { dao.getAllFMovies() } returns liveData

    val result = repository.getAllFMovies()

    assertEquals(liveData, result)
    verify { dao.getAllFMovies() }
  }

  @Test
  fun `test getMovie calls dao`() {
    val movie = Movie()
    every { dao.getMovie("123") } returns movie

    val result = repository.getMovie("123")

    assertEquals(movie, result)
    verify { dao.getMovie("123") }
  }

  @Test
  fun `test addMovie calls dao insertFMovie`() {
    val movie = Movie()
    every { dao.insertFMovie(movie) } returns Unit

    repository.addMovie(movie)

    // Using MockK verify to ensure the interaction happened
    verify { dao.insertFMovie(movie) }
  }

  @Test
  fun `test deleteMovie calls dao deleteFMovie`() {
    val movie = Movie()
    every { dao.deleteFMovie(movie) } returns Unit

    repository.deleteMovie(movie)

    verify { dao.deleteFMovie(movie) }
  }

  @Test
  fun `test getPopularMovies calls tmdbService`() {
    val moviesList =
      com.sg.moviesindex.data.remote
        .MoviesList()
    every { tmdbService.getPopularMoviesWithRx("api_key", 1) } returns
      io.reactivex.Observable.just(moviesList)

    val result = repository.getPopularMovies("api_key", 1).blockingFirst()

    assertEquals(moviesList, result)
    verify { tmdbService.getPopularMoviesWithRx("api_key", 1) }
  }

  @Test
  fun `test getGenresList calls tmdbService`() {
    val genresList =
      com.sg.moviesindex.data.remote
        .GenresList()
    every { tmdbService.getGenresList("api_key") } returns io.reactivex.Observable.just(genresList)

    val result = repository.getGenresList("api_key").blockingFirst()

    assertEquals(genresList, result)
    verify { tmdbService.getGenresList("api_key") }
  }
}
