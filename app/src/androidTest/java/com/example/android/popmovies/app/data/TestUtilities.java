package com.example.android.popmovies.app.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.test.AndroidTestCase;

import com.example.android.popmovies.app.utils.PollingCheck;

import java.util.Map;
import java.util.Set;

/**
 * Created by hengyang on 08/05/15.
 */
public class TestUtilities extends AndroidTestCase {

    static final String TEST_MOVIE = "123456";

    static void validateCursor(String error, Cursor valueCursor, ContentValues expectedValues){
        assertTrue("Empty cursor returned. " + error, valueCursor.moveToFirst());
        validateCurrentRecord(error, valueCursor, expectedValues);
        valueCursor.close();
    }

    static void validateCurrentRecord(String error, Cursor valueCursor, ContentValues expectedValues){
        Set<Map.Entry<String, Object>> valueSet = expectedValues.valueSet();
        for(Map.Entry<String, Object> entry : valueSet){
            String columnName = entry.getKey();
            int idx = valueCursor.getColumnIndex(columnName);
            assertFalse("Colum '" + columnName + "' not found. " + error, idx == -1);
            String expectedValue = entry.getValue().toString();
            assertEquals("Value '" + entry.getValue().toString() +
                    "' did not match the expected value '" +
                    expectedValue + "'. " + error, expectedValue, valueCursor.getString(idx));
        }
    }

    static ContentValues createReviewValues(long movieId) {
        ContentValues reviewValues = new ContentValues();
        reviewValues.put(MovieContract.ReviewEntry.COLUMN_REVIEW_ID, "55910381c3a36807f900065d");
        reviewValues.put(MovieContract.ReviewEntry.COLUMN_MOVIE_ID, movieId);
        reviewValues.put(MovieContract.ReviewEntry.COLUMN_REVIEW_AUTHOR, "jonlikesmoviesthatdontsuck");
        reviewValues.put(MovieContract.ReviewEntry.COLUMN_REVIEW_CONTENT, "Good movie");
        reviewValues.put(MovieContract.ReviewEntry.COLUMN_REVIEW_URL, "http://j.mp/1GHgSxi");

        return reviewValues;
    }

    static ContentValues createTrailerValues(long movieId) {
        ContentValues trailerValues = new ContentValues();
        trailerValues.put(MovieContract.TrailerEntry.COLUMN_MOVIE_ID, movieId);
        trailerValues.put(MovieContract.TrailerEntry.COLUMN_TRAILER_ID, "5576eac192514111e4001b03");
        trailerValues.put(MovieContract.TrailerEntry.COLUMN_TRAILER_NAME, "Official Trailer 3");
        trailerValues.put(MovieContract.TrailerEntry.COLUMN_TRAILER_SITE, "YouTube");
        trailerValues.put(MovieContract.TrailerEntry.COLUMN_TRAILER_TYPE, "Trailer");
        trailerValues.put(MovieContract.TrailerEntry.COLUMN_TRAILER_KEY, "lP-sUUUfamw");

        return trailerValues;
    }

    static ContentValues createMinionMovieValues() {
        ContentValues testValues = new ContentValues();
        testValues.put(MovieContract.MovieEntry.COLUMN_TITLE, "Minions");
        testValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID, TEST_MOVIE);
        testValues.put(MovieContract.MovieEntry.COLUMN_PLOT, "Haha");
        testValues.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE, "2015-07-10");
        testValues.put(MovieContract.MovieEntry.COLUMN_POSTER, "/qARJ35IrJNFzFWQGcyWP4r1jyXE.jpg");
        testValues.put(MovieContract.MovieEntry.COLUMN_RATING, "7.0");

        return testValues;
    }

    static long insertMinionMovieValues(Context context) {

        MovieDbHelper dbHelper = new MovieDbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues testValues = TestUtilities.createMinionMovieValues();

        long movieRowId;
        movieRowId = db.insert(MovieContract.MovieEntry.TABLE_NAME, null, testValues);

        assertTrue("Error: Failure to insert Minion Location Values", movieRowId != -1);

        return testValues.getAsLong(MovieContract.MovieEntry.COLUMN_MOVIE_ID);
    }

    static class TestContentObserver extends ContentObserver {
        final HandlerThread mHT;
        boolean mContentChanged;

        static TestContentObserver getTestContentObserver() {
            HandlerThread ht = new HandlerThread("ContentObserverThread");
            ht.start();
            return new TestContentObserver(ht);
        }

        private TestContentObserver(HandlerThread ht) {
            super(new Handler(ht.getLooper()));
            mHT = ht;
        }

        // On earlier versions of Android, this onChange method is called
        @Override
        public void onChange(boolean selfChange) {
            onChange(selfChange, null);
        }

        @Override
        public void onChange(boolean selfChange, Uri uri) {
            mContentChanged = true;
        }

        public void waitForNotificationOrFail() {
            // Note: The PollingCheck class is taken from the Android CTS (Compatibility Test Suite).
            // It's useful to look at the Android CTS source for ideas on how to test your Android
            // applications.  The reason that PollingCheck works is that, by default, the JUnit
            // testing framework is not running on the main Android application thread.
            new PollingCheck(5000) {
                @Override
                protected boolean check() {
                    return mContentChanged;
                }
            }.run();
            mHT.quit();
        }
    }

    static TestContentObserver getTestContentObserver() {
        return TestContentObserver.getTestContentObserver();
    }
}
