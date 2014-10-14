package com.smartcity.engine.manager;

import com.smartcity.MainActivity;

/**
 * Created by Pierre-Olivier on 12/10/2014.
 */
public class Manager {
    private static MainActivity mMainActivity;
    private static ViewManager mViewManager;
    private static LocationManager mLocationManager;

    public Manager() {
        super();
    }

    public static void onCreate(MainActivity mainActivity) {
        Manager.mMainActivity = mainActivity;
        Manager.mViewManager = new ViewManager();
        Manager.mLocationManager = new LocationManager();
    }

    public static void onDestroy() {
        Manager.mMainActivity = null;
        Manager.mViewManager = null;
        Manager.mLocationManager = null;
    }

    public static void onStart() {
        Manager.mLocationManager.connect();
    }

    public static void onStop() {
        Manager.mLocationManager.disconnect();
    }

    public static MainActivity activity() {
        return Manager.mMainActivity;
    }

    public static ViewManager view() {
        return Manager.mViewManager;
    }

    public static LocationManager location() {
        return Manager.mLocationManager;
    }
}
