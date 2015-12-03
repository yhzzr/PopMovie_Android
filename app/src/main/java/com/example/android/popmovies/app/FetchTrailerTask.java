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
public class FetchTrailerTask extends AsyncTask<String, Void, Void> {

    private final String LOG_TAG = FetchTrailerTask.class.getSimpleName();

    private final Context mContext;

    public FetchTrailerTask(Context context) { mContext = context; }

    private boolean DEBUG = true;

    private void getTrailerDataFromJson(String trailerJsonStr)
            throws JSONException {

        final String OWM_ID = "id";
        final String OWM_YOUTUBE = "youtube";
        final String OWM_TRAILER_NAME = "name";
        final String OWM_TRAILER_SOURCE = "source";

        try {
            JSONObject trailerJson = new JSONObject(trailerJsonStr);
            String movieId = trailerJson.getString(OWM_ID);
            JSONArray trailerArray = trailerJson.getJSONArray(OWM_YOUTUBE);

            Vector<ContentValues> cVVector = new Vector<ContentValues>(trailerArray.length());

            for(int i=0; i< trailerArray.length(); i++) {
                JSONObject trailer = trailerArray.getJSONObject(i);
                String trailerName = trailer.getString(OWM_TRAILER_NAME);
                String trailerSource = trailer.getString(OWM_TRAILER_SOURCE);
                String trailerMovieKey = movieId.toString();

                ContentValues trailerValues = new ContentValues();

                trailerValues.put(MovieContract.TrailerEntry.COLUMN_MOVIE_KEY, trailerMovieKey);
                trailerValues.put(MovieContract.TrailerEntry.COLUMN_TRAILER_NAME, trailerName);
                trailerValues.put(MovieContract.TrailerEntry.COLUMN_TRAILER_SOURCE, trailerSource);

                cVVector.add(trailerValues);
            }

            int inserted = 0;

            if(cVVector.size() > 0 ) {
                ContentValues[] cvArray = new ContentValues[cVVector.size()];
                cVVector.toArray(cvArray);
                inserted = mContext.getContentResolver().bulkInsert(MovieContract.TrailerEntry.CONTENT_URI, cvArray);
            }

            Log.d(LOG_TAG, "FetchTrailerTask Complete. " + inserted + " Inserted");

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
        String trailerQuery = params[0];

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        String trailerJsonStr = null;

        try {
            //Construct the URL for the ReviewDatabase
            final String TRAILER_BASE_URL =
                    "http://api.themoviedb.org/3/movie/"+ trailerQuery + "/trailers?api_key=3c1f60c814efc3e746b909305e955849";
            URL url = new URL(TRAILER_BASE_URL);

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
            trailerJsonStr = buffer.toString();
            getTrailerDataFromJson(trailerJsonStr);
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
