package com.example.android.popmovies.app;

import android.content.ContentValues;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.example.android.popmovies.app.data.MovieContract;

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
 * Created by hengyang on 08/11/15.
 */
public class FetchReviewTask extends AsyncTask<String, Void, Void> {

    private final String LOG_TAG = FetchReviewTask.class.getSimpleName();

    private final Context mContext;

    public FetchReviewTask(Context context) { mContext = context; }

    private boolean DEBUT = true;

    private void getReviewDataFromJson(String reviewJsonStr)
        throws JSONException {

        final String OWM_MOVIE_KEY = "id";
        final String OWM_RESULTS = "results";
        final String OWM_ID = "id";
        final String OWM_AUTHOR = "author";
        final String OWM_CONTENT = "content";
        final String OWM_URL = "url";

        try {
            JSONObject reviewJson = new JSONObject(reviewJsonStr);
            String movieId = reviewJson.getString(OWM_MOVIE_KEY);
            JSONArray reviewArray = reviewJson.getJSONArray(OWM_RESULTS);

            Vector<ContentValues> cVVector = new Vector<ContentValues>(reviewArray.length());

            for(int i=0; i < reviewArray.length(); i++) {
                JSONObject review = reviewArray.getJSONObject(i);
                String reviewId = review.getString(OWM_ID);
                String reviewAuthor = review.getString(OWM_AUTHOR);
                String reviewContent = review.getString(OWM_CONTENT);
                String reviewUrl = review.getString(OWM_URL);
                String reviewMovieKey = movieId.toString();

                ContentValues reviewValues = new ContentValues();

                reviewValues.put(MovieContract.ReviewEntry.COLUMN_REVIEW_ID, reviewId);
                reviewValues.put(MovieContract.ReviewEntry.COLUMN_REVIEW_AUTHOR, reviewAuthor);
                reviewValues.put(MovieContract.ReviewEntry.COLUMN_REVIEW_CONTENT, reviewContent);
                reviewValues.put(MovieContract.ReviewEntry.COLUMN_REVIEW_URL, reviewUrl);
                reviewValues.put(MovieContract.ReviewEntry.COLUMN_MOVIE_KEY, reviewMovieKey);

                cVVector.add(reviewValues);
            }

            int inserted = 0;

            if(cVVector.size() > 0 ) {
                ContentValues[] cvArray = new ContentValues[cVVector.size()];
                cVVector.toArray(cvArray);
                inserted = mContext.getContentResolver().bulkInsert(MovieContract.ReviewEntry.CONTENT_URI, cvArray);
            }

            Log.d(LOG_TAG, "FetchReviewTask Complete. " + inserted + " Inserted");

        }catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }
    }

    @Override
    protected Void doInBackground(String... params){

        if(params.length == 0) {
            return null;
        }
        String reviewQuery = params[0];

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        String reviewJsonStr = null;

        try {
            //Construct the URL for the ReviewDatabase
            final String REVIEW_BASE_URL =
                    "http://api.themoviedb.org/3/movie/"+ reviewQuery + "/reviews?api_key=3c1f60c814efc3e746b909305e955849";
            URL url = new URL(REVIEW_BASE_URL);

            //Create the request to Movie Database, and open the connection
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if(inputStream == null){
                return null;
            }
            reader = new BufferedReader((new InputStreamReader(inputStream)));

            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line + "\n");
            }

            if(buffer.length() == 0) {
                return null;
            }
            reviewJsonStr = buffer.toString();
            getReviewDataFromJson(reviewJsonStr);
        }catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);
        }catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
        }
        finally{
            if(urlConnection != null){
                urlConnection.disconnect();
            }
            if(reader != null) {
                try{
                    reader.close();
                }catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
        }

        return null;
    }
}
