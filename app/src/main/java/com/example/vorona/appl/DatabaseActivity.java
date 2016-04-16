package com.example.vorona.appl;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.example.vorona.appl.list.PerformerSelectedListener;

public class DatabaseActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,
        PerformerSelectedListener {
    private DatabaseAsyncTask databaseAsyncTask;
    String type = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_database);

        Intent i = getIntent();
        type = i.getStringExtra("TYPE");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_d);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_d);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view_d);
        if (type.equals("Recent")) {
            setTitle("Recent");
            navigationView.setCheckedItem(R.id.recent);
        }  else {
            setTitle("Favorites");
            navigationView.setCheckedItem(R.id.favs);
        }
        navigationView.setNavigationItemSelectedListener(this);

        if (savedInstanceState != null) {
            databaseAsyncTask = (DatabaseAsyncTask) getLastCustomNonConfigurationInstance();
            databaseAsyncTask.attachActivity(this);
            databaseAsyncTask.onPostExecute(null);
        } else {
            if (type.equals("Recent")) {
                databaseAsyncTask = new DatabaseAsyncTask(this);
                databaseAsyncTask.execute("Recent");
            }  else {
                databaseAsyncTask = new DatabaseAsyncTask(this);
                databaseAsyncTask.execute("Favorites");
            }
        }
    }

    @Override
    public Object onRetainCustomNonConfigurationInstance() {
        return databaseAsyncTask;
    }

    @Override
    public void onPerformerSelected(Singer singer) {
        Intent gr = new Intent(this, FullInfoActivity.class);
        gr.putExtra("SINGER", singer);
        startActivity(gr);
        overridePendingTransition(R.anim.from_right, R.anim.to_left);
    }

    /**
     * Kills current activity when pressed back button
     */
    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.from_left, R.anim.to_right);
    }


    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

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
        } else if (id == R.id.search) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_d);
        drawer.closeDrawer(GravityCompat.START);
        return true;
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
}
