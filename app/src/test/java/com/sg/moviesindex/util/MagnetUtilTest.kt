package com.sg.moviesindex.util

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class MagnetUtilTest {
  @Test
  fun `buildMagnetUri creates valid magnet URI with hash and encoded title`() {
    val hash = "ABC1234567890DEF"
    val title = "Inception (2010)"
    val trackers = listOf("udp://tracker.example.com:80")

    val magnetUri = MagnetUtil.buildMagnetUri(hash, title, trackers)

    assertTrue(magnetUri.startsWith("magnet:?xt=urn:btih:ABC1234567890DEF"))
    assertTrue(magnetUri.contains("dn=Inception") && magnetUri.contains("2010"))
    assertTrue(magnetUri.contains("&tr=udp%3A%2F%2Ftracker.example.com%3A80"))
  }

  @Test
  fun `default trackers include openbittorrent and demonii`() {
    assertTrue(MagnetUtil.DEFAULT_TRACKERS.any { it.contains("demonii") })
    assertTrue(MagnetUtil.DEFAULT_TRACKERS.any { it.contains("openbittorrent") })
  }
}
