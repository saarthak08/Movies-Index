package com.sg.moviesindex.service

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.widget.Toast
import com.sg.moviesindex.R
import com.sg.moviesindex.data.remote.Torrent

/**
 * Helper object for downloading torrent files using the system DownloadManager.
 * Runs in the background independently of the app process and avoids service limits.
 */
object TorrentDownloader {
  /**
   * Enqueues a torrent file download using Android's native DownloadManager.
   *
   * @param context Application or activity context.
   * @param torrent The torrent to download containing URL and quality info.
   * @param movieTitle The title of the movie for naming and notification.
   * @return true if successfully enqueued, false otherwise.
   */
  fun download(
    context: Context,
    torrent: Torrent,
    movieTitle: String,
    showToast: Boolean = true,
  ): Boolean {
    val downloadUrl = torrent.url
    if (downloadUrl.isNullOrEmpty()) {
      if (showToast) {
        Toast
          .makeText(
            context,
            context.getString(R.string.error_downloading_torrent),
            Toast.LENGTH_SHORT,
          ).show()
      }
      return false
    }

    return try {
      val quality = torrent.quality ?: "torrent"
      val sanitizedTitle = movieTitle.replace(Regex("[^a-zA-Z0-9.-]"), "_")
      val filename = "${sanitizedTitle}_$quality.torrent"

      val request =
        DownloadManager
          .Request(Uri.parse(downloadUrl))
          .apply {
            setTitle("$movieTitle ($quality)")
            setDescription(context.getString(R.string.downloading_file))
            setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            setDestinationInExternalPublicDir(
              Environment.DIRECTORY_DOWNLOADS,
              "Movies-Index/$filename",
            )
            setMimeType("application/x-bittorrent")
            setAllowedOverMetered(true)
            setAllowedOverRoaming(true)
          }

      val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
      downloadManager.enqueue(request)
      Toast
        .makeText(
          context,
          context.getString(R.string.download_started),
          Toast.LENGTH_SHORT,
        ).show()
      true
    } catch (e: Exception) {
      Toast
        .makeText(
          context,
          context.getString(R.string.error_downloading_torrent),
          Toast.LENGTH_SHORT,
        ).show()
      false
    }
  }
}
