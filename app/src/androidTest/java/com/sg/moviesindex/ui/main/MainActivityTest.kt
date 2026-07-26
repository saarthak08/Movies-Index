package com.sg.moviesindex.ui.main

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.DrawerActions
import androidx.test.espresso.contrib.DrawerMatchers.isClosed
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.sg.moviesindex.R
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class MainActivityTest {
  @get:Rule(order = 0)
  var hiltRule = HiltAndroidRule(this)

  @get:Rule(order = 1)
  var activityRule = ActivityScenarioRule(MainActivity::class.java)

  @Test
  fun testDrawerOpens() {
    onView(withId(R.id.drawer_layout)).perform(DrawerActions.open())
    onView(withId(R.id.nav_view)).check(matches(isDisplayed()))
  }

  @Test
  fun testNavigationToTopRated() {
    onView(withId(R.id.drawer_layout)).perform(DrawerActions.open())
    onView(withId(R.id.toprated)).perform(click())
    onView(withId(R.id.drawer_layout)).check(matches(isClosed()))
  }
}
