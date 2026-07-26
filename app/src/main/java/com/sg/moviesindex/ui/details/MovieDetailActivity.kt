package com.sg.moviesindex.ui.details

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import antonkozyriatskyi.circularprogressindicator.CircularProgressIndicator
import com.github.leandroborgesferreira.loadingbutton.customViews.CircularProgressButton
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.snackbar.Snackbar
import com.sg.moviesindex.R
import com.sg.moviesindex.data.local.Movie
import com.sg.moviesindex.data.remote.CastsList
import com.sg.moviesindex.data.remote.ReviewsList
import com.sg.moviesindex.data.remote.Torrent
import com.sg.moviesindex.data.remote.YTSService
import com.sg.moviesindex.databinding.ActivityMoviesInfoBinding
import com.sg.moviesindex.service.TorrentDownloaderService
import com.sg.moviesindex.service.TorrentFetcherService
import com.sg.moviesindex.ui.common.PaginationScrollListener
import com.sg.moviesindex.ui.main.MainViewModel
import com.varunest.sparkbutton.SparkButton
import dagger.hilt.android.AndroidEntryPoint
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Locale
import javax.inject.Inject

/**
 * Activity that displays the details of a specific movie.
 * Shows information such as release date, rating, genres, casts, and reviews.
 * Allows users to add the movie to favorites or download torrents.
 */
