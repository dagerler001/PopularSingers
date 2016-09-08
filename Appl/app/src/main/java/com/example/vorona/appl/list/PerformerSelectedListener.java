package com.example.vorona.appl.list;

import com.example.vorona.appl.model.Singer;

/**
 *  Interface for receiving events from RecycleView
 */
public interface PerformerSelectedListener {
    /**
     * Creates new activity with full information about singer
     * @param singer chosen in RecycleView singer
     */
    void onPerformerSelected(Singer singer);
}
