package com.smartcity;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.location.LocationManager;
import android.os.Build;
import android.os.Handler;
import android.provider.Settings;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.ImageView;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.smartcity.engine.manager.Manager;
import com.smartcity.engine.view.CameraPreview;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;


public class MainActivity extends ActionBarActivity {
    private Camera mCamera;
    private CameraPreview mPreview;

    private Camera.PictureCallback mPicture = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(final byte[] data, Camera camera) {
            final ImageView view = (ImageView) findViewById(R.id.cameraImage);

            Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
            Matrix matrix = new Matrix();
            matrix.postRotate(90);

            Bitmap rotation = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            view.setImageBitmap(rotation);
            if(android.os.Build.VERSION.SDK_INT >= 11) {
                view.setAlpha(1.0f);
            }

            File pictureFile = new File(getFilesDir(), "image.jpg");
            if (pictureFile == null){
                Log.e("image", "Error creating media file, check storage permissions");
                return;
            }

            try {
                FileOutputStream fos = new FileOutputStream(pictureFile);
                fos.write(data);
                fos.close();
            } catch (FileNotFoundException e) {
                Log.e("image", "File not found: " + e.getMessage());
            } catch (IOException e) {
                Log.e("image", "Error accessing file: " + e.getMessage());
            }

            Manager.view().commentView();
       }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Manager.onCreate(this);

        setContentView(R.layout.activity_main);

        getSupportActionBar().hide();

        loadCamera();

        mPreview = new CameraPreview(this, mCamera);
        FrameLayout preview = (FrameLayout) findViewById(R.id.cameraView);
        preview.addView(mPreview);

        initLocation();

        Manager.view().init();

        checkLocation();

        Manager.view().promptServer();

        Log.e("phone", "phonenumber: " + Manager.getPhoneNumber());
        Log.e("phone", "android id: " + Manager.getAndroidId());
    }

    @Override
    protected void onStart() {
        super.onStart();

        Manager.onStart();

        reloadCamera();
    }

    @Override
    protected void onStop() {
        Manager.onStop();

        super.onStop();
    }

    @Override
    protected void onPause() {
        super.onPause();
        releaseCamera();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Manager.onDestroy();
    }

    public static Camera getCameraInstance(){
        Camera c = null;
        try {
            c = Camera.open();
        } catch (Exception e) {

        }
        return c;
    }

    public void loadCamera() {
        mCamera = getCameraInstance();
        mCamera.setDisplayOrientation(90);
    }

    public void reloadCamera() {
        if (mCamera == null) {
            loadCamera();

            mPreview = new CameraPreview(this, mCamera);
            FrameLayout preview = (FrameLayout) findViewById(R.id.cameraView);
            preview.removeAllViews();
            preview.addView(mPreview);
        }
        mCamera.startPreview();
    }

    private void releaseCamera(){
        if (mCamera != null){
            mCamera.release();
            mCamera = null;
        }
    }

    private void checkLocation() {
        LocationManager locationManager = null;
        boolean gps = false;
        boolean network = false;

        if(locationManager == null) {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        }

        try {
            gps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception e) {

        }

        Log.e("location", "enable : " + gps);

        try {
            network = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception e) {

        }

        if(!gps && !network){
            final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setCancelable(false);
            dialog.setMessage(getResources().getString(R.string.gps_network_not_enabled));
            dialog.setPositiveButton(getResources().getString(R.string.open_location_settings), new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    Intent myIntent = new Intent( Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(myIntent);

                    checkLocation();
                }
            });
            dialog.setNegativeButton(getString(R.string.retry), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    checkLocation();
                }
            });
            dialog.show();

        }
    }

    private boolean initLocation() {
        // Check that Google Play services is available
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);

        if (ConnectionResult.SUCCESS == resultCode) {
            Log.e("Location Updates", "Google Play services is available.");
            return true;
        } else {
            Log.e("Location Updates", "Google Play services is NOT available." + resultCode);

            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(resultCode, this, 9000);
            if (dialog != null) {
                dialog.show();
            }

            return false;
        }
    }

    @Override
    public void onBackPressed() {
        if (!Manager.view().back()) {
            super.onBackPressed();
        }
    }

    public void onTakePicture() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Manager.view().flashLayout();
            }
        }, 300);

        mCamera.autoFocus(new Camera.AutoFocusCallback() {
            @Override
            public void onAutoFocus(boolean b, Camera camera) {
                mCamera.takePicture(null, null, mPicture);
            }
        });
    }
}
