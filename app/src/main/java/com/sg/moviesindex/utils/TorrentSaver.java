package com.sg.moviesindex.utils;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.sg.moviesindex.R;
import com.sg.moviesindex.model.yts.Torrent;
import com.sg.moviesindex.service.network.RetrofitInstance;
import com.sg.moviesindex.service.network.YTSService;

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
import retrofit2.Retrofit;
import retrofit2.http.Header;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class TorrentSaver {
    private Context context;
    private Torrent torrent;
    private File directory ;

    public TorrentSaver(Context context,Torrent torrent) {
        this.context=context;
        this.torrent=torrent;
        directory=new File(Environment.getExternalStorageDirectory() + "/" + context.getString(R.string.app_name));
    }

    public void downloadTorrent() {
        Call<ResponseBody> call = RetrofitInstance.getYTSService().downloadFileWithDynamicUrlSync(torrent.getUrl());

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if(response.isSuccessful()) {
                    Log.d(TAG, "Server Contacted and Has File");
                    Headers headers=response.headers();
                    String content=headers.get("Content-Disposition");
                    String contentSplit[] = content.split("filename=");
                    String filename = contentSplit[1].replace("filename=", "").replace("\"", "").trim();
                    boolean writtenToDisk = writeResponseBodyToDisk(response.body(),filename);
                    if(writtenToDisk) {
                        Toast.makeText(context,"File downloaded & saved successfully!",Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context,"File saving failed!",Toast.LENGTH_SHORT).show();
                    }
                    Log.d(TAG, "File download was a success? " + writtenToDisk);
                } else {
                    Log.d(TAG, "Server Contact Failed");
                    Toast.makeText(context,"Error in downloading torrent file!",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e(TAG, "Error in downloading torrent! "+t.getLocalizedMessage());
                Toast.makeText(context,"Error in downloading torrent file!",Toast.LENGTH_SHORT).show();
            }
        });
    }


    private boolean writeResponseBodyToDisk(ResponseBody body,String filename) {
        try {
            if ((!directory.exists())&& (!directory.mkdirs()))
            {
                return false;
            }
            File finalSavedFile=new File(directory.getAbsolutePath()+File.separator+filename);
            InputStream inputStream = null;
            OutputStream outputStream = null;

            try {
                byte[] fileReader = new byte[4096];

                long fileSize = body.contentLength();
                long fileSizeDownloaded = 0;

                inputStream = body.byteStream();
                outputStream = new FileOutputStream(finalSavedFile);

                while (true) {
                    int read = inputStream.read(fileReader);

                    if (read == -1) {
                        break;
                    }

                    outputStream.write(fileReader, 0, read);

                    fileSizeDownloaded += read;

                    Log.d(TAG, "file download: " + fileSizeDownloaded + " of " + fileSize);
                }

                outputStream.flush();

                return true;
            } catch (IOException e) {
                return false;
            } finally {
                if (inputStream != null) {
                    inputStream.close();
                }

                if (outputStream != null) {
                    outputStream.close();
                }
            }
        } catch (IOException e) {
            return false;
        }
    }
}
