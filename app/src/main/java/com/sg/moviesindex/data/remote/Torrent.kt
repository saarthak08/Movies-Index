package com.sg.moviesindex.data.remote

import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize
import java.io.Serializable

@Parcelize
@JsonClass(generateAdapter = true)
data class Torrent(
  @Json(name = "url")
  var url: String? = null,
  @Json(name = "hash")
  var hash: String? = null,
  @Json(name = "quality")
  var quality: String? = null,
  @Json(name = "type")
  var type: String? = null,
  @Json(name = "seeds")
  var seeds: Long? = null,
  @Json(name = "peers")
  var peers: Long? = null,
  @Json(name = "size")
  var size: String? = null,
  @Json(name = "size_bytes")
  var sizeBytes: Long? = null,
  @Json(name = "date_uploaded")
  var dateUploaded: String? = null,
  @Json(name = "date_uploaded_unix")
  var dateUploadedUnix: Long? = null,
) : Serializable,
  Parcelable {
  companion object {
    private const val serialVersionUID = -8840109725450133141L
  }
}
