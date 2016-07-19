package com.example.vorona.appl;

import android.app.Fragment;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.vorona.appl.list.FirstRecyclerAdapter;
import com.example.vorona.appl.list.PerformerSelectedListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class FullInfoFragment extends Fragment{

    /**
     * Helper for database access.
     */
    protected DBHelper dbHelper;

    private Singer singer;

    public static FullInfoFragment newInstance(Singer singer) {
        Bundle args = new Bundle();
        args.putParcelable("Singer", singer);
        FullInfoFragment fragment = new FullInfoFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getActivity().setTitle("Исполнители");
        return inflater.inflate(R.layout.fragment_info, null);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        singer = getArguments().getParcelable("Singer");
        setHasOptionsMenu(true);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        //Create an object for database control
        dbHelper = new DBHelper(getActivity());

        // Add in "Recent" table
        if (!addToTable(getString(R.string.recent_table), singer))
            addToTable(getString(R.string.recent_table), singer);

        final FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
        if (checkInTable(getString(R.string.fav_table), singer)) {
            fab.setImageResource(R.drawable.added);
        } else {
            fab.setImageResource(R.drawable.star);
        }

        //If selected performer already presents in favourite list delete it, add otherwise
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!addToTable(getString(R.string.fav_table), singer)) {
                    Snackbar.make(view, getString(R.string.deleted), Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    fab.setImageResource(R.drawable.star);
                } else {
                    Snackbar.make(view, getString(R.string.added) , Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    fab.setImageResource(R.drawable.added);
                }
            }
        });


        Typeface face = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Elbing.otf");

        //name
        TextView txt = (TextView) view.findViewById(R.id.title);
        txt.setText(singer.getName());
        txt.setTypeface(face);

        //description
        TextView bio = (TextView) view.findViewById(R.id.bio);
        bio.setMovementMethod(new ScrollingMovementMethod());
        bio.setText(singer.getName() + " - " + singer.getBio());
        bio.setTypeface(face);

        //number of tracks and albums
        TextView tracks = (TextView) view.findViewById(R.id.tracks);
        tracks.setTypeface(face);
        tracks.setText("Альбомов " + singer.getAlbums() + ", треков " + singer.getTracks());

        //cover
        ImageView cover = (ImageView) view.findViewById(R.id.cover_big);
        Context context = cover.getContext();
        Picasso.with(context).load(singer.getCover_big()).into(cover);
        ImageView back = (ImageView) view.findViewById(R.id.background);
        Picasso.with(context).load(singer.getCover_big()).into(back);

    }

    /**
     * Add record of specified singer in specified table.
     */
    private boolean addToTable(String table, Singer singer) {

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + table + " WHERE id=" + singer.getId(), null);
        boolean added = false;
        if (cursor.moveToFirst()) {
            db.delete(table, "id='" + singer.getId() + "'", null);
        } else  {
            try {
                db.insertOrThrow(table, null, createCV(singer));
                added = true;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        db.close();
        cursor.close();
        return added;
    }

    /**
     * Check if in specified table exists record of specified performer.
     */
    private boolean checkInTable(String table, Singer singer) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        try (Cursor cursor = db.rawQuery("SELECT * FROM " + table + " WHERE id=" + singer.getId(), null)) {
            return cursor.moveToFirst();
        }
    }

    /**
     * Combines ContentValues for adding in table.
     */
    private ContentValues createCV(Singer singer) {
        ContentValues cv = new ContentValues();
        cv.put("id", singer.getId());
        return cv;
    }
}
