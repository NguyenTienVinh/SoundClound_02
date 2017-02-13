package com.framgia.soundcloud_2.utils;

import com.framgia.soundcloud_2.BuildConfig;
import com.framgia.soundcloud_2.R;

public class Constant {
    public static final int REQUEST_EXTERNAL_STORAGE = 1;
    public static final String CHECK_MUSIC = "!= 0";
    public static final String SORT_MUSIC = " ASC";

    public static class ResourceImage {
        public static int[] IMAGE_CATEGORY = {
            R.drawable.ic_altinaterock,
            R.drawable.ic_ambient,
            R.drawable.ic_classical,
            R.drawable.ic_country,
            R.drawable.ic_dance_edm,
            R.drawable.ic_dance_hall,
            R.drawable.ic_deep_house,
            R.drawable.ic_disco,
            R.drawable.ic_drum_bass,
            R.drawable.ic_dubstep,
            R.drawable.ic_electronic,
            R.drawable.ic_folk_singer,
            R.drawable.ic_hiphop_rap,
            R.drawable.ic_house,
            R.drawable.ic_indie,
            R.drawable.ic_jazz_blues,
            R.drawable.ic_latin,
            R.drawable.ic_metal,
            R.drawable.ic_piano,
            R.drawable.ic_pop,
            R.drawable.ic_rb_soul,
            R.drawable.ic_reggae,
            R.drawable.ic_rock,
            R.drawable.ic_sound_track,
            R.drawable.ic_trance,
            R.drawable.ic_world,
            R.drawable.ic_audio_book,
            R.drawable.ic_business,
            R.drawable.ic_comedy,
            R.drawable.ic_entertainment,
            R.drawable.ic_learning,
            R.drawable.ic_new_bolic,
            R.drawable.ic_religion,
            R.drawable.ic_science,
            R.drawable.ic_sports,
            R.drawable.ic_story,
            R.drawable.ic_technology
        };
    }

    public static class KeyIntent {
        public static final String EXTRA_CATEGORY = "category";
        public static final String EXTRA_ICON_PLAY_PAUSE = "iconplay";
        public static final String EXTRA_ICON_REPEAT_NOREPEAT = "iconrepeat";
        public static final String EXTRA_ICON_SHUFFLE_NOSHUFFLE = "iconshuffle";
        public static final String EXTRA_ICON_FORWARD = "iconforward";
        public static final String EXTRA_ICON_BACKWARD = "iconbackward";
        public static final String EXTRA_DURATION = "duration";
        public static final String EXTRA_FULL_DURATION = "fullduration";
        public static final String ACTION_UPDATE_CONTROL =
            "com.soundcloud_2.action.ACTION_UPDATE_CONTROL";
        public static final String ACTION_UPDATE_CONTROL_DURATION = "ACTION_UPDATE_CONTROL_DURATION";
        public static final String ACTION_PLAY_NEW_SONG =
            "com.soundcloud_2.action.ACTION_PLAY_NEW_SONG";
        public static final String ACTION_UPDATE_AUDIO = "com.soundcloud_2.action.ACTION_UPDATE_AUDIO";
        public static final String ACTION_UPDATE_AUDIO_DURATION = "com.soundcloud_2.action.ACTION_UPDATE_AUDIO_DURATION";
        public static final String ACTION_PLAY = "com.soundcloud_2.action.ACTION_PLAY";
        public static final String ACTION_PAUSE = "com.soundcloud_2.action.ACTION_PAUSE";
        public static final String ACTION_STOP = "com.soundcloud_2.action.ACTION_STOP";
        public static final String ACTION_PREVIOUS = "com.soundcloud_2.action.ACTION_PREVIOUS";
        public static final String ACTION_NEXT = "com.soundcloud_2.action.ACTION_NEXT";
        public static final String ACTION_FORWARD = "com.soundcloud_2.action.ACTION_FORWARD";
        public static final String ACTION_BACKWARD = "com.soundcloud_2.action.ACTION_BACKWARD";
        public static final String ACTION_REPEAT = "com.soundcloud_2.action.ACTION_REPEAT";
        public static final String ACTION_NO_REPEAT = "com.soundcloud_2.action.ACTION_NO_REPEAT";
        public static final String ACTION_SHUFFLE = "com.soundcloud_2.action.ACTION_SHUFFLE";
        public static final String ACTION_NO_SHUFFLE = "com.soundcloud_2.action.ACTION_NO_SHUFFLE";
        public static final String ACTION_GET_AUDIO_STATE =
            "com.soundcloud_2.action.ACTION_GET_AUDIO_STATE";
        public static final String ACTION_UPDATE_SEEK_BAR =
            "com.soundcloud_2.action.ACTION_UPDATE_SEEK_BAR";
        public static final String ACTION_SEEK_TO = "com.soundcloud_2.action.ACTION_SEEK_TO";
    }

    public static class ConstantApi {
        public static final String PARAM_OFFSET = "offset";
        public static final String PARAM_QUERY = "q";
        public static final String PATH_SEARCH = "search/tracks";
        public static final String EXTRA_QUERY = "query";
        public static final String EXTRA_TITLE = "title";
        public static final String EXTRA_USER_NAME = "username";
        public static final String EXTRA_IMAGE_URL = "imageurl";
        public static final String URL_SOUNDCLOUD = "https://api-v2.soundcloud.com/";
        public static final String PATH_SONG = "charts";
        public static final String PARAM_CLIENT_ID = "client_id";
        public static final String PARAM_GENRE = "genre";
        public static final String PARAM_KIND = "kind";
        public static final String VALUE_KIND_TOP = "top";
        public static final String PARAM_LIMIT = "limit";
        public static final String VALUE_LIMIT = "20";
        public static final String STREAM_URL = "/stream?client_id" + "=" + BuildConfig.API_KEY;
    }

    public class RequestCode {
        public static final int REQUEST_CODE_WRITE_EXTERNAL_STORAGE = 1;
    }
}
