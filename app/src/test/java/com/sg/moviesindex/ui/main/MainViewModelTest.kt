package com.sg.moviesindex.ui.main

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.sg.moviesindex.InstantExecutorExtension
import com.sg.moviesindex.RxImmediateSchedulerRule
import com.sg.moviesindex.data.local.Movie
import com.sg.moviesindex.data.remote.Discover
import com.sg.moviesindex.data.remote.DiscoversList
import com.sg.moviesindex.data.remote.Genre
import com.sg.moviesindex.data.remote.GenresList
import com.sg.moviesindex.data.remote.MoviesList
import com.sg.moviesindex.data.repository.Repository
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import io.reactivex.Observable
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(RxImmediateSchedulerRule::class, InstantExecutorExtension::class)
class MainViewModelTest {
  private lateinit var viewModel: MainViewModel
  private val repository: Repository = mockk(relaxed = true)
  private val application: Application = mockk(relaxed = true)

  @BeforeEach
  fun setup() {
    every { application.getString(any()) } returns "Error message"
    viewModel = MainViewModel(application, repository)
  }

  // --- fetchMovies tests ---

  @Test
  fun `fetchMovies type 0 popular - page 1 success sets movieList and totalPages`() {
    val movies = mutableListOf(Movie(id = 1L, title = "Movie1"))
    val moviesList = MoviesList(page = 1, movies = movies, totalPages = 10, totalResults = 200)
    every { repository.getPopularMovies(any(), 1) } returns Observable.just(moviesList)

    viewModel.fetchMovies(0, 1)

    assertEquals(10, viewModel.totalPages.value)
    assertEquals(1, viewModel.movieList.value?.size)
    assertEquals(
      "Movie1",
      viewModel.movieList.value
        ?.first()
        ?.title,
    )
    assertEquals(false, viewModel.isLoading.value)
  }

  @Test
  fun `fetchMovies type 0 popular - page 2 success appends to existing movieList`() {
    // Set up initial state with page 1
    val page1Movies = mutableListOf(Movie(id = 1L, title = "Movie1"))
    val page1 = MoviesList(page = 1, movies = page1Movies, totalPages = 10)
    every { repository.getPopularMovies(any(), 1) } returns Observable.just(page1)
    viewModel.fetchMovies(0, 1)

    // Now fetch page 2
    val page2Movies = mutableListOf(Movie(id = 2L, title = "Movie2"))
    val page2 = MoviesList(page = 2, movies = page2Movies, totalPages = 10)
    every { repository.getPopularMovies(any(), 2) } returns Observable.just(page2)
    viewModel.fetchMovies(0, 2)

    assertEquals(2, viewModel.movieList.value?.size)
    assertEquals(false, viewModel.isLoading.value)
  }

  @Test
  fun `fetchMovies type 1 now playing - success`() {
    val moviesList =
      MoviesList(page = 1, movies = mutableListOf(Movie(id = 3L)), totalPages = 5)
    every { repository.getNowPlayingMovies(any(), 1, any()) } returns
      Observable.just(moviesList)

    viewModel.fetchMovies(1, 1)

    assertEquals(5, viewModel.totalPages.value)
    assertEquals(1, viewModel.movieList.value?.size)
    assertEquals(false, viewModel.isLoading.value)
  }

  @Test
  fun `fetchMovies type 2 upcoming - success`() {
    val moviesList =
      MoviesList(page = 1, movies = mutableListOf(Movie(id = 4L)), totalPages = 3)
    every { repository.getUpcomingMovies(any(), 1, any()) } returns Observable.just(moviesList)

    viewModel.fetchMovies(2, 1)

    assertEquals(3, viewModel.totalPages.value)
    assertEquals(false, viewModel.isLoading.value)
  }

  @Test
  fun `fetchMovies type 3 top rated - success`() {
    val moviesList =
      MoviesList(page = 1, movies = mutableListOf(Movie(id = 5L)), totalPages = 7)
    every { repository.getTopRatedMovies(any(), 1) } returns Observable.just(moviesList)

    viewModel.fetchMovies(3, 1)

    assertEquals(7, viewModel.totalPages.value)
    assertEquals(false, viewModel.isLoading.value)
  }

