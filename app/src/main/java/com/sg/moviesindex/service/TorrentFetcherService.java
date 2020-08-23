package com.sg.moviesindex.service;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.sg.moviesindex.R;
import com.sg.moviesindex.adapter.TorrentsListItemAdapter;
import com.sg.moviesindex.model.tmdb.Movie;
import com.sg.moviesindex.model.yts.APIResponse;
import com.sg.moviesindex.model.yts.Torrent;
import com.sg.moviesindex.service.network.RetrofitInstance;
import com.sg.moviesindex.service.network.YTSService;

import br.com.simplepass.loadingbutton.customViews.CircularProgressButton;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;


public class TorrentFetcherService {

    private Observable<APIResponse> observableAPIResponse;
    private CompositeDisposable compositeDisposable;
    private APIResponse response = new APIResponse();
    private Context context;
    public static Torrent resultantTorrent;
    private com.sg.moviesindex.model.yts.Movie resultantMovie;

    public interface OnCompleteListener {
        public void onComplete(boolean error);
    }

    private OnCompleteListener mListener;

    public TorrentFetcherService(OnCompleteListener listener, Context context) {
        mListener = listener;
        compositeDisposable = new CompositeDisposable();
        this.context = context;
    }


    public void start(final CircularProgressButton button, Movie movieTMDb) {
        final Handler handler = new Handler();
        button.startAnimation();
        final YTSService ytsService = RetrofitInstance.getYTSService();
        String movieId = movieTMDb.getImdbId();
        if (movieId == null || movieId.equals("")) {
            Toast.makeText(context, "No Torrents Found!", Toast.LENGTH_SHORT).show();
            mListener.onComplete(true);
            button.revertAnimation();
            button.stopAnimation();
            return;
        }
        observableAPIResponse = ytsService.getMoviesList(movieId);
        compositeDisposable.add(observableAPIResponse.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableObserver<APIResponse>() {
                    @Override
                    public void onNext(APIResponse apiResponse) {
                        response = apiResponse;
                        Log.e("Torrent Fetch", apiResponse.getStatus());
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e("Torrent Fetch", e.toString());
                        Toast.makeText(context, "Error in fetching torrent files! ", Toast.LENGTH_SHORT).show();
                        mListener.onComplete(true);
                        button.revertAnimation();
                        button.stopAnimation();
                    }

                    @Override
                    public void onComplete() {
                        mListener.onComplete(true);
                        button.revertAnimation();
                        button.stopAnimation();
                        if (response.getData().getMovieCount() == 0) {
                            Toast.makeText(context, "No Torrents Found!", Toast.LENGTH_SHORT).show();

                        } else {
                            resultantMovie = response.getData().getMovies().get(0);
                            showMaterialDialog(movieTMDb, button);
                        }
                    }
                }));
    }


    private void showMaterialDialog(Movie movie, CircularProgressButton button) {
        if (resultantMovie != null) {
            new MaterialDialog.Builder(context).adapter(new TorrentsListItemAdapter(context, resultantMovie.getTorrents(),button,mListener), new LinearLayoutManager(context)).dividerColor(context.getResources().getColor(android.R.color.darker_gray))
                    .title("Torrent Files")
                    .content("Note: If the torrent file isn't working, try using a VPN or check the torrent's seeds.\nYou need a torrent downloader to download the original file from the torrent file.")
                    .show();
        }
    }
}
