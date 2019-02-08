package org.michiganhackers.photoassassin;

import android.content.Context;
import android.hardware.Camera;
import android.widget.Toast;

public class CameraHandler {
    public CameraHandler(Context context) {
        
    }
    public static Camera getCameraInstance(){
        Camera c = null;
        try {
            c = Camera.open();
        } catch (Exception e) {
            Toast toast = Toast.makeText(getApplicationContext(), )
        }
        return c;
    }
}
