package com.example.vorona.appl;

import android.content.Intent;
import android.content.res.Configuration;
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

import com.example.vorona.appl.list.FirstRecyclerAdapter;
import com.example.vorona.appl.list.PerformerSelectedListener;

import java.util.ArrayList;

/**
 * Activity for representing lists of recent and favourites performers.
 */
public class DatabaseActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,
        PerformerSelectedListener {

    private DatabaseAsyncTask databaseAsyncTask;
    private NavigationView navigationView;
    String type = "";

    //flag for current orientation
    public boolean horizontal = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_database);

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
            horizontal = true;

        //Determinates if new activity should represent Recent or Favorite performers
        Intent i = getIntent();
        type = i.getStringExtra("TYPE");

        //Customizing toolbar and navigationView
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_d);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_d);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view_d);
        RecyclerView rv = (RecyclerView) findViewById(R.id.list_d);
        if (type.equals("Recent")) {
            setTitle("Recent");
            navigationView.setCheckedItem(R.id.recent);
        } else {
            setTitle("Favorites");
            navigationView.setCheckedItem(R.id.favs);
        }
        navigationView.setNavigationItemSelectedListener(this);

        //setting initial adapter and layoutManager for recyclerView.
        rv.setAdapter(new FirstRecyclerAdapter(new ArrayList<Singer>()));
        rv.setLayoutManager(new LinearLayoutManager(rv.getContext()));

        //checking for saved AsyncTask
        if (savedInstanceState != null) {
            databaseAsyncTask = (DatabaseAsyncTask) getLastCustomNonConfigurationInstance();
            databaseAsyncTask.attachActivity(this);
            databaseAsyncTask.onPostExecute(null);
        } else {
            if (type.equals("Recent")) {
                databaseAsyncTask = new DatabaseAsyncTask(this);
                databaseAsyncTask.execute("Recent");
            } else {
                databaseAsyncTask = new DatabaseAsyncTask(this);
                databaseAsyncTask.execute("Favorites");
            }
        }
    }

    /**
     * Show loading bar while getting data from database.
     * Hide when download is finished. If an error occurred during download textView is shown.
     * Otherwise recyclerView is shown.
     *
     * @param task DatabaseAsyncTask which is working at the moment
     */
    protected void updateView(DatabaseAsyncTask task) {
        //TODO
    }

    /**
     * Will continue current DatabaseAsyncTask on restart of activity.
     * @return current DatabaseAsyncTask
     * TODO нужно ли это?
     */
    @Override
    public Object onRetainCustomNonConfigurationInstance() {
        return databaseAsyncTask;
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
     * Kill current activity when pressed back button
     */
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_d);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
        finish();
        overridePendingTransition(R.anim.from_left, R.anim.to_right);
    }

    /**
     * Open selected activity. If selected activity and current activity are the same won't do anything.
     */
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (type.equals("Recent"))
            navigationView.setCheckedItem(R.id.recent);
        else
            navigationView.setCheckedItem(R.id.favs);

        if (id == R.id.main_activity) {
            Intent gr = new Intent(this, PerformersActivity.class);
            startActivity(gr);
            overridePendingTransition(R.anim.from_right, R.anim.to_left);
        } else if (id == R.id.favs && type.equals("Recent")) {
            Intent gr = new Intent(this, DatabaseActivity.class);
            gr.putExtra("TYPE", "Favorites");
            startActivity(gr);
            overridePendingTransition(R.anim.from_right, R.anim.to_left);
        } else if (id == R.id.recent && !type.equals("Recent")) {
            Intent gr = new Intent(this, DatabaseActivity.class);
            gr.putExtra("TYPE", "Recent");
            startActivity(gr);
            overridePendingTransition(R.anim.from_right, R.anim.to_left);
        }

        //close navigation menu
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_d);
        drawer.closeDrawer(GravityCompat.START);
        finish();
        return true;
    }

    /**
     * Inflate the menu, adds items to the action bar if it is present.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
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
}
