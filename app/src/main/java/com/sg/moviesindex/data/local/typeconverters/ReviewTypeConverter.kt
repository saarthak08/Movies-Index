package com.sg.moviesindex.data.local.typeconverters

import androidx.room.TypeConverter
import com.sg.moviesindex.data.remote.Review
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types

class ReviewTypeConverter {
  private val moshi = Moshi.Builder().build()
  private val listType = Types.newParameterizedType(List::class.java, Review::class.java)
  private val adapter = moshi.adapter<List<Review>>(listType)

  @TypeConverter
  fun gettingListFromString(data: String?): List<Review> {
    if (data == null) {
      return emptyList()
    }
    return adapter.fromJson(data) ?: emptyList()
  }

  @TypeConverter
  fun gettingStringFromList(someObjects: List<Review>): String = adapter.toJson(someObjects)
}
