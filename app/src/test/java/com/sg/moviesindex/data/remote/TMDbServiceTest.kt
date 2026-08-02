package com.sg.moviesindex.data.remote

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import java.net.HttpURLConnection

class TMDbServiceTest {
  private lateinit var mockWebServer: MockWebServer
  private lateinit var service: TMDbService

  @BeforeEach
  fun setup() {
    mockWebServer = MockWebServer()
    mockWebServer.start()
    val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
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
  fun teardown() {
    mockWebServer.shutdown()
  }

  @Test
  fun `getPopularMoviesWithRx - returns MoviesList with correct fields and URL`() {
    mockWebServer.enqueue(
      MockResponse()
        .setResponseCode(HttpURLConnection.HTTP_OK)
        .setBody(
          """{"page": 1, "results": [{"id": 1, "title": "Popular Movie"}], "total_results": 1, "total_pages": 5}""",
        ),
    )

    val testObserver = service.getPopularMoviesWithRx("api_key", 1).test()

    testObserver.assertNoErrors()
    testObserver.assertValue { response ->
      response.page == 1 && response.totalPages == 5 &&
        response.movies?.first()?.title == "Popular Movie"
    }

    val request = mockWebServer.takeRequest()
    assertEquals("/movie/popular?api_key=api_key&page=1", request.path)
  }

  @Test
  fun `getTopRatedMoviesWithRx - correct URL and response parsing`() {
    mockWebServer.enqueue(
      MockResponse()
        .setResponseCode(HttpURLConnection.HTTP_OK)
        .setBody(
          """{"page": 1, "results": [{"id": 2, "title": "Top Rated"}], "total_results": 1, "total_pages": 1}""",
        ),
    )

    val testObserver = service.getTopRatedMoviesWithRx("api_key", 1).test()

    testObserver.assertNoErrors()
    testObserver.assertValue { it.movies?.first()?.title == "Top Rated" }

    assertEquals("/movie/top_rated?api_key=api_key&page=1", mockWebServer.takeRequest().path)
  }

  @Test
  fun `getUpcomingMoviesWithRx - correct URL including region param`() {
    mockWebServer.enqueue(
      MockResponse()
        .setResponseCode(HttpURLConnection.HTTP_OK)
        .setBody("""{"page": 1, "results": [], "total_pages": 1}"""),
    )

    service.getUpcomingMoviesWithRx("api_key", 1, "US").test().assertNoErrors()

    assertEquals(
      "/movie/upcoming?api_key=api_key&page=1&region=US",
      mockWebServer.takeRequest().path,
    )
  }

  @Test
  fun `getNowPlayingWithRx - correct URL and response parsing`() {
    mockWebServer.enqueue(
      MockResponse()
        .setResponseCode(HttpURLConnection.HTTP_OK)
        .setBody("""{"page": 1, "results": [], "total_pages": 1}"""),
    )

    service.getNowPlayingWithRx("api_key", 1, "US").test().assertNoErrors()

    assertEquals(
      "/movie/now_playing?api_key=api_key&page=1&region=US",
      mockWebServer.takeRequest().path,
    )
  }

  @Test
  fun `getGenresList - returns GenresList with genres`() {
    mockWebServer.enqueue(
      MockResponse()
        .setResponseCode(HttpURLConnection.HTTP_OK)
        .setBody(
          """{"genres": [{"id": 28, "name": "Action"}, {"id": 35, "name": "Comedy"}]}""",
        ),
    )

    val testObserver = service.getGenresList("api_key").test()

    testObserver.assertNoErrors()
    testObserver.assertValue { response ->
      response.genres?.size == 2 && response.genres?.first()?.name == "Action"
    }

    assertEquals("/genre/movie/list?api_key=api_key", mockWebServer.takeRequest().path)
  }

  @Test
  fun `getReviews - returns ReviewsList with reviews`() {
    mockWebServer.enqueue(
      MockResponse()
        .setResponseCode(HttpURLConnection.HTTP_OK)
        .setBody(
          """{"id": 550, "page": 1, "results": [{"id": "r1", "author": "Reviewer", "content": "Great!"}], "total_pages": 1, "total_results": 1}""",
        ),
    )

    val testObserver = service.getReviews(550L, "api_key", 1).test()

    testObserver.assertNoErrors()
    testObserver.assertValue { response ->
      response.results?.size == 1 && response.results?.first()?.author == "Reviewer"
    }

    assertEquals("/movie/550/reviews?api_key=api_key&page=1", mockWebServer.takeRequest().path)
  }

  @Test
  fun `getCasts - returns CastsList with cast members`() {
    mockWebServer.enqueue(
      MockResponse()
        .setResponseCode(HttpURLConnection.HTTP_OK)
        .setBody(
          """{"id": 550, "cast": [{"id": 1, "name": "Brad Pitt", "character": "Tyler Durden"}]}""",
        ),
    )

    val testObserver = service.getCasts(550L, "api_key").test()

    testObserver.assertNoErrors()
    testObserver.assertValue { response ->
      response.cast?.size == 1 && response.cast?.first()?.name == "Brad Pitt"
    }

    assertEquals("/movie/550/credits?api_key=api_key", mockWebServer.takeRequest().path)
  }

  @Test
  fun `discover - returns DiscoversList with correct URL params`() {
    mockWebServer.enqueue(
      MockResponse()
        .setResponseCode(HttpURLConnection.HTTP_OK)
        .setBody(
          """{"page": 1, "results": [{"id": 10, "title": "Discovered"}], "total_pages": 3}""",
        ),
    )

    val testObserver =
      service
        .discover(
          apiKey = "api_key",
          genres = "28",
          adult = false,
          video = false,
          pageIndex = 1,
          sortBy = "popularity.desc",
          region = null,
          language = null,
          releaseDateGTE = null,
          releaseDateLTE = null,
        ).test()

    testObserver.assertNoErrors()
    testObserver.assertValue { it.results?.first()?.title == "Discovered" }

    val request = mockWebServer.takeRequest()
    assertNotNull(request.path)
    assertTrue(request.path!!.startsWith("/discover/movie?"))
    assertTrue(request.path!!.contains("with_genres=28"))
    assertTrue(request.path!!.contains("include_adult=false"))
  }

  @Test
  fun `search - returns DiscoversList with correct URL params`() {
    mockWebServer.enqueue(
      MockResponse()
        .setResponseCode(HttpURLConnection.HTTP_OK)
        .setBody(
          """{"page": 1, "results": [{"id": 20, "title": "Batman"}], "total_pages": 2}""",
        ),
    )

    val testObserver = service.search("api_key", false, "Batman", 1).test()

    testObserver.assertNoErrors()
    testObserver.assertValue { it.results?.first()?.title == "Batman" }

    assertEquals(
      "/search/movie?api_key=api_key&include_adult=false&query=Batman&page=1",
      mockWebServer.takeRequest().path,
    )
  }

  @Test
  fun `getFullMovieInformation - returns Movie with correct fields`() {
    mockWebServer.enqueue(
      MockResponse()
        .setResponseCode(HttpURLConnection.HTTP_OK)
        .setBody(
          """{"id": 100, "title": "Full Movie", "vote_average": 8.0, "overview": "A full movie"}""",
        ),
    )

    val testObserver = service.getFullMovieInformation(100L, "api_key").test()

    testObserver.assertNoErrors()
    testObserver.assertValue { response ->
      response.id == 100L && response.title == "Full Movie" && response.voteAverage == 8.0
    }

    assertEquals("/movie/100?api_key=api_key", mockWebServer.takeRequest().path)
  }

  @Test
  fun `getPopularMoviesWithRx - verifies correct request path and query params`() {
    mockWebServer.enqueue(
      MockResponse()
        .setResponseCode(HttpURLConnection.HTTP_OK)
        .setBody("""{"page": 3, "results": [], "total_pages": 10}"""),
    )

    service.getPopularMoviesWithRx("my_key_123", 3).test().assertNoErrors()

    val request = mockWebServer.takeRequest()
    assertEquals("/movie/popular?api_key=my_key_123&page=3", request.path)
    assertEquals("GET", request.method)
  }

  @Test
  fun `HTTP 401 error - verify error is propagated`() {
    mockWebServer.enqueue(
      MockResponse()
        .setResponseCode(HttpURLConnection.HTTP_UNAUTHORIZED)
        .setBody("""{"status_message": "Invalid API key"}"""),
    )

    val testObserver = service.getPopularMoviesWithRx("bad_key", 1).test()

    testObserver.assertError { error ->
      error is retrofit2.HttpException && error.code() == 401
    }
  }
}
