package com.sg.moviesindex.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class FavouriteMoviesDAOTest {
  @get:Rule
  val instantTaskExecutorRule = InstantTaskExecutorRule()

  private lateinit var db: Database
  private lateinit var dao: FavouriteMoviesDAO

  @Before
  fun createDb() {
    db =
      Room
        .inMemoryDatabaseBuilder(
          ApplicationProvider.getApplicationContext(),
          Database::class.java,
        ).allowMainThreadQueries()
        .build()
    dao = db.getFDAO()
  }

  @After
  @Throws(IOException::class)
  fun closeDb() {
    db.close()
  }

  @Test
  @Throws(Exception::class)
  fun writeMovieAndReadInList() {
    val movie =
      Movie(
        id = 123,
        title = "Test Movie",
        overview = "Test Overview",
        posterPath = "/poster.jpg",
        backdropPath = "/backdrop.jpg",
        releaseDate = "2024-01-01",
        voteAverage = 8.5,
      )

    dao.insertFMovie(movie)

    val loaded = dao.getMovie("Test Movie")
    assertNotNull(loaded)
    assertEquals(movie.id, loaded?.id)

    // Test delete
    dao.deleteFMovie(loaded!!)
    val deleted = dao.getMovie("Test Movie")
    assertNull(deleted)
  }
}
