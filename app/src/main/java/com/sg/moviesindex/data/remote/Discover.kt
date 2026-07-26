package com.sg.moviesindex.data.remote

import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

@Parcelize
@JsonClass(generateAdapter = true)
data class Discover(
  @Json(name = "id")
  var id: Long? = null,
  @Json(name = "video")
  var video: Boolean? = null,
  @Json(name = "vote_average")
  var voteAverage: Double? = null,
  @Json(name = "title")
  var title: String? = null,
  @Json(name = "popularity")
  var popularity: Double? = null,
  @Json(name = "poster_path")
  var posterPath: String? = null,
  @Json(name = "original_language")
  var originalLanguage: String? = null,
  @Json(name = "original_title")
  var originalTitle: String? = null,
  @Json(name = "genre_ids")
  var genreIds: List<Int>? = null,
  @Json(name = "backdrop_path")
  var backdropPath: String? = null,
  @Json(name = "adult")
  var adult: Boolean? = null,
  @Json(name = "overview")
  var overview: String? = null,
  @Json(name = "release_date")
  var releaseDate: String? = null,
) : Parcelable
