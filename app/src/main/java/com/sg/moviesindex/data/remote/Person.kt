package com.sg.moviesindex.data.remote

import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

@Parcelize
@JsonClass(generateAdapter = true)
data class Person(
  @Json(name = "id")
  var id: Long? = null,
  @Json(name = "name")
  var name: String? = null,
  @Json(name = "biography")
  var biography: String? = null,
  @Json(name = "birthday")
  var birthday: String? = null,
  @Json(name = "place_of_birth")
  var placeOfBirth: String? = null,
  @Json(name = "profile_path")
  var profilePath: String? = null,
  @Json(name = "known_for_department")
  var knownForDepartment: String? = null,
) : Parcelable
