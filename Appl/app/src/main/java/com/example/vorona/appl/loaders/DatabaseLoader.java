package com.example.vorona.appl.loaders;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.example.vorona.appl.db.DbBackend;
import com.example.vorona.appl.db.DbContract;
import com.example.vorona.appl.model.Singer;

import java.util.ArrayList;
import java.util.List;

public class DatabaseLoader extends AsyncTaskLoader<List<Singer>> implements DbContract {
    private static final String LOG_TAG = "DatabaseLoader";
    private String table;
    private List<Singer> singerList;
    private DbBackend dbBackend;

    public DatabaseLoader(Context context, Bundle args) {
        super(context);
        table = args.getString("Table");
    }

    /**
     * This is where the bulk of our work is done.  This function is
     * called in a background thread and should generate a new set of
     * data to be published by the loader.
     */
    @Override
    public List<Singer> loadInBackground() {
        try {
            dbBackend = new DbBackend(getContext());
            singerList = new ArrayList<>();
            return getFromDatabase();
        } catch (Exception e) {
            Log.w(LOG_TAG, e);
            e.printStackTrace();
            return null;
        }
    }

    private List<Singer> getFromDatabase() {
        return dbBackend.getFromTable(table);
    }

    /**
     * Called when there is new data to deliver to the client.  The
     * super class will take care of delivering it; the implementation
     * here just adds a little more logic.
     */
    @Override
    public void deliverResult(List<Singer> sin) {
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
    @Override
    protected void onStartLoading() {
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

    @Override
    protected void onStopLoading() {
        // Attempt to cancel the current load task if possible.
        cancelLoad();
    }

    /**
     * Handles a request to completely reset the Loader.
     */
    @Override
    protected void onReset() {
        super.onReset();
        onStopLoading();
        if (singerList != null) {
            singerList = null;
        }
    }
}
