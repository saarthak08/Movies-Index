package com.sg.moviesindex.data.remote

import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

@Parcelize
@JsonClass(generateAdapter = true)
data class Video(
  @Json(name = "id")
  var id: String? = null,
  @Json(name = "key")
  var key: String? = null,
  @Json(name = "name")
  var name: String? = null,
  @Json(name = "site")
  var site: String? = null,
  @Json(name = "size")
  var size: Int? = null,
  @Json(name = "type")
  var type: String? = null,
  @Json(name = "official")
  var official: Boolean? = null,
) : Parcelable
