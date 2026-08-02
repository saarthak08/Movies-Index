package com.sg.moviesindex.data.local.typeconverters

import com.sg.moviesindex.data.remote.Cast
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class CastTypeConverterTest {
  private val converter = CastTypeConverter()

  @Test
  fun `gettingListFromString with null input returns empty list`() {
    val result = converter.gettingListFromString(null)
    Assertions.assertTrue(result.isEmpty())
  }

  @Test
  fun `gettingListFromString with empty array returns empty list`() {
    val result = converter.gettingListFromString("[]")
    Assertions.assertTrue(result.isEmpty())
  }

  @Test
  fun `gettingStringFromList with empty list returns empty array string`() {
    val result = converter.gettingStringFromList(emptyList())
    assertEquals("[]", result)
  }

  @Test
  fun `round trip conversion works for single item`() {
    val cast = Cast(castId = 1, character = "Hero", name = "John")
    val list = listOf(cast)

    val json = converter.gettingStringFromList(list)
    val result = converter.gettingListFromString(json)

    assertEquals(list, result)
  }

  @Test
  fun `round trip conversion works for multiple items`() {
    val cast1 = Cast(castId = 1, character = "Hero", name = "John")
    val cast2 = Cast(castId = 2, character = "Villain", name = "Jane")
    val list = listOf(cast1, cast2)

    val json = converter.gettingStringFromList(list)
    val result = converter.gettingListFromString(json)

    assertEquals(list, result)
  }
}
