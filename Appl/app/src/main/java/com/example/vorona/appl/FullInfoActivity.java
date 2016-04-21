package com.example.vorona.appl;

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
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

/**
 * Activity for representing full information about single performer.
 */
public class FullInfoActivity extends AppCompatActivity {

    /**
     * Helper for database access.
     */
    protected DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_info);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_full_info);
        setSupportActionBar(toolbar);

        //Create an object for database control
        dbHelper = new DBHelper(this);

        //Get information about selected performer
        Intent i = getIntent();
        final Singer singer = i.getParcelableExtra("SINGER");

        // Add in "Recent" table
        if (!addToTable("Recent", singer))
            addToTable("Recent", singer);

        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        if (checkInTable("Favorites", singer)) {
            fab.setImageResource(R.drawable.added);
        } else {
            fab.setImageResource(R.drawable.star);
        }

        //If selected performer already presents in favourite list delete it, add otherwise
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!addToTable("Favorites", singer)) {
                    Snackbar.make(view, "Deleted from favorites", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    fab.setImageResource(R.drawable.star);
                } else {
                    Snackbar.make(view, "Added to favorites", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    fab.setImageResource(R.drawable.added);
                }
            }
        });


        Typeface face = Typeface.createFromAsset(getAssets(), "fonts/Elbing.otf");

        //name
        TextView txt = (TextView) findViewById(R.id.title);
        txt.setText(singer.getName());
        txt.setTypeface(face);

        //description
        TextView bio = (TextView) findViewById(R.id.bio);
        bio.setMovementMethod(new ScrollingMovementMethod());
        bio.setText(singer.getName() + " - " + singer.getBio());
        bio.setTypeface(face);

        //number of tracks and albums
        TextView tracks = (TextView) findViewById(R.id.tracks);
        tracks.setTypeface(face);
        tracks.setText("Альбомов " + singer.getAlbums() + ", треков " + singer.getTracks());

        //cover
        ImageView cover = (ImageView) findViewById(R.id.cover_big);
        Context context = cover.getContext();
        Picasso.with(context).load(singer.getCover_big()).into(cover);
        ImageView back = (ImageView) findViewById(R.id.background);
        Picasso.with(context).load(singer.getCover_big()).into(back);

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
        cv.put("name", singer.getName());
        cv.put("bio", singer.getBio());
        cv.put("albums", singer.getAlbums());
        cv.put("tracks", singer.getTracks());
        cv.put("cover", singer.getCover_big());
        cv.put("genres", singer.getGenres());
        cv.put("cover_small", singer.getCover_small());
        return cv;
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
     * Kill current activity on pressed back button
     */
    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.from_left, R.anim.to_right);
    }
}
