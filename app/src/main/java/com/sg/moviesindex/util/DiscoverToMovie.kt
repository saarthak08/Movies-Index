package com.sg.moviesindex.util

import com.sg.moviesindex.data.local.Movie
import com.sg.moviesindex.data.remote.Discover
import java.util.ArrayList

class DiscoverToMovie(
  discovers: ArrayList<Discover>,
) {
  val movies = ArrayList<Movie>()

  init {
    for (discover in discovers) {
      val movie =
        Movie().apply {
          id = discover.id
          title = discover.title
          posterPath = discover.posterPath
          releaseDate = discover.releaseDate
          voteAverage = discover.voteAverage
          overview = discover.overview
          popularity = discover.popularity
          adult = discover.adult
          backdropPath = discover.backdropPath
          video = discover.video
          originalLanguage = discover.originalLanguage
          originalTitle = discover.originalTitle
          genreIds = discover.genreIds?.toMutableList() ?: mutableListOf()
        }
      movies.add(movie)
    }
  }
}
