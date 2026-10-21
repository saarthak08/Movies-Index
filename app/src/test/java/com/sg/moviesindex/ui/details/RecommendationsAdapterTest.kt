package com.sg.moviesindex.ui.details

import com.sg.moviesindex.data.local.Movie
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class RecommendationsAdapterTest {
  @Test
  fun `areItemsTheSame returns true for same movie id`() {
    val movie1 = Movie(id = 101L, title = "Interstellar")
    val movie2 = Movie(id = 101L, title = "Interstellar Updated")

    assertTrue(RecommendationsAdapter.DiffCallback.areItemsTheSame(movie1, movie2))
  }

  @Test
  fun `areItemsTheSame returns false for different movie ids`() {
    val movie1 = Movie(id = 101L, title = "Interstellar")
    val movie2 = Movie(id = 102L, title = "Inception")

    assertFalse(RecommendationsAdapter.DiffCallback.areItemsTheSame(movie1, movie2))
  }

  @Test
  fun `areContentsTheSame returns true for equal movie objects`() {
    val movie1 = Movie(id = 101L, title = "Interstellar", voteAverage = 8.6)
    val movie2 = Movie(id = 101L, title = "Interstellar", voteAverage = 8.6)

    assertTrue(RecommendationsAdapter.DiffCallback.areContentsTheSame(movie1, movie2))
  }

  @Test
  fun `areContentsTheSame returns false for modified movie objects`() {
    val movie1 = Movie(id = 101L, title = "Interstellar", voteAverage = 8.6)
    val movie2 = Movie(id = 101L, title = "Interstellar", voteAverage = 8.9)

    assertFalse(RecommendationsAdapter.DiffCallback.areContentsTheSame(movie1, movie2))
  }
}
