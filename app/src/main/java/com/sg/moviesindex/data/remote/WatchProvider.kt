package com.sg.moviesindex.data.remote

import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

@Parcelize
@JsonClass(generateAdapter = true)
data class WatchProvider(
  @Json(name = "logo_path")
  var logoPath: String? = null,
  @Json(name = "provider_id")
  var providerId: Long? = null,
  @Json(name = "provider_name")
  var providerName: String? = null,
  @Json(name = "display_priority")
  var displayPriority: Int? = null,
) : Parcelable
