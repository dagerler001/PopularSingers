package com.example.vorona.appl;

public enum DownloadState {
    DOWNLOADING(R.string.downloading),
    DONE(R.string.done),
    ERROR(R.string.error),
    EMPTY(R.string.empty);

    final int titleResId;

    DownloadState(int titleResId) {
        this.titleResId = titleResId;
    }
}
