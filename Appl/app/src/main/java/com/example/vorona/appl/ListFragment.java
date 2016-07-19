package com.example.vorona.appl;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
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
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.vorona.appl.list.FirstRecyclerAdapter;
import com.example.vorona.appl.list.PerformerSelectedListener;
import com.example.vorona.appl.list.RecyclerAdapter;

import java.util.ArrayList;
import java.util.List;

public class ListFragment extends Fragment implements PerformerSelectedListener,
        LoaderManager.LoaderCallbacks<List<Singer>> {

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

    public String getTitle() {
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

        Bundle arg = new Bundle();
        arg.putString("Table", type);
        getLoaderManager().initLoader(0, arg, this);
    }

    /**
     * Show loading bar while getting data from database.
     * Hide when download is finished. If an error occurred during download textView is shown.
     * Otherwise recyclerView is shown.
     *
     * @param state DatabaseAsyncTask which is working at the moment
     */
    protected void updateView(DownloadState state) {
        switch (state) {
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
                if (type.equals("Performers")) {
                    getLoaderManager().initLoader(0, null, this);
                }
                break;
            default:
                break;
        }
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
    public Loader<List<Singer>> onCreateLoader(int id, Bundle args) {
        if (args != null) {
            return new DatabaseLoader(getActivity(), args);
        }
        return new JsonLoader(getActivity());
    }

    @Override
    public void onLoadFinished(Loader<List<Singer>> loader, List<Singer> data) {
        if (data == null || data.size() == 0)
            if (loader instanceof JsonLoader)
                updateView(DownloadState.ERROR);
            else updateView(DownloadState.EMPTY);
        else
            updateView(DownloadState.DONE);
        FirstRecyclerAdapter mAdapter = new FirstRecyclerAdapter(data);
        setListener(rv, mAdapter);
    }

    private void setListener(RecyclerView rv, RecyclerAdapter adapter) {
        rv.setHasFixedSize(true);
        int cnt = (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE ? 3 : 2);
        StaggeredGridLayoutManager mLayoutManager = new StaggeredGridLayoutManager(cnt, StaggeredGridLayoutManager.VERTICAL);
        rv.setLayoutManager(mLayoutManager);
        adapter.setPerformerSelectedListener(this);
        rv.setAdapter(adapter);
    }

    @Override
    public void onLoaderReset(Loader<List<Singer>> loader) {
        rv.setAdapter(new FirstRecyclerAdapter(null));
    }
}