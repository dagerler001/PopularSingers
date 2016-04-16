package com.example.vorona.appl.list;

import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.vorona.appl.R;

/**
 * Adapter for RecycleView
 */
public abstract class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.GroupsViewHolder>
        implements View.OnClickListener {

    public abstract void setPerformerSelectedListener(PerformerSelectedListener listener);

    class GroupsViewHolder extends RecyclerView.ViewHolder {
        TextView groupName, genres;
        ImageView cover;

        public GroupsViewHolder(View itemView) {
            super(itemView);

            groupName = (TextView) itemView.findViewById(R.id.name);
            Typeface face = Typeface.createFromAsset(groupName.getContext().getAssets(), "fonts/Elbing.otf");
            groupName.setTypeface(face);

            genres = (TextView) itemView.findViewById(R.id.genres);
            genres.setTypeface(face);

            cover = (ImageView) itemView.findViewById(R.id.cover);
        }
    }

    @Override
    public GroupsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item, parent, false);
        view.setOnClickListener(this);
        return new GroupsViewHolder(view);
    }


}
