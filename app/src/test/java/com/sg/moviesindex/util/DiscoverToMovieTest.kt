package com.sg.moviesindex.util

import com.sg.moviesindex.data.remote.Discover
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class DiscoverToMovieTest {
  @Test
  @DisplayName("Empty ArrayList input - movies list is empty")
  fun `empty ArrayList input - movies list is empty`() {
    val discovers = ArrayList<Discover>()
    val util = DiscoverToMovie(discovers)
    assertTrue(util.movies.isEmpty())
  }

  @Test
  @DisplayName("Single Discover with all fields populated - all fields map correctly")
  fun `single Discover with all fields populated - all fields map correctly`() {
    val discover =
      Discover(
        id = 1L,
        video = true,
        voteAverage = 8.5,
        title = "Test Movie",
        popularity = 100.0,
        posterPath = "/poster.jpg",
        originalLanguage = "en",
        originalTitle = "Test Movie Original",
        genreIds = listOf(1, 2, 3),
        backdropPath = "/backdrop.jpg",
        adult = false,
        overview = "Overview text",
        releaseDate = "2023-01-01",
      )
    val util = DiscoverToMovie(arrayListOf(discover))
    val movie = util.movies.first()

    assertEquals(1L, movie.id)
    assertEquals(true, movie.video)
    assertEquals(8.5, movie.voteAverage)
    assertEquals("Test Movie", movie.title)
    assertEquals(100.0, movie.popularity)
    assertEquals("/poster.jpg", movie.posterPath)
    assertEquals("en", movie.originalLanguage)
    assertEquals("Test Movie Original", movie.originalTitle)
    assertEquals(listOf(1, 2, 3), movie.genreIds)
    assertEquals("/backdrop.jpg", movie.backdropPath)
    assertEquals(false, movie.adult)
    assertEquals("Overview text", movie.overview)
    assertEquals("2023-01-01", movie.releaseDate)
  }

  @Test
  @DisplayName("Multiple Discovers - correct count and mapping")
  fun `multiple Discovers - correct count and mapping`() {
    val discover1 = Discover(id = 1L, title = "Movie 1")
    val discover2 = Discover(id = 2L, title = "Movie 2")
    val discover3 = Discover(id = 3L, title = "Movie 3")
    val util = DiscoverToMovie(arrayListOf(discover1, discover2, discover3))

    assertEquals(3, util.movies.size)
    assertEquals(1L, util.movies[0].id)
    assertEquals("Movie 1", util.movies[0].title)
    assertEquals(2L, util.movies[1].id)
    assertEquals("Movie 2", util.movies[1].title)
    assertEquals(3L, util.movies[2].id)
    assertEquals("Movie 3", util.movies[2].title)
  }

  @Test
  @DisplayName("Discover with null fields - movie fields are null")
  fun `Discover with null fields - movie fields are null`() {
    val discover = Discover(id = null, title = null, posterPath = null, overview = null)
    val util = DiscoverToMovie(arrayListOf(discover))
    val movie = util.movies.first()

    assertNull(movie.id)
    assertNull(movie.title)
    assertNull(movie.posterPath)
    assertNull(movie.overview)
  }

  @Test
  @DisplayName("Discover with null genreIds - genreIds defaults to empty mutableList")
  fun `Discover with null genreIds - genreIds defaults to empty mutableList`() {
    val discover = Discover(genreIds = null)
    val util = DiscoverToMovie(arrayListOf(discover))
    val movie = util.movies.first()

    assertNotNull(movie.genreIds)
    assertTrue(movie.genreIds.isEmpty())
  }

  @Test
  @DisplayName("Discover with non-null genreIds - genreIds are correctly copied")
  fun `Discover with non-null genreIds - genreIds are correctly copied`() {
    val discover = Discover(genreIds = listOf(4, 5, 6))
    val util = DiscoverToMovie(arrayListOf(discover))
    val movie = util.movies.first()

    assertEquals(listOf(4, 5, 6), movie.genreIds)
  }

  @Test
  @DisplayName("Fields NOT mapped remain at defaults")
  fun `fields NOT mapped remain at defaults`() {
    val discover = Discover(id = 1L)
    val util = DiscoverToMovie(arrayListOf(discover))
    val movie = util.movies.first()

    assertNull(movie.budget)
    assertNull(movie.revenue)
    assertNull(movie.runtime)
    assertNull(movie.status)
    assertNull(movie.tagline)
    assertNull(movie.imdbId)
    assertTrue(movie.genres.isEmpty())
    assertTrue(movie.castsList.isEmpty())
    assertTrue(movie.reviewsList.isEmpty())
  }
}
