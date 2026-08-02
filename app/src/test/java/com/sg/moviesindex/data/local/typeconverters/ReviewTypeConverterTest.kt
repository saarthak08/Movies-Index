package com.sg.moviesindex.data.local.typeconverters

import com.sg.moviesindex.data.remote.Review
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class ReviewTypeConverterTest {
  private val converter = ReviewTypeConverter()

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
    Assertions.assertEquals("[]", result)
  }

  @Test
  fun `round trip conversion works for single item`() {
    val review =
      Review(
        author = "User1",
        content = "Great movie!",
        id = "rev1",
        url = "http://example.com",
      )
    val list = listOf(review)

    val json = converter.gettingStringFromList(list)
    val result = converter.gettingListFromString(json)

    Assertions.assertEquals(list, result)
  }

  @Test
  fun `round trip conversion works for multiple items`() {
    val review1 = Review(author = "User1", content = "Great movie!", id = "rev1")
    val review2 = Review(author = "User2", content = "Bad movie!", id = "rev2")
    val list = listOf(review1, review2)

    val json = converter.gettingStringFromList(list)
    val result = converter.gettingListFromString(json)

    Assertions.assertEquals(list, result)
  }
}
