package com.example.android.popmovies.app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.android.popmovies.app.data.MovieContract;
import com.squareup.picasso.Picasso;

/**
 * Created by hengyang on 08/12/15.
 */
public class DetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String LOG_TAG = DetailFragment.class.getSimpleName();
    static final String DETAIL_URI = "URI";

    private static final String MOVIE_SHARE_HASHTAG = " #Popmovies";

    private Uri mUri;
    private TrailerAdapter mTrailerAdapter;
    private ReviewAdapter mReviewAdapter;

    private static final int DETAIL_LOADER = 0;
    private static final int TRAILER_LOADER = 1;
    private static final int REVIEW_LOADER = 2;

    private static final String[] DETAIL_COLUMNS = {
            MovieContract.MovieEntry.TABLE_NAME + "." + MovieContract.MovieEntry._ID,
            MovieContract.MovieEntry.COLUMN_TITLE,
            MovieContract.MovieEntry.COLUMN_RELEASE_DATE,
            MovieContract.MovieEntry.COLUMN_POSTER,
            MovieContract.MovieEntry.COLUMN_RATING,
            MovieContract.MovieEntry.COLUMN_PLOT,
            MovieContract.MovieEntry.COLUMN_MOVIE_ID
    };

    public static final int COL_MOVIE_ID = 0;
    public static final int COL_MOVIE_TITLE = 1;
    public static final int COL_MOVIE_RELEASE_DATE = 2;
    public static final int COL_MOVIE_POSTER = 3;
    public static final int COL_MOVIE_RATING = 4;
    public static final int COL_MOVIE_PLOT = 5;
    public static final int COL_MOVIE_MOVIE_ID = 6;

    private static final String[] TRAILER_COLUMNS = {
            MovieContract.TrailerEntry.TABLE_NAME + "." + MovieContract.TrailerEntry._ID,
            MovieContract.TrailerEntry.COLUMN_TRAILER_NAME,
            MovieContract.TrailerEntry.COLUMN_TRAILER_SOURCE,
            MovieContract.TrailerEntry.COLUMN_MOVIE_KEY
    };

    static final int COL_TRAILER_ID = 0;
    static final int COL_TRAILER_NAME = 1;
    static final int COL_TRAILER_SOURCE = 2;
    static final int COL_TRAILER_MOVIE_KEY = 3;

    private static final String[] REVIEW_COLUMNS = {
            MovieContract.ReviewEntry.TABLE_NAME + "." + MovieContract.ReviewEntry._ID,
            MovieContract.ReviewEntry.COLUMN_REVIEW_ID,
            MovieContract.ReviewEntry.COLUMN_MOVIE_KEY,
            MovieContract.ReviewEntry.COLUMN_REVIEW_CONTENT,
            MovieContract.ReviewEntry.COLUMN_REVIEW_URL,
            MovieContract.ReviewEntry.COLUMN_REVIEW_AUTHOR
    };

    static final int COL_REVIEW_ID = 0;
    static final int COL_REVIEW_REVIEW_ID = 1;
    static final int COL_REVIEW_MOVIE_KEY = 2;
    static final int COL_REVIEW_CONTENT = 3;
    static final int COL_REVIEW_URL = 4;
    static final int COL_REVIEW_AUTHOR = 5;

    private ImageView mPosterView;
    private TextView mTitleView;
    private TextView  mReleaseDateView;
    private TextView  mRatingView;
    private TextView  mPlotView;
    private Button mButton;

    public DetailFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mTrailerAdapter = new TrailerAdapter(getActivity(), null, 0);
        mReviewAdapter = new ReviewAdapter(getActivity(), null, 0);

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
        mButton = (Button) rootView.findViewById(R.id.favorites);

        mButton.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View V){
                String movieId = MovieContract.MovieEntry.getMovieIdFromUri(mUri);
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString("movie"+movieId, movieId);
                editor.commit();
            }
        });

        ListView trailerView = (ListView) rootView.findViewById(R.id.trailerview);
        trailerView.setAdapter(mTrailerAdapter);

        trailerView.setOnItemClickListener(new GridView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Cursor cursor = (Cursor) adapterView.getItemAtPosition(position);
                if (cursor != null) {
                    String source = "https://www.youtube.com/watch?v="+cursor.getString(COL_TRAILER_SOURCE);
                    Uri uri = Uri.parse(source);
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(uri);
                    startActivity(intent);
                }
            }
        });


        ListView reviewView = (ListView) rootView.findViewById(R.id.reviewview);
        reviewView.setAdapter(mReviewAdapter);

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(DETAIL_LOADER, null, this);
        getLoaderManager().initLoader(TRAILER_LOADER, null, this);
        getLoaderManager().initLoader(REVIEW_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

       if(null != mUri) {
           switch (id) {

               case DETAIL_LOADER: {
                   if (null != mUri) {
                       return new CursorLoader(
                               getActivity(),
                               mUri,
                               DETAIL_COLUMNS,
                               null,
                               null,
                               null
                       );
                   }
                   break;
               }
               case TRAILER_LOADER: {
                   if (null != mUri) {
                       String movieId = MovieContract.MovieEntry.getMovieIdFromUri(mUri);
                       Uri mTrailerUri = MovieContract.TrailerEntry.buildTrailerMovie(movieId);

                       return new CursorLoader(
                               getActivity(),
                               mTrailerUri,
                               TRAILER_COLUMNS,
                               null,
                               null,
                               null
                       );
                   }
                   break;
               }
               case REVIEW_LOADER: {
                   if (null != mUri) {
                       String movieId = MovieContract.MovieEntry.getMovieIdFromUri(mUri);
                       Uri mReviewUri = MovieContract.ReviewEntry.buildReviewMovie(movieId);

                       return new CursorLoader(
                               getActivity(),
                               mReviewUri,
                               REVIEW_COLUMNS,
                               null,
                               null,
                               null
                       );
                   }
                   break;
               }
           }
       }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        switch (loader.getId()) {
            //set Details of movie
            case DETAIL_LOADER: {

                if (data != null && data.moveToFirst()) {
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
                break;
            }
            case TRAILER_LOADER: {
                if(data != null && data.moveToFirst()) {
                    mTrailerAdapter.swapCursor(data);
                }
                break;
            }
            case REVIEW_LOADER: {
                if(data != null && data.moveToFirst()) {
                    mReviewAdapter.swapCursor(data);
                }
                break;
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {}

    @Override
    public void onStart() {
        super.onStart();
        if(mUri != null) {
            updateTrailerData();
            updateReviewData();
        }
    }

    private void updateTrailerData() {
        String movieId = MovieContract.MovieEntry.getMovieIdFromUri(mUri);
        FetchTrailerTask trailerTask = new FetchTrailerTask(getActivity());
        trailerTask.execute(movieId);
    }

    private void updateReviewData() {
        String movieId = MovieContract.MovieEntry.getMovieIdFromUri(mUri);
        FetchReviewTask reviewTask = new FetchReviewTask(getActivity());
        reviewTask.execute(movieId);
    }
}
