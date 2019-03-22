package org.michiganhackers.photoassassin;

import android.content.Context;
import android.hardware.Camera;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {
    private SurfaceHolder mHolder;
    private Camera mCamera;
    private CameraHandler camHan;
    ScaleGestureDetector gestureDetector;

    public CameraPreview(Context context, Camera camera, CameraHandler camHan_in){
        super(context);
        mCamera = camera;
        camHan = camHan_in;
        mHolder = getHolder();
        mHolder.addCallback(this);
        gestureDetector = new ScaleGestureDetector(context,
                new ScaleGestureDetector.OnScaleGestureListener() {
                    @Override
                    public boolean onScale(ScaleGestureDetector detector) {
                        //getCurrentZoom()
                        if(detector.getScaleFactor() > 1) {
                            camHan.zoom(camHan.getCurrentZoom()
                                    + 2/detector.getScaleFactor());
                        } else if(detector.getScaleFactor() < 1) {
                            camHan.zoom(camHan.getCurrentZoom()
                                    - 2*(detector.getScaleFactor()));
                        }
                        //camHan.zoom(camHan.getCurrentZoom() * detector.getScaleFactor());
                        return true;
                    }
                    /*

    public void setZoom(int index) {
        mCircleSize = (int) (mMinCircle + index * (mMaxCircle - mMinCircle) / (mMaxZoom - mMinZoom));
    }
    mMinCircle = res.getDimensionPixelSize(R.dimen.zoom_ring_min);
    public boolean onScale(ScaleGestureDetector detector) {
        final float sf = detector.getScaleFactor();
        float circle = (int) (mCircleSize * sf * sf);
        circle = Math.max(mMinCircle, circle);
        circle = Math.min(mMaxCircle, circle);
        if (mListener != null && (int) circle != mCircleSize) {
            mCircleSize = (int) circle;
            int zoom = mMinZoom + (int) ((mCircleSize - mMinCircle) * (mMaxZoom - mMinZoom) / (mMaxCircle - mMinCircle));
            mListener.onZoomValueChanged(zoom);
        }
        return true;
    }
    mMaxCircle = Math.min(getWidth(), getHeight());
        mMaxCircle = (mMaxCircle - mMinCircle) / 2;
        mMinCircle = res.getDimensionPixelSize(R.dimen.zoom_ring_min);
                    */
                    @Override
                    public boolean onScaleBegin(ScaleGestureDetector detector) {
                        return true;
                    }

                    @Override
                    public void onScaleEnd(ScaleGestureDetector detector) {
                    }
                });
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