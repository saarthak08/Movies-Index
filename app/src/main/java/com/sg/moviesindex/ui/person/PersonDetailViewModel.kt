package com.sg.moviesindex.ui.person

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.sg.moviesindex.data.remote.Person
import com.sg.moviesindex.data.remote.PersonMovieCredits
import com.sg.moviesindex.data.repository.Repository
import com.sg.moviesindex.util.BuildConfigs
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

/**
 * ViewModel for [PersonDetailActivity].
 * Handles fetching actor/crew biography, details, and filmography credits.
 */
@HiltViewModel
class PersonDetailViewModel
  @Inject
  constructor(
    application: Application,
    private val repository: Repository,
  ) : AndroidViewModel(application) {
    private val compositeDisposable = CompositeDisposable()

    val person = MutableLiveData<Person>()
    val movieCredits = MutableLiveData<PersonMovieCredits>()
    val isLoading = MutableLiveData(false)

    /**
     * Fetches detailed biography and information for a person.
     *
     * @param personId TMDb person ID.
     */
    fun fetchPersonDetails(personId: Long) {
      isLoading.value = true
      compositeDisposable.add(
        repository
          .getPersonDetails(personId, BuildConfigs.API_KEY)
          .subscribeOn(Schedulers.io())
          .observeOn(AndroidSchedulers.mainThread())
          .subscribeWith(
            object : DisposableObserver<Person>() {
              override fun onNext(p: Person) {
                person.value = p
              }

              override fun onError(e: Throwable) {
                isLoading.value = false
              }

              override fun onComplete() {
                isLoading.value = false
              }
            },
          ),
      )
    }

    /**
     * Fetches movie credits (filmography) for a person.
     *
     * @param personId TMDb person ID.
     */
    fun fetchPersonMovieCredits(personId: Long) {
      compositeDisposable.add(
        repository
          .getPersonMovieCredits(personId, BuildConfigs.API_KEY)
          .subscribeOn(Schedulers.io())
          .observeOn(AndroidSchedulers.mainThread())
          .subscribeWith(
            object : DisposableObserver<PersonMovieCredits>() {
              override fun onNext(credits: PersonMovieCredits) {
                movieCredits.value = credits
              }

              override fun onError(e: Throwable) {}

              override fun onComplete() {}
            },
          ),
      )
    }

    override fun onCleared() {
      compositeDisposable.clear()
    }
  }
