package com.sg.moviesindex.data.remote

import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize
import java.io.Serializable

@Parcelize
@JsonClass(generateAdapter = true)
data class Meta(
  @Json(name = "server_time")
  var serverTime: Long? = null,
  @Json(name = "server_timezone")
  var serverTimezone: String? = null,
  @Json(name = "api_version")
  var apiVersion: Long? = null,
  @Json(name = "execution_time")
  var executionTime: String? = null,
) : Serializable,
  Parcelable {
  companion object {
    private const val serialVersionUID = 5939424027146566642L
  }
}
