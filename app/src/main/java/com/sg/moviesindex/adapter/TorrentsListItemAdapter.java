package com.sg.moviesindex.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.databinding.BindingAdapter;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.sg.moviesindex.R;
import com.sg.moviesindex.databinding.TorrentListItemsLayoutBinding;
import com.sg.moviesindex.model.yts.Torrent;
import com.sg.moviesindex.service.TorrentFetcherService;

import java.util.List;

import br.com.simplepass.loadingbutton.customViews.CircularProgressButton;


public class TorrentsListItemAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final List<Torrent> torrentList;
    private final CircularProgressButton button;
    private final TorrentFetcherService.OnCompleteListener completeListener;

    public TorrentsListItemAdapter(List<Torrent> torrentList, CircularProgressButton button, TorrentFetcherService.OnCompleteListener completeListener) {
        this.torrentList = torrentList;
        this.button = button;
        this.completeListener = completeListener;
    }

    @BindingAdapter({"boldText", "normalText"})
    public static void format(TextView textView, String boldText, String normalText) {
        SpannableString str = new SpannableString(boldText + normalText);
        str.setSpan(new StyleSpan(Typeface.BOLD), 0, boldText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        textView.setText(str);
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        TorrentListItemsLayoutBinding torrentListItemsLayoutBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.torrent_list_items_layout, parent, false);
        return new TorrentListItemViewHolder(torrentListItemsLayoutBinding, button, completeListener);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof TorrentListItemViewHolder) {
            Torrent torrent = torrentList.get(position);
            ((TorrentListItemViewHolder) holder).torrentListItemsLayoutBinding.setTorrent(torrent);
        }
    }

    @Override
    public int getItemCount() {
        return torrentList == null ? 0 : torrentList.size();
    }


    static class TorrentListItemViewHolder extends RecyclerView.ViewHolder {
        private final TorrentListItemsLayoutBinding torrentListItemsLayoutBinding;
        private final CircularProgressButton button;
        private final TorrentFetcherService.OnCompleteListener completeListener;

        TorrentListItemViewHolder(@NonNull final TorrentListItemsLayoutBinding torrentListItemsLayoutBinding, CircularProgressButton button, TorrentFetcherService.OnCompleteListener completeListener) {
            super(torrentListItemsLayoutBinding.getRoot());
            this.button = button;
            this.completeListener = completeListener;
            this.torrentListItemsLayoutBinding = torrentListItemsLayoutBinding;
            torrentListItemsLayoutBinding.ivDownloadButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TorrentFetcherService.resultantTorrent = torrentListItemsLayoutBinding.getTorrent();
                    completeListener.onComplete(false);
                    button.revertAnimation();
                    button.stopAnimation();
                }
            });
        }

    }
}
