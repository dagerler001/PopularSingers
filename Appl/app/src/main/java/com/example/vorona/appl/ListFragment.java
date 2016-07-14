package com.example.vorona.appl;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.vorona.appl.list.FirstRecyclerAdapter;
import com.example.vorona.appl.list.PerformerSelectedListener;

import java.util.ArrayList;

public class ListFragment extends Fragment implements PerformerSelectedListener{

    private RecyclerView rv;
    private ProgressBar p_bar;
    private TextView title;
    private String type = "";

    public static ListFragment newInstance(String t) {
        Bundle args = new Bundle();
        args.putString("Type", t);
        ListFragment fragment = new ListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public String getTitle(){
        return type;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getActivity().setTitle(type);
        return inflater.inflate(R.layout.fragment_list, null);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        type = getArguments().getString("Type");
        setHasOptionsMenu(true);
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle(type);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        title = (TextView) view.findViewById(R.id.no_d);
        rv = (RecyclerView) view.findViewById(R.id.list_d);
        p_bar = (ProgressBar) view.findViewById(R.id.progress_d);
        Typeface face = Typeface.createFromAsset(title.getContext().getAssets(), "fonts/Elbing.otf");
        title.setTypeface(face);

        //set initial adapter and layoutManager for recyclerView.
        rv.setAdapter(new FirstRecyclerAdapter(new ArrayList<Singer>()));
        rv.setLayoutManager(new LinearLayoutManager(rv.getContext()));

        DatabaseAsyncTask databaseAsyncTask;
        if (type.equals(getString(R.string.recent_table))) {
            databaseAsyncTask = new DatabaseAsyncTask(this);
            databaseAsyncTask.execute(getString(R.string.recent_table));
        } else {
            databaseAsyncTask = new DatabaseAsyncTask(this);
            databaseAsyncTask.execute(getString(R.string.fav_table));
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
        switch (task.getState()) {
            case DOWNLOADING:
                p_bar.setVisibility(View.VISIBLE);
                rv.setVisibility(View.INVISIBLE);
                title.setVisibility(View.INVISIBLE);
                break;
            case DONE:
                p_bar.setVisibility(View.INVISIBLE);
                rv.setVisibility(View.VISIBLE);
                title.setVisibility(View.INVISIBLE);
                break;
            case ERROR:
                p_bar.setVisibility(View.INVISIBLE);
                rv.setVisibility(View.INVISIBLE);
                title.setVisibility(View.VISIBLE);
                title.setText(R.string.txt_error);
                break;
            case EMPTY:
                p_bar.setVisibility(View.INVISIBLE);
                rv.setVisibility(View.INVISIBLE);
                title.setVisibility(View.VISIBLE);
                title.setText(R.string.txt_empty);
                break;
            default: break;
        }
    }

    /**
     * Open new activity with full information about selected performer.
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
}
