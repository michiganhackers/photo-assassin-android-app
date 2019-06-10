package org.michiganhackers.photoassassin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.hardware.Camera;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

@SuppressWarnings("deprecation")
public class MainActivity extends AppCompatActivity
        implements ActivityCompat.OnRequestPermissionsResultCallback {
    private static final int CAMERA_PERMISSION_REQUESTS = 42069;
    private CameraHandler camHan;
    private Camera mCamera;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        camHan = new CameraHandler(this,this);


        final Button switchCameraButton = findViewById(R.id.switch_camera_button);
        final Button takePictureButton = findViewById(R.id.button);
        final Button testButton = findViewById(R.id.test_button);
        switchCameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                camHan.switchCamera();
            }
        });
        takePictureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                camHan.takePicture();
            }
        });
        testButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                camHan.zoom(100);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        //TODO save opened camera to shared preferences when it actually exists
        mCamera = camHan.openCamera(0);
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
           camHan.openCamera(0);
           camHan.showCameraPreview(mCamera);

       }
    }



}
