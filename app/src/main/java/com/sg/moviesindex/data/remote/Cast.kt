package com.sg.moviesindex.data.remote

import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize
import java.io.Serializable

@Parcelize
@JsonClass(generateAdapter = true)
data class Cast(
  @Json(name = "cast_id")
  var castId: Int? = null,
  @Json(name = "character")
  var character: String? = null,
  @Json(name = "credit_id")
  var creditId: String? = null,
  @Json(name = "gender")
  var gender: Int? = null,
  @Json(name = "id")
  var id: Int? = null,
  @Json(name = "name")
  var name: String? = null,
  @Json(name = "order")
  var order: Int? = null,
  @Json(name = "profile_path")
  var profilePath: String? = null,
) : Serializable,
  Parcelable {
  companion object {
    private const val serialVersionUID = -64479243734966527L
  }
}
