package com.example.vorona.appl.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Class for database access.
 */
public class DBHelper extends SQLiteOpenHelper implements DbContract {

    public DBHelper(Context context) {
        super(context, DB_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("create table " + FAVOURITES + " ("
                + Favs.ID + " integer primary key"
                +");");

        db.execSQL("create table " + RECENT + " ("
                + Recs.ID + " integer primary key"
                +");");

        db.execSQL("create table " + ARTISTS + " ("
                + Artists.LOCAL_ID + " integer primary key autoincrement,"
                + Artists.ID + " integer,"
                + Artists.NAME + " text,"
                + Artists.BIO + " text,"
                + Artists.ALBUM + " integer,"
                + Artists.TRACKS + " integer,"
                + Artists.COVER + " text,"
                + Artists.GENRES + " text,"
                + Artists.COVER_SMALL + " text"
                +");");

        db.execSQL("CREATE INDEX idx_" + Artists.ID +
                " ON " + ARTISTS + "(" + Artists.ID + ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE " + ARTISTS);
        db.execSQL("DROP TABLE " + FAVOURITES);
        db.execSQL("DROP TABLE " + RECENT);
        db.setVersion(newVersion);
    }
}
