package org.michiganhackers.photoassassin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.hardware.Camera;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity
        implements ActivityCompat.OnRequestPermissionsResultCallback {
    private static final int CAMERA_PERMISSION_REQUESTS = 42069;
    CameraHandler camHan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        camHan = new CameraHandler(this,this);



    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults){
       if(requestCode == CAMERA_PERMISSION_REQUESTS) {
           camHan.getCameraInstance();

       }
    }
}
