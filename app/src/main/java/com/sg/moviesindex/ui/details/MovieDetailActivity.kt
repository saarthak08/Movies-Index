package com.sg.moviesindex.ui.details

import android.Manifest
import android.content.ActivityNotFoundException
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.app.ShareCompat
import androidx.core.content.ContextCompat
import androidx.core.widget.NestedScrollView
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
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
import com.sg.moviesindex.ui.common.MoviesAdapter
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
  private lateinit var recommendationsAdapter: RecommendationsAdapter
  private lateinit var watchProvidersAdapter: WatchProvidersAdapter
  private var currentWatchProviderLink: String? = null
  private var isMovieInWatchlist = false
  private val reviewsList = ReviewsList()
  private val castsList = CastsList()
  private var currentReviewsPage = 1
  private var isLoadingReviews = false
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
    supportActionBar?.setDisplayHomeAsUpEnabled(true)

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
        activityMoviesInfoBinding.ctbMovie.title = it.title ?: it.originalTitle
        observeViewModel()
        detailViewModel.fetchFullInformation(it.id!!)
        detailViewModel.fetchCasts(it.id!!)
        detailViewModel.fetchReviews(it.id!!, 1)
        detailViewModel.fetchVideos(it.id!!)
        detailViewModel.fetchRecommendations(it.id!!)
        detailViewModel.fetchWatchProviders(it.id!!)
        observeFavoriteStatus()
        observeWatchlistStatus()
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

    val nestedScrollView = activityMoviesInfoBinding.secondaryLayout.root as NestedScrollView

    nestedScrollView.setOnScrollChangeListener { v: NestedScrollView, _, scrollY, _, oldScrollY ->
      if (scrollY == v.getChildAt(0).measuredHeight - v.measuredHeight && scrollY > oldScrollY) {
        if (!isLoadingReviews && currentReviewsPage <= (reviewsList.totalPages ?: 1)) {
          isLoadingReviews = true
          movie?.id?.let { detailViewModel.fetchReviews(it, currentReviewsPage) }
        }
      }
    }

    castsAdapter =
      CastsAdapter { cast ->
        val intent =
          Intent(this, com.sg.moviesindex.ui.person.PersonDetailActivity::class.java).apply {
            putExtra("person_id", cast.id)
            putExtra("person_name", cast.name)
            putExtra("person_profile", cast.profilePath)
          }
        startActivity(intent)
      }
    val rvCasts = activityMoviesInfoBinding.secondaryLayout.rvCasts
    rvCasts.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
    rvCasts.itemAnimator = DefaultItemAnimator()
    rvCasts.adapter = castsAdapter

    watchProvidersAdapter =
      WatchProvidersAdapter {
        val url = currentWatchProviderLink ?: "https://www.themoviedb.org/movie/${movie?.id}/watch"
        try {
          val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
          startActivity(browserIntent)
        } catch (e: Exception) {
          // Ignore if no browser available
        }
      }
    val rvWatchProviders = activityMoviesInfoBinding.secondaryLayout.rvWatchProviders
    rvWatchProviders.layoutManager =
      LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
    rvWatchProviders.itemAnimator = DefaultItemAnimator()
    rvWatchProviders.adapter = watchProvidersAdapter

    recommendationsAdapter = RecommendationsAdapter(this)
    val rvRecs = activityMoviesInfoBinding.secondaryLayout.rvRecommendations
    rvRecs.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
    rvRecs.itemAnimator = DefaultItemAnimator()
    rvRecs.adapter = recommendationsAdapter
  }

  /**
   * Observes LiveData from [MovieDetailViewModel] to update the UI
   * when full movie details, casts, reviews, trailers, or recommendations are fetched.
   */
  private fun observeViewModel() {
    detailViewModel.fullMovieInfo.observe(this) { fullInfo ->
      if (fullInfo != null) {
        movie = fullInfo
        formatReleaseDate()
        activityMoviesInfoBinding.movie = movie
        activityMoviesInfoBinding.ctbMovie.title = movie?.title ?: movie?.originalTitle

        val languageCode = fullInfo.originalLanguage ?: ""
        val locale = Locale.forLanguageTag(languageCode)
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
        currentReviewsPage++
        isLoadingReviews = false
      }
    }

    detailViewModel.videos.observe(this) { videosList ->
      val trailer =
        videosList?.results?.firstOrNull {
          it.site.equals("YouTube", ignoreCase = true) &&
            it.type.equals("Trailer", ignoreCase = true)
        } ?: videosList?.results?.firstOrNull {
          it.site.equals("YouTube", ignoreCase = true)
        }

      val btnTrailer = activityMoviesInfoBinding.secondaryLayout.btnTrailer
      val downloadContainer =
        activityMoviesInfoBinding.secondaryLayout.layoutDownloadContainer
      if (trailer?.key != null) {
        btnTrailer.visibility = View.VISIBLE
        (downloadContainer.layoutParams as? LinearLayout.LayoutParams)?.let { params ->
          params.marginStart = (6 * resources.displayMetrics.density).toInt()
          downloadContainer.layoutParams = params
        }
        btnTrailer.setOnClickListener {
          openYouTubeTrailer(trailer.key!!)
        }
      } else {
        btnTrailer.visibility = View.GONE
        (downloadContainer.layoutParams as? LinearLayout.LayoutParams)?.let { params ->
          params.marginStart = 0
          downloadContainer.layoutParams = params
        }
      }
    }

    detailViewModel.recommendations.observe(this) { moviesList ->
      if (!moviesList?.movies.isNullOrEmpty()) {
        activityMoviesInfoBinding.secondaryLayout.tvRecommendationsTitle.visibility = View.VISIBLE
        activityMoviesInfoBinding.secondaryLayout.rvRecommendations.visibility = View.VISIBLE
        recommendationsAdapter.submitList(moviesList.movies)
      }
    }

    detailViewModel.watchProviders.observe(this) { response ->
      val countryCode = Locale.getDefault().country.ifEmpty { "US" }
      val countryProviders =
        response?.results?.get(countryCode) ?: response?.results?.get("US")
      val providersList = mutableListOf<com.sg.moviesindex.data.remote.WatchProvider>()
      countryProviders?.flatrate?.let { providersList.addAll(it) }
      countryProviders?.rent?.let { rentList ->
        rentList.forEach { p ->
          if (providersList.none { it.providerId == p.providerId }) providersList.add(p)
        }
      }
      countryProviders?.buy?.let { buyList ->
        buyList.forEach { p ->
          if (providersList.none { it.providerId == p.providerId }) providersList.add(p)
        }
      }

      val tvTitle = activityMoviesInfoBinding.secondaryLayout.tvWatchProvidersTitle
      val rv = activityMoviesInfoBinding.secondaryLayout.rvWatchProviders
      if (providersList.isNotEmpty()) {
        tvTitle.visibility = View.VISIBLE
        rv.visibility = View.VISIBLE
        currentWatchProviderLink = countryProviders?.link
        watchProvidersAdapter.submitList(providersList)
      } else {
        tvTitle.visibility = View.GONE
        rv.visibility = View.GONE
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
      val currentMovie = movie ?: return@setOnClickListener
      if (sparkButton.isChecked) {
        mainViewModel.deleteMovie(currentMovie)
        sparkButton.playAnimation()
        Snackbar
          .make(
            v,
            getString(R.string.unmarked_as_favourite),
            Snackbar.LENGTH_SHORT,
          ).show()
      } else {
        currentMovie.castsList = ArrayList(castsList.cast ?: ArrayList())
        currentMovie.reviewsList = ArrayList(reviewsList.results ?: ArrayList())
        mainViewModel.addMovie(currentMovie)
        Snackbar
          .make(
            v,
            getString(R.string.marked_as_favourite),
            Snackbar.LENGTH_SHORT,
          ).show()
        sparkButton.playAnimation()
      }
    }

    val btnWatchlist = activityMoviesInfoBinding.secondaryLayout.btnWatchlist
    btnWatchlist.setOnClickListener { v ->
      val currentMovie = movie ?: return@setOnClickListener
      val movieId = currentMovie.id ?: return@setOnClickListener
      if (isMovieInWatchlist) {
        mainViewModel.removeWatchlistMovie(movieId)
        Snackbar
          .make(
            v,
            getString(R.string.removed_from_watchlist),
            Snackbar.LENGTH_SHORT,
          ).show()
      } else {
        val watchlistMovie =
          com.sg.moviesindex.data.local.WatchlistMovie
            .fromMovie(currentMovie)
        mainViewModel.addWatchlistMovie(watchlistMovie)
        Snackbar
          .make(
            v,
            getString(R.string.added_to_watchlist),
            Snackbar.LENGTH_SHORT,
          ).show()
      }
    }
  }

  /**
   * Observes whether the current movie is in the local favorites database
   * and updates the favorite button UI accordingly without blocking the main thread.
   */
  private fun observeFavoriteStatus() {
    val movieId = movie?.id ?: return
    mainViewModel.isFavorite(movieId).observe(this) { isFav ->
      val isFavorite = isFav == true
      val sparkButton = activityMoviesInfoBinding.secondaryLayout.sparkButton
      sparkButton.isChecked = isFavorite
      if (isFavorite) {
        sparkButton.setActiveImage(R.drawable.ic_heart_on)
      } else {
        sparkButton.setInactiveImage(R.drawable.ic_heart_off)
      }
    }
  }

  /**
   * Observes whether the current movie is in the watchlist database
   * and updates the watchlist button UI accordingly without blocking the main thread.
   */
  private fun observeWatchlistStatus() {
    val movieId = movie?.id ?: return
    mainViewModel.isWatchlistMovie(movieId).observe(this) { inWatchlist ->
      isMovieInWatchlist = inWatchlist == true
      val btnWatchlist = activityMoviesInfoBinding.secondaryLayout.btnWatchlist
      if (isMovieInWatchlist) {
        btnWatchlist.setIconResource(R.drawable.ic_bookmark)
        btnWatchlist.contentDescription = getString(R.string.removed_from_watchlist)
      } else {
        btnWatchlist.setIconResource(R.drawable.ic_bookmark_border)
        btnWatchlist.contentDescription = getString(R.string.add_to_watchlist)
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
      val title = movie?.title ?: movie?.originalTitle ?: "Movie"
      com.sg.moviesindex.service.TorrentDownloader
        .download(this, torrent, title)
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

  private fun openYouTubeTrailer(videoKey: String) {
    val appIntent = Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:$videoKey"))
    val webIntent =
      Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/watch?v=$videoKey"))
    try {
      startActivity(appIntent)
    } catch (e: ActivityNotFoundException) {
      startActivity(webIntent)
    }
  }

  override fun onCreateOptionsMenu(menu: Menu): Boolean {
    menuInflater.inflate(R.menu.menu_movie_detail, menu)
    return true
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean =
    when (item.itemId) {
      android.R.id.home -> {
        finish()
        true
      }

      R.id.action_share -> {
        shareMovie()
        true
      }

      else -> {
        super.onOptionsItemSelected(item)
      }
    }

  private fun shareMovie() {
    val currentMovie = movie ?: return
    val movieTitle = currentMovie.title ?: currentMovie.originalTitle ?: ""
    val rating = currentMovie.voteAverage ?: 0.0
    val overview = currentMovie.overview ?: ""
    val tmdbUrl = "https://www.themoviedb.org/movie/${currentMovie.id}"
    val shareText = getString(R.string.share_movie_format, movieTitle, rating, overview, tmdbUrl)

    ShareCompat
      .IntentBuilder(this)
      .setType("text/plain")
      .setSubject(movieTitle)
      .setText(shareText)
      .setChooserTitle(getString(R.string.share_movie))
      .startChooser()
  }

  override fun onDestroy() {
    super.onDestroy()
    unregisterReceiver(mBroadcastReceiver)
    downloadButton.dispose()
  }
}
