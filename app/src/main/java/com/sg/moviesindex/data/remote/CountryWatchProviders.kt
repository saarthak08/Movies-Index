package com.sg.moviesindex.data.remote

import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

@Parcelize
@JsonClass(generateAdapter = true)
data class CountryWatchProviders(
  @Json(name = "link")
  var link: String? = null,
  @Json(name = "flatrate")
  var flatrate: List<WatchProvider>? = null,
  @Json(name = "rent")
  var rent: List<WatchProvider>? = null,
  @Json(name = "buy")
  var buy: List<WatchProvider>? = null,
) : Parcelable