  @Test
  fun `fetchMovies invalid type - sets isLoading to false and does not crash`() {
    viewModel.fetchMovies(99, 1)

    assertEquals(false, viewModel.isLoading.value)
  }

  @Test
  fun `fetchMovies error - sets isLoading false and errorMessage`() {
    every { repository.getPopularMovies(any(), 1) } returns
      Observable.error(RuntimeException("Network error"))

    viewModel.fetchMovies(0, 1)

    assertEquals(false, viewModel.isLoading.value)
    assertNotNull(viewModel.errorMessage.value)
  }

  // --- fetchGenreMovies tests ---

  @Test
  fun `fetchGenreMovies page 1 success - sets movieList from DiscoversList`() {
    val discovers = mutableListOf(Discover(id = 10L, title = "Genre Movie"))
    val discoversList = DiscoversList(page = 1, results = discovers, totalPages = 4)
    every { repository.discoverMovies(any(), any(), 1) } returns Observable.just(discoversList)

    viewModel.fetchGenreMovies(1)

    assertEquals(4, viewModel.totalPagesGenres.value)
    assertEquals(1, viewModel.movieList.value?.size)
    assertEquals(
      "Genre Movie",
      viewModel.movieList.value
        ?.first()
        ?.title,
    )
    assertEquals(false, viewModel.isLoading.value)
  }

  @Test
  fun `fetchGenreMovies page 2 success - appends to existing movieList`() {
    // Set up page 1
    val discovers1 = mutableListOf(Discover(id = 10L, title = "Genre Movie 1"))
    val page1 = DiscoversList(page = 1, results = discovers1, totalPages = 4)
    every { repository.discoverMovies(any(), any(), 1) } returns Observable.just(page1)
    viewModel.fetchGenreMovies(1)

    // Fetch page 2
    val discovers2 = mutableListOf(Discover(id = 11L, title = "Genre Movie 2"))
    val page2 = DiscoversList(page = 2, results = discovers2, totalPages = 4)
    every { repository.discoverMovies(any(), any(), 2) } returns Observable.just(page2)
    viewModel.fetchGenreMovies(2)

    assertEquals(2, viewModel.movieList.value?.size)
    assertEquals(false, viewModel.isLoading.value)
  }

  @Test
  fun `fetchGenreMovies error - sets errorMessage`() {
    every { repository.discoverMovies(any(), any(), 1) } returns
      Observable.error(RuntimeException("Error"))

    viewModel.fetchGenreMovies(1)

    assertEquals(false, viewModel.isLoading.value)
    assertNotNull(viewModel.errorMessage.value)
  }

  // --- fetchGenres tests ---

  @Test
  fun `fetchGenres success - sets genres list`() {
    val genres = listOf(Genre(id = 28L, name = "Action"), Genre(id = 35L, name = "Comedy"))
    val genresList = GenresList(genres = genres)
    every { repository.getGenresList(any()) } returns Observable.just(genresList)

    viewModel.fetchGenres()

    assertEquals(2, viewModel.genres.value?.size)
    assertEquals(
      "Action",
      viewModel.genres.value
        ?.first()
        ?.name,
    )
    assertEquals(false, viewModel.isLoading.value)
  }

  @Test
  fun `fetchGenres error - sets errorMessage`() {
    every { repository.getGenresList(any()) } returns
      Observable.error(RuntimeException("Error"))

    viewModel.fetchGenres()

    assertEquals(false, viewModel.isLoading.value)
    assertNotNull(viewModel.errorMessage.value)
  }

  // --- searchMovies tests ---

  @Test
  fun `searchMovies page 1 success - sets movieList and totalPages`() {
    val discovers = mutableListOf(Discover(id = 20L, title = "Search Result"))
    val discoversList = DiscoversList(page = 1, results = discovers, totalPages = 2)
    every { repository.searchMovies(any(), "batman", 1) } returns Observable.just(discoversList)

    viewModel.searchMovies("batman", 1)

    assertEquals(2, viewModel.totalPages.value)
    assertEquals(1, viewModel.movieList.value?.size)
    assertEquals(
      "Search Result",
      viewModel.movieList.value
        ?.first()
        ?.title,
    )
    assertEquals(false, viewModel.isLoading.value)
  }

