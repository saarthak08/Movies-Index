package com.sg.moviesindex.service;


import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.afollestad.materialdialogs.MaterialDialog;
import com.sg.moviesindex.adapter.TorrentsListItemAdapter;
import com.sg.moviesindex.model.tmdb.Movie;
import com.sg.moviesindex.model.yts.APIResponse;
import com.sg.moviesindex.model.yts.Torrent;
import com.sg.moviesindex.service.network.RetrofitInstance;
import com.sg.moviesindex.service.network.YTSService;

import org.jetbrains.annotations.NotNull;

import br.com.simplepass.loadingbutton.customViews.CircularProgressButton;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;


public class TorrentFetcherService {

    private final CompositeDisposable compositeDisposable;
    private APIResponse response = new APIResponse();
    private final Context context;
    public static Torrent resultantTorrent;
    private com.sg.moviesindex.model.yts.Movie resultantMovie;

    public interface OnCompleteListener {
        void onComplete(boolean error);
    }

    private final OnCompleteListener mListener;

    public TorrentFetcherService(OnCompleteListener listener, Context context) {
        mListener = listener;
        compositeDisposable = new CompositeDisposable();
        this.context = context;
    }


    public void start(final CircularProgressButton button, Movie movieTMDb) {
        final Handler handler = new Handler();
        button.startAnimation();
        final YTSService ytsService = RetrofitInstance.getYTSService(context);
        String movieId = movieTMDb.getImdbId();
        if (movieId == null || movieId.equals("")) {
            Toast.makeText(context, "No Torrents Found! Please try again!", Toast.LENGTH_SHORT).show();
            mListener.onComplete(true);
            button.revertAnimation();
            button.stopAnimation();
            return;
        }
        Observable<APIResponse> observableAPIResponse = ytsService.getMoviesList(movieId);
        compositeDisposable.add(observableAPIResponse.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableObserver<APIResponse>() {
                    @Override
                    public void onNext(@NotNull APIResponse apiResponse) {
                        response = apiResponse;
                        Log.e("Torrent Fetch", apiResponse.getStatus());
                    }

                    @Override
                    public void onError(@NotNull Throwable e) {
                        Log.e("Torrent Fetch", e.toString());
                        Toast.makeText(context, "Error in fetching torrent files! Please try again using a VPN.", Toast.LENGTH_SHORT).show();
                        mListener.onComplete(true);
                        button.revertAnimation();
                        button.stopAnimation();
                    }

                    @Override
                    public void onComplete() {
                        mListener.onComplete(true);
                        button.revertAnimation();
                        button.stopAnimation();
                        if(response!=null) {
                            if (response.getData().getMovieCount() == 0) {
                                Toast.makeText(context, "No Torrents Found! Please try again", Toast.LENGTH_SHORT).show();

                            } else {
                                resultantMovie = response.getData().getMovies().get(0);
                                showMaterialDialog(movieTMDb, button);
                            }
                        }
                    }
                }));
    }


    private void showMaterialDialog(Movie movie, CircularProgressButton button) {
        if (resultantMovie != null) {
            try {
                new MaterialDialog.Builder(context).adapter(new TorrentsListItemAdapter(resultantMovie.getTorrents(), button, mListener), new LinearLayoutManager(context)).dividerColor(context.getResources().getColor(android.R.color.darker_gray))
                        .title("Torrent Files")
                        .content("Note: If the torrent file isn't working, try using a VPN or check the torrent's seeds.\nYou need a torrent downloader to download the original file from the torrent file.")
                        .show();
            } catch (Exception e) {
                Log.e("MaterialDialogException", e.toString());
            }
        }
    }
}
