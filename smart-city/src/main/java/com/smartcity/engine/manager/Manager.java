package com.smartcity.engine.manager;

import com.smartcity.MainActivity;

/**
 * Created by Pierre-Olivier on 12/10/2014.
 */
public class Manager {
    private static MainActivity mMainActivity;
    private static ViewManager mViewManager;

    public Manager() {
        super();
    }

    public static void onCreate(MainActivity mainActivity) {
        Manager.mMainActivity = mainActivity;
        Manager.mViewManager = new ViewManager();
    }

    public static void onDestroy() {
        Manager.mMainActivity = null;
        Manager.mViewManager = null;
    }

    public static MainActivity activity() {
        return mMainActivity;
    }

    public static ViewManager view() {
        return mViewManager;
    }
}
