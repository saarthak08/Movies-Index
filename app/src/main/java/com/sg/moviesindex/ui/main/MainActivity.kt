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
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.core.view.get
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.navigation.NavigationView
import com.sg.moviesindex.R
import com.sg.moviesindex.ui.favorites.FavouriteMoviesFragment
import com.sg.moviesindex.ui.movies.MoviesFragment
import com.sg.moviesindex.ui.search.SearchUtil
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.disposables.CompositeDisposable

/**
 * The main entry point and primary UI container for the Movies Index app.
 * Handles the navigation drawer, fragment transactions for different movie categories,
 * and delegates business logic to [MainViewModel].
 */
@AndroidEntryPoint
class MainActivity :
  AppCompatActivity(),
  NavigationView.OnNavigationItemSelectedListener {
  companion object {
    private const val MY_PERMISSIONS_REQUESTS_NOTIFICATION_PERMISSIONS = 4
  }

  lateinit var viewModel: MainViewModel
  private val compositeDisposable = CompositeDisposable()
  private lateinit var progressBar: ProgressBar
  private lateinit var fragmentManager: FragmentManager
  private lateinit var linearLayoutError: View
  private lateinit var refreshButtonError: View
  private lateinit var navigationView: NavigationView

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    viewModel = ViewModelProvider(this)[MainViewModel::class.java]
    fragmentManager = supportFragmentManager
    navigationView = findViewById(R.id.nav_view)
    progressBar = findViewById(R.id.progressBar)
    val toolbar: Toolbar = findViewById(R.id.toolbar)
    setSupportActionBar(toolbar)

    val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
    val toggle =
      ActionBarDrawerToggle(
        this,
        drawerLayout,
        toolbar,
        R.string.navigation_drawer_open,
        R.string.navigation_drawer_close,
      )
    drawerLayout.addDrawerListener(toggle)
    toggle.syncState()

    navigationView.setNavigationItemSelectedListener(this)
    progressBar.animate().alpha(1f).duration = 500
    progressBar.isIndeterminate = true
    linearLayoutError = findViewById(R.id.llError)
    refreshButtonError = findViewById(R.id.buttonllError)
    navigationView.menu[0].isChecked = true

    onBackPressedDispatcher.addCallback(
      this,
      object : OnBackPressedCallback(enabled = true) {
        override fun handleOnBackPressed() {
          val drawer: DrawerLayout = findViewById(R.id.drawer_layout)
          if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START)
          } else {
            val currentDrawer = viewModel.drawer.value
            if (currentDrawer != null && currentDrawer != 0) {
              viewModel.drawer.value = 0
              supportFragmentManager.popBackStack(
                null,
                FragmentManager.POP_BACK_STACK_INCLUSIVE,
              )
              navigationView.menu[0].isChecked = true
              viewModel.fetchMovies(0, 1)
            } else {
              isEnabled = false
              onBackPressedDispatcher.onBackPressed()
            }
          }
        }
      },
    )

    observeViewModel()

    viewModel.fetchMovies(0, 1)
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
   * Handles item clicks in the navigation drawer.
   *
   * @param item The selected menu item.
   * @return true if the event was handled.
   */
  override fun onNavigationItemSelected(item: MenuItem): Boolean {
    progressBar.isIndeterminate = true
    progressBar.visibility = View.VISIBLE
    linearLayoutError.visibility = View.GONE

    val id = item.itemId
    var newDrawer = -1

    when (id) {
      R.id.movies -> newDrawer = 0
      R.id.favmovies -> newDrawer = 5
      R.id.toprated -> newDrawer = 3
      R.id.genres -> newDrawer = 4
      R.id.upcoming_movies -> newDrawer = 2
      R.id.now_playing -> newDrawer = 1
    }

    if (newDrawer != -1) {
      viewModel.drawer.value = newDrawer
      handleNavigation(newDrawer)
    }

    val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
    drawerLayout.closeDrawer(GravityCompat.START)
    return true
  }

  /**
   * Handles the navigation logic and fragment replacement based on the selected drawer item.
   *
   * @param drawer The integer code representing the selected drawer item.
   */
  private fun handleNavigation(drawer: Int) {
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
    val menuItem = menu.findItem(R.id.app_bar_search)
    val searchView = menuItem.actionView as SearchView
    val searchUtil = SearchUtil(compositeDisposable, supportFragmentManager, this, viewModel)
    searchUtil.search(searchView)
    return true
  }

  override fun onDestroy() {
    super.onDestroy()
    compositeDisposable.clear()
  }
}
