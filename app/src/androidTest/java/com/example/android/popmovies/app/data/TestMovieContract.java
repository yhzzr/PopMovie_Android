package com.example.android.popmovies.app.data;

import android.net.Uri;
import android.test.AndroidTestCase;

/**
 * Created by Heng on 8/1/15.
 */
public class TestMovieContract extends AndroidTestCase {

    private static final String TEST_MOVIE_TITLE = "/Jurassic Park";


    public void testBuildMovieTitle(){
        Uri movieUri = MovieContract.MovieEntry.buildMovieTitle(TEST_MOVIE_TITLE);
        assertNotNull("Error: Null Uri returned.  You must fill-in buildMovieLocation in " +
                        "MovieContract.",
                movieUri);
        assertEquals("Error: Movie location not properly appended to the end of the Uri",
                TEST_MOVIE_TITLE, movieUri.getLastPathSegment());
        assertEquals("Error: Movie location Uri doesn't match our expected result",
                movieUri.toString(),
                "content://com.example.android.popmovies.app/movie/%2FJurassic%20Park");

    }
}