  @Test
  fun `searchMovies page 2 success - appends to movieList`() {
    // Page 1
    val discovers1 = mutableListOf(Discover(id = 20L, title = "Result 1"))
    val page1 = DiscoversList(page = 1, results = discovers1, totalPages = 2)
    every { repository.searchMovies(any(), "batman", 1) } returns Observable.just(page1)
    viewModel.searchMovies("batman", 1)

    // Page 2
    val discovers2 = mutableListOf(Discover(id = 21L, title = "Result 2"))
    val page2 = DiscoversList(page = 2, results = discovers2, totalPages = 2)
    every { repository.searchMovies(any(), "batman", 2) } returns Observable.just(page2)
    viewModel.searchMovies("batman", 2)

    assertEquals(2, viewModel.movieList.value?.size)
  }

  @Test
  fun `searchMovies error - sets errorMessage`() {
    every { repository.searchMovies(any(), "batman", 1) } returns
      Observable.error(RuntimeException("Error"))

    viewModel.searchMovies("batman", 1)

    assertEquals(false, viewModel.isLoading.value)
    assertNotNull(viewModel.errorMessage.value)
  }

  @Test
  fun `searchMovies sets searchQuery`() {
    val discoversList = DiscoversList(page = 1, results = mutableListOf(), totalPages = 0)
    every { repository.searchMovies(any(), "joker", 1) } returns Observable.just(discoversList)

    viewModel.searchMovies("joker", 1)

    assertEquals("joker", viewModel.searchQuery.value)
  }

  // --- getSearchSuggestions test ---

  @Test
  fun `getSearchSuggestions delegates to repository`() {
    val discoversList = DiscoversList(page = 1, results = mutableListOf())
    every { repository.searchMovies(any(), "test", 1) } returns Observable.just(discoversList)

    val result = viewModel.getSearchSuggestions("test").blockingFirst()

    assertNotNull(result)
    verify { repository.searchMovies(any(), "test", 1) }
  }

  // --- getMovie tests ---

  @Test
  fun `getMovie returns movie when found`() {
    val movie = Movie(id = 1L, title = "Found Movie")
    every { repository.getMovie("Found Movie") } returns movie

    val result = viewModel.getMovie("Found Movie")

    assertEquals("Found Movie", result?.title)
  }

  @Test
  fun `getMovie returns null when not found`() {
    every { repository.getMovie("Unknown") } returns null

    val result = viewModel.getMovie("Unknown")

    assertNull(result)
  }

  // --- getAllMovies test ---

  @Test
  fun `getAllMovies returns LiveData from repository`() {
    val liveData = MutableLiveData<List<Movie>>(listOf(Movie(id = 1L)))
    every { repository.getAllFMovies() } returns liveData

    val result = viewModel.getAllMovies()

    assertEquals(liveData, result)
  }

  // --- addMovie / deleteMovie tests ---

  @Test
  fun `addMovie delegates to repository`() {
    val movie = Movie(id = 1L, title = "Test")

    viewModel.addMovie(movie)

    verify { repository.addMovie(movie) }
  }

  @Test
  fun `deleteMovie delegates to repository`() {
    val movie = Movie(id = 1L, title = "Test")

    viewModel.deleteMovie(movie)

    verify { repository.deleteMovie(movie) }
  }

  // --- fetchMovies with null movies in response ---

  @Test
  fun `fetchMovies with null movies in response - does not update movieList`() {
    val moviesList = MoviesList(page = 1, movies = null, totalPages = 1)
    every { repository.getPopularMovies(any(), 1) } returns Observable.just(moviesList)

    val initialList = viewModel.movieList.value
    viewModel.fetchMovies(0, 1)

    // movieList should remain as initial (empty ArrayList)
    assertTrue(
      viewModel.movieList.value?.isEmpty() == true || viewModel.movieList.value == initialList,
    )
  }
}
