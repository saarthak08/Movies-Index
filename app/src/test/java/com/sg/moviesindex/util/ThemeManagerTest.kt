package com.sg.moviesindex.util

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import android.content.res.Resources
import androidx.appcompat.app.AppCompatDelegate
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import io.mockk.verify
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class ThemeManagerTest {
  private val context: Context = mockk(relaxed = true)
  private val sharedPreferences: SharedPreferences = mockk(relaxed = true)
  private val editor: SharedPreferences.Editor = mockk(relaxed = true)
  private val resources: Resources = mockk(relaxed = true)
  private val configuration = Configuration()

  @BeforeEach
  fun setUp() {
    mockkStatic(AppCompatDelegate::class)
    every {
      context.getSharedPreferences(ThemeManager.PREFS_NAME, Context.MODE_PRIVATE)
    } returns sharedPreferences
    every { sharedPreferences.edit() } returns editor
    every { editor.putInt(any(), any()) } returns editor
    every { context.resources } returns resources
    every { resources.configuration } returns configuration
  }

  @AfterEach
  fun tearDown() {
    unmockkStatic(AppCompatDelegate::class)
  }

  @Test
  fun `isDarkMode returns true when preference is MODE_NIGHT_YES`() {
    every { sharedPreferences.contains(ThemeManager.KEY_NIGHT_MODE) } returns true
    every {
      sharedPreferences.getInt(ThemeManager.KEY_NIGHT_MODE, any())
    } returns AppCompatDelegate.MODE_NIGHT_YES

    assertTrue(ThemeManager.isDarkMode(context))
  }

  @Test
  fun `isDarkMode returns false when preference is MODE_NIGHT_NO`() {
    every { sharedPreferences.contains(ThemeManager.KEY_NIGHT_MODE) } returns true
    every {
      sharedPreferences.getInt(ThemeManager.KEY_NIGHT_MODE, any())
    } returns AppCompatDelegate.MODE_NIGHT_NO

    assertFalse(ThemeManager.isDarkMode(context))
  }

  @Test
  fun `isDarkMode falls back to configuration when no preference exists`() {
    every { sharedPreferences.contains(ThemeManager.KEY_NIGHT_MODE) } returns false
    configuration.uiMode = Configuration.UI_MODE_NIGHT_YES

    assertTrue(ThemeManager.isDarkMode(context))

    configuration.uiMode = Configuration.UI_MODE_NIGHT_NO
    assertFalse(ThemeManager.isDarkMode(context))
  }

  @Test
  fun `applyTheme applies persisted night mode if present`() {
    every { sharedPreferences.contains(ThemeManager.KEY_NIGHT_MODE) } returns true
    every {
      sharedPreferences.getInt(ThemeManager.KEY_NIGHT_MODE, any())
    } returns AppCompatDelegate.MODE_NIGHT_YES

    ThemeManager.applyTheme(context)

    verify { AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES) }
  }

  @Test
  fun `toggleTheme switches from light to dark mode and saves preference`() {
    every { sharedPreferences.contains(ThemeManager.KEY_NIGHT_MODE) } returns true
    every {
      sharedPreferences.getInt(ThemeManager.KEY_NIGHT_MODE, any())
    } returns AppCompatDelegate.MODE_NIGHT_NO

    val newIsDark = ThemeManager.toggleTheme(context)

    assertTrue(newIsDark)
    verify { editor.putInt(ThemeManager.KEY_NIGHT_MODE, AppCompatDelegate.MODE_NIGHT_YES) }
    verify { editor.apply() }
    verify { AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES) }
  }

  @Test
  fun `toggleTheme switches from dark to light mode and saves preference`() {
    every { sharedPreferences.contains(ThemeManager.KEY_NIGHT_MODE) } returns true
    every {
      sharedPreferences.getInt(ThemeManager.KEY_NIGHT_MODE, any())
    } returns AppCompatDelegate.MODE_NIGHT_YES

    val newIsDark = ThemeManager.toggleTheme(context)

    assertFalse(newIsDark)
    verify { editor.putInt(ThemeManager.KEY_NIGHT_MODE, AppCompatDelegate.MODE_NIGHT_NO) }
    verify { editor.apply() }
    verify { AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO) }
  }
}
