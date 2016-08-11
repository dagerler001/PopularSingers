package com.example.vorona.appl.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.vorona.appl.model.Singer;

import java.util.ArrayList;
import java.util.List;

import static com.example.vorona.appl.db.DbContract.Favs.ID;

public class DbBackend implements DbContract {

    private final DBHelper mDbOpenHelper;

    public DbBackend(Context context) {
        mDbOpenHelper = new DBHelper(context);
    }

    public List<Singer> getSingers() {
        SQLiteDatabase db = mDbOpenHelper.getWritableDatabase();
        List<Singer> singerList = new ArrayList<>();
        ArtistCursor c = new ArtistCursor(db.query(ARTISTS, null, null, null, null, null, null));
        if (c.moveToFirst()) {
            do {
                Singer singer = new Singer();
                setSingers(singer, c);
                singerList.add(singer);
            } while (c.moveToNext());
        }
        c.close();
        return singerList;
    }

    public List<Singer> getRecs() {
        return getListFromTable(RECENT);
    }

    public List<Singer> getFavs() {
        return getListFromTable(FAVOURITES);
    }

    public List<Singer> getFromTable(String table) {
        switch (table) {
            case FAVOURITES:
                return getFavs();
            case RECENT:
                return getRecs();
            case ARTISTS:
                return getSingers();
            default:
                return null;
        }
    }

    private List<Singer> getListFromTable(String table) {
        List<Singer> list = new ArrayList<>();
        SQLiteDatabase db = mDbOpenHelper.getReadableDatabase();
        ArtistCursor cursor = new ArtistCursor(db.rawQuery("SELECT * FROM " + table +
                " LEFT JOIN " + ARTISTS + " ON " + table + "." + ID + " = " +
                ARTISTS + "." + Artists.ID, null));
        while (cursor.moveToNext()) {
            Singer singer = new Singer();
            setSingers(singer, cursor);
            list.add(singer);
        }
        cursor.close();
        return list;
    }

    private void setSingers(Singer singer, ArtistCursor c) {
        singer.setId(c.getId());
        singer.setName(c.getName());
        singer.setBio(c.getBio());
        singer.setAlbums(c.getAlbums());
        singer.setTracks(c.getTracks());
        singer.setCover_big(c.getCover_big());
        singer.setGenres(c.getGenres());
        singer.setCover_small(c.getCover_small());
    }

    public void deleteSingerFromFavourites(Singer singer) {
        SQLiteDatabase db = mDbOpenHelper.getWritableDatabase();
        db.delete(FAVOURITES, Favs.ID + " = ?", new String[]{Long.toString(singer.getId())});
    }

    public Singer getSinger(long id, String table) {
        SQLiteDatabase db = mDbOpenHelper.getWritableDatabase();
        Singer singer = null;
        ArtistCursor c = new ArtistCursor(db.query(table, null, Artists.ID + " = ?",
                new String[]{Long.toString(id)}, null, null, null));
        if (c.moveToFirst()) {
            singer = new Singer();
            if (table.equals(ARTISTS))
                setSingers(singer, c);
            else {
                singer.setId(c.getInt(c.getColumnIndex(Favs.ID)));
            }
        }
        c.close();
        return singer;
    }

    public void insertSinger(Singer singer, String table) {
        SQLiteDatabase db = mDbOpenHelper.getWritableDatabase();
        ContentValues values;
        if (table.equals(ARTISTS))
            values = createCV(singer);
        else {
            values = new ContentValues();
            values.put(Favs.ID, singer.getId());
        }
        Cursor presence = db.query(table, null, Artists.ID + " = ?",
                new String[]{Long.toString(singer.getId())}, null, null, null);
        if (presence.moveToFirst()) {
            presence.close();
            return;
        }
        db.insert(table, null, values);
    }

    public void insertList(List<Singer> singers) {
        for (Singer singer : singers) {
            insertSinger(singer, ARTISTS);
        }
    }

    private ContentValues createCV(Singer singer) {
        ContentValues cv = new ContentValues();
        cv.put(Artists.ID, singer.getId());
        cv.put(Artists.NAME, singer.getName());
        cv.put(Artists.BIO, singer.getBio());
        cv.put(Artists.ALBUM, singer.getAlbums());
        cv.put(Artists.TRACKS, singer.getTracks());
        cv.put(Artists.COVER, singer.getCover_big());
        cv.put(Artists.COVER_SMALL, singer.getCover_small());
        return cv;
    }
}
