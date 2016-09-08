package com.example.vorona.appl.list;

import android.content.Context;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.example.vorona.appl.R;
import com.example.vorona.appl.model.Singer;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Adapter for given list of performers
 */
public class FirstRecyclerAdapter extends RecyclerAdapter implements Animation.AnimationListener{

    protected PerformerSelectedListener performerSelectedListener;

    /**
     * Will show list of performers in RecycleView
     */
    protected List<Singer> singers;

    /**
     * Animation on click on performer's card
     */
    private Animation open;

    /**
     * Creates an instance of FirstRecyclerAdapter on specified list
     * @param s list of perfomers to show in RecycleView
     */
    public FirstRecyclerAdapter(List<Singer> s) {
        singers = s;
    }

    /**
     * Sets specified listener for RecyclerView
     * @param listener given listener
     */
    public void setPerformerSelectedListener (PerformerSelectedListener listener) {
        performerSelectedListener = listener;
    }

    /**
     * Fills single card with performer's information(name, genres, cover)
     * @param holder holder for card(view)
     * @param position position of performer in given list
     */
    @Override
    public void onBindViewHolder(GroupsViewHolder holder, int position) {
        open = AnimationUtils.loadAnimation(holder.groupName.getContext(),
                R.anim.openning);
        open.setAnimationListener(this);
        holder.groupName.setText(singers.get(position).getName());
        holder.genres.setText(singers.get(position).getGenres());
        Context context = holder.cover.getContext();

        Picasso.with(context).load(singers.get(position).getCover_small()).into(holder.cover);
        holder.itemView.setTag(R.id.tag, singers.get(position));
    }

    /**
     * Returns number of performers in list
     * @return number of performers in list
     */
    @Override
    public int getItemCount() {
        if (singers == null) return 0;
        return singers.size();
    }

    /**
     * Calls listener's method handling clicks on views
     * @param v view on which clicked
     */
    @Override
    public void onClick(View v) {
        Singer singer = (Singer) v.getTag(R.id.tag);
        v.startAnimation(open);
        if (performerSelectedListener != null) {
            performerSelectedListener.onPerformerSelected(singer);
        }
    }

    @Override
    public void onAnimationStart(Animation animation) {

    }

    @Override
    public void onAnimationEnd(Animation animation) {

    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }

}
