package com.sg.moviesindex.ui.search

import android.content.Context
import androidx.fragment.app.FragmentManager
import com.sg.moviesindex.InstantExecutorExtension
import com.sg.moviesindex.RxImmediateSchedulerRule
import com.sg.moviesindex.data.remote.DiscoversList
import com.sg.moviesindex.ui.main.MainViewModel
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.schedulers.TestScheduler
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.util.concurrent.TimeUnit

@ExtendWith(RxImmediateSchedulerRule::class, InstantExecutorExtension::class)
class SearchUtilTest {
  private val compositeDisposable = CompositeDisposable()
  private val fragmentManager: FragmentManager = mockk(relaxed = true)
  private val context: Context = mockk(relaxed = true)
  private val viewModel: MainViewModel = mockk(relaxed = true)
  private lateinit var testScheduler: TestScheduler
  private lateinit var searchUtil: SearchUtil

  @BeforeEach
  fun setup() {
    testScheduler = TestScheduler()
    RxJavaPlugins.setComputationSchedulerHandler { testScheduler }

    searchUtil = SearchUtil(compositeDisposable, fragmentManager, context, viewModel)
    searchUtil.setupSuggestionPipeline(null)
  }

  @AfterEach
  fun tearDown() {
    compositeDisposable.clear()
    RxJavaPlugins.reset()
  }

  @Test
  fun `searchSubject debounces and queries viewModel for non-empty input`() {
    val dummyResponse = DiscoversList(results = mutableListOf())
    every { viewModel.getSearchSuggestions("Inception") } returns Observable.just(dummyResponse)

    searchUtil.searchSubject.onNext("Inception")
    testScheduler.advanceTimeBy(340, TimeUnit.MILLISECONDS)
    verify(exactly = 0) { viewModel.getSearchSuggestions(any()) }

    testScheduler.advanceTimeBy(20, TimeUnit.MILLISECONDS)
    verify(exactly = 1) { viewModel.getSearchSuggestions("Inception") }
  }

  @Test
  fun `searchSubject ignores blank or empty query`() {
    searchUtil.searchSubject.onNext("   ")
    testScheduler.advanceTimeBy(400, TimeUnit.MILLISECONDS)

    verify(exactly = 0) { viewModel.getSearchSuggestions(any()) }
  }

  @Test
  fun `searchSubject does not re-query if input has not changed`() {
    val dummyResponse = DiscoversList(results = mutableListOf())
    every { viewModel.getSearchSuggestions("Batman") } returns Observable.just(dummyResponse)

    searchUtil.searchSubject.onNext("Batman")
    testScheduler.advanceTimeBy(400, TimeUnit.MILLISECONDS)

    searchUtil.searchSubject.onNext("Batman")
    testScheduler.advanceTimeBy(400, TimeUnit.MILLISECONDS)

    verify(exactly = 1) { viewModel.getSearchSuggestions("Batman") }
  }
}
