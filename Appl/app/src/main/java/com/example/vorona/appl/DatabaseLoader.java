package com.example.vorona.appl;

import android.content.AsyncTaskLoader;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.JsonReader;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by vorona on 19.07.16.
 */

public class DatabaseLoader extends AsyncTaskLoader<List<Singer>> {
    private List<Singer> singerList;
    private DBHelper dbHelper;
    private final String LOG_TAG = "DatabaseLoader";
    String table;

    public DatabaseLoader(Context context, Bundle args) {
        super(context);
        if (args != null)
            table = args.getString("Table");
    }

    /**
     * This is where the bulk of our work is done.  This function is
     * called in a background thread and should generate a new set of
     * data to be published by the loader.
     */
    @Override public List<Singer> loadInBackground() {
        try {
            Log.w(LOG_TAG, "Loading from the database");
            dbHelper = new DBHelper(getContext());
            singerList = new ArrayList<>();
            return getFromDatabase(table);
        } catch (Exception e) {
            Log.w(LOG_TAG, "We got exception during download from database");
            e.printStackTrace();
            return null;
        }
    }


    public List<Singer> getFromDatabase(String table) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = db.query(table, null, null, null, null, null, null);
        if (table.equals("Performers")) {
            if (c.moveToFirst()) {
                do {
                    Singer singer = new Singer();
                    singer.setId(c.getInt(c.getColumnIndex("id")));
                    singer.setName(c.getString(c.getColumnIndex("name")));
                    singer.setBio(c.getString(c.getColumnIndex("bio")));
                    singer.setAlbums(c.getInt(c.getColumnIndex("albums")));
                    singer.setTracks(c.getInt(c.getColumnIndex("tracks")));
                    singer.setCover_big(c.getString(c.getColumnIndex("cover")));
                    singer.setGenres(c.getString(c.getColumnIndex("genres")));
                    singer.setCover_small(c.getString(c.getColumnIndex("cover_small")));
                    singerList.add(singer);
                } while (c.moveToNext());
            }
        } else {
            if (c.moveToFirst()) {
                do {
                    Singer singer = new Singer();
                    singer.setId(c.getInt(c.getColumnIndex("id")));
                    Cursor main_cursor = db.query("Performers", null, "id = " + Long.toString(singer.getId()), null, null, null, null);
                    if (main_cursor.moveToFirst()) {
                        singer.setName(main_cursor.getString(main_cursor.getColumnIndex("name")));
                        singer.setBio(main_cursor.getString(main_cursor.getColumnIndex("bio")));
                        singer.setAlbums(main_cursor.getInt(main_cursor.getColumnIndex("albums")));
                        singer.setTracks(main_cursor.getInt(main_cursor.getColumnIndex("tracks")));
                        singer.setCover_big(main_cursor.getString(main_cursor.getColumnIndex("cover")));
                        singer.setGenres(main_cursor.getString(main_cursor.getColumnIndex("genres")));
                        singer.setCover_small(main_cursor.getString(main_cursor.getColumnIndex("cover_small")));
                    }
                    main_cursor.close();
                    singerList.add(singer);
                } while (c.moveToNext());
            }
        }
        c.close();
        return singerList;
    }

    /**
     * Called when there is new data to deliver to the client.  The
     * super class will take care of delivering it; the implementation
     * here just adds a little more logic.
     */
    @Override public void deliverResult(List<Singer> sin) {
        if (isReset()) {
            // An async query came in while the loader is stopped.  We
            // don't need the result.
            if (sin != null) {
                onReleaseResources(sin);
            }
        }
        singerList = sin;

        if (isStarted()) {
            // If the Loader is currently started, we can immediately
            // deliver its results.
            super.deliverResult(sin);
        }
    }

    /**
     * Handles a request to start the Loader.
     */
    @Override protected void onStartLoading() {
        if (singerList != null) {
            // If we currently have a result available, deliver it
            // immediately.
            deliverResult(singerList);
        }

        if (takeContentChanged() || singerList == null) {
            // If the data has changed since the last time it was loaded
            // or is not currently available, start a load.
            forceLoad();
        }
    }

    /**
     * Handles a request to stop the Loader.
     */
    @Override protected void onStopLoading() {
        // Attempt to cancel the current load task if possible.
        cancelLoad();
    }

    /**
     * Handles a request to cancel a load.
     */
    @Override public void onCanceled(List<Singer> singers) {
        super.onCanceled(singers);

        // At this point we can release the resources associated with 'apps'
        // if needed.
        onReleaseResources(singers);
    }

    /**
     * Handles a request to completely reset the Loader.
     */
    @Override protected void onReset() {
        super.onReset();

        // Ensure the loader is stopped
        onStopLoading();

        // At this point we can release the resources associated with 'apps'
        // if needed.
        if (singerList != null) {
            onReleaseResources(singerList);
            singerList = null;
        }
    }

    /**
     * Helper function to take care of releasing resources associated
     * with an actively loaded data set.
     * @param singers
     */
    protected void onReleaseResources(List<Singer> singers) {
        // For a simple List<> there is nothing to do.  For something
        // like a Cursor, we would close it here.
    }
}
