package com.sg.moviesindex.ui.details

import android.content.Intent
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.sg.moviesindex.data.local.Movie
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class MovieDetailActivityTest {
  @get:Rule
  var hiltRule = HiltAndroidRule(this)

  @Test
  fun testMovieDetailsDisplay() {
    val movie =
      Movie().apply {
        id = 1L
        title = "Test Movie"
        overview = "Test Overview"
        posterPath = ""
        releaseDate = "2024-01-01"
        voteAverage = 7.0
      }
    val intent =
      Intent(
        ApplicationProvider.getApplicationContext(),
        MovieDetailActivity::class.java,
      ).apply {
        putExtra("movie", movie)
      }
    ActivityScenario.launch<MovieDetailActivity>(intent).use {
      onView(withText("Test Movie")).check(matches(isDisplayed()))
      onView(withText("Test Overview")).check(matches(isDisplayed()))
    }
  }
}
