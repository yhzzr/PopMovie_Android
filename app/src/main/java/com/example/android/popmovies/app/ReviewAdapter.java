package com.example.android.popmovies.app;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

/**
 * Created by hengyang on 08/11/15.
 */
public class ReviewAdapter extends CursorAdapter {

    public ReviewAdapter(Context context, Cursor c, int flags) { super(context, c, flags); }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.review_info, parent, false);

        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        String reviewAuthor = cursor.getString(DetailFragment.COL_REVIEW_AUTHOR);
        String reviewContent = cursor.getString(DetailFragment.COL_REVIEW_CONTENT);

        TextView authorView = (TextView) view.findViewById(R.id.review_author);
        TextView contentView = (TextView) view.findViewById(R.id.review_content);

        authorView.setText(reviewAuthor);
        contentView.setText(reviewContent);
    }
}
