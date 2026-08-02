package com.sg.moviesindex.data.local.typeconverters

import com.sg.moviesindex.data.remote.Genre
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class GenreTypeConverterTest {
  private val converter = GenreTypeConverter()

  @Test
  fun `gettingListFromString with null input returns empty list`() {
    val result = converter.gettingListFromString(null)
    assertTrue(result.isEmpty())
  }

  @Test
  fun `gettingListFromString with empty array returns empty list`() {
    val result = converter.gettingListFromString("[]")
    assertTrue(result.isEmpty())
  }

  @Test
  fun `gettingStringFromList with empty list returns empty array string`() {
    val result = converter.gettingStringFromList(emptyList())
    assertEquals("[]", result)
  }

  @Test
  fun `round trip conversion works for single item`() {
    val genre = Genre(id = 1L, name = "Action")
    val list = listOf(genre)

    val json = converter.gettingStringFromList(list)
    val result = converter.gettingListFromString(json)

    assertEquals(list, result)
  }

  @Test
  fun `round trip conversion works for multiple items`() {
    val genre1 = Genre(id = 1L, name = "Action")
    val genre2 = Genre(id = 2L, name = "Comedy")
    val list = listOf(genre1, genre2)

    val json = converter.gettingStringFromList(list)
    val result = converter.gettingListFromString(json)

    assertEquals(list, result)
  }
}
