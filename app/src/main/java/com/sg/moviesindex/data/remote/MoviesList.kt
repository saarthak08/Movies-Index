package com.sg.moviesindex.data.remote

import android.os.Parcelable
import com.sg.moviesindex.data.local.Movie
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

@Parcelize
@JsonClass(generateAdapter = true)
data class MoviesList(
  @Json(name = "page")
  var page: Int? = null,
  @Json(name = "total_results")
  var totalResults: Int? = null,
  @Json(name = "total_pages")
  var totalPages: Int? = null,
  @Json(name = "results")
  var movies: MutableList<Movie>? = null,
) : Parcelable
