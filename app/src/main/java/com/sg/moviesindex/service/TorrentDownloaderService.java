package com.sg.moviesindex.service;

import android.app.IntentService;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.sg.moviesindex.R;
import com.sg.moviesindex.model.yts.Torrent;
import com.sg.moviesindex.service.network.RetrofitInstance;
import com.sg.moviesindex.view.MoviesInfo;

import org.jetbrains.annotations.NotNull;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import okhttp3.Headers;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class TorrentDownloaderService extends IntentService {
    private final Torrent torrent;
    private File directory;
    private NotificationCompat.Builder notificationBuilder;
    private NotificationManager notificationManager;

    public TorrentDownloaderService() {
        super("Torrent Downloader Service");
        this.torrent = TorrentFetcherService.resultantTorrent;
    }


    @Override
    protected void onHandleIntent(Intent intent) {

        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel("id", "Download Notification", NotificationManager.IMPORTANCE_LOW);
            notificationChannel.setDescription("no sound");
            notificationChannel.setSound(null, null);
            notificationChannel.enableLights(false);
            notificationChannel.setLightColor(Color.BLUE);
            notificationChannel.enableVibration(false);
            notificationManager.createNotificationChannel(notificationChannel);
        }

        notificationBuilder = new NotificationCompat.Builder(this, "id")
                .setSmallIcon(android.R.drawable.stat_sys_download)
                .setContentTitle("Download")
                .setContentText("Downloading File")
                .setDefaults(0)
                .setAutoCancel(true);
        notificationManager.notify(0, notificationBuilder.build());
        Drawable drawable = ContextCompat.getDrawable(this, R.mipmap.ic_launcher);
        assert drawable != null;
        Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
        notificationBuilder.setLargeIcon(bitmap);
        downloadTorrent();

    }

    public void downloadTorrent() {
        Call<ResponseBody> call = RetrofitInstance.getYTSService(getApplicationContext()).downloadFileWithDynamicUrlSync(torrent.getUrl());
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "Server Contacted and Has File");
                    Headers headers = response.headers();
                    String content = headers.get("Content-Disposition");
                    String[] contentSplit = content.split("filename=");
                    String filename = contentSplit[1].replace("filename=", "").replace("\"", "").trim();
                    boolean writtenToDisk = downloadFile(response.body(), filename);
                    if (!writtenToDisk) {
                        Toast.makeText(getApplicationContext(), "File saving failed!", Toast.LENGTH_SHORT).show();
                        notificationManager.cancel(0);
                    }
                    Log.d(TAG, "File download was a success? " + writtenToDisk);
                } else {
                    Log.d(TAG, "Server Contact Failed");
                    Toast.makeText(getApplicationContext(), "Error in downloading torrent file!", Toast.LENGTH_SHORT).show();
                    notificationManager.cancel(0);
                }

            }

            @Override
            public void onFailure(@NotNull Call<ResponseBody> call, @NotNull Throwable t) {
                Log.e(TAG, "Error in downloading torrent! " + t.getLocalizedMessage());
                Toast.makeText(getApplicationContext(), "Error in downloading torrent file!", Toast.LENGTH_SHORT).show();
                notificationManager.cancel(0);
            }
        });
    }


    private boolean downloadFile(ResponseBody body, String filename) {
        boolean downloadComplete = false;
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.Q) {
            ContentValues contentValues = new ContentValues();

            contentValues.put(MediaStore.Downloads.DISPLAY_NAME, filename);
            contentValues.put(MediaStore.Downloads.MIME_TYPE, "application/x-bittorrent");
            contentValues.put(MediaStore.Downloads.IS_PENDING, true);
            contentValues.put(MediaStore.Downloads.RELATIVE_PATH, "Download/" + "Movies-Index");

            Uri uri = MediaStore.Downloads.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY);
            Uri itemUri = getContentResolver().insert(uri, contentValues);

            if (itemUri != null) {
                try {
                    OutputStream outputStream = getContentResolver().openOutputStream(itemUri);
                    int count;
                    byte[] data = new byte[1024 * 4];
                    long fileSize = body.contentLength();
                    InputStream inputStream = new BufferedInputStream(body.byteStream(), 1024 * 8);
                    long total = 0;
                    while ((count = inputStream.read(data)) != -1) {
                        total += count;
                        int progress = (int) ((double) (total * 100) / (double) fileSize);
                        updateNotification(progress, filename);
                        outputStream.write(data, 0, count);
                        downloadComplete = true;
                    }
                    onDownloadComplete(downloadComplete, filename,itemUri);
                    outputStream.flush();
                    outputStream.close();
                    inputStream.close();
                    contentValues.put(MediaStore.Images.Media.IS_PENDING, false);
                    getContentResolver().update(itemUri, contentValues, null, null);
                    return true;
                } catch (IOException e) {
                    e.printStackTrace();
                    return false;
                }
            }
            return false;
        } else {
            try {
                directory = new File(Environment.getExternalStorageDirectory() + "/" + getApplicationContext().getString(R.string.app_name));
                if ((!directory.exists()) && (!directory.mkdirs())) {
                    return false;
                }
                File finalSavedFile = new File(directory.getAbsolutePath() + File.separator + filename);
                if (finalSavedFile.exists()) {
                    finalSavedFile.delete();
                }
                int count;
                byte[] data = new byte[1024 * 4];
                long fileSize = body.contentLength();
                InputStream inputStream = new BufferedInputStream(body.byteStream(), 1024 * 8);
                OutputStream outputStream = new FileOutputStream(finalSavedFile);
                long total = 0;
                while ((count = inputStream.read(data)) != -1) {
                    total += count;
                    int progress = (int) ((double) (total * 100) / (double) fileSize);
                    updateNotification(progress, filename);
                    outputStream.write(data, 0, count);
                    downloadComplete = true;
                }
                onDownloadComplete(downloadComplete, filename,null);
                outputStream.flush();
                outputStream.close();
                inputStream.close();
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                return false;
            }
            return true;
        }

    }

    private void updateNotification(int currentProgress, String filename) {
        notificationBuilder.setProgress(100, currentProgress, false);
        notificationBuilder.setContentText("Downloaded: " + currentProgress + "%");
        notificationBuilder.setStyle(new NotificationCompat.BigTextStyle()
                .bigText("Downloaded: " + currentProgress + "%\n" + filename));
        notificationManager.notify(0, notificationBuilder.build());
    }


    private void sendProgressUpdate(boolean downloadComplete) {
        Intent intent = new Intent(MoviesInfo.PROGRESS_UPDATE);
        intent.putExtra("downloadComplete", downloadComplete);
        LocalBroadcastManager.getInstance(TorrentDownloaderService.this).sendBroadcast(intent);
    }

    private void onDownloadComplete(boolean downloadComplete, String filename, Uri itemUri) {
        sendProgressUpdate(downloadComplete);
        notificationManager.cancel(0);
        notificationBuilder.setProgress(0, 0, false);
        notificationBuilder.setContentText("File Downloaded: " + filename);
        notificationBuilder.setStyle(new NotificationCompat.BigTextStyle()
                .bigText("File Downloaded: " + filename));
        Drawable drawable = ContextCompat.getDrawable(this, R.mipmap.ic_launcher);
        Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
        notificationBuilder.setLargeIcon(bitmap);
        notificationBuilder.setSmallIcon(android.R.drawable.stat_sys_download_done);
        Intent intent = new Intent();
        intent.setAction(android.content.Intent.ACTION_VIEW);
        Uri uri=itemUri;
        if (Build.VERSION.SDK_INT<Build.VERSION_CODES.Q) {
            File file = new File(directory.getAbsolutePath() + File.separator + filename);
            uri = FileProvider.getUriForFile(
                    getApplicationContext(),
                    getApplicationContext()
                            .getPackageName() + ".provider", file);
        }
        intent.setDataAndType(uri, "application/x-bittorrent");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);
        notificationBuilder.setContentIntent(pIntent);
        notificationManager.notify(0, notificationBuilder.build());
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        notificationManager.cancel(0);
    }

}
