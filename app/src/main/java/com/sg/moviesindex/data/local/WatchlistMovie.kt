package com.sg.moviesindex.data.local

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

@Entity(tableName = "watchlist_movies")
@Parcelize
@JsonClass(generateAdapter = true)
data class WatchlistMovie(
  @PrimaryKey(autoGenerate = false)
  @ColumnInfo(name = "id")
  @Json(name = "id")
  var id: Long,
  @ColumnInfo(name = "title")
  @Json(name = "title")
  var title: String? = null,
  @ColumnInfo(name = "poster_path")
  @Json(name = "poster_path")
  var posterPath: String? = null,
  @ColumnInfo(name = "release_date")
  @Json(name = "release_date")
  var releaseDate: String? = null,
  @ColumnInfo(name = "vote_average")
  @Json(name = "vote_average")
  var voteAverage: Double? = null,
  @ColumnInfo(name = "overview")
  @Json(name = "overview")
  var overview: String? = null,
  @ColumnInfo(name = "backdrop_path")
  @Json(name = "backdrop_path")
  var backdropPath: String? = null,
  @ColumnInfo(name = "added_at")
  var addedAt: Long = System.currentTimeMillis(),
) : Parcelable {
  fun toMovie(): Movie =
    Movie(
      id = id,
      title = title,
      posterPath = posterPath,
      releaseDate = releaseDate,
      voteAverage = voteAverage,
      overview = overview,
      backdropPath = backdropPath,
    )

  companion object {
    fun fromMovie(movie: Movie): WatchlistMovie =
      WatchlistMovie(
        id = movie.id ?: 0L,
        title = movie.title,
        posterPath = movie.posterPath,
        releaseDate = movie.releaseDate,
        voteAverage = movie.voteAverage,
        overview = movie.overview,
        backdropPath = movie.backdropPath,
      )
  }
}
