package com.sg.moviesindex.data.remote

import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize
import java.io.Serializable

@Parcelize
@JsonClass(generateAdapter = true)
data class Genre(
  @Json(name = "id")
  var id: Long? = null,
  @Json(name = "name")
  var name: String? = null,
) : Serializable,
  Parcelable {
  companion object {
    private const val serialVersionUID = -7094649396211162968L
  }
}
