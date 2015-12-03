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
public class TrailerAdapter extends CursorAdapter {

    public TrailerAdapter(Context context, Cursor c, int flags) { super(context, c, flags); }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.trailer_info, parent, false);

        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        String trailerName = cursor.getString(DetailFragment.COL_TRAILER_NAME);

        TextView textView = (TextView) view.findViewById(R.id.movie_trailer);
        textView.setText(trailerName);

    }
}
