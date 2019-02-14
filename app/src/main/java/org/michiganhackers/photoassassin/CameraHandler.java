package org.michiganhackers.photoassassin;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

@SuppressWarnings("deprecation")
class CameraHandler {
    private Activity activity;
    private Context context;
    private Camera mCamera;
    private static final String TAG = "CameraHandler";
    //rear camera is 0, front is 1
    private static int openedCamera = -1;
    private static final int CAMERA_PERMISSION_REQUESTS = 42069;

    private static int numberOfCameras;
    private static Toast errorToast, succToast;
    @SuppressWarnings("ShowToast")
    CameraHandler(Context context, Activity activity) {

        errorToast = Toast.makeText(context, "Error Opening Camera", Toast.LENGTH_LONG);
        succToast = Toast.makeText(context, "Successfully Opened Camera", Toast.LENGTH_LONG);

        this.activity = activity;
        this.context = context;

        numberOfCameras = Camera.getNumberOfCameras();
        Log.v(TAG, Integer.toString(numberOfCameras));
        //request permissions if not already granted
        if (ContextCompat.checkSelfPermission(activity,
                Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity,
                    new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUESTS);
        }
    }
    //returns a Camera set in portrait mode with autofocus mode on
    Camera openCamera(int cameraId) {
        mCamera = null;
        try {
            mCamera = Camera.open(cameraId);
            openedCamera = 0;
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

    void switchCamera() {
        try {
            mCamera.stopPreview();
            mCamera.release();
            if(openedCamera == 0) {
                mCamera = openCamera(1);
                openedCamera = 1;
            } else if (openedCamera == 1){
                mCamera = openCamera(0);
                openedCamera = 0;
            } else {
                mCamera = openCamera(0);
                Log.e(TAG,  "Something went wrong, defaulting to camera 0");
            }

        } catch (Exception e) {
            Log.e(TAG, "failed to switch camera, probably only one camera");
        }
        mCamera.startPreview();
        showCameraPreview(mCamera);
    }

    void showCameraPreview(Camera camera) {
        CameraPreview mPreview = new CameraPreview(activity, camera);
        FrameLayout preview = activity.findViewById(R.id.camera_preview);
        preview.addView(mPreview);
    }
}
