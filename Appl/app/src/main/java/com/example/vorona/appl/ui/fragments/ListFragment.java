package com.example.vorona.appl.ui.fragments;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.LoaderManager;
import android.content.Loader;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.vorona.appl.R;
import com.example.vorona.appl.list.FirstRecyclerAdapter;
import com.example.vorona.appl.list.PerformerSelectedListener;
import com.example.vorona.appl.loaders.DatabaseLoader;
import com.example.vorona.appl.loaders.DownloadState;
import com.example.vorona.appl.loaders.JsonLoader;
import com.example.vorona.appl.model.Singer;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class ListFragment extends Fragment implements PerformerSelectedListener,
        LoaderManager.LoaderCallbacks<List<Singer>> {

    @BindView(R.id.list_d)
    RecyclerView rv;
    @BindView(R.id.progress_d)
    ProgressBar progressBar;
    @BindView(R.id.no_d)
    TextView title;

    private Unbinder viewBinder;

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getActivity().setTitle(type);
        View rootView = inflater.inflate(R.layout.fragment_list, container, false);
        viewBinder = ButterKnife.bind(this, rootView);
        Typeface face = Typeface.createFromAsset(title.getContext().getAssets(), "fonts/Elbing.otf");
        title.setTypeface(face);
        rv.setAdapter(new FirstRecyclerAdapter(null));
        setRecyclerViewLayoutManager(savedInstanceState);
        return rootView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        type = getArguments().getString("Type");
        setHasOptionsMenu(true);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
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
                progressBar.setVisibility(View.VISIBLE);
                rv.setVisibility(View.INVISIBLE);
                title.setVisibility(View.INVISIBLE);
                break;
            case DONE:
                progressBar.setVisibility(View.INVISIBLE);
                rv.setVisibility(View.VISIBLE);
                title.setVisibility(View.INVISIBLE);
                break;
            case ERROR:
                progressBar.setVisibility(View.INVISIBLE);
                rv.setVisibility(View.INVISIBLE);
                title.setVisibility(View.VISIBLE);
                title.setText(R.string.txt_error);
                break;
            case EMPTY:
                progressBar.setVisibility(View.INVISIBLE);
                rv.setVisibility(View.INVISIBLE);
                title.setVisibility(View.VISIBLE);
                title.setText(R.string.txt_empty);
                if (type.equals("Performers")) {
                    getLoaderManager().restartLoader(0, null, this);
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        viewBinder.unbind();
    }


    public void setRecyclerViewLayoutManager(Bundle savedInstanceState) {
        int cnt = (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE ? 3 : 2);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getActivity(), cnt);
        rv.setLayoutManager(layoutManager);
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
        mAdapter.setPerformerSelectedListener(this);
        rv.setAdapter(mAdapter);
    }

    @Override
    public void onLoaderReset(Loader<List<Singer>> loader) {
    }
}