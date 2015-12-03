package com.example.android.popmovies.app;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;

import com.example.android.popmovies.app.data.MovieContract;
import com.example.android.popmovies.app.data.MovieDbHelper;
import com.squareup.picasso.Picasso;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by hengyang on 07/09/15.
 */
public class MovieFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String LOG_TAG = DetailFragment.class.getSimpleName();
    private static final int MOVIE_LOADER = 0;

    private static final String[] MOVIE_COLUMNS = {
            MovieContract.MovieEntry.TABLE_NAME + "." + MovieContract.MovieEntry._ID,
            MovieContract.MovieEntry.COLUMN_TITLE,
            MovieContract.MovieEntry.COLUMN_RELEASE_DATE,
            MovieContract.MovieEntry.COLUMN_POSTER,
            MovieContract.MovieEntry.COLUMN_RATING,
            MovieContract.MovieEntry.COLUMN_PLOT,
            MovieContract.MovieEntry.COLUMN_MOVIE_ID
    };

    static final int COL_MOVIE_ID = 0;
    static final int COL_MOVIE_TITLE = 1;
    static final int COL_MOVIE_RELEASE_DATE = 2;
    static final int COL_MOVIE_POSTER = 3;
    static final int COL_MOVIE_RATING = 4;
    static final int COL_MOVIE_PLOT = 5;
    static final int COL_MOVIE_MOVIE_ID = 6;

    private MovieAdapter mMovieAdapter;

    public interface Callback {

        public void onItemSelected(Uri movieUri);
    }

    public MovieFragment(){
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        inflater.inflate(R.menu.moviefragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();

        if(id == R.id.popular){
            updateMovie();
            return true;
        }
        if(id == R.id.reviewed){
            MovieDbHelper dbHelper = new MovieDbHelper(getActivity());
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            Cursor cursor = db.query(MovieContract.MovieEntry.TABLE_NAME,
                    MOVIE_COLUMNS,
                    null,
                    null,
                    null,
                    null,
                    "rating DESC"
                    );
            mMovieAdapter.swapCursor(cursor);
            db.close();
        }
        if(id == R.id.userfavorites){
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
            Map<String,?> keys = prefs.getAll();
            Set<String> movieSet = new HashSet<String>();

            String selection = "movie_id=";

            for (Map.Entry<String, ?> entry : keys.entrySet()) {
                Log.d(LOG_TAG, entry.getValue().toString());
                String mid = entry.getValue().toString();
                movieSet.add(mid);
                selection = selection + mid + " OR movie_id=";
            }
            if(selection.length()>13) {
                selection = selection.substring(0, selection.length() - 13);

                MovieDbHelper dbHelper = new MovieDbHelper(getActivity());
                SQLiteDatabase db = dbHelper.getReadableDatabase();
                Cursor cursor = db.query(MovieContract.MovieEntry.TABLE_NAME,
                        MOVIE_COLUMNS,
                        selection,
                        null,
                        null,
                        null,
                        null);
                mMovieAdapter.swapCursor(cursor);
                db.close();
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                               Bundle savedInstanceState){
        mMovieAdapter = new MovieAdapter(getActivity(), null, 0);

        View rootView = inflater.inflate(R.layout.overview, container,false);

        ImageView imageView = (ImageView) rootView.findViewById(R.id.imageView);
        Picasso.with(getActivity()).load("http://i.imgur.com/DvpvklR.png").resize(1,1).centerCrop().into(imageView);

        //set gridview that holds the movie_info layout
        GridView gridView = (GridView) rootView.findViewById(R.id.grid);
        gridView.setAdapter(mMovieAdapter);

        updateMovie();
        MovieDbHelper dbHelper = new MovieDbHelper(getActivity());
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(MovieContract.MovieEntry.TABLE_NAME,
                MOVIE_COLUMNS,
                null,
                null,
                null,
                null,
                null
        );
        mMovieAdapter.swapCursor(cursor);

        //set the detail view here
        gridView.setOnItemClickListener(new GridView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                Cursor cursor = (Cursor) adapterView.getItemAtPosition(position);
                if (cursor != null) {
                    ((Callback) getActivity())
                            .onItemSelected(MovieContract.MovieEntry.buildMovieId(cursor.getString(COL_MOVIE_MOVIE_ID)));
                }
            }
        });

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(MOVIE_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }


    private void updateMovie() {
        FetchMovieTask movieTask = new FetchMovieTask(getActivity());
        movieTask.execute();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle bundle) {

        return new CursorLoader(getActivity(),
                MovieContract.MovieEntry.CONTENT_URI,
                MOVIE_COLUMNS,
                null,
                null,
                null
        );

    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
                mMovieAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        mMovieAdapter.swapCursor(null);
    }
}
