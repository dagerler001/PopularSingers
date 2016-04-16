package com.example.vorona.appl;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.JsonReader;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.vorona.appl.list.FirstRecyclerAdapter;
import com.example.vorona.appl.list.PerformerSelectedListener;
import com.example.vorona.appl.list.RecyclerAdapter;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class GetInfoAsyncTask extends AsyncTask<String, Void, Void> {

    private Activity activity;
    private DownloadState state;
    private String LOG_TAG = "GetInfoAsyncTask";

    List<Singer> singers;

    public GetInfoAsyncTask(Activity activity) {
        Log.w(LOG_TAG, "Started Async Task");
        this.activity = activity;
        singers = new ArrayList<Singer>();
    }

    public void attachActivity(Activity activity) {
        this.activity = activity;
        state = DownloadState.DOWNLOADING;
        updateView(activity);
    }

    private void updateView(Activity activity) {
//        if (activity != null) {
//            if (state == DownloadState.DOWNLOADING) {
//                activity.findViewById(R.id.progress_perf).setVisibility(View.VISIBLE);
//            } else {
//                activity.findViewById(R.id.progress_perf).setVisibility(View.INVISIBLE);
//                activity.findViewById(R.id.list_perf).setVisibility(View.VISIBLE);
//            }
//        }
    }

    @Override
    protected Void doInBackground(String... params) {
        try {
            singers = getSinger();
            if (singers != null) {
                state = DownloadState.DONE;
            } else {
                state = DownloadState.EMPTY;
            }
            updateView(activity);
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            Log.w(LOG_TAG, "We got exception during download");
            state = DownloadState.ERROR;
            return null;
        }
    }


    @Override
    protected void onPostExecute(Void vi) {

        Log.w(LOG_TAG, "Finished Async Task");
        if (state == DownloadState.DONE) {
            RecyclerView rv = (RecyclerView) activity.findViewById(R.id.list_perf);
            FirstRecyclerAdapter mAdapter = new FirstRecyclerAdapter(singers);
            setListener(rv, mAdapter);
        }
        else {
            TextView msg = (TextView) activity.findViewById(R.id.txt_perf);
            msg.setVisibility(View.VISIBLE);
            Typeface face = Typeface.createFromAsset(msg.getContext().getAssets(), "fonts/Elbing.otf");
            msg.setTypeface(face);
            if (state == DownloadState.EMPTY) {
                msg.setText(R.string.txt_empty);
            } else {
                msg.setText(R.string.txt_error);
            }
        }
    }

    private void setListener(RecyclerView rv, RecyclerAdapter adapter) {
        rv.setHasFixedSize(true);
        StaggeredGridLayoutManager mLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        rv.setLayoutManager(mLayoutManager);
        adapter.setPerformerSelectedListener((PerformerSelectedListener) activity);
        rv.setAdapter(adapter);
    }

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



