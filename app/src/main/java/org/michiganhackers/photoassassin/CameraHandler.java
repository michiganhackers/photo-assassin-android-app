package org.michiganhackers.photoassassin;

import android.content.Context;
import android.hardware.Camera;
import android.widget.Toast;

class CameraHandler {

    private static Toast errorToast, succToast;
    CameraHandler(Context context) {
        errorToast = Toast.makeText(context, "Error Opening Camera", Toast.LENGTH_LONG);
        succToast = Toast.makeText(context, "Successfully Opened Camera", Toast.LENGTH_LONG);
    }

    static Camera getCameraInstance(){
        Camera c = null;
        try {
            c = Camera.open();
            succToast.show();
        } catch (RuntimeException e) {

        } catch (Exception e1) {
            errorToast.show();
        }
        return c;
    }
}
