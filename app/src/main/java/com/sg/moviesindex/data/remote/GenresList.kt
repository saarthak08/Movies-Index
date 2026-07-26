package com.sg.moviesindex.data.remote

import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

@Parcelize
@JsonClass(generateAdapter = true)
data class GenresList(
  @Json(name = "genres")
  var genres: List<Genre>? = null,
) : Parcelable
