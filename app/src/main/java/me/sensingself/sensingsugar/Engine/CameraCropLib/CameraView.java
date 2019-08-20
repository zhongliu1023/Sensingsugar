package me.sensingself.sensingsugar.Engine.CameraCropLib;

import android.app.Activity;
import android.content.Context;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import me.sensingself.sensingsugar.Engine.CameraCropLib.Manager.AutoFocusManager;

import java.io.IOException;
import java.util.List;


/**
 * Preview of camera
 * Created by zhouzhuo810 on 2017/6/15.
 */
public class CameraView extends SurfaceView implements SurfaceHolder.Callback {

    private Camera mCamera;
    private AutoFocusManager autoFocusManager;
    private Context mContext;

    public CameraView(Context context, Camera camera) {
        super(context);
        mCamera = camera;
        mContext = context;
        getHolder().addCallback(this);
        getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        try {
            initCamera(mCamera);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initCamera(Camera camera) throws Exception {
        Camera.Parameters parameters = camera.getParameters();
        List<Camera.Size> previewSizes = parameters.getSupportedPreviewSizes();
        for (Camera.Size size : previewSizes) {
            Log.e("XXX", "preview:"+size.width+","+size.height);
            if (size.width / 16 == size.height / 9) {
                parameters.setPreviewSize(size.width, size.height);
                break;
            }
        }
        List<Camera.Size> pictureSizes = parameters.getSupportedPictureSizes();
        for (Camera.Size size : pictureSizes) {
            Log.e("XXX", "picture:"+size.width+","+size.height);
            if (size.width / 16 == size.height / 9) {
                parameters.setPictureSize(size.width, size.height);
                break;
            }
        }
        parameters.set("orientation", "portrait");
        Camera.CameraInfo info = new Camera.CameraInfo();
        Camera.getCameraInfo(CameraUtils.findCameraId(false), info);

        int rotation = ((Activity)mContext).getWindowManager().getDefaultDisplay()
                .getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0: degrees = 0; break;
            case Surface.ROTATION_90: degrees = 90; break;
            case Surface.ROTATION_180: degrees = 180; break;
            case Surface.ROTATION_270: degrees = 270; break;
        }

        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;  // compensate the mirror
        } else {  // back-facing
            result = (info.orientation - degrees + 360) % 360;
        }



//        int rotation = info.orientation % 360;
        Log.d("XXX", "Rotation :"+rotation);
        parameters.setRotation(result);
        camera.setDisplayOrientation(result);
        parameters.setJpegQuality(100);
        camera.setParameters(parameters);
    }

    public CameraView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CameraView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        try {
            mCamera.setPreviewDisplay(holder);
            mCamera.startPreview();
            if (autoFocusManager == null) {
                autoFocusManager = new AutoFocusManager(mCamera);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        if (getHolder().getSurface() == null) {
            return;
        }
        try {
            mCamera.stopPreview();
            if (autoFocusManager != null) {
                autoFocusManager.stop();
                autoFocusManager = null;
            }
        } catch (Exception e) {

        }

        try {
            initCamera(mCamera);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            mCamera.setPreviewDisplay(holder);
            mCamera.startPreview();
            if (autoFocusManager == null) {
                autoFocusManager = new AutoFocusManager(mCamera);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        if (autoFocusManager != null) {
            autoFocusManager.stop();
            autoFocusManager = null;
        }
    }
}
