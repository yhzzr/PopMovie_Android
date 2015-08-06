package com.example.android.popmovies.app.data;

import android.content.UriMatcher;
import android.net.Uri;
import android.test.AndroidTestCase;

/**
 * Created by hengyang on 08/04/15.
 */
public class TestUriMatcher extends AndroidTestCase{

    private static final String MOVIE_QUERY  = "Minions";

    private static final Uri TEST_REVIEW_DIR = MovieContract.ReviewEntry.CONTENT_URI;
    private static final Uri TEST_TRAILER_DIR = MovieContract.TrailerEntry.CONTENT_URI;
    private static final Uri TEST_REVIEW_WITH_NAME_DIR = MovieContract.ReviewEntry.buildReviewMovie(MOVIE_QUERY);
    private static final Uri TEST_TRAILER_WITH_NAME_DIR = MovieContract.TrailerEntry.buildTrailerMovie(MOVIE_QUERY);
    private static final Uri TEST_MOVIE_DIR = MovieContract.MovieEntry.CONTENT_URI;

    public void testUriMatcher() {
        UriMatcher testMatcher = MovieProvider.buildUriMatcher();

        assertEquals("Error: The Review Uri was matched incorrectly.",
                testMatcher.match(TEST_REVIEW_DIR), MovieProvider.REVIEW);
        assertEquals("Error: The Trailer Uri was matched incorrectly.",
                testMatcher.match(TEST_TRAILER_DIR), MovieProvider.TRAILER);
        assertEquals("Error: The REVIEW WITH NAME URI was matched incorrectly.",
                testMatcher.match(TEST_REVIEW_WITH_NAME_DIR), MovieProvider.REVIEW_WITH_NAME);
        assertEquals("Error: The TRAILER WITH NAME URI was matched incorrectly.",
                testMatcher.match(TEST_TRAILER_WITH_NAME_DIR), MovieProvider.TRAILER_WITH_NAME);
        assertEquals("Error: The MOVIE URI was matched incorrectly.",
                testMatcher.match(TEST_MOVIE_DIR), MovieProvider.MOVIE);
    }
}
