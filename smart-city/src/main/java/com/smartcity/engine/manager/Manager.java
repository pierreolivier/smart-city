package com.smartcity.engine.manager;

import android.content.Context;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import com.smartcity.MainActivity;

/**
 * Created by Pierre-Olivier on 12/10/2014.
 */
public class Manager {
    private static MainActivity mMainActivity;
    private static ViewManager mViewManager;
    private static LocationManager mLocationManager;
    private static NetworkManager mNetworkManager;

    public Manager() {
        super();
    }

    public static void onCreate(MainActivity mainActivity) {
        Manager.mMainActivity = mainActivity;
        Manager.mViewManager = new ViewManager();
        Manager.mLocationManager = new LocationManager();
        Manager.mNetworkManager = new NetworkManager();
    }

    public static void onDestroy() {
        Manager.mMainActivity = null;
        Manager.mViewManager = null;
        Manager.mLocationManager = null;
        Manager.mNetworkManager = null;
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

    public static NetworkManager network() {
        return Manager.mNetworkManager;
    }

    public static String getAndroidId() {
        return Settings.Secure.getString(Manager.mMainActivity.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    public static String getPhoneNumber() {
        TelephonyManager telephonyManager = (TelephonyManager) Manager.mMainActivity.getSystemService(Context.TELEPHONY_SERVICE);
        String phoneNumber = telephonyManager.getLine1Number();
        return (phoneNumber == null ? "" : phoneNumber);
    }


}