@AndroidEntryPoint
class MovieDetailActivity :
  AppCompatActivity(),
  TorrentFetcherService.OnCompleteListener {
  companion object {
    const val PROGRESS_UPDATE = "progress_update"
    private const val MY_PERMISSIONS_REQUESTS_STORAGE_PERMISSIONS = 3
  }

  private lateinit var activityMoviesInfoBinding: ActivityMoviesInfoBinding
  private lateinit var mainViewModel: MainViewModel
  private lateinit var detailViewModel: MovieDetailViewModel

  @Inject
  lateinit var ytsService: YTSService

  private var movie: Movie? = null
  private lateinit var reviewsAdapter: ReviewsAdapter
  private lateinit var castsAdapter: CastsAdapter
  private val reviewsList = ReviewsList()
  private val castsList = CastsList()
  private lateinit var downloadButton: CircularProgressButton
  private lateinit var chipGroup: ChipGroup
  private lateinit var torrentFetcherService: TorrentFetcherService

  private val mBroadcastReceiver =
    object : BroadcastReceiver() {
      override fun onReceive(
        context: Context,
        intent: Intent,
      ) {
        if (PROGRESS_UPDATE == intent.action) {
          val downloadComplete = intent.getBooleanExtra("downloadComplete", false)
          if (downloadComplete) {
            Toast
              .makeText(
                applicationContext,
                getString(R.string.file_download_completed),
                Toast.LENGTH_LONG,
              ).show()
          }
        }
      }
    }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    activityMoviesInfoBinding =
      DataBindingUtil.setContentView(this, R.layout.activity_movies_info)

    val toolbar: Toolbar = findViewById(R.id.toolbar)
    setSupportActionBar(toolbar)

    mainViewModel = ViewModelProvider(this)[MainViewModel::class.java]
    detailViewModel = ViewModelProvider(this)[MovieDetailViewModel::class.java]

    reviewsList.results = ArrayList()
    castsList.cast = ArrayList()

    downloadButton = activityMoviesInfoBinding.secondaryLayout.btnId
    chipGroup = activityMoviesInfoBinding.secondaryLayout.chipGroup
    torrentFetcherService = TorrentFetcherService(this, this, ytsService)

    setupRecyclerViews()

    val intent = intent
    if (intent.hasExtra("movie")) {
      movie =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
          intent.getParcelableExtra("movie", Movie::class.java)
        } else {
          @Suppress("DEPRECATION")
          intent.getParcelableExtra("movie")
        }

      movie?.let {
        activityMoviesInfoBinding.movie = it
        observeViewModel()
        detailViewModel.fetchFullInformation(it.id!!)
        detailViewModel.fetchCasts(it.id!!)
        detailViewModel.fetchReviews(it.id!!, 1)
        checkIsFavourite()
      }
    }

    setupListeners()
    registerReceiver()
  }

  /**
   * Initializes the RecyclerViews for reviews and casts.
   * Sets up their layout managers and adapters.
   */
  private fun setupRecyclerViews() {
    reviewsAdapter = ReviewsAdapter()
    val rvReviews = activityMoviesInfoBinding.secondaryLayout.rvReviews
    rvReviews.layoutManager = LinearLayoutManager(this)
    rvReviews.itemAnimator = DefaultItemAnimator()
    rvReviews.adapter = reviewsAdapter

    rvReviews.addOnScrollListener(
      object : PaginationScrollListener(
        rvReviews.layoutManager as LinearLayoutManager,
      ) {
        override fun onLoadMore(
          page: Int,
          totalItemsCount: Int,
          view: RecyclerView,
        ) {
          if (page + 1 <= reviewsList.totalPages!!) {
            movie?.id?.let { detailViewModel.fetchReviews(it, page + 1) }
          }
        }
      },
    )

    castsAdapter = CastsAdapter()
    val rvCasts = activityMoviesInfoBinding.secondaryLayout.rvCasts
    rvCasts.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
    rvCasts.itemAnimator = DefaultItemAnimator()
    rvCasts.adapter = castsAdapter
  }

  /**
   * Observes LiveData from [MovieDetailViewModel] to update the UI
   * when full movie details, casts, or reviews are fetched.
   */
  private fun observeViewModel() {
    detailViewModel.fullMovieInfo.observe(this) { fullInfo ->
      if (fullInfo != null) {
        movie = fullInfo
        formatReleaseDate()
        activityMoviesInfoBinding.movie = movie

        val languageCode = fullInfo.originalLanguage ?: ""
        val locale = Locale(languageCode)
        val languageName =
          locale.getDisplayLanguage(Locale.US).replaceFirstChar {
            if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
          }
        activityMoviesInfoBinding.locale = languageName

        updateGenres()
        updateProgressIndicator()
      }
    }

    detailViewModel.casts.observe(this) { casts ->
      if (casts != null && casts.cast != null) {
        castsList.cast = casts.cast
        castsAdapter.submitList(casts.cast)
      }
    }

    detailViewModel.reviews.observe(this) { reviews ->
      if (reviews != null && reviews.results != null) {
        reviewsList.totalPages = reviews.totalPages
        reviewsList.results?.addAll(reviews.results!!)
        reviewsAdapter.submitList(ArrayList(reviewsList.results!!))
      }
    }
  }

  /**
   * Sets up click listeners for the UI elements, such as the download button
   * and the favorite spark button.
   */
  private fun setupListeners() {
    downloadButton.setOnClickListener {
      if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q) {
        requestStoragePermissions()
      } else {
        movie?.let { torrentFetcherService.start(downloadButton, it) }
      }
    }

    activityMoviesInfoBinding.secondaryLayout.sparkButton.setOnClickListener { v ->
      val sparkButton = v as SparkButton
      if (sparkButton.isChecked) {
        movie?.let { mainViewModel.deleteMovie(it) }
        sparkButton.playAnimation()
        Snackbar
          .make(
            v,
            getString(R.string.unmarked_as_favourite),
            Snackbar.LENGTH_SHORT,
          ).show()
        sparkButton.setInactiveImage(R.drawable.ic_heart_off)
        sparkButton.isChecked = false
      } else {
        movie?.let {
          it.castsList = ArrayList(castsList.cast ?: ArrayList())
          it.reviewsList = ArrayList(reviewsList.results ?: ArrayList())
          mainViewModel.addMovie(it)
          Snackbar
            .make(
              v,
              getString(R.string.marked_as_favourite),
              Snackbar.LENGTH_SHORT,
            ).show()
          sparkButton.playAnimation()
          sparkButton.setInactiveImage(R.drawable.ic_heart_on)
          sparkButton.isChecked = true
        }
      }
    }
  }

  /**
   * Checks if the current movie is already marked as a favorite
   * in the local database and updates the favorite button UI accordingly.
   */
  private fun checkIsFavourite() {
    movie?.let {
      if (mainViewModel.getMovie(it.title!!) != null) {
        activityMoviesInfoBinding.secondaryLayout.sparkButton.isChecked = true
        activityMoviesInfoBinding.secondaryLayout.sparkButton.setActiveImage(
          R.drawable.ic_heart_on,
        )
      } else {
        activityMoviesInfoBinding.secondaryLayout.sparkButton.isChecked = false
        activityMoviesInfoBinding.secondaryLayout.sparkButton.setInactiveImage(
          R.drawable.ic_heart_off,
        )
      }
    }
  }

  private fun formatReleaseDate() {
    try {
      movie?.releaseDate?.let { dateStr ->
        if (!dateStr.contains(",")) {
          val date = SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(dateStr)
          date?.let {
            val format = SimpleDateFormat("MMM d, yyyy", Locale.US)
            movie?.releaseDate = format.format(it)
          }
        }
      }
    } catch (e: ParseException) {
      Log.e("MovieDetailActivity", "Error parsing date", e)
    }
  }

  private fun updateGenres() {
    chipGroup.removeAllViews()
    movie?.genres?.let {
      for (genre in it) {
        val chip =
          layoutInflater.inflate(
            R.layout.chip_layout_item,
            chipGroup,
            false,
          ) as Chip
        chip.text = genre.name
        chipGroup.addView(chip)
      }
    }
  }

  private fun updateProgressIndicator() {
    val indicator: CircularProgressIndicator =
      activityMoviesInfoBinding.secondaryLayout.circularProgress
    Handler(Looper.getMainLooper()).postDelayed({
      movie?.voteAverage?.let { indicator.setProgress(it, 10.0) }
    }, 1000)
  }

  override fun onComplete(
    error: Boolean,
    torrent: Torrent?,
  ) {
    if (!error && torrent != null) {
      val intent = Intent(this, TorrentDownloaderService::class.java)
      intent.putExtra("torrent", torrent as android.os.Parcelable)
      startService(intent)
    }
  }

  private fun requestStoragePermissions() {
    if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
      PackageManager.PERMISSION_GRANTED
    ) {
      ActivityCompat.requestPermissions(
        this,
        arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
        MY_PERMISSIONS_REQUESTS_STORAGE_PERMISSIONS,
      )
    } else {
      movie?.let { torrentFetcherService.start(downloadButton, it) }
    }
  }

  override fun onRequestPermissionsResult(
    requestCode: Int,
    permissions: Array<out String>,
    grantResults: IntArray,
  ) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    if (requestCode == MY_PERMISSIONS_REQUESTS_STORAGE_PERMISSIONS) {
      if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
        movie?.let { torrentFetcherService.start(downloadButton, it) }
      } else {
        Toast
          .makeText(
            this,
            getString(R.string.permission_denied),
            Toast.LENGTH_SHORT,
          ).show()
      }
    }
  }

  private fun registerReceiver() {
    ContextCompat.registerReceiver(
      this,
      mBroadcastReceiver,
      IntentFilter(PROGRESS_UPDATE),
      ContextCompat.RECEIVER_NOT_EXPORTED,
    )
  }

  override fun onDestroy() {
    super.onDestroy()
    unregisterReceiver(mBroadcastReceiver)
    downloadButton.dispose()
  }
}
