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
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Pierre-Olivier on 31/10/2014.
 */
public class NetworkManager {
    public static final String DEFAULT_SERVER = "http://10.0.2.2:81";


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

    public void postData() {
        Thread thread = new Thread(new Runnable(){
                public void run() {
                    /*
            // Create a new HttpClient and Post Header
                    HttpClient httpclient = new DefaultHttpClient();
                    HttpPost httppost = new HttpPost("http://192.168.26.17:81/createDec");
                    try {
                        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(5);
                        nameValuePairs.add(new BasicNameValuePair("lat", "49.50244"));
                        nameValuePairs.add(new BasicNameValuePair("type", "1"));
                        nameValuePairs.add(new BasicNameValuePair("lon", "5.94188"));
                        nameValuePairs.add(new BasicNameValuePair("texte", "Ca envoie du pâté"));
                        nameValuePairs.add(new BasicNameValuePair("adresse", "Avenue du Swing 4365 Belval, Luxembourg"));
                        httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                        HttpResponse response = httpclient.execute(httppost);
                        System.out.println(response.toString());
                    } catch (ClientProtocolException e) {
                        // TODO Auto-generated catch block
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                    }
                    */
                    HttpResponse response = null;
                    try {
                        HttpClient client = new DefaultHttpClient();
                        HttpGet request = new HttpGet();
                        request.setURI(new URI(server+"/createDec?lon=48.611776&type=1&lat=5.127115&texte=Ca_envoie_du_pate&adress=Avenue_du_Swing_4365_Belval,Luxembourg"));
                        response = client.execute(request);
                        String responseStr = EntityUtils.toString(response.getEntity());
                        Log.e("RESPONSE", responseStr);
                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                    } catch (ClientProtocolException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                   //System.out.println(response.toString());
                }

                //}



            });
        thread.start();
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
    public void sendComment2(String comment, Location location, String address, String type, final ProgressDialog p) {
        p.setIndeterminate(false);
        // "https://koush.clockworkmod.com/test/echo"
        try {
            comment = URLEncoder.encode(comment,"utf-8");
            address = URLEncoder.encode(address,"utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        Ion.with(Manager.activity())
                .load(server + "/createDec?lon="+location.getLongitude()+"&type=1&lat="+location.getLatitude()+"&texte="+comment+"&adress="+address)
                /*.uploadProgressDialog(p)
                .uploadProgressHandler(new ProgressCallback() {
                    @Override
                    public void onProgress(long uploaded, long total) {
                        // Displays the progress bar for the first time.
                        Log.e("progress", uploaded + " " + total);
                    }
                })*/
                /*.setMultipartParameter("comment", comment)
                .setMultipartParameter("latitude", "" + location.getLatitude())
                .setMultipartParameter("longitude", "" + location.getLongitude())
                .setMultipartParameter("address", address)
                .setMultipartParameter("type", type)
                .setMultipartParameter("android_id", Manager.getAndroidId())
                .setMultipartParameter("phone_number", Manager.getPhoneNumber())
                //.setMultipartFile("picture", new File(Manager.activity().getFilesDir().getPath() + "/image.jpg"))*/
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        //p.hide();

                        if (e == null) {
                            if (result.has("gid")) {
                                final String status = result.get("gid").getAsString();
                                Log.e("gid",status);
                                Ion.with(Manager.activity())
                                        .load(server + "/upload?gid=" + status)
                                        .uploadProgressDialog(p)
                                        .uploadProgressHandler(new ProgressCallback() {
                                            @Override
                                            public void onProgress(long uploaded, long total) {
                                                // Displays the progress bar for the first time.
                                                Log.e("progress", uploaded + " " + total);
                                            }
                                        })
                                        .setMultipartParameter("gid", status)
                                        .setMultipartFile("picture", new File(Manager.activity().getFilesDir().getPath() + "/image.jpg"))
                                        .asString()
                                        .setCallback(new FutureCallback<String>() {
                                            @Override
                                            public void onCompleted(Exception e, String result) {
                                                p.hide();

                                                if (result != null)
                                                    Log.e("RE", result);

                                                Manager.view().endView();
                                            }
                                        });
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
