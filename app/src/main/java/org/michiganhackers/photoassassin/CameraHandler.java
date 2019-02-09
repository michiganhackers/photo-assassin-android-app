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

class CameraHandler {
    Activity activity;
    Camera mCamera;
    CameraPreview mPreview;

    private static final int CAMERA_PERMISSION_REQUESTS = 42069;

    private static Toast errorToast, succToast;
    CameraHandler(Context context, Activity act) {
        errorToast = Toast.makeText(context, "Error Opening Camera", Toast.LENGTH_LONG);
        succToast = Toast.makeText(context, "Successfully Opened Camera", Toast.LENGTH_LONG);
        activity = act;
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUESTS);
        }
        else{
            mCamera = getCameraInstance();
            mCamera.setDisplayOrientation(90);
            Camera.Parameters params = mCamera.getParameters();
            params.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
            mCamera.setParameters(params);
            mPreview = new CameraPreview(context, mCamera);
            FrameLayout preview = (FrameLayout) activity.findViewById(R.id.camera_preview);
            preview.addView(mPreview);

        }
    }

    Camera getCameraInstance(){
        Camera c = null;

        try {
            c = Camera.open();
            succToast.show();

        } catch (Exception e) {
            errorToast.show();
        }
        return c;
    }
}
