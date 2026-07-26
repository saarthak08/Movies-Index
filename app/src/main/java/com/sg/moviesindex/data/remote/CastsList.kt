package com.sg.moviesindex.data.remote

import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize
import java.io.Serializable

@Parcelize
@JsonClass(generateAdapter = true)
data class CastsList(
  @Json(name = "id")
  var id: Int? = null,
  @Json(name = "cast")
  var cast: MutableList<Cast>? = null,
) : Serializable,
  Parcelable {
  companion object {
    private const val serialVersionUID = -6389819486142662649L
  }
}
