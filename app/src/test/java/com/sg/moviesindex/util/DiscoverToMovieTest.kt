package com.sg.moviesindex.util

import com.sg.moviesindex.data.remote.Discover
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class DiscoverToMovieTest {
  @Test
  fun `test mapping discover to movie`() {
    // Arrange
    val discover =
      Discover().apply {
        id = 123L
        title = "Test Movie"
        posterPath = "/path.jpg"
        releaseDate = "2024-01-01"
        voteAverage = 8.5
        overview = "Test Overview"
        popularity = 100.0
        adult = false
        backdropPath = "/backdrop.jpg"
        video = false
        originalLanguage = "en"
        originalTitle = "Original Test Movie"
        genreIds = listOf(1, 2, 3)
      }
    val discovers = arrayListOf(discover)

    // Act
    val discoverToMovie = DiscoverToMovie(discovers)
    val movies = discoverToMovie.movies

    // Assert
    assertEquals(1, movies.size)
    val movie = movies[0]
    assertEquals(discover.id, movie.id)
    assertEquals(discover.title, movie.title)
    assertEquals(discover.posterPath, movie.posterPath)
    assertEquals(discover.releaseDate, movie.releaseDate)
    assertEquals(discover.voteAverage, movie.voteAverage)
    assertEquals(discover.overview, movie.overview)
    assertEquals(discover.popularity, movie.popularity)
    assertEquals(discover.adult, movie.adult)
    assertEquals(discover.backdropPath, movie.backdropPath)
    assertEquals(discover.video, movie.video)
    assertEquals(discover.originalLanguage, movie.originalLanguage)
    assertEquals(discover.originalTitle, movie.originalTitle)
    assertEquals(discover.genreIds, movie.genreIds)
  }
}
