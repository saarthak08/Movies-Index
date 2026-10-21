package com.sg.moviesindex.util

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import com.sg.moviesindex.R
import java.net.URLEncoder

/**
 * Utility for constructing BitTorrent magnet links and launching external torrent clients.
 */
object MagnetUtil {
  val DEFAULT_TRACKERS =
    listOf(
      "udp://open.demonii.com:1337/announce",
      "udp://tracker.openbittorrent.com:80",
      "udp://tracker.coppersurfer.tk:6969",
      "udp://glotorrents.pw:6969/announce",
      "udp://tracker.opentrackr.org:1337/announce",
      "udp://torrent.gresille.org:80/announce",
      "udp://p4p.arenabg.com:1337",
      "udp://tracker.leechers-paradise.org:6969",
    )

  /**
   * Builds a standard BitTorrent magnet link from a torrent hash and movie title.
   */
  fun buildMagnetUri(
    hash: String,
    title: String,
    trackers: List<String> = DEFAULT_TRACKERS,
  ): String {
    val encodedTitle = URLEncoder.encode(title, "UTF-8").replace("+", "%20")
    val trackerParams = trackers.joinToString("") { "&tr=" + URLEncoder.encode(it, "UTF-8") }
    return "magnet:?xt=urn:btih:$hash&dn=$encodedTitle$trackerParams"
  }

  /**
   * Launches an installed BitTorrent client via an Intent.ACTION_VIEW intent.
   *
   * @return true if the intent was dispatched, false if no client is installed.
   */
  fun openInTorrentClient(
    context: Context,
    magnetUri: String,
  ): Boolean =
    try {
      val intent =
        Intent(Intent.ACTION_VIEW, Uri.parse(magnetUri)).apply {
          addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
      val chooser =
        Intent.createChooser(intent, context.getString(R.string.open_magnet_link)).apply {
          addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
      context.startActivity(chooser)
      true
    } catch (e: Exception) {
      Toast
        .makeText(
          context,
          context.getString(R.string.no_torrent_client_found),
          Toast.LENGTH_SHORT,
        ).show()
      false
    }

  /**
   * Copies the magnet link to the user's clipboard.
   */
  fun copyToClipboard(
    context: Context,
    magnetUri: String,
  ) {
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clip = ClipData.newPlainText("Magnet Link", magnetUri)
    clipboard.setPrimaryClip(clip)
    Toast
      .makeText(
        context,
        context.getString(R.string.magnet_link_copied),
        Toast.LENGTH_SHORT,
      ).show()
  }
}
