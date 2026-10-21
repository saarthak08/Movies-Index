package com.sg.moviesindex.service

import android.content.Context
import com.sg.moviesindex.data.remote.Torrent
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Test

class TorrentDownloaderTest {
  @Test
  fun `download returns false when torrent url is null`() {
    val context: Context = mockk(relaxed = true)
    val torrent = Torrent(url = null, quality = "1080p")

    val result = TorrentDownloader.download(context, torrent, "Inception", showToast = false)

    assertFalse(result)
  }

  @Test
  fun `download returns false when torrent url is empty`() {
    val context: Context = mockk(relaxed = true)
    val torrent = Torrent(url = "", quality = "720p")

    val result = TorrentDownloader.download(context, torrent, "Interstellar", showToast = false)

    assertFalse(result)
  }
}
