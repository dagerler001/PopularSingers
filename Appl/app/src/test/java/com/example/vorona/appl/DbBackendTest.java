package com.example.vorona.appl;


import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.vorona.appl.db.DBHelper;
import com.example.vorona.appl.db.DbBackend;
import com.example.vorona.appl.model.Singer;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

import java.util.ArrayList;
import java.util.List;

import static com.example.vorona.appl.db.DbContract.ARTISTS;
import static com.example.vorona.appl.db.DbContract.FAVOURITES;


/**
 * Created by vorona on 05.08.16.
 */

@RunWith(RobolectricTestRunner.class)
public class DbBackendTest {

    @Test
    public void testInsertSinger() {
        DBHelper helper = new DBHelper(RuntimeEnvironment.application);
        SQLiteDatabase db = helper.getWritableDatabase();

        DbBackend dbBackend = new DbBackend(RuntimeEnvironment.application);

        //Empty table, one singer added
        Singer singer = new Singer();
        singer.setName("AAAA");
        singer.setId(1);
        dbBackend.insertSinger(singer, ARTISTS);
        Singer s = dbBackend.getSinger(singer.getId(), ARTISTS);
        Assert.assertEquals(1, getCount(db, ARTISTS));
        Assert.assertEquals(s.getName(), singer.getName());

        //One another
        Singer singer2 = new Singer();
        singer.setName("BBB");
        singer2.setId(2);
        dbBackend.insertSinger(singer2, ARTISTS);
        Assert.assertEquals(2, getCount(db, ARTISTS));

        //The same singer should not be added
        Singer singer3 = new Singer();
        singer.setName("AAAA");
        singer3.setId(1);
        dbBackend.insertSinger(singer3, ARTISTS);
        int p = getCount(db, ARTISTS);
        Assert.assertEquals(2, p);
    }


    @Test
    public void testInsertList() {
        DBHelper helper = new DBHelper(RuntimeEnvironment.application);
        SQLiteDatabase db = helper.getWritableDatabase();
        DbBackend dbBackend = new DbBackend(RuntimeEnvironment.application);
        List<Singer> singerList = new ArrayList<>();
        //Empty table, list of singers added
        Singer singer = new Singer();
        singer.setName("AAAA");
        singer.setId(1);
        Singer singer2 = new Singer();
        singer.setName("BBB");
        singer2.setId(2);
        Singer singer3 = new Singer();
        singer.setName("AAAA");
        singer3.setId(3);
        singerList.add(singer);
        singerList.add(singer2);
        singerList.add(singer3);
        dbBackend.insertList(singerList);
        int p = getCount(db, ARTISTS);
        Assert.assertEquals(3, p);
    }


    @Test
    public void testGetAllSingers() {
        DBHelper helper = new DBHelper(RuntimeEnvironment.application);
        SQLiteDatabase db = helper.getWritableDatabase();
        DbBackend dbBackend = new DbBackend(RuntimeEnvironment.application);
        List<Singer> singerList = new ArrayList<>();
        //Empty table, list of singers added
        Singer singer = new Singer();
        singer.setName("AAAA");
        singer.setId(1);
        Singer singer2 = new Singer();
        singer2.setName("BBB");
        singer2.setId(2);
        singerList.add(singer);
        singerList.add(singer2);
        dbBackend.insertList(singerList);
        List<Singer> s = dbBackend.getSingers();
        Assert.assertEquals(s.size(), singerList.size());
    }


    @Test
    public void testGetAllFavSingers() {
        DBHelper helper = new DBHelper(RuntimeEnvironment.application);
        SQLiteDatabase db = helper.getWritableDatabase();
        DbBackend dbBackend = new DbBackend(RuntimeEnvironment.application);
        List<Singer> singerList = new ArrayList<>();
        //Empty table, list of singers added
        Singer singer = new Singer();
        singer.setName("AAAA");
        singer.setId(1);

        Singer singer2 = new Singer();
        singer2.setName("BBB");
        singer2.setId(2);

        singerList.add(singer);
        singerList.add(singer2);
        dbBackend.insertList(singerList);

        dbBackend.insertSinger(singer, FAVOURITES);
        dbBackend.insertSinger(singer2, FAVOURITES);

        Assert.assertEquals(getCount(db, FAVOURITES), 2);
        List<Singer> s = dbBackend.getFavs();
        Assert.assertEquals(s.size(), 2);
    }

    private int getCount(SQLiteDatabase db, String table) {
        Cursor c = db.rawQuery("select * from " + table, null);
        int p = c.getCount();
        c.close();
        return p;
    }

}
