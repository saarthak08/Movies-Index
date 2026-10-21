package com.sg.moviesindex.ui.main

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ProgressBar
import androidx.activity.OnBackPressedCallback
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.sg.moviesindex.R
import com.sg.moviesindex.ui.favorites.FavouriteMoviesFragment
import com.sg.moviesindex.ui.movies.MoviesFragment
import com.sg.moviesindex.ui.search.SearchUtil
import com.sg.moviesindex.util.ThemeManager
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.disposables.CompositeDisposable

/**
 * The main entry point and primary UI container for the Movies Index app.
 * Handles the bottom navigation bar, fragment transactions for different movie categories,
 * and delegates business logic to [MainViewModel].
 */
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
  companion object {
    private const val MY_PERMISSIONS_REQUESTS_NOTIFICATION_PERMISSIONS = 4
  }

  lateinit var viewModel: MainViewModel
  private val compositeDisposable = CompositeDisposable()
  private lateinit var progressBar: ProgressBar
  private lateinit var fragmentManager: FragmentManager
  private lateinit var linearLayoutError: View
  private lateinit var refreshButtonError: View
  private lateinit var bottomNavigationView: BottomNavigationView
  private var searchMenuItem: MenuItem? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    viewModel = ViewModelProvider(this)[MainViewModel::class.java]
    fragmentManager = supportFragmentManager
    bottomNavigationView = findViewById(R.id.bottom_navigation)
    progressBar = findViewById(R.id.progressBar)
    val toolbar: Toolbar = findViewById(R.id.toolbar)
    setSupportActionBar(toolbar)

    window.statusBarColor = ContextCompat.getColor(this, R.color.colorPrimaryDark)
    androidx.core.view.WindowCompat
      .getInsetsController(
        window,
        window.decorView,
      ).isAppearanceLightStatusBars =
      false

    bottomNavigationView.setOnItemSelectedListener { item ->
      val newDrawer =
        when (item.itemId) {
          R.id.nav_popular -> 0
          R.id.nav_now_playing -> 1
          R.id.nav_top_rated -> 3
          R.id.nav_genres -> 4
          R.id.nav_favorites -> 5
          else -> 0
        }
      if (viewModel.drawer.value != newDrawer) {
        viewModel.drawer.value = newDrawer
        handleNavigation(newDrawer)
      }
      true
    }

    progressBar.animate().alpha(1f).duration = 500
    progressBar.isIndeterminate = true
    linearLayoutError = findViewById(R.id.llError)
    refreshButtonError = findViewById(R.id.buttonllError)

    onBackPressedDispatcher.addCallback(
      this,
      object : OnBackPressedCallback(enabled = true) {
        override fun handleOnBackPressed() {
          val currentDrawer = viewModel.drawer.value
          if (currentDrawer != null && currentDrawer != 0) {
            viewModel.drawer.value = 0
            searchMenuItem?.isVisible = true
            supportFragmentManager.popBackStack(
              null,
              FragmentManager.POP_BACK_STACK_INCLUSIVE,
            )
            bottomNavigationView.selectedItemId = R.id.nav_popular
            viewModel.fetchMovies(0, 1)
          } else {
            isEnabled = false
            onBackPressedDispatcher.onBackPressed()
          }
        }
      },
    )

    observeViewModel()

    if (savedInstanceState == null) {
      viewModel.fetchMovies(0, 1)
    } else {
      val currentDrawer = viewModel.drawer.value ?: 0
      handleNavigation(currentDrawer)
    }
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
      requestNotificationPermissions()
    }
  }

  /**
   * Observes LiveData from [MainViewModel] and updates the UI accordingly.
   * Handles showing/hiding the progress bar, displaying errors, and replacing fragments
   * when the movie list changes.
   */
  private fun observeViewModel() {
    viewModel.isLoading.observe(this) { loading ->
      progressBar.visibility = if (loading == true) View.VISIBLE else View.GONE
      if (loading == true) progressBar.isIndeterminate = true
    }

    viewModel.errorMessage.observe(this) { error ->
      if (error != null) {
        linearLayoutError.visibility = View.VISIBLE
        refreshButtonError.setOnClickListener {
          linearLayoutError.visibility = View.GONE
          handleNavigation(viewModel.drawer.value ?: 0)
        }
      } else {
        linearLayoutError.visibility = View.GONE
      }
    }

    viewModel.movieList.observe(this) { movies ->
      if (!movies.isNullOrEmpty()) {
        val currentFragment = fragmentManager.findFragmentById(R.id.frame_layout)
        if (currentFragment == null) {
          fragmentManager
            .beginTransaction()
            .add(R.id.frame_layout, MoviesFragment.newInstance())
            .commit()
        } else if (currentFragment !is MoviesFragment) {
          fragmentManager
            .beginTransaction()
            .addToBackStack(null)
            .replace(R.id.frame_layout, MoviesFragment.newInstance())
            .commit()
        }
      }
    }
  }

  /**
   * Requests permission to post notifications, required for Android 13 (TIRAMISU) and above.
   * Notifications are typically used for background tasks like torrent downloads.
   */
  @RequiresApi(Build.VERSION_CODES.TIRAMISU)
  private fun requestNotificationPermissions() {
    if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) !=
      PackageManager.PERMISSION_GRANTED
    ) {
      ActivityCompat.requestPermissions(
        this,
        arrayOf(Manifest.permission.POST_NOTIFICATIONS),
        MY_PERMISSIONS_REQUESTS_NOTIFICATION_PERMISSIONS,
      )
    }
  }

  /**
   * Handles the navigation logic and fragment replacement based on the selected drawer item.
   *
   * @param drawer The integer code representing the selected drawer item.
   */
  private fun handleNavigation(drawer: Int) {
    searchMenuItem?.isVisible = (drawer != 4 && drawer != 5)
    val bottomId =
      when (drawer) {
        0 -> R.id.nav_popular
        1 -> R.id.nav_now_playing
        3 -> R.id.nav_top_rated
        4 -> R.id.nav_genres
        5 -> R.id.nav_favorites
        else -> null
      }
    if (bottomId != null && bottomNavigationView.selectedItemId != bottomId) {
      bottomNavigationView.selectedItemId = bottomId
    }

    when (drawer) {
      5 -> {
        supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        supportFragmentManager
          .beginTransaction()
          .addToBackStack(null)
          .replace(R.id.frame_layout, FavouriteMoviesFragment.newInstance())
          .commit()
      }

      4 -> {
        supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        observeGenres()
        viewModel.fetchGenres()
      }

      1, 2 -> {
        showRegionDialog(drawer)
      }

      else -> {
        supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        viewModel.fetchMovies(drawer, 1)
      }
    }
  }

  private fun showRegionDialog(drawer: Int) {
    val regions =
      arrayOf(
        getString(R.string.region_all),
        getString(R.string.region_india),
        getString(R.string.region_usa),
        getString(R.string.region_uk),
      )
    com.google.android.material.dialog
      .MaterialAlertDialogBuilder(this)
      .setTitle(R.string.choose_region)
      .setSingleChoiceItems(regions, -1) { dialog, which ->
        val regionCode =
          when (which) {
            1 -> "IN"
            2 -> "US"
            3 -> "GB"
            else -> ""
          }
        viewModel.region.value = regionCode
        supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        viewModel.fetchMovies(drawer, 1)
        dialog.dismiss()
      }.setCancelable(false)
      .show()
  }

  private fun observeGenres() {
    viewModel.genres.observe(this) { genres ->
      if (!genres.isNullOrEmpty()) {
        val genreNames = genres.map { it.name }.toTypedArray()
        com.google.android.material.dialog
          .MaterialAlertDialogBuilder(this)
          .setTitle(R.string.choose_category)
          .setSingleChoiceItems(genreNames, -1) { dialog, which ->
            viewModel.selectedGenreIndex.value = which
            viewModel.genreId.value = genres[which].id
            viewModel.fetchGenreMovies(1)
            dialog.dismiss()
          }.setCancelable(false)
          .show()
        viewModel.genres.removeObservers(this)
      }
    }
  }

  override fun onCreateOptionsMenu(menu: Menu): Boolean {
    menuInflater.inflate(R.menu.search_view, menu)

    val themeItem = menu.findItem(R.id.action_theme_toggle)
    val isDark = ThemeManager.isDarkMode(this)
    if (isDark) {
      themeItem.setIcon(R.drawable.ic_light_mode)
      themeItem.setTitle(R.string.switch_to_light_mode)
    } else {
      themeItem.setIcon(R.drawable.ic_dark_mode)
      themeItem.setTitle(R.string.switch_to_dark_mode)
    }

    val menuItem = menu.findItem(R.id.app_bar_search)
    searchMenuItem = menuItem
    val currentDrawer = viewModel.drawer.value ?: 0
    menuItem.isVisible = (currentDrawer != 4 && currentDrawer != 5)
    val searchView = menuItem.actionView as SearchView
    val searchUtil = SearchUtil(compositeDisposable, supportFragmentManager, this, viewModel)
    searchUtil.search(searchView)
    return true
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean =
    when (item.itemId) {
      R.id.action_theme_toggle -> {
        ThemeManager.toggleTheme(this)
        true
      }

      else -> {
        super.onOptionsItemSelected(item)
      }
    }

  override fun onDestroy() {
    super.onDestroy()
    compositeDisposable.clear()
  }
}
