package org.michiganhackers.photoassassin;

import android.app.Activity;
import android.content.Context;
import android.hardware.Camera;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Button;

import java.io.IOException;

public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback,
        ScaleGestureDetector.OnScaleGestureListener  {
    private SurfaceHolder mHolder;
    private Camera mCamera;
    private CameraHandler camHan;
    private ScaleGestureDetector gestureDetector;
    private final String TAG = "CameraPreview";

    public CameraPreview(Context context_in, Camera camera, CameraHandler camHan_in) {
        super(context_in);
        mCamera = camera;
        camHan = camHan_in;
        mHolder = getHolder();
        mHolder.addCallback(this);
        gestureDetector = new ScaleGestureDetector(context_in, this);
    }

    @Override
    public boolean onScale(ScaleGestureDetector detector) {
        int maxSpan = getWidth();
        int minSpan = maxSpan/2;

        camHan.zoom(100 * (detector.getCurrentSpan()-minSpan)/(maxSpan-minSpan));
        return true;
    }
    @Override
    public boolean onScaleBegin(ScaleGestureDetector detector) {
        return true;
    }

    @Override
        public void onScaleEnd(ScaleGestureDetector detector) {
    }
    public void surfaceCreated(SurfaceHolder holder){
        try{
            mCamera.setPreviewDisplay(holder);
            mCamera.startPreview();
        }
        catch(IOException e){

        }
    }

    public void surfaceDestroyed(SurfaceHolder holder){

    }

    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h){
        if(mHolder.getSurface() == null){
            return;
        }

        try{
            mCamera.stopPreview();
        }catch( Exception e){

        }

        try{
            mCamera.setPreviewDisplay(mHolder);
            mCamera.startPreview();
        }catch(Exception e){

        }
    }
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        gestureDetector.onTouchEvent(ev);
        return true;

    }

}