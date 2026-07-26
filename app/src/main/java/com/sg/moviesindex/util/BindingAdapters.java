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

public class BindingAdapters {

    @BindingAdapter("imageUrl")
    public static void loadImage(ImageView view, String url) {
        CircularProgressDrawable circularProgressDrawable = new CircularProgressDrawable(view.getContext());
        circularProgressDrawable.setStrokeWidth(5f);
        circularProgressDrawable.setCenterRadius(30f);
        circularProgressDrawable.start();

        Glide.with(view.getContext())
                .load(url)
                .placeholder(circularProgressDrawable)
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
                .placeholder(circularProgressDrawable)
                .into(view);
    }

    @BindingAdapter("boldText")
    public static void setBoldText(TextView textView, String boldText) {
        setBoldAndNormalText(textView, boldText, "");
    }

    @BindingAdapter(value = {"boldText", "normalText"}, requireAll = false)
    public static void setBoldAndNormalText(TextView textView, String boldText, String normalText) {
        String bText = boldText != null ? boldText : "";
        String nText = normalText != null ? normalText : "";
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
