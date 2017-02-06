package com.framgia.soundcloud_2.utils;

import android.app.Application;
import android.content.Context;

/**
 * Created by Vinh on 06/02/2017.
 */
public class MyApplication extends Application {
    private static Context context;

    public void onCreate() {
        super.onCreate();
        MyApplication.context = getApplicationContext();
    }

    public static Context getAppContext() {
        return MyApplication.context;
    }
}
