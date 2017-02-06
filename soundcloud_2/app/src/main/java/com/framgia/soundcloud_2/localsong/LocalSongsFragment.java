package com.framgia.soundcloud_2.localsong;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.framgia.soundcloud_2.R;
import com.framgia.soundcloud_2.adapter.SongsOfflineAdapter;
import com.framgia.soundcloud_2.data.model.Track;
import com.framgia.soundcloud_2.utils.Constant;
import com.framgia.soundcloud_2.utils.DatabaseManager;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class LocalSongsFragment extends Fragment
    implements SongOfflineContract.View, SwipeRefreshLayout
    .OnRefreshListener,
    SongsOfflineAdapter.ItemClickListener {
    @BindView(R.id.recycle_local)
    RecyclerView mRecyclerView;
    @BindView(R.id.swipe_refresh)
    SwipeRefreshLayout mSwifeToRefresh;
    private SongsOfflineAdapter mSongOfflineAdapter;
    private SongOfflineContract.Presenter mSongOfflinePresenter;
    private List<Track> mTracks = new ArrayList<>();

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_local_songs, container, false);
        setPresenter(new SongOfflinePresenter(this));
        ButterKnife.bind(this, view);
        mSongOfflinePresenter.start();
        verifyPermissions();
        return view;
    }

    @Override
    public void onRefresh() {
        mSwifeToRefresh.setRefreshing(false);
        verifyPermissions();
    }

    @Override
    public void showSongOffline(List<Track> list) {
        if (list == null) return;
        mTracks.clear();
        mTracks.addAll(list);
        mSongOfflineAdapter.notifyDataSetChanged();
    }

    @Override
    public void setPresenter(SongOfflineContract.Presenter presenter) {
        mSongOfflinePresenter = presenter;
    }

    @Override
    public void start() {
        mSwifeToRefresh.setOnRefreshListener(this);
        mSongOfflineAdapter = new SongsOfflineAdapter(getActivity(), mTracks, this);
        mRecyclerView.setHasFixedSize(true);
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(mSongOfflineAdapter);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[],
                                           int[] grantResults) {
        if (requestCode != Constant.REQUEST_EXTERNAL_STORAGE) return;
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            mSongOfflinePresenter.getSongOffline();
        else Toast.makeText(getActivity(), R.string.access_denied,
            Toast.LENGTH_LONG).show();
    }

    private void verifyPermissions() {
        int result = ContextCompat.checkSelfPermission(getActivity(),
            WRITE_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED) {
            mSongOfflinePresenter.getSongOffline();
        } else {
            requestPermissions(new String[]{
                WRITE_EXTERNAL_STORAGE}, Constant.REQUEST_EXTERNAL_STORAGE);
        }
    }

    private void playMusic(int audioIndex) {
    }

    @Override
    public void onClick(View view, int position) {
        playMusic(position);
    }
}