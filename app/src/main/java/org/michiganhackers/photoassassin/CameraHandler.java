package org.michiganhackers.photoassassin;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Calendar;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

@SuppressWarnings("deprecation")
class CameraHandler {
    //Activity deals with what is running in the app
    //Camera activity,
    private final Activity activity;
    //Deals with the "context" of the activity
    private final Context context;
    //Camera class, deals with the functions for zoom, taking a picture --> camera functionality
    private Camera mCamera;

    private FirebaseStorage storage = FirebaseStorage.getInstance("gs://photo-assassin.appspot.com/");
    private static final String TAG = "CameraHandler";
    //rear camera is 0, front is 1
    private static int openedCamera = -1;
    private static final int CAMERA_PERMISSION_REQUESTS = 42069;

    private static Toast errorToast, succToast;
    //if you have egregious code, it'll create a red squiggly
    @SuppressWarnings("ShowToast")

    //non default constructor
    CameraHandler(Context context, Activity activity) {

        errorToast = Toast.makeText(context, "Error Opening Camera", Toast.LENGTH_LONG);
        succToast = Toast.makeText(context, "Successfully Opened Camera", Toast.LENGTH_LONG);
        //this.activity is the activity in the class
        this.activity = activity;
        this.context = context;

        int numberOfCameras = Camera.getNumberOfCameras();
        Log.v(TAG, "Number of Cameras: " + numberOfCameras);
        //request permissions if not already granted
        //If permission not granted, request Permission to ActivityCompat
        if (ContextCompat.checkSelfPermission(activity,
                Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity,
                    //using new to create a string in dynamic memory?
                    //automatically deletes it at the end because it has garbage collection
                    new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUESTS);
        }
    }
    //returns a Camera set in portrait mode with autofocus mode on
    //if it fails, a null camera instance returns
    Camera openCamera(int cameraId) {
        //cameraID is in reference to "rear camera = 0" and "front = 1?"
        //we use don't use "this" because there is no other variable with the name "mCamera"
        mCamera = null;
        try {
            mCamera = Camera.open(cameraId);
            openedCamera = cameraId;
            mCamera.setDisplayOrientation(90);

            Camera.Parameters params = mCamera.getParameters();
            Camera.Size size = params.getSupportedPreviewSizes().get(0);
            params.setPreviewSize(size.width, size.height);
            params.setPictureSize(size.width, size.height);
            if(params.getSupportedFocusModes().contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
                params.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
            } else {
                params.setFocusMode(Camera.Parameters.FOCUS_MODE_FIXED);
            }
            if(openedCamera == 0) {
                params.setRotation(90);
            } else if (openedCamera == 1) {

                params.setRotation(270);
            }
            mCamera.setParameters(params);
            succToast.show();
            //if there's any error or exception that occurs in try, the catch is executed and the
            // error message shows
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
                //log e deals with printing to the Logcat
                //not viewable by the user, but can be used for error checking
                Log.e(TAG,  "Something went wrong, defaulting to camera 0");
            }
            //if exception e is to catch any possible error that occurs
        } catch (Exception e) {
            Log.e(TAG, "failed to switch camera, probably only one camera");
        }
        mCamera.startPreview();
        showCameraPreview(mCamera);
    }

    void showCameraPreview(Camera camera) {
        //CameraPreview deals with the live video that's shown when the camera app is open
        CameraPreview mPreview = new CameraPreview(activity, camera, this);
        FrameLayout preview = activity.findViewById(R.id.camera_preview);
        preview.addView(mPreview);
    }
    //takes a picture from mCamera and returns it
    void takePicture() {
        try {
            mCamera.takePicture(null, null, picture);
        } catch (Exception e) {
            //TODO
        }

    }

    void zoom(float zoomPercentage) {
        if(zoomPercentage < 0 || zoomPercentage > 100 || zoomPercentage == getCurrentZoom()) {
            return;
        }
        Camera.Parameters params = mCamera.getParameters();
        if(params.isSmoothZoomSupported()) {
            //zooms from current to the passed percentage
            mCamera.startSmoothZoom((int) (zoomPercentage/100 * params.getMaxZoom()));
        } else if (params.isZoomSupported()) {
            params.setZoom((int) (zoomPercentage/100 * params.getMaxZoom()));
            mCamera.setParameters(params);
        }
    }

    int getCurrentZoom() {
        Log.v(TAG, "Zoom is set to " + mCamera.getParameters().getZoom() + "%");
        return mCamera.getParameters().getZoom();
    }

    private final Camera.PictureCallback picture = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            try {
                StorageReference storageRef = storage.getReference();
                //this might be bullshit
                StorageReference imageRef = storageRef.child(Calendar.getInstance().getTime().toString());
                UploadTask uploadTask = imageRef.putBytes(data);
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }






            File pictureFile = getOutputMediaFile();

            if(pictureFile == null) {
                Log.e(TAG,  "Error onPictureTaken. pictureFile was null");
                return;
            }
            try {
                FileOutputStream fos = new FileOutputStream(pictureFile);
                fos.write(data);
                fos.close();
            } catch (FileNotFoundException e) {
                Log.e(TAG,  "FileNotFoundException error in onPictureTaken function");

            } catch (Exception e) {
                Log.e(TAG,  "General exception error in onPictureTaken function");
            }
            showCameraPreview(camera);
        }
    };
    private File getOutputMediaFile() {
        File mediaFile;
        mediaFile = new File(context.getExternalFilesDir(null) + "/IMAGE.jpg");
        Log.v(TAG, context.getExternalFilesDir(null) + "/IMAGE.jpg");
        return mediaFile;
    }


}
