package com.sg.moviesindex.data.remote

import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize
import java.io.Serializable

@Parcelize
@JsonClass(generateAdapter = true)
data class Review(
  @Json(name = "author")
  var author: String? = null,
  @Json(name = "content")
  var content: String? = null,
  @Json(name = "id")
  var id: String? = null,
  @Json(name = "url")
  var url: String? = null,
) : Serializable,
  Parcelable {
  companion object {
    private const val serialVersionUID = 633836138228227141L
  }
}
