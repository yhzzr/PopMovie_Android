package com.example.android.popmovies.app;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.example.android.popmovies.app.data.MovieContract;
import com.example.android.popmovies.app.data.MovieContract.MovieEntry;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Vector;

/**
 * Created by Heng on 8/7/15.
 */


public class FetchMovieTask extends AsyncTask<Void, Void, Void> {

    private final String LOG_TAG = FetchMovieTask.class.getSimpleName();

    private final Context mContext;

    public FetchMovieTask(Context context) { mContext = context; }

    private boolean DEBUG = true;

    long addMovie(String movieTitle, String releaseDate, String posterPath, String movieRating, String moviePlot, String movieId){
        long movieRowId;

        Cursor movieCursor = mContext.getContentResolver().query(
                MovieContract.MovieEntry.CONTENT_URI,
                new String[] {MovieContract.MovieEntry._ID},
                MovieContract.MovieEntry.COLUMN_MOVIE_ID + " = ?",
                new String[]{movieId},
                null
        );

        if(movieCursor.moveToFirst()){
            int movieRowIdIndex = movieCursor.getColumnIndex(MovieContract.MovieEntry._ID);
            movieRowId = movieCursor.getLong(movieRowIdIndex);
        }else {
            ContentValues movieValues = new ContentValues();

            movieValues.put(MovieContract.MovieEntry.COLUMN_TITLE, movieTitle);
            movieValues.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE, releaseDate);
            movieValues.put(MovieContract.MovieEntry.COLUMN_POSTER, posterPath);
            movieValues.put(MovieContract.MovieEntry.COLUMN_RATING, movieRating);
            movieValues.put(MovieContract.MovieEntry.COLUMN_PLOT, moviePlot);
            movieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID, movieId);

            Uri insertedUri = mContext.getContentResolver().insert(
                    MovieContract.MovieEntry.CONTENT_URI,
                    movieValues
            );

            movieRowId = ContentUris.parseId(insertedUri);
        }

        movieCursor.close();
        return movieRowId;
    }

    private void getMovieDataFromJson(String movieJsonStr)
            throws JSONException {

        final String OWM_RESULTS = "results";
        final String OWM_ID = "id";
        final String OWM_TITLE = "title";
        final String OWM_POSTER_PATH = "poster_path";
        final String OWM_VOTE_AVERAGE = "vote_average";
        final String OWM_OVERVIEW = "overview";
        final String OWM_RELEASE_DATE = "release_date";

        try {
            JSONObject movieJson = new JSONObject(movieJsonStr);
            JSONArray movieArray = movieJson.getJSONArray(OWM_RESULTS);

            Vector<ContentValues> cVVector = new Vector<ContentValues>(movieArray.length());

            //set up the parameters, get String from movie object.
            for(int i = 0; i < movieArray.length(); i++) {
                JSONObject movie = movieArray.getJSONObject(i);
                String movieTitle = movie.getString(OWM_TITLE);
                String releaseDate = movie.getString(OWM_RELEASE_DATE);
                String posterPath = movie.getString(OWM_POSTER_PATH);
                String movieRating = movie.getString(OWM_VOTE_AVERAGE);
                String moviePlot = movie.getString(OWM_OVERVIEW);
                String movieId = movie.getString(OWM_ID);

                ContentValues movieValues = new ContentValues();

                movieValues.put(MovieEntry.COLUMN_TITLE, movieTitle);
                movieValues.put(MovieEntry.COLUMN_RELEASE_DATE, releaseDate);
                movieValues.put(MovieEntry.COLUMN_POSTER, posterPath);
                movieValues.put(MovieEntry.COLUMN_RATING, movieRating);
                movieValues.put(MovieEntry.COLUMN_PLOT, moviePlot);
                movieValues.put(MovieEntry.COLUMN_MOVIE_ID, movieId);

                cVVector.add(movieValues);
            }

            int inserted = 0;
            //add to database
            if(cVVector.size() > 0) {
                ContentValues[] cvArray = new ContentValues[cVVector.size()];
                cVVector.toArray(cvArray);
                inserted = mContext.getContentResolver().bulkInsert(MovieEntry.CONTENT_URI, cvArray);
            }

            Log.d(LOG_TAG, "FetchMovieTask Complete. " + inserted + " Inserted");

        }catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }
    }

    @Override
    protected Void doInBackground(Void... params){

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        String movieJsonStr = null;

        try{
            //Construct the URL for the Movie Database API
            URL url = new URL("http://api.themoviedb.org/3/discover/movie?sort_by=popularity.desc&api_key=3c1f60c814efc3e746b909305e955849");

            //Create the request to Movie Database, and open the connection
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            //Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if(inputStream == null){
                return null;
            }
            reader = new BufferedReader((new InputStreamReader(inputStream)));

            String line;
            while((line = reader.readLine()) != null){
                buffer.append(line + "\n");
            }

            if(buffer.length() == 0){
                return null;
            }
            movieJsonStr = buffer.toString();
            getMovieDataFromJson(movieJsonStr);
        }catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);
            return null;
        }catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }
        finally{
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
        }

        return null;
    }
}
