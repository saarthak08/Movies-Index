package com.sg.moviesindex.data.remote

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory

class TMDbServiceTest {
  private lateinit var mockWebServer: MockWebServer
  private lateinit var service: TMDbService

  @BeforeEach
  fun createService() {
    mockWebServer = MockWebServer()
    mockWebServer.start()

    val moshi =
      Moshi
        .Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    service =
      Retrofit
        .Builder()
        .baseUrl(mockWebServer.url("/"))
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .build()
        .create(TMDbService::class.java)
  }

  @AfterEach
  fun stopService() {
    mockWebServer.shutdown()
  }

  @Test
  fun getPopularMovies_returnsMoviesList() {
    val mockResponse =
      MockResponse()
        .setResponseCode(200)
        .setBody(
          """
          {
            "page": 1,
            "results": [
              {
                "id": 1,
                "title": "Mock Movie",
                "overview": "Overview of mock movie",
                "poster_path": "/mock_poster.jpg",
                "backdrop_path": "/mock_backdrop.jpg",
                "release_date": "2024-01-01",
                "vote_average": 8.5,
                "vote_count": 100
              }
            ],
            "total_pages": 10,
            "total_results": 100
          }
          """.trimIndent(),
        )

    mockWebServer.enqueue(mockResponse)

    val observer = service.getPopularMoviesWithRx("api_key", 1).test()

    observer.awaitTerminalEvent()
    observer.assertNoErrors()

    val response = observer.values()[0]
    assertNotNull(response)
    assertEquals(1, response.page)
    assertEquals(10, response.totalPages)
    assertEquals(100, response.totalResults)
    assertEquals(1, response.movies?.size)

    val movie = response.movies?.get(0)
    assertEquals(1L, movie?.id) // Movie class has id as Long
    assertEquals("Mock Movie", movie?.title)
  }
}
