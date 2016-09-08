package com.example.vorona.appl.ui.fragments;

import android.app.Fragment;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.vorona.appl.R;
import com.example.vorona.appl.db.DbBackend;
import com.example.vorona.appl.db.DbContract;
import com.example.vorona.appl.model.Singer;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class FullInfoFragment extends Fragment implements DbContract {

    protected DbBackend dbBackend;
    @BindView(R.id.title)
    TextView txt;
    @BindView(R.id.bio)
    TextView bio;
    @BindView(R.id.tracks)
    TextView tracks;
    @BindView(R.id.cover_big)
    ImageView cover;
    @BindView(R.id.background)
    ImageView back;
    private Singer singer;
    private Unbinder viewBinder;

    public static FullInfoFragment newInstance(Singer singer) {
        Bundle args = new Bundle();
        args.putParcelable("Singer", singer);
        FullInfoFragment fragment = new FullInfoFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getActivity().setTitle("Исполнители");
        View view = inflater.inflate(R.layout.fragment_info, container, false);
        viewBinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        singer = getArguments().getParcelable("Singer");
        setHasOptionsMenu(true);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        dbBackend = new DbBackend(getActivity());
        // Add in "Recent" table
        dbBackend.insertSinger(singer, RECENT);

        final boolean inFavs;
        final FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
        if (dbBackend.getSinger(singer.getId(), FAVOURITES) != null) {
            fab.setImageResource(R.drawable.added);
            inFavs = true;
        } else {
            fab.setImageResource(R.drawable.star);
            inFavs = false;
        }

        //If selected performer already presents in favourite list delete it, add otherwise
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (inFavs) {
                    Snackbar.make(view, getString(R.string.deleted), Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    fab.setImageResource(R.drawable.star);
                    dbBackend.deleteSingerFromFavourites(singer);
                } else {
                    Snackbar.make(view, getString(R.string.added), Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    fab.setImageResource(R.drawable.added);
                    dbBackend.insertSinger(singer, FAVOURITES);
                }
            }
        });


        Typeface face = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Elbing.otf");

        txt.setText(singer.getName());
        txt.setTypeface(face);

        bio.setMovementMethod(new ScrollingMovementMethod());
        bio.setText(singer.getName() + " - " + singer.getBio());
        bio.setTypeface(face);

        tracks.setTypeface(face);
        tracks.setText("Альбомов " + singer.getAlbums() + ", треков " + singer.getTracks());

        Context context = cover.getContext();
        Picasso.with(context).load(singer.getCover_big()).into(cover);
        Picasso.with(context).load(singer.getCover_big()).into(back);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        viewBinder.unbind();
    }
}
