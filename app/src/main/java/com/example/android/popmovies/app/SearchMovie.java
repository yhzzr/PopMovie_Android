package com.example.android.popmovies.app;

import android.content.Intent;
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
import android.widget.AdapterView;
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

        Movie[] data = {
                new Movie("/uXZYawqUsChGSj54wcuBtEdUJbh.jpg","Jurassic World","2015-06-12","Twenty-two years after the events of Jurassic Park, Isla Nublar now features a fully functioning dinosaur theme park, Jurassic World, as originally envisioned by John Hammond.", 7.0),
                new Movie("/5JU9ytZJyR3zmClGmVm9q4Geqbd.jpg","Terminator Genisys","2015-07-01","The year is 2029. John Connor, leader of the resistance continues the war against the machines. At the Los Angeles offensive, John's fears of the unknown future begin to emerge when TECOM spies reveal a new plot by SkyNet that will attack him from both fronts; past and future, and will ultimately change warfare forever.",6.3),
                new Movie("/s5uMY8ooGRZOL0oe4sIvnlTsYQO.jpg","Minions","2015-07-10","Minions Stuart, Kevin and Bob are recruited by Scarlet Overkill, a super-villain who, alongside her inventor husband Herb, hatches a plot to take over the world.",7.4),
                new Movie("/kqjL17yufvn9OVLyXYpvtyrFfak.jpg","Mad Max: Fury Road","2015-05-15","An apocalyptic story set in the furthest reaches of our planet, in a stark desert landscape where humanity is broken, and most everyone is crazed fighting for the necessities of life. Within this world exist two rebels on the run who just might be able to restore order. There's Max, a man of action and a man of few words, who seeks peace of mind following the loss of his wife and child in the aftermath of the chaos. And Furiosa, a woman of action and a woman who believes her path to survival may be achieved if she can make it across the desert back to her childhood homeland.",7.8),
        };

        List<Movie>  movieInfo= new ArrayList<>(Arrays.asList(data));

        mMovieAdapter = new CustomArrayAdapter(getActivity(),
                                                 R.layout.movie_info,
                                                 movieInfo);
        View rootView = inflater.inflate(R.layout.overview,container,false);

        ImageView imageView = (ImageView) rootView.findViewById(R.id.imageView);
        Picasso.with(getActivity()).load("http://i.imgur.com/DvpvklR.png").resize(1,1).centerCrop().into(imageView);

        GridView gridView = (GridView) rootView.findViewById(R.id.grid);
        gridView.setAdapter(mMovieAdapter);
        gridView.setOnItemClickListener(new GridView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l){
                Movie movie = mMovieAdapter.getItem(position);
                Bundle extras = new Bundle();
                extras.putString("POSTER",movie.getPosterpath());
                extras.putString("TITLE", movie.getTitle());
                extras.putString("RELEASE", movie.getRelease_date());
                extras.putString("PLOT", movie.getPlot());
                extras.putDouble("RATING", movie.getRating());
                Intent showDetail = new Intent(getActivity(), DetailActivity.class).putExtras(extras);
                startActivity(showDetail);
            }
        });
        return rootView;
    }

    public class FetchPopularTask extends AsyncTask<Void,Void,Movie[]>{

        private final String LOG_TAG = FetchPopularTask.class.getSimpleName();

        private Movie[] getPosterPathFromJson(String resultJsonStr)
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

            Movie[] movies = new Movie[movieArray.length()];
            for(int i = 0; i < movieArray.length(); i++){
                JSONObject movie = movieArray.getJSONObject(i);
                movies[i] = new Movie(movie.getString(OWM_POSTER_PATH),
                        movie.getString(OWM_TITLE),
                        movie.getString(OWM_RELEASE_DATE),
                        movie.getString(OWM_OVERVIEW),
                        movie.getDouble(OWM_VOTE_AVERAGE));
            }
            return movies;
        }

        @Override
        protected Movie[] doInBackground(Void... params){

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
        protected void onPostExecute(Movie[] result){
            if(result != null){
                mMovieAdapter.clear();
                for(Movie movie:result){
                    mMovieAdapter.add(movie);
                }
            }
        }

    }

    public class FetchRatedTask extends AsyncTask<Void,Void,Movie[]>{

        private final String LOG_TAG = FetchRatedTask.class.getSimpleName();

        private Movie[] getPosterPathFromJson(String resultJsonStr)
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

            Movie[] movies = new Movie[movieArray.length()];

            //get data from JSONArray
            for(int i = 0; i < movieArray.length(); i++){
                    JSONObject movie = movieArray.getJSONObject(i);
                    movies[i] = new Movie(movie.getString(OWM_POSTER_PATH),
                                          movie.getString(OWM_TITLE),
                                          movie.getString(OWM_RELEASE_DATE),
                                          movie.getString(OWM_OVERVIEW),
                                          movie.getDouble(OWM_VOTE_AVERAGE));
            }

            //sorting the array
            InsertionSort.sort(movies);

            return movies;
        }

        @Override
        protected Movie[] doInBackground(Void... params){

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
        protected void onPostExecute(Movie[] result){
            if(result != null){
                mMovieAdapter.clear();
                for(Movie movie:result){
                    mMovieAdapter.add(movie);
                }
            }
        }

    }
}
