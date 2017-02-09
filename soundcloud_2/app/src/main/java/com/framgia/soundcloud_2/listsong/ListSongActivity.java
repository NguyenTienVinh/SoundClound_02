package com.framgia.soundcloud_2.listsong;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.framgia.soundcloud_2.R;
import com.framgia.soundcloud_2.adapter.SongOnlineAdapter;
import com.framgia.soundcloud_2.data.SongRepository;
import com.framgia.soundcloud_2.data.model.Category;
import com.framgia.soundcloud_2.data.model.Track;
import com.framgia.soundcloud_2.service.SongDownloadManager;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.framgia.soundcloud_2.utils.Constant.ConstantApi.EXTRA_QUERY;
import static com.framgia.soundcloud_2.utils.Constant.KeyIntent.EXTRA_CATEGORY;
import static com.framgia.soundcloud_2.utils.Constant.RequestCode.REQUEST_CODE_WRITE_EXTERNAL_STORAGE;

public class ListSongActivity extends AppCompatActivity implements ListSongContract.View,
    SongOnlineAdapter.ItemClickListener {
    @BindView(R.id.recycle_listsong)
    RecyclerView mRecyclerView;
    private Category mCategory;
    private ListSongContract.Presenter mPresenter;
    private SongOnlineAdapter mSongOnlineAdapter;
    private List<Track> mTracks = new ArrayList<>();
    private SongDownloadManager mDownloadSong;
    private String mQuery;

    public static Intent getListSongItent(Context context, Category category) {
        Intent intent = new Intent(context, ListSongActivity.class);
        intent.putExtra(EXTRA_CATEGORY, category);
        return intent;
    }

    public static Intent getSongFromSearch(Context context, String query) {
        Intent intent = new Intent(context, ListSongActivity.class);
        intent.putExtra(EXTRA_QUERY, query);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_song);
        setPresenter(new ListSongPresenter(this, SongRepository.getInstance(this)));
        ButterKnife.bind(this);
        mPresenter.start();
    }

    private void getIntentData() {
        mCategory = getIntent().getParcelableExtra(EXTRA_CATEGORY);
        mQuery = getIntent().getStringExtra(EXTRA_QUERY);
    }

    public void setupToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setTitle(mCategory != null ? mCategory.getCategoryTitle() : mQuery);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void initView() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setHasFixedSize(true);
        mSongOnlineAdapter = new SongOnlineAdapter(this, mTracks, this);
        mRecyclerView.setAdapter(mSongOnlineAdapter);
    }

    @Override
    public void showSong(List<Track> list) {
        if (list == null) return;
        mTracks.addAll(list);
        mSongOnlineAdapter.notifyDataSetChanged();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[],
                                           int[] grantResults) {
        if (requestCode != REQUEST_CODE_WRITE_EXTERNAL_STORAGE) return;
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (mDownloadSong == null) return;
            mDownloadSong.startDownload();
        } else Toast.makeText(this, R.string.message_access_denied,
            Toast.LENGTH_LONG).show();
    }

    @Override
    public void showError() {
        Toast.makeText(this, R.string.error_get_song_fail, Toast.LENGTH_LONG).show();
    }

    @Override
    public void checkPermissionDownload() {
        int result =
            ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED)
            mDownloadSong.startDownload();
        else ActivityCompat.requestPermissions(this, new String[]{Manifest.permission
            .WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_WRITE_EXTERNAL_STORAGE);
    }

    @Override
    public void setPresenter(ListSongContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void start() {
        initView();
        getIntentData();
        setupToolbar();
        mPresenter.getSong(mCategory, mQuery);
    }

    @Override
    public void onClick(int position) {
        // TODO : item click
    }

    @Override
    public void onDownloadListener(Track track) {
        if (track == null) return;
        mDownloadSong = new SongDownloadManager(this, track.getTitle(), track.getFullUri());
        checkPermissionDownload();
    }
}
