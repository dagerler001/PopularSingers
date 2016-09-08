package com.example.vorona.appl.ui.activities;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.NotificationCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.vorona.appl.R;
import com.example.vorona.appl.ui.fragments.ListFragment;
import com.example.vorona.appl.ui.fragments.ProgInfoFragment;

import static com.example.vorona.appl.db.DbContract.ARTISTS;
import static com.example.vorona.appl.db.DbContract.FAVOURITES;
import static com.example.vorona.appl.db.DbContract.RECENT;

/**
 * Start Activity with list of performers
 */
public class PerformersActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, FragmentManager.OnBackStackChangedListener {

    private NavigationView navigationView;

    private MusicIntentReceiver myReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_performers);

        setTitle("Исполнители");
        myReceiver = new MusicIntentReceiver();

        getFragmentManager().addOnBackStackChangedListener(this);
        //Customize toolbar and navigationView
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setCheckedItem(R.id.main_activity);
        navigationView.setNavigationItemSelectedListener(this);

        if (getFragmentManager().findFragmentById(R.id.fragment_holder) == null) {
            Fragment fragment = ListFragment.newInstance(ARTISTS);
            FragmentTransaction fTrans = getFragmentManager().beginTransaction();
            fTrans.add(R.id.fragment_holder, fragment);
            fTrans.addToBackStack(null);
            fTrans.commit();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        navigationView.setCheckedItem(R.id.main_activity);
        IntentFilter filter = new IntentFilter(Intent.ACTION_HEADSET_PLUG);
        registerReceiver(myReceiver, filter);
    }

    /**
     * Kill current fragment when pressed back button
     */
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (getFragmentManager().getBackStackEntryCount() <= 1)
                super.onBackPressed();
            else
                getFragmentManager().popBackStack();
        }
    }

    /**
     * Inflate the menu, adds items to the action bar if it is present.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.performers, menu);
        return true;
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        if (id == R.id.main_activity || id == R.id.favs || id == R.id.recent) {
            String table;
            if (id == R.id.main_activity) {
                int cnt = getFragmentManager().getBackStackEntryCount();
                for (int i = 0; i < cnt; i++)
                    getFragmentManager().popBackStack();
                table = ARTISTS;
            } else if (id == R.id.favs) {
                table = FAVOURITES;
            } else
                table = RECENT;
            Fragment fragment = ListFragment.newInstance(table);
            FragmentTransaction fTrans = getFragmentManager().beginTransaction();
            fTrans.add(R.id.fragment_holder, fragment);
            fTrans.addToBackStack(null);
            fTrans.commit();

        } else if (id == R.id.info) {
            Fragment fragment = new ProgInfoFragment();
            FragmentTransaction fTrans = getFragmentManager().beginTransaction();
            fTrans.add(R.id.fragment_holder, fragment);
            fTrans.addToBackStack(null);
            fTrans.commit();
        } else if (id == R.id.email) {
            Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
            String aEmail = "veda345@yandex.ru";
            emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, aEmail);
            emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Popular Singers App");
            emailIntent.setType("plain/text");
            emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, "Write your text here.");

            startActivity(Intent.createChooser(emailIntent, "Send your email in:"));
        }
        return true;
    }

    @Override
    public void onPause() {
        unregisterReceiver(myReceiver);
        super.onPause();
    }

    @Override
    public void onBackStackChanged() {
        if (getFragmentManager().getBackStackEntryCount() > 0) {
            Fragment curFragment = getFragmentManager().findFragmentById(R.id.fragment_holder);
            if (curFragment instanceof ListFragment)
                setTitle(((ListFragment) curFragment).getTitle());
            else if (curFragment instanceof ProgInfoFragment)
                setTitle("О программе");
            else
                setTitle("Исполнители");
        } else
            setTitle("Исполнители");
    }

    private void show(String notificationText, String installText, int notificationId) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_music)
                .setContentTitle(getString(R.string.plugged))
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC);
        Intent launchIntentMusic = getPackageManager().getLaunchIntentForPackage(getString(R.string.music_package));
        if (launchIntentMusic == null) {
            launchIntentMusic = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + getString(R.string.music_package)));
            builder.setContentText(installText);
        } else {
            builder.setContentText(notificationText);
        }
        Intent launchIntentRadio = getPackageManager().getLaunchIntentForPackage(getString(R.string.radio_package));
        if (launchIntentRadio == null) {
            launchIntentRadio = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + getString(R.string.radio_package)));
            builder.setContentText(installText);
        } else {
            builder.setContentText(notificationText);
        }
        builder.addAction(R.drawable.ic_music, getString(R.string.yandex_music), PendingIntent.getActivity(this, 1, launchIntentMusic, 0));
        builder.addAction(R.drawable.ic_mail, getString(R.string.yandex_radio), PendingIntent.getActivity(this, 2, launchIntentRadio, 0));

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(notificationId, builder.build());
    }

    private class MusicIntentReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Intent.ACTION_HEADSET_PLUG)) {
                int state = intent.getIntExtra("state", -1);
                switch (state) {
                    case 0:
                        break;
                    case 1:
                        show(getString(R.string.open), getString(R.string.install), 1);
                        break;
                    default:
                        Toast.makeText(context, "WTF?!",
                                Toast.LENGTH_LONG).show();
                }
            }
        }
    }
}
