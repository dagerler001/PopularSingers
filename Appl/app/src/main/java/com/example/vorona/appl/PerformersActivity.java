package com.example.vorona.appl;

import android.content.Intent;
import android.content.res.Configuration;
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

    //flag for current orientation
    public boolean horizontal = false;

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

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
            horizontal = true;

        //Customizing toolbar and navigationView
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

        RecyclerView rv = (RecyclerView) findViewById(R.id.list_perf);
        rv.setAdapter(new FirstRecyclerAdapter(new ArrayList<Singer>()));
        rv.setLayoutManager(new LinearLayoutManager(rv.getContext()));

        if (savedInstanceState != null) {
            downloadTask = (GetInfoAsyncTask) getLastCustomNonConfigurationInstance();
            downloadTask.attachActivity(this);
            downloadTask.onPostExecute(null);
        } else {
            downloadTask = new GetInfoAsyncTask(this);
            downloadTask.execute();
        }
    }

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

    @Override
    public Object onRetainCustomNonConfigurationInstance() {
        if (downloadTask.getState() == DownloadState.ERROR)
            return null;
        return downloadTask;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.performers, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        navigationView.setCheckedItem(R.id.main_activity);
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

    @Override
    public void onPerformerSelected(Singer singer) {
        Intent gr = new Intent(this, FullInfoActivity.class);
        gr.putExtra("SINGER", singer);
        startActivity(gr);
        overridePendingTransition(R.anim.from_right, R.anim.to_left);

    }

    public void onRetryClick(View view) {
//        downloadTask = new GetInfoAsyncTask(this);
//        downloadTask.execute(); TODO
//        recreate();
//        Intent intent = getIntent();
//        finish();
//        startActivity(intent);
    }
}
