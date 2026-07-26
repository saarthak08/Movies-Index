package com.sg.moviesindex.ui.details

import android.app.Application
import com.sg.moviesindex.InstantExecutorExtension
import com.sg.moviesindex.RxImmediateSchedulerRule
import com.sg.moviesindex.data.local.Movie
import com.sg.moviesindex.data.remote.CastsList
import com.sg.moviesindex.data.remote.ReviewsList
import com.sg.moviesindex.data.repository.Repository
import io.mockk.every
import io.mockk.mockk
import io.reactivex.Observable
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(RxImmediateSchedulerRule::class, InstantExecutorExtension::class)
class MovieDetailViewModelTest {
  private lateinit var viewModel: MovieDetailViewModel
  private val repository: Repository = mockk()
  private val application: Application = mockk()

  @BeforeEach
  fun setup() {
    viewModel = MovieDetailViewModel(application, repository)
  }

  @Test
  fun `test fetchFullInformation success updates fullMovieInfo`() {
    // Arrange
    val movie =
      Movie().apply {
        id = 123L
        title = "Test"
      }
    every { repository.getFullMovieInformation(123L, any()) } returns Observable.just(movie)

    // Act
    viewModel.fetchFullInformation(123L)

    // Assert
    assertEquals(movie, viewModel.fullMovieInfo.value)
    assertEquals(false, viewModel.isLoading.value)
  }

  @Test
  fun `test fetchCasts success updates casts`() {
    // Arrange
    val castsList = CastsList().apply { cast = arrayListOf() }
    every { repository.getCasts(123L, any()) } returns Observable.just(castsList)

    // Act
    viewModel.fetchCasts(123L)

    // Assert
    assertEquals(castsList, viewModel.casts.value)
  }

  @Test
  fun `test fetchReviews success updates reviews`() {
    // Arrange
    val reviewsList = ReviewsList().apply { results = arrayListOf() }
    every { repository.getReviews(123L, any(), 1) } returns Observable.just(reviewsList)

    // Act
    viewModel.fetchReviews(123L, 1)

    // Assert
    assertEquals(reviewsList, viewModel.reviews.value)
  }
}
