package com.sg.moviesindex.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.ContentValues
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.IBinder
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.sg.moviesindex.R
import com.sg.moviesindex.data.remote.Torrent
import com.sg.moviesindex.data.remote.YTSService
import com.sg.moviesindex.ui.details.MovieDetailActivity
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.BufferedInputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import javax.inject.Inject

/**
 * A background service that downloads a torrent file.
 * Handles creating a foreground notification, tracking progress,
 * and saving the downloaded file to the device storage.
 */
@AndroidEntryPoint
class TorrentDownloaderService : Service() {
  @Inject
  lateinit var ytsService: YTSService

  private var torrent: Torrent? = null
  private var directory: File? = null
  private lateinit var notificationBuilder: NotificationCompat.Builder
  private lateinit var notificationManager: NotificationManager

  companion object {
    private const val TAG = "TorrentDownloader"
  }

  override fun onBind(intent: Intent?): IBinder? = null

  override fun onStartCommand(
    intent: Intent?,
    flags: Int,
    startId: Int,
  ): Int {
    intent?.let {
      torrent =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
          it.getParcelableExtra("torrent", Torrent::class.java)
        } else {
          @Suppress("DEPRECATION")
          it.getParcelableExtra("torrent")
        }
    }

    val currentTorrent = torrent ?: return START_NOT_STICKY

    notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      val notificationChannel =
        NotificationChannel(
          getString(R.string.download_notification_channel_id),
          getString(R.string.download_notification_channel_name),
          NotificationManager.IMPORTANCE_LOW,
        ).apply {
          description = getString(R.string.download_notification_description)
          setSound(null, null)
          enableLights(false)
          lightColor = Color.BLUE
          enableVibration(false)
        }
      notificationManager.createNotificationChannel(notificationChannel)
    }

    notificationBuilder =
      NotificationCompat
        .Builder(this, getString(R.string.download_notification_channel_id))
        .setSmallIcon(android.R.drawable.stat_sys_download)
        .setContentTitle(getString(R.string.download_title))
        .setContentText(getString(R.string.downloading_file))
        .setDefaults(0)
        .setAutoCancel(true)

    notificationManager.notify(0, notificationBuilder.build())

    val drawable = ContextCompat.getDrawable(this, R.mipmap.ic_launcher)
    drawable?.let {
      val bitmap = (it as BitmapDrawable).bitmap
      notificationBuilder.setLargeIcon(bitmap)
    }

    downloadTorrent(currentTorrent)

    return START_NOT_STICKY
  }

  /**
   * Initiates the torrent file download by calling the network API.
   *
   * @param torrent The torrent object containing the download URL.
   */
  private fun downloadTorrent(torrent: Torrent) {
    val call = ytsService.downloadFileWithDynamicUrlSync(torrent.url!!)
    call.enqueue(
      object : Callback<ResponseBody> {
        override fun onResponse(
          call: Call<ResponseBody>,
          response: Response<ResponseBody>,
        ) {
          if (response.isSuccessful) {
            Log.d(TAG, "Server Contacted and Has File")
            val content = response.headers()["Content-Disposition"]
            val filename =
              content
                ?.split("filename=")
                ?.get(1)
                ?.replace("\"", "")
                ?.trim()
                ?: "torrent.torrent"

            val writtenToDisk = downloadFile(response.body()!!, filename)
            if (!writtenToDisk) {
              Toast
                .makeText(
                  applicationContext,
                  getString(R.string.file_saving_failed),
                  Toast.LENGTH_SHORT,
                ).show()
              notificationManager.cancel(0)
              stopSelf()
            }
            Log.d(TAG, "File download was a success? $writtenToDisk")
          } else {
            Log.d(TAG, "Server Contact Failed")
            Toast
              .makeText(
                applicationContext,
                getString(R.string.error_downloading_torrent),
                Toast.LENGTH_SHORT,
              ).show()
            notificationManager.cancel(0)
            stopSelf()
          }
        }

        override fun onFailure(
          call: Call<ResponseBody>,
          t: Throwable,
        ) {
          Log.e(TAG, "Error in downloading torrent! ${t.localizedMessage}")
          Toast
            .makeText(
              applicationContext,
              "Error in downloading torrent file!",
              Toast.LENGTH_SHORT,
            ).show()
          notificationManager.cancel(0)
          stopSelf()
        }
      },
    )
  }

  /**
   * Reads the response body and writes it to a file on disk.
   * Uses MediaStore for API 29+ and regular file writing for older versions.
   *
   * @param body The network response body.
   * @param filename The name of the file to save.
   * @return true if successful, false otherwise.
   */
  private fun downloadFile(
    body: ResponseBody,
    filename: String,
  ): Boolean {
    var downloadComplete = false
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
      val contentValues =
        ContentValues().apply {
          put(MediaStore.Downloads.DISPLAY_NAME, filename)
          put(MediaStore.Downloads.MIME_TYPE, "application/x-bittorrent")
          put(MediaStore.Downloads.IS_PENDING, true)
          put(MediaStore.Downloads.RELATIVE_PATH, getString(R.string.download_relative_path))
        }

      val uri = MediaStore.Downloads.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
      val itemUri = contentResolver.insert(uri, contentValues)

      if (itemUri != null) {
        try {
          contentResolver.openOutputStream(itemUri)?.use { outputStream ->
            val data = ByteArray(1024 * 4)
            val fileSize = body.contentLength()
            val inputStream = BufferedInputStream(body.byteStream(), 1024 * 8)
            var total: Long = 0
            var count: Int
            while (inputStream.read(data).also { count = it } != -1) {
              total += count.toLong()
              val progress = (total * 100 / fileSize).toInt()
              updateNotification(progress, filename)
              outputStream.write(data, 0, count)
              downloadComplete = true
            }
            onDownloadComplete(downloadComplete, filename, itemUri)
            inputStream.close()
          }
          contentValues.clear()
          contentValues.put(MediaStore.Downloads.IS_PENDING, false)
          contentResolver.update(itemUri, contentValues, null, null)
          return true
        } catch (e: IOException) {
          Log.e(TAG, "Error in downloading file", e)
          return false
        }
      }
      return false
    } else {
      try {
        directory =
          File(
            Environment.getExternalStorageDirectory().toString() + "/" +
              getString(R.string.app_name),
          )
        if (directory?.exists() == false && !directory!!.mkdirs()) {
          return false
        }
        val finalSavedFile = File(directory!!.absolutePath + File.separator + filename)
        if (finalSavedFile.exists()) {
          finalSavedFile.delete()
        }
        val data = ByteArray(1024 * 4)
        val fileSize = body.contentLength()
        val inputStream = BufferedInputStream(body.byteStream(), 1024 * 8)
        val outputStream = FileOutputStream(finalSavedFile)
        var total: Long = 0
        var count: Int
        while (inputStream.read(data).also { count = it } != -1) {
          total += count.toLong()
          val progress = (total * 100 / fileSize).toInt()
          updateNotification(progress, filename)
          outputStream.write(data, 0, count)
          downloadComplete = true
        }
        onDownloadComplete(downloadComplete, filename, null)
        outputStream.flush()
        outputStream.close()
        inputStream.close()
      } catch (e: Exception) {
        Toast.makeText(applicationContext, e.localizedMessage, Toast.LENGTH_SHORT).show()
        return false
      }
      return true
    }
  }

  private fun updateNotification(
    currentProgress: Int,
    filename: String,
  ) {
    val progressText = getString(R.string.download_progress_format, currentProgress)
    notificationBuilder
      .setProgress(100, currentProgress, false)
      .setContentText(progressText)
      .setStyle(NotificationCompat.BigTextStyle().bigText("$progressText\n$filename"))
    notificationManager.notify(0, notificationBuilder.build())
  }

  private fun sendProgressUpdate(downloadComplete: Boolean) {
    val intent =
      Intent(MovieDetailActivity.PROGRESS_UPDATE).apply {
        setPackage(packageName)
        putExtra("downloadComplete", downloadComplete)
      }
    sendBroadcast(intent)
  }

  private fun onDownloadComplete(
    downloadComplete: Boolean,
    filename: String,
    itemUri: Uri?,
  ) {
    sendProgressUpdate(downloadComplete)
    notificationManager.cancel(0)
    val completeText = getString(R.string.file_downloaded_format, filename)
    notificationBuilder
      .setProgress(0, 0, false)
      .setContentText(completeText)
      .setStyle(NotificationCompat.BigTextStyle().bigText(completeText))

    val drawable = ContextCompat.getDrawable(this, R.mipmap.ic_launcher)
    drawable?.let {
      val bitmap = (it as BitmapDrawable).bitmap
      notificationBuilder.setLargeIcon(bitmap)
    }
    notificationBuilder.setSmallIcon(android.R.drawable.stat_sys_download_done)

    val intent =
      Intent(Intent.ACTION_VIEW).apply {
        val uri =
          itemUri ?: FileProvider.getUriForFile(
            applicationContext,
            "$packageName.provider",
            File(directory!!.absolutePath + File.separator + filename),
          )
        setDataAndType(uri, "application/x-bittorrent")
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
      }

    val pIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)
    notificationBuilder.setContentIntent(pIntent)
    notificationManager.notify(0, notificationBuilder.build())
    stopSelf()
  }

  override fun onTaskRemoved(rootIntent: Intent?) {
    notificationManager.cancel(0)
  }
}
