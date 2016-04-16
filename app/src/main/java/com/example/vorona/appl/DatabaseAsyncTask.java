package com.example.vorona.appl;

import android.app.Activity;
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
 * Loads lists of performers(all records from selected table).
 */
public class DatabaseAsyncTask extends AsyncTask<String, Void, Void> {

    /**
     * Activity attached to concrete DatabaseAsyncTask
     */
    protected Activity activity;

    /**
     * Data from database.
     * List of performers
     */
    List<Singer> singerList;

    protected DBHelper dbHelper;

    DownloadState state;

    /**
     * Creates an instance of DatabaseAsyncTask and attaches related activity
     * @param activity activity on which DatabaseAsyncTask was called
     */
    public DatabaseAsyncTask(Activity activity) {
        this.activity = activity;
        singerList = new ArrayList<>();
    }

    /**
     * Attaches related activity
     * @param activity activity on which DatabaseAsyncTask was called
     */
    public void attachActivity(Activity activity) {
        this.activity = activity;
        state = DownloadState.DOWNLOADING;
        updateView(activity);
    }

    /**
     * Shows loading bar while getting data from database.
     * Hides when download is finished.
     * @param activity activity on which DatabaseAsyncTask was called
     */
    protected void updateView(Activity activity) {
        //TODO
    }


    /**
     * Load all performers from table in database.
     * @param params String containing name of table in database
     */
    @Override
    protected Void doInBackground(String... params) {
        try {
            dbHelper = new DBHelper(activity);
            singerList = getFromDatabase(params[0]);
            if (singerList.size() > 0) {
                state = DownloadState.DONE;
            } else {
                state = DownloadState.EMPTY;
            }
            updateView(activity);
            return null;
        } catch (Exception e) {
            state = DownloadState.ERROR;
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Checks that was uploaded.
     * If uploaded list of performers is empty, shows TextView with message,
     * otherwise shows RecyclerView with performers from list.
     * @param vi useless parameter
     */
    @Override
    protected void onPostExecute(Void vi) {

//        Log.w(LOG_TAG, "Finished Async Task");

        RecyclerView rv = (RecyclerView) activity.findViewById(R.id.list_d);
        TextView txt_no = (TextView) activity.findViewById(R.id.no_d);

        ArrayList<Singer> list = new ArrayList<>();
        list.addAll(singerList);
        Collections.reverse(list);
        if (state == DownloadState.DONE) {
            rv.setVisibility(View.VISIBLE);
            setListener(rv, new FirstRecyclerAdapter(list));
        }
        else {
            txt_no.setVisibility(View.VISIBLE);
            Typeface face = Typeface.createFromAsset(txt_no.getContext().getAssets(), "fonts/Elbing.otf");
            txt_no.setTypeface(face);
            if (state == DownloadState.EMPTY) {
                txt_no.setText(R.string.txt_empty);
            } else {
                txt_no.setText(R.string.unknown_error);
            }
        }
    }


    protected void setListener(RecyclerView rv, RecyclerAdapter adapter) {
        rv.setHasFixedSize(true);
        StaggeredGridLayoutManager mLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        rv.setLayoutManager(mLayoutManager);
        adapter.setPerformerSelectedListener((PerformerSelectedListener) activity);
        rv.setAdapter(adapter);
    }

    /**
     * Opens database and specified table, reads all records.
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

    enum DownloadState {
        DOWNLOADING(R.string.downloading),
        DONE(R.string.done),
        ERROR(R.string.error),
        EMPTY(R.string.empty);

        final int titleResId;

        DownloadState(int titleResId) {
            this.titleResId = titleResId;
        }
    }
}
