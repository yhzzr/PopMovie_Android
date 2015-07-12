package com.example.android.popmovies.app;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by hengyang on 07/09/15.
 */
public class SearchMovie extends Fragment{

    private CustomArrayAdapter mMovieAdapter;

    public SearchMovie(){
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
            FetchPopularTask movieTask = new FetchPopularTask();
            movieTask.execute();
            return true;
        }

        if(id == R.id.reviewed){
            FetchRatedTask movieTask = new FetchRatedTask();
            movieTask.execute();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                               Bundle savedInstanceState){

        String[] data = {
                "/5JU9ytZJyR3zmClGmVm9q4Geqbd.jpg",
                "/uXZYawqUsChGSj54wcuBtEdUJbh.jpg",
                "/kqjL17yufvn9OVLyXYpvtyrFfak.jpg",
                "/s5uMY8ooGRZOL0oe4sIvnlTsYQO.jpg",
                "/qey0tdcOp9kCDdEZuJ87yE3crSe.jpg",
                "/A7HtCxFe7Ms8H7e7o2zawppbuDT.jpg",
                "/aMEsvTUklw0uZ3gk3Q6lAj6302a.jpg",
        };

        List<String>  movieInfo= new ArrayList<>(Arrays.asList(data));

        mMovieAdapter = new CustomArrayAdapter(getActivity(),
                                                 R.layout.movie_info,
                                                 movieInfo);
        View rootView = inflater.inflate(R.layout.overview,container,false);

        ImageView imageView = (ImageView) rootView.findViewById(R.id.imageView);
        Picasso.with(getActivity()).load("http://i.imgur.com/DvpvklR.png").resize(1,1).centerCrop().into(imageView);

        GridView gridView = (GridView) rootView.findViewById(R.id.grid);
        gridView.setAdapter(mMovieAdapter);

        return rootView;
    }

    public class FetchPopularTask extends AsyncTask<Void,Void,String[]>{

        private final String LOG_TAG = FetchPopularTask.class.getSimpleName();

        private String[] getPosterPathFromJson(String resultJsonStr)
                throws JSONException{

            final String OWM_RESULTS = "results";
            final String OWM_ID = "id";
            final String OWM_TITLE = "title";
            final String OWM_POSTER_PATH = "poster_path";
            final String OWM_VOTE_AVERAGE = "vote_average";
            final String OWM_OVERVIEW = "overview";
            final String OWM_RELEASE_DATE = "release_date";

            JSONObject resultJson = new JSONObject(resultJsonStr);
            JSONArray movieArray = resultJson.getJSONArray(OWM_RESULTS);

            String[] resultStrs = new String[movieArray.length()];
            for(int i = 0; i < movieArray.length(); i++){
                String poster;

                JSONObject movie = movieArray.getJSONObject(i);
                poster = movie.getString(OWM_POSTER_PATH);
                resultStrs[i] = poster;
            }

            for(String s:resultStrs){
                Log.v(LOG_TAG, "Movie entry: " + s);
            }

            return resultStrs;
        }

        @Override
        protected String[] doInBackground(Void... params){

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            String resultJsonStr = null;

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
                resultJsonStr = buffer.toString();

            }catch (IOException e){
                Log.e(LOG_TAG, "Error ", e);
                return null;
            }finally{
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

            try{
                return getPosterPathFromJson(resultJsonStr);
            }catch (JSONException e){
                Log.e(LOG_TAG, e.getMessage(),e);
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String[] result){
            if(result != null){
                mMovieAdapter.clear();
                for(String movieStr:result){
                    mMovieAdapter.add(movieStr);
                }
            }
        }

    }

    public class FetchRatedTask extends AsyncTask<Void,Void,String[]>{

        private final String LOG_TAG = FetchRatedTask.class.getSimpleName();

        private String[] getPosterPathFromJson(String resultJsonStr)
                throws JSONException{

            final String OWM_RESULTS = "results";
            final String OWM_ID = "id";
            final String OWM_TITLE = "title";
            final String OWM_POSTER_PATH = "poster_path";
            final String OWM_VOTE_AVERAGE = "vote_average";
            final String OWM_OVERVIEW = "overview";
            final String OWM_RELEASE_DATE = "release_date";

            JSONObject resultJson = new JSONObject(resultJsonStr);
            JSONArray movieArray = resultJson.getJSONArray(OWM_RESULTS);

            String[] resultStrs = new String[movieArray.length()];
            Movie[] movies = new Movie[movieArray.length()];

            //get data from JSONArray
            for(int i = 0; i < movieArray.length(); i++){
                    JSONObject movie = movieArray.getJSONObject(i);
                    movies[i] = new Movie(movie.getString(OWM_POSTER_PATH), movie.getDouble(OWM_VOTE_AVERAGE));
            }

            //sorting the array
            InsertionSort.sort(movies);

            for(int i=0; i<movieArray.length(); i++){
                resultStrs[i] = movies[i].getPosterpath();
            }

            for(String s:resultStrs){
                Log.v(LOG_TAG, "Movie entry: " + s);
            }

            return resultStrs;
        }

        @Override
        protected String[] doInBackground(Void... params){

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            String resultJsonStr = null;

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
                resultJsonStr = buffer.toString();

            }catch (IOException e){
                Log.e(LOG_TAG, "Error ", e);
                return null;
            }finally{
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

            try{
                return getPosterPathFromJson(resultJsonStr);
            }catch (JSONException e){
                Log.e(LOG_TAG, e.getMessage(),e);
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String[] result){
            if(result != null){
                mMovieAdapter.clear();
                for(String movieStr:result){
                    mMovieAdapter.add(movieStr);
                }
            }
        }

    }
}
