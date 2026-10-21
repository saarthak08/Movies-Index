package com.sg.moviesindex.data.remote

import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

@Parcelize
@JsonClass(generateAdapter = true)
data class VideosList(
  @Json(name = "id")
  var id: Long? = null,
  @Json(name = "results")
  var results: MutableList<Video>? = null,
) : Parcelable
