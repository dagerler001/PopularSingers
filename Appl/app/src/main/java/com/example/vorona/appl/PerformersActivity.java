package com.example.vorona.appl;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.vorona.appl.list.FirstRecyclerAdapter;
import com.example.vorona.appl.list.PerformerSelectedListener;

import java.util.ArrayList;

/**
 * Start Activity with list of performers
 */
public class PerformersActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, PerformerSelectedListener {

    private GetInfoAsyncTask downloadTask;
    private NavigationView navigationView;

    private TextView title;
    private RecyclerView rv;
    private ProgressBar p_bar;
    private Typeface face;
    private ImageView retry;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_performers);

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
        face = Typeface.createFromAsset(title.getContext().getAssets(), "fonts/Elbing.otf");
        title.setTypeface(face);
        retry = (ImageView) findViewById(R.id.retry);

        //set initial adapter and layoutManager for recyclerView.
        RecyclerView rv = (RecyclerView) findViewById(R.id.list_perf);
        rv.setAdapter(new FirstRecyclerAdapter(new ArrayList<Singer>()));
        rv.setLayoutManager(new LinearLayoutManager(rv.getContext()));

        //check for saved AsyncTask
        if (savedInstanceState != null) {
            downloadTask = (GetInfoAsyncTask) getLastCustomNonConfigurationInstance();
            downloadTask.attachActivity(this);
            downloadTask.onPostExecute(null);
        } else {
            downloadTask = new GetInfoAsyncTask(this);
            downloadTask.execute();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        navigationView.setCheckedItem(R.id.main_activity);
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
            default: break;
        }

    }

    /**
     * Will continue current DatabaseAsyncTask on restart of activity.
     * Don't save if error occurred during download.
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
            super.onBackPressed();
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

    /**
     * Open selected activity. If selected activity and current activity are the same won't do anything.
     */
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id == R.id.main_activity) {

        } else if (id == R.id.favs) {
            Intent gr = new Intent(this, DatabaseActivity.class);
            gr.putExtra("TYPE", "Favorites");
            startActivity(gr);
            overridePendingTransition(R.anim.from_right, R.anim.to_left);
        } else if (id == R.id.recent) {
            Intent gr = new Intent(this, DatabaseActivity.class);
            gr.putExtra("TYPE", "Recent");
            startActivity(gr);
            overridePendingTransition(R.anim.from_right, R.anim.to_left);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * Open new activity with full information about selected performer.
     * @param singer selected in RecycleView singer
     */
    @Override
    public void onPerformerSelected(Singer singer) {
        Intent gr = new Intent(this, FullInfoActivity.class);
        gr.putExtra("SINGER", singer);
        startActivity(gr);
        overridePendingTransition(R.anim.from_right, R.anim.to_left);

    }

    /**
     * Restart GetInfoAsyncTask
     */
    public void onRetryClick(View view) {
        downloadTask = new GetInfoAsyncTask(this);
        downloadTask.execute();
    }
}
