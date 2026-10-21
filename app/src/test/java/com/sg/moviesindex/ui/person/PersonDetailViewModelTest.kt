package com.sg.moviesindex.ui.person

import android.app.Application
import com.sg.moviesindex.InstantExecutorExtension
import com.sg.moviesindex.RxImmediateSchedulerRule
import com.sg.moviesindex.data.local.Movie
import com.sg.moviesindex.data.remote.Person
import com.sg.moviesindex.data.remote.PersonMovieCredits
import com.sg.moviesindex.data.repository.Repository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.reactivex.Observable
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(RxImmediateSchedulerRule::class, InstantExecutorExtension::class)
class PersonDetailViewModelTest {
  private lateinit var viewModel: PersonDetailViewModel
  private val repository: Repository = mockk(relaxed = true)
  private val application: Application = mockk(relaxed = true)

  @BeforeEach
  fun setup() {
    viewModel = PersonDetailViewModel(application, repository)
  }

  @Test
  fun `fetchPersonDetails success - updates person LiveData and sets isLoading false`() {
    val person =
      Person(
        id = 287L,
        name = "Brad Pitt",
        biography = "William Bradley Pitt is an American actor...",
        birthday = "1963-12-18",
        placeOfBirth = "Shawnee, Oklahoma, USA",
        profilePath = "/kU3B75TyRiCgE270Y4XYeqP2qvJ.jpg",
        knownForDepartment = "Acting",
      )
    every { repository.getPersonDetails(287L, any()) } returns Observable.just(person)

    viewModel.fetchPersonDetails(287L)

    assertEquals(person, viewModel.person.value)
    assertEquals("Brad Pitt", viewModel.person.value?.name)
    assertEquals("Acting", viewModel.person.value?.knownForDepartment)
    assertFalse(viewModel.isLoading.value ?: true)
    verify { repository.getPersonDetails(287L, any()) }
  }

  @Test
  fun `fetchPersonDetails error - isLoading set to false and person remains null`() {
    every { repository.getPersonDetails(287L, any()) } returns
      Observable.error(RuntimeException("Network error"))

    viewModel.fetchPersonDetails(287L)

    assertNull(viewModel.person.value)
    assertFalse(viewModel.isLoading.value ?: true)
    verify { repository.getPersonDetails(287L, any()) }
  }

  @Test
  fun `fetchPersonMovieCredits success - updates movieCredits LiveData`() {
    val movie1 = Movie(id = 550L, title = "Fight Club")
    val movie2 = Movie(id = 16869L, title = "Inglourious Basterds")
    val credits =
      PersonMovieCredits(
        id = 287L,
        cast = mutableListOf(movie1, movie2),
      )
    every { repository.getPersonMovieCredits(287L, any()) } returns Observable.just(credits)

    viewModel.fetchPersonMovieCredits(287L)

    assertEquals(credits, viewModel.movieCredits.value)
    assertEquals(
      2,
      viewModel.movieCredits.value
        ?.cast
        ?.size,
    )
    assertEquals(
      "Fight Club",
      viewModel.movieCredits.value
        ?.cast
        ?.first()
        ?.title,
    )
    verify { repository.getPersonMovieCredits(287L, any()) }
  }

  @Test
  fun `fetchPersonMovieCredits error - does not throw and leaves movieCredits null`() {
    every { repository.getPersonMovieCredits(287L, any()) } returns
      Observable.error(RuntimeException("Credits fetch error"))

    viewModel.fetchPersonMovieCredits(287L)

    assertNull(viewModel.movieCredits.value)
    verify { repository.getPersonMovieCredits(287L, any()) }
  }
}
