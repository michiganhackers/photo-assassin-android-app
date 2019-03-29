package org.michiganhackers.photoassassin;

import android.content.Context;
import android.hardware.Camera;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;

@SuppressWarnings("ALL")
public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback,
        ScaleGestureDetector.OnScaleGestureListener  {
    private final SurfaceHolder mHolder;
    private final Camera mCamera;
    private final CameraHandler camHan;
    private final ScaleGestureDetector gestureDetector;
    private final String TAG = "CameraPreview";

    private int initialZoomSpan;
    private float initialSpan;



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
        //converts the actual span based on initial span and initial zoom span
        int currentZoomSpan = initialZoomSpan - (int)(initialSpan - detector.getCurrentSpan());
        camHan.zoom(100 * (float)(currentZoomSpan-minSpan)/(maxSpan-minSpan));
        return true;
    }
    @Override
    public boolean onScaleBegin(ScaleGestureDetector detector) {
        int maxSpan = getWidth();
        int minSpan = maxSpan/2;
        //converts currentZoom to the equivalent span when gesture begins, zoom/100 was an int
        //division so it was only ever 1 or 0, needed to have decimal places
        initialZoomSpan = (int)(((camHan.getCurrentZoom()/100.0) *(maxSpan-minSpan)) +minSpan);
        initialSpan = detector.getCurrentSpan();
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
        //TODO
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
        //TODO
        }

        try{
            mCamera.setPreviewDisplay(mHolder);
            mCamera.startPreview();
        }catch(Exception e){
        //TODO
        }
    }
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        gestureDetector.onTouchEvent(ev);
        return true;

    }

}