package com.smartcity.engine.manager;

import android.app.ProgressDialog;
import android.location.Location;
import android.util.Log;
import android.widget.ProgressBar;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.ProgressCallback;

import java.io.File;

/**
 * Created by Pierre-Olivier on 31/10/2014.
 */
public class NetworkManager {
    private String server;

    public NetworkManager() {
        super();
    }

    public void setServer(String server) {
        this.server = server;
    }

    public String getServer() {
        return server;
    }

    public void sendComment(String comment, Location location, String address, String type, final ProgressDialog p) {
        p.setIndeterminate(false);
        // "https://koush.clockworkmod.com/test/echo"
        Ion.with(Manager.activity())
            .load(server + "/report/add")
            .uploadProgressDialog(p)
            .uploadProgressHandler(new ProgressCallback() {
                @Override
                public void onProgress(long uploaded, long total) {
                    // Displays the progress bar for the first time.
                    Log.e("progress", uploaded + " " + total);
                }
            })
            .setMultipartParameter("comment", comment)
            .setMultipartParameter("latitude", "" + location.getLatitude())
            .setMultipartParameter("longitude", "" + location.getLongitude())
            .setMultipartParameter("address", address)
            .setMultipartParameter("type", type)
            .setMultipartParameter("android_id", Manager.getAndroidId())
            .setMultipartParameter("phone_number", Manager.getPhoneNumber())
            .setMultipartFile("picture", new File(Manager.activity().getFilesDir().getPath() + "/image.jpg"))
            .asString()
            .setCallback(new FutureCallback<String>() {
                @Override
                public void onCompleted(Exception e, String result) {
                    p.hide();

                    if (e != null) {
                        e.printStackTrace();
                    } else {
                        Log.e("result", result);
                    }
                }
            });
    }
}
