package com.sg.moviesindex.ui.person

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.sg.moviesindex.R
import com.sg.moviesindex.data.remote.Person
import com.sg.moviesindex.databinding.ActivityPersonDetailBinding
import com.sg.moviesindex.ui.details.RecommendationsAdapter
import dagger.hilt.android.AndroidEntryPoint

/**
 * Activity for displaying actor and crew details, biography, and filmography credits.
 */
@AndroidEntryPoint
class PersonDetailActivity : AppCompatActivity() {
  private lateinit var binding: ActivityPersonDetailBinding
  private lateinit var viewModel: PersonDetailViewModel
  private lateinit var filmographyAdapter: RecommendationsAdapter

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    binding = DataBindingUtil.setContentView(this, R.layout.activity_person_detail)

    val toolbar: Toolbar = findViewById(R.id.toolbar)
    setSupportActionBar(toolbar)
    supportActionBar?.setDisplayHomeAsUpEnabled(true)

    viewModel = ViewModelProvider(this)[PersonDetailViewModel::class.java]

    setupFilmographyRecyclerView()

    val personId =
      intent.getLongExtra("person_id", -1L).takeIf { it != -1L }
        ?: intent.getIntExtra("person_id", -1).takeIf { it != -1 }?.toLong()

    val initialName = intent.getStringExtra("person_name")
    val initialProfile = intent.getStringExtra("person_profile")
    if (initialName != null || initialProfile != null) {
      binding.person =
        Person(
          id = personId,
          name = initialName,
          profilePath = initialProfile,
        )
      binding.collapsingToolbar.title = initialName
    }

    observeViewModel()

    if (personId != null && personId > 0) {
      viewModel.fetchPersonDetails(personId)
      viewModel.fetchPersonMovieCredits(personId)
    }
  }

  private fun setupFilmographyRecyclerView() {
    filmographyAdapter = RecommendationsAdapter(this)
    val rvFilmography = binding.rvFilmography
    rvFilmography.layoutManager =
      LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
    rvFilmography.itemAnimator = DefaultItemAnimator()
    rvFilmography.adapter = filmographyAdapter
  }

  private fun observeViewModel() {
    viewModel.person.observe(this) { person ->
      if (person != null) {
        binding.person = person
        binding.collapsingToolbar.title = person.name

        if (!person.knownForDepartment.isNullOrBlank()) {
          binding.tvDepartment.visibility = View.VISIBLE
          binding.tvDepartment.text =
            getString(R.string.department_format, person.knownForDepartment)
        } else {
          binding.tvDepartment.visibility = View.GONE
        }

        if (!person.birthday.isNullOrBlank()) {
          binding.tvBirthday.visibility = View.VISIBLE
          binding.tvBirthday.text =
            getString(R.string.born_format, person.birthday)
        } else {
          binding.tvBirthday.visibility = View.GONE
        }

        if (!person.placeOfBirth.isNullOrBlank()) {
          binding.tvPlaceOfBirth.visibility = View.VISIBLE
          binding.tvPlaceOfBirth.text =
            getString(R.string.birthplace_format, person.placeOfBirth)
        } else {
          binding.tvPlaceOfBirth.visibility = View.GONE
        }

        if (!person.biography.isNullOrBlank()) {
          binding.tvBiography.text = person.biography
        } else {
          binding.tvBiography.text = getString(R.string.no_biography)
        }
      }
    }

    viewModel.movieCredits.observe(this) { credits ->
      val movies = credits?.cast?.filter { it.posterPath != null }
      if (!movies.isNullOrEmpty()) {
        binding.tvFilmographyTitle.visibility = View.VISIBLE
        binding.rvFilmography.visibility = View.VISIBLE
        filmographyAdapter.submitList(movies)
      } else {
        binding.tvFilmographyTitle.visibility = View.GONE
        binding.rvFilmography.visibility = View.GONE
      }
    }
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    if (item.itemId == android.R.id.home) {
      finish()
      return true
    }
    return super.onOptionsItemSelected(item)
  }
}
