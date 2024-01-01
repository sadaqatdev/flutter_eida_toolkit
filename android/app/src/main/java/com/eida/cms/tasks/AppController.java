package com.eida.cms.tasks;


import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.preference.PreferenceManager;

import io.flutter.app.FlutterApplication;

public class AppController extends FlutterApplication {

    public static String VG_URL = "";

    public static boolean isReading = false;
    public static boolean IN_PROCESS = true;
    public static boolean isInitialized = false;

    public static String path;
    private static Context context;

    public static Context getContext() {
        return context.getApplicationContext();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences
                (getApplicationContext());

        String url = sharedPreferences.getString("VG_URL", "https://connect.tawzea.ae/ValidationGateway/Service");

        VG_URL = url.trim();
        IN_PROCESS = sharedPreferences.getBoolean("IN_PROCESS", true);
        Logger.d("VG_URL__" + VG_URL);
        path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/EIDAToolkit/";

        context = this;
    }
}