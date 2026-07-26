package com.sg.moviesindex.data.local

import android.annotation.SuppressLint
import android.os.Parcelable
import androidx.recyclerview.widget.DiffUtil
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.sg.moviesindex.data.remote.Cast
import com.sg.moviesindex.data.remote.Genre
import com.sg.moviesindex.data.remote.Review
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

@Entity(tableName = "favourite_movies")
@Parcelize
@JsonClass(generateAdapter = true)
data class Movie(
  @ColumnInfo(name = "adult")
  @Json(name = "adult")
  var adult: Boolean? = null,
  @ColumnInfo(name = "backdrop_path")
  @Json(name = "backdrop_path")
  var backdropPath: String? = null,
  @ColumnInfo(name = "budget")
  @Json(name = "budget")
  var budget: Long? = null,
  @ColumnInfo(name = "genres")
  @Json(name = "genres")
  var genres: List<Genre> = emptyList(),
  @PrimaryKey(autoGenerate = true)
  @ColumnInfo(name = "id")
  @Json(name = "id")
  var id: Long? = null,
  @ColumnInfo(name = "imdb_id")
  @Json(name = "imdb_id")
  var imdbId: String? = null,
  @ColumnInfo(name = "original_language")
  @Json(name = "original_language")
  var originalLanguage: String? = null,
  @ColumnInfo(name = "original_title")
  @Json(name = "original_title")
  var originalTitle: String? = null,
  @ColumnInfo(name = "overview")
  @Json(name = "overview")
  var overview: String? = null,
  @ColumnInfo(name = "popularity")
  @Json(name = "popularity")
  var popularity: Double? = null,
  @ColumnInfo(name = "poster_path")
  @Json(name = "poster_path")
  var posterPath: String? = null,
  @ColumnInfo(name = "release_date")
  @Json(name = "release_date")
  var releaseDate: String? = null,
  @ColumnInfo(name = "revenue")
  @Json(name = "revenue")
  var revenue: Long? = null,
  @ColumnInfo(name = "runtime")
  @Json(name = "runtime")
  var runtime: Long? = null,
  @ColumnInfo(name = "status")
  @Json(name = "status")
  var status: String? = null,
  @ColumnInfo(name = "tagline")
  @Json(name = "tagline")
  var tagline: String? = null,
  @ColumnInfo(name = "title")
  @Json(name = "title")
  var title: String? = null,
  @ColumnInfo(name = "video")
  @Json(name = "video")
  var video: Boolean? = null,
  @ColumnInfo(name = "vote_average")
  @Json(name = "vote_average")
  var voteAverage: Double? = null,
  @ColumnInfo(name = "casts_list")
  var castsList: List<Cast> = emptyList(),
  @ColumnInfo(name = "reviews_list")
  var reviewsList: List<Review> = emptyList(),
) : Parcelable {
  @IgnoredOnParcel
  @Ignore
  @Json(name = "genre_ids")
  var genreIds: List<Int> = emptyList()

  override fun toString(): String = "Movie(title='$title', originalLanguage='$originalLanguage')"

  companion object {
    val callback =
      object : DiffUtil.ItemCallback<Movie>() {
        override fun areItemsTheSame(
          oldItem: Movie,
          newItem: Movie,
        ): Boolean = oldItem.id == newItem.id

        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(
          oldItem: Movie,
          newItem: Movie,
        ): Boolean = oldItem == newItem
      }
  }
}
