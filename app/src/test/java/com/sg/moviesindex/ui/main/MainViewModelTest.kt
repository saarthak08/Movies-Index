package com.sg.moviesindex.ui.main

import android.app.Application
import com.sg.moviesindex.InstantExecutorExtension
import com.sg.moviesindex.RxImmediateSchedulerRule
import com.sg.moviesindex.data.remote.MoviesList
import com.sg.moviesindex.data.repository.Repository
import io.mockk.every
import io.mockk.mockk
import io.reactivex.Observable
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(RxImmediateSchedulerRule::class, InstantExecutorExtension::class)
class MainViewModelTest {
  private lateinit var viewModel: MainViewModel
  private val repository: Repository = mockk()
  private val application: Application = mockk()

  @BeforeEach
  fun setup() {
    viewModel = MainViewModel(application, repository)
  }

  @Test
  fun `test fetchMovies popular success updates movieList`() {
    // Arrange
    val moviesList =
      MoviesList().apply {
        movies = arrayListOf()
        totalPages = 10
      }
    every { repository.getPopularMovies(any(), 1) } returns Observable.just(moviesList)

    // Act
    viewModel.fetchMovies(0, 1)

    // Assert
    assertEquals(moviesList.totalPages, viewModel.totalPages.value)
    assertEquals(false, viewModel.isLoading.value)
  }
}
