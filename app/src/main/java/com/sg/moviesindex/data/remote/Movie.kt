package com.sg.moviesindex.data.remote

import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize
import java.io.Serializable

@Parcelize
@JsonClass(generateAdapter = true)
data class Movie(
  @Json(name = "id")
  var id: Long? = null,
  @Json(name = "url")
  var url: String? = null,
  @Json(name = "imdb_code")
  var imdbCode: String? = null,
  @Json(name = "title")
  var title: String? = null,
  @Json(name = "title_english")
  var titleEnglish: String? = null,
  @Json(name = "title_long")
  var titleLong: String? = null,
  @Json(name = "slug")
  var slug: String? = null,
  @Json(name = "year")
  var year: Long? = null,
  @Json(name = "rating")
  var rating: Double? = null,
  @Json(name = "runtime")
  var runtime: Long? = null,
  @Json(name = "genres")
  var genres: List<String>? = null,
  @Json(name = "summary")
  var summary: String? = null,
  @Json(name = "description_full")
  var descriptionFull: String? = null,
  @Json(name = "synopsis")
  var synopsis: String? = null,
  @Json(name = "yt_trailer_code")
  var ytTrailerCode: String? = null,
  @Json(name = "language")
  var language: String? = null,
  @Json(name = "mpa_rating")
  var mpaRating: String? = null,
  @Json(name = "background_image")
  var backgroundImage: String? = null,
  @Json(name = "background_image_original")
  var backgroundImageOriginal: String? = null,
  @Json(name = "small_cover_image")
  var smallCoverImage: String? = null,
  @Json(name = "medium_cover_image")
  var mediumCoverImage: String? = null,
  @Json(name = "large_cover_image")
  var largeCoverImage: String? = null,
  @Json(name = "state")
  var state: String? = null,
  @Json(name = "torrents")
  var torrents: List<Torrent>? = null,
  @Json(name = "date_uploaded")
  var dateUploaded: String? = null,
  @Json(name = "date_uploaded_unix")
  var dateUploadedUnix: Long? = null,
) : Serializable,
  Parcelable {
  companion object {
    private const val serialVersionUID = -4667057645999373583L
  }
}
