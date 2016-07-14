package com.example.vorona.appl;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vorona.appl.list.FirstRecyclerAdapter;
import com.example.vorona.appl.list.PerformerSelectedListener;

import java.util.ArrayList;

/**
 * Start Activity with list of performers
 */
public class PerformersActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, PerformerSelectedListener, FragmentManager.OnBackStackChangedListener {

    private GetInfoAsyncTask downloadTask;
    private NavigationView navigationView;

    private TextView title;
    private RecyclerView rv;
    private ProgressBar p_bar;
    private ImageView retry;

    private Fragment curFragment;
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

        title = (TextView) findViewById(R.id.txt_perf);
        rv = (RecyclerView) findViewById(R.id.list_perf);
        p_bar = (ProgressBar) findViewById(R.id.progress_perf);
        Typeface face = Typeface.createFromAsset(title.getContext().getAssets(), "fonts/Elbing.otf");
        title.setTypeface(face);
        retry = (ImageView) findViewById(R.id.retry);

        //set initial adapter and layoutManager for recyclerView.
        rv.setAdapter(new FirstRecyclerAdapter(new ArrayList<Singer>()));
        rv.setLayoutManager(new LinearLayoutManager(rv.getContext()));

        //check for saved AsyncTask
        if (savedInstanceState != null && downloadTask != null) {
            downloadTask = (GetInfoAsyncTask) getLastCustomNonConfigurationInstance();
            downloadTask.attachActivity(this);
            downloadTask.onPostExecute(0);
        } else {
            downloadTask = new GetInfoAsyncTask(this);
            downloadTask.execute();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        navigationView.setCheckedItem(R.id.main_activity);
        IntentFilter filter = new IntentFilter(Intent.ACTION_HEADSET_PLUG);
        registerReceiver(myReceiver, filter);
        setTitle("Исполнители");
    }

    /**
     * Show loading bar while getting data.
     * Hide when download is finished. If an error occurred during download textView and retry button are shown.
     * Otherwise recyclerView is shown.
     *
     * @param task GetInfoAsyncTask which is working at the moment
     */
    void updateView(GetInfoAsyncTask task) {
        switch (task.getState()) {
            case DOWNLOADING:
                p_bar.setVisibility(View.VISIBLE);
                rv.setVisibility(View.INVISIBLE);
                title.setVisibility(View.INVISIBLE);
                retry.setVisibility(View.INVISIBLE);
                break;
            case DONE:
                p_bar.setVisibility(View.INVISIBLE);
                rv.setVisibility(View.VISIBLE);
                title.setVisibility(View.INVISIBLE);
                retry.setVisibility(View.INVISIBLE);
                break;
            case ERROR:
                p_bar.setVisibility(View.INVISIBLE);
                rv.setVisibility(View.INVISIBLE);
                title.setVisibility(View.VISIBLE);
                title.setText(R.string.txt_error);
                retry.setVisibility(View.VISIBLE);
                break;
            case EMPTY:
                p_bar.setVisibility(View.INVISIBLE);
                rv.setVisibility(View.INVISIBLE);
                title.setVisibility(View.VISIBLE);
                retry.setVisibility(View.INVISIBLE);
                title.setText(R.string.txt_empty);
                break;
            default:
                break;
        }

    }

    /**
     * Will continue current DatabaseAsyncTask on restart of activity.
     * Don't save if error occurred during download.
     *
     * @return current DatabaseAsyncTask
     */
    @Override
    public Object onRetainCustomNonConfigurationInstance() {
        if (downloadTask.getState() == DownloadState.ERROR)
            return null;
        return downloadTask;
    }

    /**
     * Kill current activity when pressed back button
     */
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (getFragmentManager().getBackStackEntryCount() == 0)
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
        if (id == R.id.main_activity) {
            int cnt = getFragmentManager().getBackStackEntryCount();
            for (int i = 0; i < cnt; i++)
                getFragmentManager().popBackStack();
            setTitle("Исполнители");
            recreate();
        } else if (id == R.id.favs) {
            Fragment fragment = ListFragment.newInstance(getString(R.string.fav_table));
            FragmentTransaction fTrans = getFragmentManager().beginTransaction();
            fTrans.add(R.id.fragment_holder, fragment);
            fTrans.addToBackStack(null);
            fTrans.commit();
        } else if (id == R.id.recent) {
            Fragment fragment = ListFragment.newInstance(getString(R.string.recent_table));
            FragmentTransaction fTrans = getFragmentManager().beginTransaction();
            fTrans.add(R.id.fragment_holder, fragment);
            fTrans.addToBackStack(null);
            fTrans.commit();
        }  else if (id == R.id.info) {
            Fragment fragment = new ProgInfoFragment();
            FragmentTransaction fTrans = getFragmentManager().beginTransaction();
            fTrans.add(R.id.fragment_holder, fragment);
            fTrans.addToBackStack(null);
            fTrans.commit();
        } else if (id == R.id.email) {
            Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
            String aEmail =  "veda345@yandex.ru" ;

            emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, aEmail);

            emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Popular Siners App");

            emailIntent.setType("plain/text");
            emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, "Write your text here.");

            startActivity(Intent.createChooser(emailIntent, "Send your email in:"));
        }
        return true;
    }

    /**
     * Open new activity with full information about selected performer.
     *
     * @param singer selected in RecycleView singer
     */
    @Override
    public void onPerformerSelected(Singer singer) {
        Fragment fragment = FullInfoFragment.newInstance(singer);
        FragmentTransaction fTrans = getFragmentManager().beginTransaction();
        fTrans.add(R.id.fragment_holder, fragment);
        fTrans.addToBackStack(null);
        fTrans.commit();
    }

    @Override
    public void onPause() {
        unregisterReceiver(myReceiver);
        super.onPause();
    }

    /**
     * Restart GetInfoAsyncTask
     */
    public void onRetryClick(View view) {
        downloadTask = new GetInfoAsyncTask(this);
        downloadTask.execute();
    }

    @Override
    public void onBackStackChanged() {
        if (getFragmentManager().getBackStackEntryCount() > 0) {
            curFragment = getFragmentManager().findFragmentById(R.id.fragment_holder);
            if (curFragment instanceof ListFragment)
                setTitle(((ListFragment) curFragment).getTitle());
            else if (curFragment instanceof ProgInfoFragment)
                setTitle("О программе");
            else
                setTitle("Исполнители");
        }
        else
            setTitle("Исполнители");
    }

    private class MusicIntentReceiver extends BroadcastReceiver {
        @Override public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Intent.ACTION_HEADSET_PLUG)) {
                int state = intent.getIntExtra("state", -1);
                switch (state) {
                    case 0:
                        Toast.makeText(context, "ooops",
                                Toast.LENGTH_LONG).show();
                        break;
                    case 1:
                        Toast.makeText(context, "Heeeey",
                                Toast.LENGTH_LONG).show();
                        break;
                    default:
                        Toast.makeText(context, "WTF?!",
                                Toast.LENGTH_LONG).show();
                }
            }
        }
    }
}
