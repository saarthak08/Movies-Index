package com.sg.moviesindex.util

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatDelegate

/**
 * Utility for persisting and applying the user's selected application theme (Light/Dark mode).
 */
object ThemeManager {
  const val PREFS_NAME = "movies_index_theme_prefs"
  const val KEY_NIGHT_MODE = "key_night_mode"

  /**
   * Returns true if dark mode is active, either through explicit user selection
   * or falling back to the system night mode configuration.
   */
  fun isDarkMode(context: Context): Boolean {
    val prefs = getPrefs(context)
    if (prefs.contains(KEY_NIGHT_MODE)) {
      return prefs.getInt(KEY_NIGHT_MODE, AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM) ==
        AppCompatDelegate.MODE_NIGHT_YES
    }
    val currentNightMode =
      context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
    return currentNightMode == Configuration.UI_MODE_NIGHT_YES
  }

  /**
   * Applies the persisted night mode setting upon application initialization.
   */
  fun applyTheme(context: Context) {
    val prefs = getPrefs(context)
    if (prefs.contains(KEY_NIGHT_MODE)) {
      val mode = prefs.getInt(KEY_NIGHT_MODE, AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
      AppCompatDelegate.setDefaultNightMode(mode)
    }
  }

  /**
   * Toggles between Dark Mode and Light Mode, saves the selection in preferences,
   * and immediately applies the new default night mode.
   *
   * @return true if the new active theme is Dark Mode, false if Light Mode.
   */
  fun toggleTheme(context: Context): Boolean {
    val currentlyDark = isDarkMode(context)
    val newMode =
      if (currentlyDark) {
        AppCompatDelegate.MODE_NIGHT_NO
      } else {
        AppCompatDelegate.MODE_NIGHT_YES
      }
    getPrefs(context).edit().putInt(KEY_NIGHT_MODE, newMode).apply()
    AppCompatDelegate.setDefaultNightMode(newMode)
    return !currentlyDark
  }

  fun getPrefs(context: Context): SharedPreferences =
    context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
}
