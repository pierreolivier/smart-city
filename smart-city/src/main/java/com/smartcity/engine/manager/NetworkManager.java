package com.smartcity.engine.manager;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
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
    public static final String DEFAULT_SERVER = "http://192.168.1.152:5001";

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

    public void saveServer(String server) {
        SharedPreferences sharedPref = Manager.activity().getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("server", server);
        editor.commit();
    }

    public String loadServer() {
        SharedPreferences sharedPref = Manager.activity().getPreferences(Context.MODE_PRIVATE);
        return sharedPref.getString("server", NetworkManager.DEFAULT_SERVER);
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
            .asJsonObject()
            .setCallback(new FutureCallback<JsonObject>() {
                @Override
                public void onCompleted(Exception e, JsonObject result) {
                    p.hide();

                    if (e == null) {
                        if (result.has("status")) {
                            String status = result.get("status").getAsString();
                            if (status.equals("sent")) {
                                Manager.view().endView();
                            } else if (status.equals("banned")) {
                                Manager.view().showBannedUser();
                            } else {
                                Manager.view().showServerError();
                            }
                        }
                    } else {
                        Manager.view().showServerError();

                        e.printStackTrace();
                    }
                }
            });

             /*
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
            });*/
    }
}
