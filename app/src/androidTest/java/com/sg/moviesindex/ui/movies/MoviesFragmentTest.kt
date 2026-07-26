package com.sg.moviesindex.ui.movies

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.sg.moviesindex.R
import com.sg.moviesindex.launchFragmentInHiltContainer
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class MoviesFragmentTest {
  @get:Rule
  var hiltRule = HiltAndroidRule(this)

  @Test
  fun testMoviesFragmentDisplay() {
    launchFragmentInHiltContainer<MoviesFragment>()
    onView(withId(R.id.rv2)).check(matches(isDisplayed()))
  }
}
