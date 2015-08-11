package com.example.android.popmovies.app;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

/**
 * Created by Heng on 8/8/15.
 */
public class MovieAdapter extends CursorAdapter {

    final String BASE_URL = "http://image.tmdb.org/t/p/";
    final String SIZE = "w500";

    public MovieAdapter(Context context, Cursor c, int flags) { super(context, c, flags); }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {

        View view = LayoutInflater.from(context).inflate(R.layout.movie_info, parent, false);

        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor){

        String poster = cursor.getString(MovieFragment.COL_MOVIE_POSTER);
        String poster_path = BASE_URL+SIZE+poster;
        Uri myUri = Uri.parse(poster_path);

        ImageView moviePoster = (ImageView) view.findViewById(R.id.movie_poster);
        Picasso.with(context).load(myUri).into(moviePoster);

    }
}
