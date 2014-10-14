package com.smartcity;

import android.app.Dialog;
import android.hardware.Camera;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.FrameLayout;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.smartcity.engine.manager.Manager;
import com.smartcity.engine.view.CameraPreview;


public class MainActivity extends ActionBarActivity {
    private Camera mCamera;
    private CameraPreview mPreview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Manager.onCreate(this);

        setContentView(R.layout.activity_main);

        getSupportActionBar().hide();

        // Create an instance of Camera
        mCamera = getCameraInstance();
        mCamera.setDisplayOrientation(90);

        // Create our Preview view and set it as the content of our activity.
        mPreview = new CameraPreview(this, mCamera);
        FrameLayout preview = (FrameLayout) findViewById(R.id.cameraView);
        preview.addView(mPreview);

        initLocation();

        Manager.view().init();
    }

    @Override
    protected void onStart() {
        super.onStart();

        Manager.onStart();
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
            c = Camera.open(); // attempt to get a Camera instance
        } catch (Exception e){
            // Camera is not available (in use or does not exist)
        }
        return c; // returns null if camera is unavailable
    }

    private void releaseCamera(){
        if (mCamera != null){
            mCamera.release();        // release the camera for other applications
            mCamera = null;
        }
    }

    private boolean initLocation() {
        // Check that Google Play services is available
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);

        if (ConnectionResult.SUCCESS == resultCode) {
            // In debug mode, log the status
            Log.e("Location Updates", "Google Play services is available.");
            // Continue
            return true;
            // Google Play services was not available for some reason.
            // resultCode holds the error code.
        } else {
            Log.e("Location Updates", "Google Play services is NOT available." + resultCode);

            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(resultCode, this, 9000);
            if (dialog != null) {
                dialog.show();
            }

            return false;
        }
    }
}
