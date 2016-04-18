package com.example.vorona.appl;

import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.JsonReader;
import android.util.Log;

import com.example.vorona.appl.list.FirstRecyclerAdapter;
import com.example.vorona.appl.list.RecyclerAdapter;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Asynchronous task for loading json-file and parsing.
 * The final list of perforemers is displayed in recyclerView
 */
public class GetInfoAsyncTask extends AsyncTask<String, Integer, Void> {

    /**
     * Activity attached to concrete GetInfoAsyncTask
     */
    private PerformersActivity activity;

    /**
     * Current state of task
     */
    private DownloadState state;

    private String LOG_TAG = "GetInfoAsyncTask";

    /**
     * Final list of performers from json-file
     */
    List<Singer> singers;

    /**
     * Create an instance of GetInfoAsyncTask and attach related activity
     * @param activity activity on which GetInfoAsyncTask was called
     */
    public GetInfoAsyncTask(PerformersActivity activity) {
        this.activity = activity;
        singers = new ArrayList<>();
    }

    /**
     * Attach related activity
     * @param activity activity on which GetInfoAsyncTask was called
     */
    public void attachActivity(PerformersActivity activity) {
        this.activity = activity;
        state = DownloadState.DOWNLOADING;
        activity.updateView(this);
    }

    /**
     * @return current state of task
     */
    DownloadState getState (){
        return state;
    }

    /**
     * Load json-file and parse it into
     * @code {List<Singer> }.
     * @param params ignored
     */
    @Override
    protected Void doInBackground(String... params) {
        Log.w(LOG_TAG, "Started Async Task");
        try {
            singers = getSinger();
            if (singers != null) {
                state = DownloadState.DONE;
            } else {
                state = DownloadState.EMPTY;
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            Log.w(LOG_TAG, "We got exception during download");
            state = DownloadState.ERROR;
            return null;
        } finally {
            activity.updateView(this);
        }
    }

    /**
     * Show uploaded list in recyclerView
     * @param vi
     */
    @Override
    protected void onPostExecute(Void vi) {
        Log.w(LOG_TAG, "Finished Async Task");
        if (state == DownloadState.DONE) {
            RecyclerView rv = (RecyclerView) activity.findViewById(R.id.list_perf);
            FirstRecyclerAdapter mAdapter = new FirstRecyclerAdapter(singers);
            setListener(rv, mAdapter);
        }
        activity.updateView(this);
    }

    private void setListener(RecyclerView rv, RecyclerAdapter adapter) {
        rv.setHasFixedSize(true);
        int cnt = (activity.horizontal ? 3 : 2);
        StaggeredGridLayoutManager mLayoutManager = new StaggeredGridLayoutManager(cnt, StaggeredGridLayoutManager.VERTICAL);
        rv.setLayoutManager(mLayoutManager);
        adapter.setPerformerSelectedListener(activity);
        rv.setAdapter(adapter);
    }

    /**
     * Open http url connection, start reading json
     * @return list of performers
     */
    public List<Singer> getSinger() throws IOException {

        URL url = new URL("http://cache-spb05.cdn.yandex.net/download.cdn.yandex.net/mobilization-2016/artists.json");

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        InputStream in = connection.getInputStream();
        try {
            JsonReader reader = new JsonReader(new InputStreamReader(in));
            List<Singer> res = readJson(reader);
            reader.close();
            return res;
        }
        finally {
            if (in != null) {
                in.close();
            }
            connection.disconnect();
        }

    }

    /**
     * Combine all performers
     */
    private List<Singer> readJson(JsonReader reader) throws IOException {
        ArrayList<Singer> ar = new ArrayList<Singer>();
        Singer cur;
        reader.beginArray();
        while (reader.hasNext()) {
            cur = takeData(reader);
            ar.add(cur);
        }
        reader.endArray();

        return ar;
    }

    /**
     * Read information about individual performers
     */
    private Singer takeData(JsonReader reader) throws IOException {
        Singer res = new Singer();

        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            switch (name) {
                case "id":
                    res.setId(reader.nextLong());
                    break;
                case "name":
                    res.setName(reader.nextString());
                    break;
                case "tracks":
                    res.setTracks((int)reader.nextLong());
                    break;
                case "albums":
                    res.setAlbums((int)reader.nextLong());
                    break;
                case "link":
                    res.setLink(reader.nextString());
                    break;
                case "description":
                    res.setBio(reader.nextString());
                    break;
                case "cover":
                    reader.beginObject();
                    while (reader.hasNext()) {
                        String s = reader.nextName();
                        switch (s) {
                            case "small":
                                res.setCover_small(reader.nextString());
                                break;
                            case "big":
                                res.setCover_big(reader.nextString());
                                break;
                            default:
                                reader.skipValue();
                        }
                    }
                    reader.endObject();
                    break;
                case "genres":
                    reader.beginArray();
                    StringBuilder str = new StringBuilder();
                    while (reader.hasNext()) {
                        String s = reader.nextString();
                        str.append(s).append(", ");
                    }
                    reader.endArray();
                    if (str.lastIndexOf(", ") > 0 && str.length() > 0) {
                        res.setGenres(str.toString().substring(0, str.lastIndexOf(",")));
                    }
                    break;
                default:
                    reader.skipValue();
            }
        }
        reader.endObject();
        return res;
    }

}



