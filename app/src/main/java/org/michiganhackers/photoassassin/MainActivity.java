package org.michiganhackers.photoassassin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.hardware.Camera;
import android.os.Bundle;
import android.widget.FrameLayout;
@SuppressWarnings("deprecation")
public class MainActivity extends AppCompatActivity
        implements ActivityCompat.OnRequestPermissionsResultCallback {
    private static final int CAMERA_PERMISSION_REQUESTS = 42069;
    CameraHandler camHan;
    Camera mCamera;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        camHan = new CameraHandler(this,this);

    }

    @Override
    protected void onStart() {
        super.onStart();
        mCamera = camHan.openCamera();
        camHan.showCameraPreview(mCamera);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mCamera.release();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults){
       if(requestCode == CAMERA_PERMISSION_REQUESTS) {
           camHan.openCamera();

       }
    }

}
