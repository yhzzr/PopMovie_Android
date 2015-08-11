package com.example.android.popmovies.app;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.ShareActionProvider;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.popmovies.app.data.MovieContract.MovieEntry;
import com.squareup.picasso.Picasso;

public class DetailActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        if (savedInstanceState == null) {
            Bundle arguments = new Bundle();
            arguments.putParcelable(DetailFragment.DETAIL_URI, getIntent().getData());

            DetailFragment fragment = new DetailFragment();
            fragment.setArguments(arguments);

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container1, fragment)
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
    public static class DetailFragment extends Fragment implements LoaderCallbacks<Cursor> {

        private static final String LOG_TAG = DetailFragment.class.getSimpleName();
        static final String DETAIL_URI = "URI";

        private static final String MOVIE_SHARE_HASHTAG = " #Popmovies";

        private ShareActionProvider mShareActionProvider;
        private String mMovie;
        private Uri mUri;

        private static final int DETAIL_LOADER = 0;

        private static final String[] DETAIL_COLUMNS = {
                MovieEntry.TABLE_NAME + "." + MovieEntry._ID,
                MovieEntry.COLUMN_TITLE,
                MovieEntry.COLUMN_RELEASE_DATE,
                MovieEntry.COLUMN_POSTER,
                MovieEntry.COLUMN_RATING,
                MovieEntry.COLUMN_PLOT,
                MovieEntry.COLUMN_MOVIE_ID
        };

        public static final int COL_MOVIE_ID = 0;
        public static final int COL_MOVIE_TITLE = 1;
        public static final int COL_MOVIE_RELEASE_DATE = 2;
        public static final int COL_MOVIE_POSTER = 3;
        public static final int COL_MOVIE_RATING = 4;
        public static final int COL_MOVIE_PLOT = 5;
        public static final int COL_MOVIE_MOVIE_ID = 6;

        private ImageView mPosterView;
        private TextView  mTitleView;
        private TextView  mReleaseDateView;
        private TextView  mRatingView;
        private TextView  mPlotView;

        public DetailFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            Bundle arguments = getArguments();
            if(arguments != null) {
                mUri = arguments.getParcelable(DetailFragment.DETAIL_URI);
            }

            View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
            mPosterView = (ImageView) rootView.findViewById(R.id.poster);
            mTitleView = (TextView) rootView.findViewById(R.id.title);
            mReleaseDateView = (TextView) rootView.findViewById(R.id.releasedate);
            mRatingView = (TextView) rootView.findViewById(R.id.rating);
            mPlotView = (TextView) rootView.findViewById(R.id.plot);

            return rootView;
        }

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            getLoaderManager().initLoader(DETAIL_LOADER, null, this);
            super.onActivityCreated(savedInstanceState);
        }

        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            if(null != mUri ){
                return new CursorLoader(
                        getActivity(),
                        mUri,
                        DETAIL_COLUMNS,
                        null,
                        null,
                        null
                );
            }
            return null;
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            if(data != null && data.moveToFirst()) {

                //set poster image
                final String BASE_URL = "http://image.tmdb.org/t/p/";
                final String SIZE = "w500";
                String poster = data.getString(COL_MOVIE_POSTER);
                String posterPath = BASE_URL + SIZE + poster;
                Uri myUri = Uri.parse(posterPath);
                Picasso.with(getActivity()).load(myUri).into(mPosterView);

                //set title
                String title = data.getString(COL_MOVIE_TITLE);
                mTitleView.setText(title);
                mReleaseDateView.setText(data.getString(COL_MOVIE_RELEASE_DATE));
                mRatingView.setText(data.getString(COL_MOVIE_RATING));
                mPlotView.setText(data.getString(COL_MOVIE_PLOT));
            }
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {}
    }
}