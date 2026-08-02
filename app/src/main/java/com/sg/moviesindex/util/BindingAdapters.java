package com.sg.moviesindex.util;

import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.databinding.BindingAdapter;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
public class BindingAdapters {

    @BindingAdapter("imageUrl")
    public static void loadImage(ImageView view, String url) {
        CircularProgressDrawable circularProgressDrawable = new CircularProgressDrawable(view.getContext());
        circularProgressDrawable.setStrokeWidth(5f);
        circularProgressDrawable.setCenterRadius(30f);
        circularProgressDrawable.start();

        Glide.with(view.getContext())
                .load(url)
                .transition(DrawableTransitionOptions.withCrossFade())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(circularProgressDrawable)
                .error(com.sg.moviesindex.R.drawable.bg_image_error)
                .into(view);
    }

    @BindingAdapter("profileUrl")
    public static void loadProfileImage(ImageView view, String url) {
        CircularProgressDrawable circularProgressDrawable = new CircularProgressDrawable(view.getContext());
        circularProgressDrawable.setStrokeWidth(5f);
        circularProgressDrawable.setCenterRadius(30f);
        circularProgressDrawable.start();

        Glide.with(view.getContext())
                .load(url)
                .transition(DrawableTransitionOptions.withCrossFade())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(circularProgressDrawable)
                .error(com.sg.moviesindex.R.drawable.bg_image_error)
                .into(view);
    }

    @BindingAdapter(value = {"boldText", "normalText"}, requireAll = false)
    public static void setBoldAndNormalText(TextView textView, String boldText, String normalText) {
        String bText = (boldText != null && !boldText.equals("null")) ? boldText : "";
        String nText = (normalText != null && !normalText.equals("null")) ? normalText : "";
        SpannableString str = new SpannableString(bText + nText);
        str.setSpan(
                new StyleSpan(Typeface.BOLD),
                0,
                bText.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        );
        textView.setText(str);
    }
}
