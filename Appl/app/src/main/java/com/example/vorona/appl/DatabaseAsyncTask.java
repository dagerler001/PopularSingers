package com.example.vorona.appl;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.vorona.appl.list.FirstRecyclerAdapter;
import com.example.vorona.appl.list.PerformerSelectedListener;
import com.example.vorona.appl.list.RecyclerAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Asynchronous task for loading data from database.
 * Load lists of performers(all records from selected table).
 */
public class DatabaseAsyncTask extends AsyncTask<String, Void, Void> {

    /**
     * Activity attached to concrete DatabaseAsyncTask
     */
    protected DatabaseActivity activity;

    /**
     * Data from database.
     * List of performers
     */
    List<Singer> singerList;

    /**
     * Helper for database access.
     */
    protected DBHelper dbHelper;
    /**
     * Current state of task
     */
    private DownloadState state;

    private final String LOG_TAG = "DatabaseAsyncTask";

    /**
     * Create an instance of DatabaseAsyncTask and attach related activity
     * @param activity activity on which DatabaseAsyncTask was called
     */
    public DatabaseAsyncTask(DatabaseActivity activity) {
        this.activity = activity;
        singerList = new ArrayList<>();
        state = DownloadState.DOWNLOADING;
        activity.updateView(this);
    }

    /**
     * Attach related activity
     * @param activity activity on which DatabaseAsyncTask was called
     */
    public void attachActivity(DatabaseActivity activity) {
        this.activity = activity;
        activity.updateView(this);
    }

    /**
     * @return current state of task
     */
    DownloadState getState (){
        return state;
    }

    /**
     * Load all performers from table in database.
     * @param params String containing name of table in database
     */
    @Override
    protected Void doInBackground(String... params) {
        Log.w(LOG_TAG, "Started Async Task");
        try {
            dbHelper = new DBHelper(activity);
            singerList = getFromDatabase(params[0]);
            if (singerList.size() > 0) {
                state = DownloadState.DONE;
            } else {
                state = DownloadState.EMPTY;
            }
            return null;
        } catch (Exception e) {
            Log.w(LOG_TAG, "We got exception during download from database");
            state = DownloadState.ERROR;
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Check for uploaded data.
     * If uploaded list of performers is empty, show TextView with message,
     * otherwise show RecyclerView with performers from list.
     * @param vi useless parameter
     */
    @Override
    protected void onPostExecute(Void vi) {
        activity.updateView(this);
        Log.w(LOG_TAG, "Finished Async Task");

        RecyclerView rv = (RecyclerView) activity.findViewById(R.id.list_d);

        ArrayList<Singer> list = new ArrayList<>();
        list.addAll(singerList);
        Collections.reverse(list);
        if (state == DownloadState.DONE) {
            rv.setVisibility(View.VISIBLE);
            setListener(rv, new FirstRecyclerAdapter(list));
        }
    }


    protected void setListener(RecyclerView rv, RecyclerAdapter adapter) {
        rv.setHasFixedSize(true);
        int cnt = (activity.horizontal ? 3 : 2);
        StaggeredGridLayoutManager mLayoutManager = new StaggeredGridLayoutManager(cnt, StaggeredGridLayoutManager.VERTICAL);
        rv.setLayoutManager(mLayoutManager);
        adapter.setPerformerSelectedListener(activity);
        rv.setAdapter(adapter);
    }

    /**
     * Open database and specified table, read all records.
     * @param table name of the table
     * @return list containing all records from the table
     */
    public List<Singer> getFromDatabase(String table) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = db.query(table, null, null, null, null, null, null);
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
        c.close();
        return singerList;
    }
}
