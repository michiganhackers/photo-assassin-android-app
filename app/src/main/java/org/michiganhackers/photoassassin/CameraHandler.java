package org.michiganhackers.photoassassin;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

@SuppressWarnings("deprecation")
class CameraHandler {
    private Activity activity;
    private Context context;
    private Camera mCamera;

    private static final int CAMERA_PERMISSION_REQUESTS = 42069;

    private int numberOfCameras;
    private static Toast errorToast, succToast;
    @SuppressWarnings("ShowToast")
    CameraHandler(Context context, Activity activity) {

        errorToast = Toast.makeText(context, "Error Opening Camera", Toast.LENGTH_LONG);
        succToast = Toast.makeText(context, "Successfully Opened Camera", Toast.LENGTH_LONG);

        this.activity = activity;
        this.context = context;

        numberOfCameras = Camera.getNumberOfCameras();

        //request permissions if not already granted
        if (ContextCompat.checkSelfPermission(activity,
                Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity,
                    new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUESTS);
        }
    }
    //returns a Camera set in portrait mode with autofocus mode on
    Camera openCamera(){
        mCamera = null;
        try {
            mCamera = Camera.open();
            mCamera.setDisplayOrientation(90);
            Camera.Parameters params = mCamera.getParameters();
            params.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
            mCamera.setParameters(params);
            succToast.show();

        } catch (Exception e) {
            errorToast.show();
        }
        return mCamera;
    }
}
