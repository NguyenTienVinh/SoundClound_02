package com.framgia.soundcloud_2.utils;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import com.framgia.soundcloud_2.data.model.Track;

import java.util.ArrayList;
import java.util.List;

public class DataRepository {
    public static List getLocalData() {
        ContentResolver contentResolver = MyApplication.getAppContext().getContentResolver();
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String check = MediaStore.Audio.Media.IS_MUSIC + Constant.CHECK_MUSIC;
        String sort = MediaStore.Audio.Media.TITLE + Constant.SORT_MUSIC;
        Cursor cursor = contentResolver.query(uri, null, check, null, sort);
        ArrayList<Track> trackArrayList = new ArrayList<>();
        while (cursor.moveToNext()) {
            Track track = new Track();
            track.setTitle(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE)));
            track.setUri(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA)));
            track
                .setDuration(cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION)));
            trackArrayList.add(track);
        }
        cursor.close();
        return trackArrayList;
    }
}
