package com.sg.moviesindex.data.remote

import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize
import java.io.Serializable

@Parcelize
@JsonClass(generateAdapter = true)
data class ReviewsList(
  @Json(name = "id")
  var id: Int? = null,
  @Json(name = "page")
  var page: Int? = null,
  @Json(name = "results")
  var results: MutableList<Review>? = null,
  @Json(name = "total_pages")
  var totalPages: Int? = null,
  @Json(name = "total_results")
  var totalResults: Int? = null,
) : Serializable,
  Parcelable {
  companion object {
    private const val serialVersionUID = 1562391630760522611L
  }
}
