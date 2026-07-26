package com.sg.moviesindex.data.local.typeconverters

import androidx.room.TypeConverter
import com.sg.moviesindex.data.remote.Genre
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types

class GenreTypeConverter {
  private val moshi = Moshi.Builder().build()
  private val listType = Types.newParameterizedType(List::class.java, Genre::class.java)
  private val adapter = moshi.adapter<List<Genre>>(listType)

  @TypeConverter
  fun gettingListFromString(data: String?): List<Genre> {
    if (data == null) {
      return emptyList()
    }
    return adapter.fromJson(data) ?: emptyList()
  }

  @TypeConverter
  fun gettingStringFromList(someObjects: List<Genre>): String = adapter.toJson(someObjects)
}
