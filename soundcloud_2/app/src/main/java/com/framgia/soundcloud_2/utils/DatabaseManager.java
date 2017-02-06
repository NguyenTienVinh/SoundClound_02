package com.framgia.soundcloud_2.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import com.framgia.soundcloud_2.data.model.Track;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tri on 02/02/2017.
 */
public class DatabaseManager extends SQLiteOpenHelper {
    public static final String TABLE_TRACK = "tbl_track";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_ARTWORK_URL = "artwork_url";
    public static final String COLUMN_DOWNLOADABLE = "downloadable";
    public static final String COLUMN_DURATION = "duration";
    public static final String COLUMN_URI = "uri";
    public static final String COLUMN_PLAYBACK_COUNT = "playback_count";
    public static final String COLUMN_USER = "user";
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "SoundCloud";
    private static final int TRUE = 1;
    private static final int FALSE = 0;
    private SQLiteDatabase mDatabase;

    public DatabaseManager(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE " + TABLE_TRACK + "("
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,"
            + COLUMN_TITLE + " TEXT,"
            + COLUMN_ARTWORK_URL + " TEXT,"
            + COLUMN_DOWNLOADABLE + " INTEGER,"
            + COLUMN_DURATION + " INTEGER,"
            + COLUMN_URI + " TEXT,"
            + COLUMN_PLAYBACK_COUNT + " REAL,"
            + COLUMN_USER + " TEXT)"
        );
    }

    public void open() {
        mDatabase = this.getWritableDatabase();
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_TRACK);
        onCreate(sqLiteDatabase);
    }

    public void addListTrack(List<Track> list) {
        if (list == null) return;
        open();
        try {
            for (Track track : list) {
                if (track.getUser().getUserName() == null) return;
                ContentValues values = new ContentValues();
                values.put(COLUMN_TITLE, track.getTitle());
                values.put(COLUMN_ARTWORK_URL, track.getArtworkUrl());
                values.put(COLUMN_DOWNLOADABLE, track.isDownloadAble() ? TRUE : FALSE);
                values.put(COLUMN_DURATION, track.getDuration());
                values.put(COLUMN_URI, track.getUri());
                values.put(COLUMN_USER, track.getUser().getUserName());
                values.put(COLUMN_PLAYBACK_COUNT, track.getPlaybackCount());
                mDatabase.insertOrThrow(TABLE_TRACK, null, values);
            }
        } catch (SQLiteException e) {
            e.printStackTrace();
        } finally {
            close();
        }
    }

    public List<Track> getListTrack() {
        List<Track> list = new ArrayList<>();
        open();
        try {
            Cursor cursor = mDatabase.query(TABLE_TRACK, null, null, null, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                while (!cursor.isAfterLast()) {
                    list.add(new Track(cursor));
                    cursor.moveToNext();
                }
            }
        } catch (SQLiteException ex) {
        } finally {
            mDatabase.close();
        }
        return list;
    }

    public void clearListTrack() {
        open();
        mDatabase.delete(TABLE_TRACK, null, null);
        mDatabase.close();
    }
}
