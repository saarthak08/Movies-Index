package com.sg.moviesindex.data.remote

import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize
import java.io.Serializable

@Parcelize
@JsonClass(generateAdapter = true)
data class APIResponse(
  @Json(name = "status")
  var status: String? = null,
  @Json(name = "status_message")
  var statusMessage: String? = null,
  @Json(name = "data")
  var data: Data? = null,
  @Json(name = "@meta")
  var meta: Meta? = null,
) : Serializable,
  Parcelable {
  companion object {
    private const val serialVersionUID = 8980920924593425494L
  }
}
