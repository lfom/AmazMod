package com.edotassi.amazmod;

import android.app.Application;
import android.content.Context;
import android.content.ContextWrapper;

import com.edotassi.amazmod.setup.Setup;
import com.edotassi.amazmod.util.LocaleUtils;
import com.edotassi.amazmod.watch.Watch;
import com.pixplicity.easyprefs.library.Prefs;
import com.raizlabs.android.dbflow.config.FlowManager;

import org.tinylog.Logger;
import org.tinylog.configuration.Configuration;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.util.Locale;

import amazmod.com.transport.Constants;

public class AmazModApplication extends Application {

    public static Locale defaultLocale;
    private static boolean isWatchConnected;
    private static Timestamp timeLastSeen;
    //public static int syncInterval;

    public static long timeLastSync;
    public static long timeLastWatchfaceDataSend;

    public static int currentScreenBrightness;
    public static int currentScreenBrightnessMode;

    @Override
    public void onCreate() {
        super.onCreate();

        setupLogs();

        FlowManager.init(this);

        Watch.init(getApplicationContext());


        setWatchConnected(true);
        setupLocale();

        Setup.run(getApplicationContext());
    }

    @Override
    protected void attachBaseContext(Context base) {
        new Prefs.Builder()
                .setContext(base)
                .setMode(ContextWrapper.MODE_PRIVATE)
                .setUseDefaultSharedPreference(true)
                .build();
        super.attachBaseContext(LocaleUtils.onAttach(base));

    }

    private void setupLocale() {
        defaultLocale = LocaleUtils.getLocale();
    }

    public static void setWatchConnected(boolean connected){
        isWatchConnected = connected;
        if (connected){
            timeLastSeen = new Timestamp(System.currentTimeMillis());
        }
        Logger.debug("AmazModApplication setWatchConnected - connected: " + connected + " | last seen: " + getTimeLastSeen());
    }

    public static boolean isWatchConnected(){
        return isWatchConnected;
    }

    private void setupLogs(){
        Configuration.set("writerLogcat","logcat");
        if (Prefs.getBoolean(Constants.PREF_LOG_TO_FILE,Constants.PREF_LOG_TO_FILE_DEFAULT)) {
            String level = Prefs.getString(Constants.PREF_LOG_TO_FILE_LEVEL,Constants.PREF_LOG_TO_FILE_LEVEL_DEFAULT).toLowerCase();
            Configuration.set("writerFile", "file");
            Configuration.set("writerFile.file", Constants.LOGFILE);
            Configuration.set("writerFile.level", level);
            Logger.info("Logging to {} using the level {}", Constants.LOGFILE, level);
        }else{
            Logger.info("Logging to LOGCAT only");
        }
    }

    public static String getTimeLastSeen(){
        DateFormat localizedDateFormat = DateFormat.getDateTimeInstance(DateFormat.SHORT,DateFormat.SHORT);
        return localizedDateFormat.format(timeLastSeen);
    }
}
