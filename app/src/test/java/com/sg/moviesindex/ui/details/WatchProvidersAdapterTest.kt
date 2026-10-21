package com.sg.moviesindex.ui.details

import com.sg.moviesindex.data.remote.WatchProvider
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class WatchProvidersAdapterTest {
  @Test
  fun `areItemsTheSame returns true for same provider id`() {
    val provider1 = WatchProvider(providerId = 8, providerName = "Netflix")
    val provider2 = WatchProvider(providerId = 8, providerName = "Netflix US")

    assertTrue(WatchProvidersAdapter.DiffCallback.areItemsTheSame(provider1, provider2))
  }

  @Test
  fun `areItemsTheSame returns false for different provider ids`() {
    val provider1 = WatchProvider(providerId = 8, providerName = "Netflix")
    val provider2 = WatchProvider(providerId = 9, providerName = "Amazon Prime")

    assertFalse(WatchProvidersAdapter.DiffCallback.areItemsTheSame(provider1, provider2))
  }

  @Test
  fun `areContentsTheSame returns true for equal providers`() {
    val provider1 =
      WatchProvider(providerId = 8, providerName = "Netflix", logoPath = "/netflix.png")
    val provider2 =
      WatchProvider(providerId = 8, providerName = "Netflix", logoPath = "/netflix.png")

    assertTrue(WatchProvidersAdapter.DiffCallback.areContentsTheSame(provider1, provider2))
  }

  @Test
  fun `areContentsTheSame returns false for modified providers`() {
    val provider1 =
      WatchProvider(providerId = 8, providerName = "Netflix", logoPath = "/netflix.png")
    val provider2 =
      WatchProvider(providerId = 8, providerName = "Netflix 4K", logoPath = "/netflix.png")

    assertFalse(WatchProvidersAdapter.DiffCallback.areContentsTheSame(provider1, provider2))
  }
}
