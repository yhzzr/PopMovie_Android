package com.example.android.popmovies.app.data;

import android.content.ComponentName;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.test.AndroidTestCase;
import android.util.Log;

import com.example.android.popmovies.app.data.MovieContract.MovieEntry;
import com.example.android.popmovies.app.data.MovieContract.ReviewEntry;
import com.example.android.popmovies.app.data.MovieContract.TrailerEntry;

/**
 * Created by hengyang on 08/05/15.
 */
public class TestProvider extends AndroidTestCase {

    public static final String LOG_TAG = TestProvider.class.getSimpleName();

    public void deleteAllRecordsFromProvider(){
        mContext.getContentResolver().delete(
                MovieEntry.CONTENT_URI,
                null,
                null
        );
        mContext.getContentResolver().delete(
                ReviewEntry.CONTENT_URI,
                null,
                null
        );
        mContext.getContentResolver().delete(
                TrailerEntry.CONTENT_URI,
                null,
                null
        );

        Cursor cursor = mContext.getContentResolver().query(
                ReviewEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        assertEquals("Error: Records not deleted from Review table during delete", 0, cursor.getCount());
        cursor.close();

        cursor = mContext.getContentResolver().query(
                TrailerEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        assertEquals("Error: Records not deleted from Trailer table during delete", 0, cursor.getCount());
        cursor.close();

        cursor = mContext.getContentResolver().query(
                MovieEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        assertEquals("Error: Records not deleted from Movie table during delete", 0, cursor.getCount());
        cursor.close();
    }

    public void deleteAllRecords(){ deleteAllRecordsFromProvider();}

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        deleteAllRecords();
    }

    public void testProviderRegistry() {
        PackageManager pm = mContext.getPackageManager();

        ComponentName componentName = new ComponentName(mContext.getPackageName(),
                MovieProvider.class.getName());
        try {
            ProviderInfo providerInfo = pm.getProviderInfo(componentName, 0);

            assertEquals("Error: MovieProvider registered with authority: " + providerInfo.authority +
                    " instead of authority: " + MovieContract.CONTENT_AUTHORITY,
                    providerInfo.authority, MovieContract.CONTENT_AUTHORITY);
        }catch (PackageManager.NameNotFoundException e){
            assertTrue("Error: MovieProvider not registered at " + mContext.getPackageName(),
                    false);
        }
    }

    public void testGetType() {
        String type1 = mContext.getContentResolver().getType(ReviewEntry.CONTENT_URI);
        String type2 = mContext.getContentResolver().getType(TrailerEntry.CONTENT_URI);

        assertEquals("Error: the ReviewEntry CONTENT_URI should return ReviewEntry.CONTENT_TYPE",
                ReviewEntry.CONTENT_TYPE, type1);
        assertEquals("Error: the TrailerEntry CONTENT_URI should return TrailerEntry.CONTENT_TYPE",
                TrailerEntry.CONTENT_TYPE, type2);

        String testMovie = "123456";
        type1 = mContext.getContentResolver().getType(
                ReviewEntry.buildReviewMovie(testMovie));
        assertEquals("Error: the ReviewEntry with CONTENT_URI with movie name should return ReviewEntry.CONTENT_TYPE",
                ReviewEntry.CONTENT_TYPE, type1);
        type2 = mContext.getContentResolver().getType(
                TrailerEntry.buildTrailerMovie(testMovie));
        assertEquals("Error: the TrailerEntry with CONTENT_URI with movie name should return TrailerEntry.CONTENT_TYPE",
                TrailerEntry.CONTENT_TYPE, type2);

        String type = mContext.getContentResolver().getType(MovieEntry.CONTENT_URI);
        assertEquals("Error: the MovieEntry CONTENT_URI should return MovieEntry.CONTENT_TYPE",
                MovieEntry.CONTENT_TYPE, type);
    }

    public void testBasicReviewQuery() {
        MovieDbHelper dbHelper = new MovieDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues testValues = TestUtilities.createMinionMovieValues();
        long movieId = TestUtilities.insertMinionMovieValues(mContext);

        ContentValues reviewValues = TestUtilities.createReviewValues(movieId);

        long reviewRowId = db.insert(ReviewEntry.TABLE_NAME, null, reviewValues);
        assertTrue("Unable to Insert ReviewEntry into the Database", reviewRowId != -1);

        db.close();

        Cursor reviewCursor = mContext.getContentResolver().query(
                ReviewEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );

        TestUtilities.validateCursor("testBasicReviewQuery", reviewCursor, reviewValues);
    }

    public void testBasicTrailerQuery() {
        MovieDbHelper dbHelper = new MovieDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues testValues = TestUtilities.createMinionMovieValues();
        long movieId = TestUtilities.insertMinionMovieValues(mContext);

        ContentValues trailerValues = TestUtilities.createTrailerValues(movieId);

        long trailerRowId = db.insert(TrailerEntry.TABLE_NAME, null, trailerValues);
        assertTrue("Unable to Insert ReviewEntry into the Database", trailerRowId != -1);

        db.close();

        Cursor trailerCursor = mContext.getContentResolver().query(
                TrailerEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );

        TestUtilities.validateCursor("testBasicTrailerQuery", trailerCursor, trailerValues);
    }

    public void testBasicMovieQueries() {

        MovieDbHelper dbHelper = new MovieDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues testValues = TestUtilities.createMinionMovieValues();
        long movieRowId = TestUtilities.insertMinionMovieValues(mContext);

        Cursor movieCursor = mContext.getContentResolver().query(
                MovieEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );

        TestUtilities.validateCursor("testBasicMovieQueries, movie query", movieCursor, testValues);

        if(Build.VERSION.SDK_INT >= 19){
            assertEquals("Error: Movie Query did not properly set NotificationUri",
                    movieCursor.getNotificationUri(), MovieEntry.CONTENT_URI);
        }
    }

    public void testUpdateMovie() {
        ContentValues values = TestUtilities.createMinionMovieValues();

        Uri movieUri = mContext.getContentResolver().
                insert(MovieEntry.CONTENT_URI, values);
        long movieRowId = ContentUris.parseId(movieUri);

        assertTrue(movieRowId != -1);
        Log.d(LOG_TAG, "New row id: " + movieRowId);

        ContentValues updateValues = new ContentValues(values);
        updateValues.put(MovieEntry._ID, movieRowId);
        updateValues.put(MovieEntry.COLUMN_TITLE, "Jurassic World");

        Cursor movieCursor = mContext.getContentResolver().query(MovieEntry.CONTENT_URI, null, null, null, null);

        TestUtilities.TestContentObserver tco = TestUtilities.getTestContentObserver();
        movieCursor.registerContentObserver(tco);

        int count = mContext.getContentResolver().update(
                MovieEntry.CONTENT_URI, updateValues, MovieEntry._ID + "= ?",
                new String[] { Long.toString(movieRowId)});
        assertEquals(count, 1);

        tco.waitForNotificationOrFail();

        movieCursor.unregisterContentObserver(tco);
        movieCursor.close();

        Cursor cursor = mContext.getContentResolver().query(
                MovieEntry.CONTENT_URI,
                null,
                MovieEntry._ID + " = " + movieRowId,
                null,
                null
        );

        TestUtilities.validateCursor("testUpdateMovie. Error validating location entry update.",
                cursor, updateValues);

        cursor.close();
    }

    public void testInsertReadProvider() {
        ContentValues testValues = TestUtilities.createMinionMovieValues();

        TestUtilities.TestContentObserver tco = TestUtilities.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(MovieEntry.CONTENT_URI, true, tco);
        Uri movieUri = mContext.getContentResolver().insert(MovieEntry.CONTENT_URI, testValues);

        tco.waitForNotificationOrFail();
        mContext.getContentResolver().unregisterContentObserver(tco);

        long movieRowId = ContentUris.parseId(movieUri);

        assertTrue(movieRowId != -1);

        Cursor cursor = mContext.getContentResolver().query(
                MovieEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );

        TestUtilities.validateCursor("testInsertReadProvider. Error validating MovieEntry.",
                cursor, testValues);

        //Test Insert Review
        /*ContentValues reviewValues = TestUtilities.createReviewValues(movieRowId);
        tco = TestUtilities.getTestContentObserver();

        mContext.getContentResolver().registerContentObserver(ReviewEntry.CONTENT_URI, true, tco);

        Uri reviewInsertUri = mContext.getContentResolver()
                .insert(ReviewEntry.CONTENT_URI, reviewValues);
        assertTrue(reviewInsertUri != null);

        tco.waitForNotificationOrFail();
        mContext.getContentResolver().unregisterContentObserver(tco);

        Cursor reviewCursor = mContext.getContentResolver().query(
                ReviewEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );

        TestUtilities.validateCursor("testInsertReadProvider. Error validating ReviewEntry insert.",
                reviewCursor, reviewValues);

        reviewValues.putAll(testValues);

        reviewCursor = mContext.getContentResolver().query(
                ReviewEntry.buildReviewMovie(TestUtilities.TEST_MOVIE),
                null,
                null,
                null,
                null
        );
        TestUtilities.validateCursor("testInsertReadProvider. Error validating joined Review and Movie Data.",
                reviewCursor, reviewValues);
*/

        //Test Insert Trailer
        ContentValues trailerValues = TestUtilities.createTrailerValues(movieRowId);
        tco = TestUtilities.getTestContentObserver();

        mContext.getContentResolver().registerContentObserver(TrailerEntry.CONTENT_URI, true, tco);

        Uri trailerInsertUri = mContext.getContentResolver()
                .insert(TrailerEntry.CONTENT_URI, trailerValues);
        assertTrue(trailerInsertUri != null);

        tco.waitForNotificationOrFail();
        mContext.getContentResolver().unregisterContentObserver(tco);

        Cursor trailerCursor = mContext.getContentResolver().query(
                TrailerEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );

        TestUtilities.validateCursor("testInsertReadProvider. Error validating TrailerEntry insert.",
                trailerCursor, trailerValues);

        trailerValues.putAll(testValues);

        trailerCursor = mContext.getContentResolver().query(
                TrailerEntry.buildTrailerMovie(TestUtilities.TEST_MOVIE),
                null,
                null,
                null,
                null
        );
        TestUtilities.validateCursor("testInsertReadProvider. Error validating joined Trailer and Movie Data.",
                trailerCursor, trailerValues);
    }

   /* public void testDeleteRecords() {
        testInsertReadProvider();

        TestUtilities.TestContentObserver movieObserver = TestUtilities.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(MovieEntry.CONTENT_URI, true, movieObserver);

        TestUtilities.TestContentObserver reviewObserver = TestUtilities.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(ReviewEntry.CONTENT_URI, true, reviewObserver);

        TestUtilities.TestContentObserver trailerObserver = TestUtilities.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(TrailerEntry.CONTENT_URI, true, trailerObserver);

        deleteAllRecordsFromProvider();

        movieObserver.waitForNotificationOrFail();
        reviewObserver.waitForNotificationOrFail();
        trailerObserver.waitForNotificationOrFail();

        mContext.getContentResolver().unregisterContentObserver(movieObserver);
        mContext.getContentResolver().unregisterContentObserver(reviewObserver);
        mContext.getContentResolver().unregisterContentObserver(trailerObserver);
    }*/


}
