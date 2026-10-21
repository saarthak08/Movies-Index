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
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.sg.moviesindex.R
import com.sg.moviesindex.data.remote.Genre
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
  private var isRevertingNavigation = false
  private var pendingGenresObserver: Observer<ArrayList<Genre>>? = null
  private var pendingErrorObserver: Observer<String?>? = null

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
      if (isRevertingNavigation) return@setOnItemSelectedListener true

      val targetDrawer =
        when (item.itemId) {
          R.id.nav_popular -> 0
          R.id.nav_now_playing -> 1
          R.id.nav_top_rated -> 3
          R.id.nav_genres -> 4
          R.id.nav_favorites -> 5
          else -> 0
        }

      val currentDrawer = viewModel.drawer.value ?: 0
      if (targetDrawer == currentDrawer) {
        return@setOnItemSelectedListener true
      }

      clearPendingGenreObservers()

      when (targetDrawer) {
        1 -> {
          showRegionDialog(targetDrawer, previousDrawer = currentDrawer)
          true
        }

        4 -> {
          showGenreDialog(previousDrawer = currentDrawer)
          true
        }

        else -> {
          viewModel.drawer.value = targetDrawer
          handleNavigation(targetDrawer)
          true
        }
      }
    }

    bottomNavigationView.setOnItemReselectedListener { item ->
      when (item.itemId) {
        R.id.nav_now_playing -> showRegionDialog(1, previousDrawer = 1)
        R.id.nav_genres -> showGenreDialog(previousDrawer = 4)
        else -> Unit
      }
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
            clearPendingGenreObservers()
            viewModel.drawer.value = 0
            searchMenuItem?.isVisible = true
            supportFragmentManager.popBackStack(
              null,
              FragmentManager.POP_BACK_STACK_INCLUSIVE,
            )
            isRevertingNavigation = true
            bottomNavigationView.selectedItemId = R.id.nav_popular
            isRevertingNavigation = false
            val currentFragment = supportFragmentManager.findFragmentById(R.id.frame_layout)
            if (currentFragment !is MoviesFragment) {
              supportFragmentManager
                .beginTransaction()
                .replace(R.id.frame_layout, MoviesFragment.newInstance())
                .commit()
            }
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
      restoreNavigationState()
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
          val currentDrawer = viewModel.drawer.value ?: 0
          if (currentDrawer == 4) {
            viewModel.fetchGenreMovies(1)
          } else if (currentDrawer != 5) {
            viewModel.fetchMovies(currentDrawer, 1)
          }
        }
      } else {
        linearLayoutError.visibility = View.GONE
      }
    }

    viewModel.movieList.observe(this) { movies ->
      if (!movies.isNullOrEmpty() && viewModel.drawer.value != 5) {
        val currentFragment = fragmentManager.findFragmentById(R.id.frame_layout)
        if (currentFragment == null) {
          fragmentManager
            .beginTransaction()
            .add(R.id.frame_layout, MoviesFragment.newInstance())
            .commit()
        } else if (currentFragment !is MoviesFragment) {
          fragmentManager
            .beginTransaction()
            .replace(R.id.frame_layout, MoviesFragment.newInstance())
            .commit()
        }
      }
    }
  }

  /**
   * Restores the navigation tab and fragment state across configuration changes (such as theme toggle).
   */
  private fun restoreNavigationState() {
    val currentDrawer = viewModel.drawer.value ?: 0
    val targetBottomId = getNavIdForDrawer(currentDrawer)
    if (bottomNavigationView.selectedItemId != targetBottomId) {
      isRevertingNavigation = true
      bottomNavigationView.selectedItemId = targetBottomId
      isRevertingNavigation = false
    }
    searchMenuItem?.isVisible = (currentDrawer != 4 && currentDrawer != 5)

    val currentFragment = supportFragmentManager.findFragmentById(R.id.frame_layout)
    if (currentDrawer == 5) {
      if (currentFragment !is FavouriteMoviesFragment) {
        supportFragmentManager
          .beginTransaction()
          .replace(R.id.frame_layout, FavouriteMoviesFragment.newInstance())
          .commit()
      }
    } else {
      if (currentFragment !is MoviesFragment) {
        supportFragmentManager
          .beginTransaction()
          .replace(R.id.frame_layout, MoviesFragment.newInstance())
          .commit()
      }
    }
  }

  private fun getNavIdForDrawer(drawer: Int): Int =
    when (drawer) {
      0 -> R.id.nav_popular
      1 -> R.id.nav_now_playing
      3 -> R.id.nav_top_rated
      4 -> R.id.nav_genres
      5 -> R.id.nav_favorites
      else -> R.id.nav_popular
    }

  private fun revertTab(previousDrawer: Int) {
    isRevertingNavigation = true
    try {
      viewModel.drawer.value = previousDrawer
      val previousNavId = getNavIdForDrawer(previousDrawer)
      bottomNavigationView.selectedItemId = previousNavId
      searchMenuItem?.isVisible = (previousDrawer != 4 && previousDrawer != 5)
    } finally {
      isRevertingNavigation = false
    }
  }

  private fun clearPendingGenreObservers() {
    pendingGenresObserver?.let { viewModel.genres.removeObserver(it) }
    pendingGenresObserver = null
    pendingErrorObserver?.let { viewModel.errorMessage.removeObserver(it) }
    pendingErrorObserver = null
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
    val bottomId = getNavIdForDrawer(drawer)
    if (bottomNavigationView.selectedItemId != bottomId) {
      isRevertingNavigation = true
      bottomNavigationView.selectedItemId = bottomId
      isRevertingNavigation = false
    }

    supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)

    when (drawer) {
      5 -> {
        val currentFragment = supportFragmentManager.findFragmentById(R.id.frame_layout)
        if (currentFragment !is FavouriteMoviesFragment) {
          supportFragmentManager
            .beginTransaction()
            .replace(R.id.frame_layout, FavouriteMoviesFragment.newInstance())
            .commit()
        }
      }

      1, 2 -> {
        // Now Playing is handled via showRegionDialog
      }

      4 -> {
        // Genres is handled via showGenreDialog
      }

      else -> {
        val currentFragment = supportFragmentManager.findFragmentById(R.id.frame_layout)
        if (currentFragment !is MoviesFragment) {
          supportFragmentManager
            .beginTransaction()
            .replace(R.id.frame_layout, MoviesFragment.newInstance())
            .commit()
        }
        viewModel.fetchMovies(drawer, 1)
      }
    }
  }

  private fun showRegionDialog(
    drawer: Int,
    previousDrawer: Int,
  ) {
    var itemSelected = false
    val regions =
      arrayOf(
        getString(R.string.region_all),
        getString(R.string.region_india),
        getString(R.string.region_usa),
        getString(R.string.region_uk),
      )
    val currentRegion = viewModel.region.value ?: ""
    val selectedIndex =
      when (currentRegion) {
        "IN" -> 1
        "US" -> 2
        "GB" -> 3
        else -> 0
      }

    MaterialAlertDialogBuilder(this)
      .setTitle(R.string.choose_region)
      .setSingleChoiceItems(regions, selectedIndex) { dialog, which ->
        itemSelected = true
        dialog.dismiss()
        onRegionSelected(drawer, which)
      }.setNegativeButton(android.R.string.cancel) { dialog, _ ->
        dialog.dismiss()
      }.setOnDismissListener {
        if (!itemSelected) {
          revertTab(previousDrawer)
        }
      }.setCancelable(true)
      .show()
  }

  private fun onRegionSelected(
    drawer: Int,
    which: Int,
  ) {
    val regionCode =
      when (which) {
        1 -> "IN"
        2 -> "US"
        3 -> "GB"
        else -> ""
      }
    viewModel.region.value = regionCode
    viewModel.drawer.value = drawer
    searchMenuItem?.isVisible = true
    if (bottomNavigationView.selectedItemId != R.id.nav_now_playing) {
      isRevertingNavigation = true
      bottomNavigationView.selectedItemId = R.id.nav_now_playing
      isRevertingNavigation = false
    }
    supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
    val currentFragment = supportFragmentManager.findFragmentById(R.id.frame_layout)
    if (currentFragment !is MoviesFragment) {
      supportFragmentManager
        .beginTransaction()
        .replace(R.id.frame_layout, MoviesFragment.newInstance())
        .commit()
    }
    viewModel.fetchMovies(drawer, 1)
  }

  private fun showGenreDialog(previousDrawer: Int) {
    val existingGenres = viewModel.genres.value
    if (!existingGenres.isNullOrEmpty()) {
      displayGenrePickerDialog(existingGenres, previousDrawer)
      return
    }

    clearPendingGenreObservers()

    val genresObserver =
      object : Observer<ArrayList<Genre>> {
        override fun onChanged(value: ArrayList<Genre>) {
          if (!value.isNullOrEmpty()) {
            clearPendingGenreObservers()
            displayGenrePickerDialog(value, previousDrawer)
          }
        }
      }

    val errorObserver =
      object : Observer<String?> {
        override fun onChanged(value: String?) {
          if (value != null) {
            clearPendingGenreObservers()
            revertTab(previousDrawer)
          }
        }
      }

    pendingGenresObserver = genresObserver
    pendingErrorObserver = errorObserver

    viewModel.genres.observe(this, genresObserver)
    viewModel.errorMessage.observe(this, errorObserver)
    viewModel.fetchGenres()
  }

  private fun displayGenrePickerDialog(
    genres: List<Genre>,
    previousDrawer: Int,
  ) {
    var itemSelected = false
    val genreNames = genres.map { it.name }.toTypedArray()
    val currentSelected = viewModel.selectedGenreIndex.value ?: -1

    MaterialAlertDialogBuilder(this)
      .setTitle(R.string.choose_category)
      .setSingleChoiceItems(genreNames, currentSelected) { dialog, which ->
        itemSelected = true
        dialog.dismiss()
        onGenreSelected(genres, which)
      }.setNegativeButton(android.R.string.cancel) { dialog, _ ->
        dialog.dismiss()
      }.setOnDismissListener {
        if (!itemSelected) {
          revertTab(previousDrawer)
        }
      }.setCancelable(true)
      .show()
  }

  private fun onGenreSelected(
    genres: List<Genre>,
    which: Int,
  ) {
    viewModel.drawer.value = 4
    viewModel.selectedGenreIndex.value = which
    viewModel.genreId.value = genres[which].id
    searchMenuItem?.isVisible = false
    if (bottomNavigationView.selectedItemId != R.id.nav_genres) {
      isRevertingNavigation = true
      bottomNavigationView.selectedItemId = R.id.nav_genres
      isRevertingNavigation = false
    }
    supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
    val currentFragment = supportFragmentManager.findFragmentById(R.id.frame_layout)
    if (currentFragment !is MoviesFragment) {
      supportFragmentManager
        .beginTransaction()
        .replace(R.id.frame_layout, MoviesFragment.newInstance())
        .commit()
    }
    viewModel.fetchGenreMovies(1)
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
    clearPendingGenreObservers()
    compositeDisposable.clear()
  }
}
