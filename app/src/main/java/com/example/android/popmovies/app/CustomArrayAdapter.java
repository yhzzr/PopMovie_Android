package com.example.android.popmovies.app;

import android.app.Activity;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by hengyang on 07/10/15.
 */
public class CustomArrayAdapter extends ArrayAdapter<Movie> {

    final String BASE_URL = "http://image.tmdb.org/t/p/";
    final String SIZE = "w500";

    private Activity context;
    private List<Movie> poster_paths;

    public CustomArrayAdapter(Activity context, int resource, List<Movie> poster_paths){
        super(context, resource, poster_paths);
        this.context = context;
        this.poster_paths = poster_paths;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){

        Movie PATH = getItem(position);
        String poster_path = BASE_URL+SIZE+PATH.getPosterpath();
        Uri myUri = Uri.parse(poster_path);


        View view = LayoutInflater.from(getContext()).inflate(R.layout.movie_info, parent, false);

        ImageView moviePoster = (ImageView) view.findViewById(R.id.movie_poster);
        Picasso.with(context).load(myUri).into(moviePoster);

        return view;
    }
}
