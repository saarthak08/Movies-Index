package com.sg.moviesindex.ui.main

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.sg.moviesindex.R
import com.sg.moviesindex.ui.common.MoviesAdapter
import com.sg.moviesindex.ui.details.MovieDetailActivity
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class E2EMovieAppTest {
  @get:Rule(order = 0)
  var hiltRule = HiltAndroidRule(this)

  @get:Rule(order = 1)
  var activityRule = ActivityScenarioRule(MainActivity::class.java)

  @Before
  fun setUp() {
    Intents.init()
    // If there's an IdlingResource, it should be registered here.
    // E.g. IdlingRegistry.getInstance().register(EspressoIdlingResource.getIdlingResource())
  }

  @After
  fun tearDown() {
    Intents.release()
  }

  @Test
  fun appLaunch_and_navigateToMovieDetails() {
    // Since it relies on real network, we might need a delay or IdlingResource.
    // We will just wait a bit for the sake of simple E2E if IdlingResource isn't set up.
    Thread.sleep(3000) // Sleep just to wait for initial load

    // Check if RecyclerView is displayed
    onView(withId(R.id.recyclerView)).check(matches(isDisplayed()))

    // Click on the first item in the RecyclerView
    onView(withId(R.id.recyclerView))
      .perform(
        RecyclerViewActions.actionOnItemAtPosition<MoviesAdapter.ViewHolder>(0, click()),
      )

    // Wait for MovieDetailActivity to load
    Thread.sleep(1000)

    // Check if intent to MovieDetailActivity was fired
    Intents.intended(hasComponent(MovieDetailActivity::class.java.name))

    // Check if MovieDetailActivity elements are displayed
    onView(withId(R.id.movie_detail_title)).check(matches(isDisplayed()))
  }
}
