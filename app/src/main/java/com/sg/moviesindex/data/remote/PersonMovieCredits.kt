package com.sg.moviesindex.data.remote

import android.os.Parcelable
import com.sg.moviesindex.data.local.Movie
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

@Parcelize
@JsonClass(generateAdapter = true)
data class PersonMovieCredits(
  @Json(name = "id")
  var id: Long? = null,
  @Json(name = "cast")
  var cast: MutableList<Movie>? = null,
) : Parcelable
