package com.example.vorona.appl.db;

import android.database.Cursor;
import android.database.CursorWrapper;

/**
 * Created by vorona on 11.08.16.
 */

public class ArtistCursor extends CursorWrapper implements DbContract {

    private final int name;
    private final int id;
    private final int genres;
    private final int cover_small;
    private final int cover_big;
    private final int tracks;
    private final int albums;
    private final int bio;

    public ArtistCursor(Cursor cursor) {
        super(cursor);
        id = cursor.getColumnIndex(Artists.ID);
        name = cursor.getColumnIndex(Artists.NAME);
        genres = cursor.getColumnIndex(Artists.GENRES);
        bio = cursor.getColumnIndex(Artists.BIO);
        cover_big = cursor.getColumnIndex(Artists.COVER);
        cover_small = cursor.getColumnIndex(Artists.COVER_SMALL);
        tracks = cursor.getColumnIndex(Artists.TRACKS);
        albums = cursor.getColumnIndex(Artists.ALBUM);
    }

    public String getName() {
        return getString(name);
    }

    public int getAlbums() {
        return getInt(albums);
    }

    public int getTracks() {
        return getInt(tracks);
    }

    public long getId() {
        return getInt(id);
    }

    public String getBio() {
        return getString(bio);
    }

    public String getCover_big() {
        return getString(cover_big);
    }

    public String getCover_small() {
        return getString(cover_small);
    }

    public String getGenres() {
        return getString(genres);
    }
}
