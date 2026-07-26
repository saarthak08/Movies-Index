package com.sg.moviesindex.data.remote

import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize
import java.io.Serializable

@Parcelize
@JsonClass(generateAdapter = true)
data class Data(
  @Json(name = "movie_count")
  var movieCount: Long? = null,
  @Json(name = "limit")
  var limit: Long? = null,
  @Json(name = "page_number")
  var pageNumber: Long? = null,
  @Json(name = "movies")
  var movies: List<Movie>? = null,
) : Serializable,
  Parcelable {
  companion object {
    private const val serialVersionUID = 8675435186511918158L
  }
}
