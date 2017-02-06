package com.framgia.soundcloud_2.localsong;

import android.support.annotation.NonNull;

import com.framgia.soundcloud_2.utils.DataRepository;

public class SongOfflinePresenter implements SongOfflineContract.Presenter {
    private SongOfflineContract.View mView;

    public SongOfflinePresenter(@NonNull SongOfflineContract.View mSongOfflineView) {
        mView = mSongOfflineView;
        mView.setPresenter(this);
    }

    @Override
    public void getSongOffline() {
        mView.showSongOffline(DataRepository.getLocalData());
    }

    @Override
    public void start() {
        mView.start();
    }
}
