package com.sg.moviesindex.data.local.typeconverters

import androidx.room.TypeConverter
import com.sg.moviesindex.data.remote.Cast
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types

class CastTypeConverter {
  private val moshi = Moshi.Builder().build()
  private val listType = Types.newParameterizedType(List::class.java, Cast::class.java)
  private val adapter = moshi.adapter<List<Cast>>(listType)

  @TypeConverter
  fun gettingListFromString(data: String?): List<Cast> {
    if (data == null) {
      return emptyList()
    }
    return adapter.fromJson(data) ?: emptyList()
  }

  @TypeConverter
  fun gettingStringFromList(someObjects: List<Cast>): String = adapter.toJson(someObjects)
}
