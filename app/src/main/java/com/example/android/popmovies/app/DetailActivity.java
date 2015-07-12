package com.example.android.popmovies.app;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class DetailActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container1, new DetailFragment())
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class DetailFragment extends Fragment {

        public DetailFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

            Intent intent = getActivity().getIntent();
            Bundle extras = intent.getExtras();
            if(intent != null) {
                String movieTitle = extras.getString("TITLE");
                String moviePoster = "http://image.tmdb.org/t/p/"+"w500"+extras.getString("POSTER");
                String movieRelease = extras.getString("RELEASE");
                String moviePlot = extras.getString("PLOT");
                double rating = extras.getDouble("RATING");
                String movieRating = rating+"/10";

                ((TextView) rootView.findViewById(R.id.title)).setText(movieTitle);
                ((TextView) rootView.findViewById(R.id.releasedate)).setText(movieRelease);
                ((TextView) rootView.findViewById(R.id.rating)).setText(movieRating);
                ((TextView) rootView.findViewById(R.id.plot)).setText(moviePlot);

                Uri myUri = Uri.parse(moviePoster);
                ImageView imageView = (ImageView) rootView.findViewById(R.id.poster);
                Picasso.with(getActivity()).load(myUri).resize(150,200).centerCrop().into(imageView);

            }
            return rootView;
        }
    }
}