package me.sensingself.sensingsugar.Engine.CameraCropLib;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.hardware.Camera;
import android.os.Build;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kaopiz.kprogresshud.KProgressHUD;

import me.sensingself.sensingsugar.Common.util.ImageCropUtils;
import me.sensingself.sensingsugar.Constants.PermissionRequest;
import me.sensingself.sensingsugar.Engine.CameraCropLib.Manager.ArcOcrScannerListener;
import me.sensingself.sensingsugar.Lib.FontUtility;
import me.sensingself.sensingsugar.R;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * Activity for taking photo and crop.
 * <p>
 * Created by zhouzhuo810 on 2017/6/15.
 */
public class CropActivity extends Activity implements ArcOcrScannerListener {


    private static final String TAG = "Jella_log";
    private FrameLayout framelayout;
    private RelativeLayout spoonImageLayout;
    private String imagePath;
    private RectView rectView;

    private RelativeLayout takingLayout;

    private ImageView spoonImageView, ivFlash;

    private boolean hasSurface;

    private Camera camera;
    private ImageView ivTake;
    private int ratioWidth;
    private int ratioHeight;
    private float percentWidth;

    private boolean flashOpen = false;
    private KProgressHUD hud;

    private ImageView ivBack;

    private TextView takePhotoTxt, takephotoNoteTxt;

    private ImageView stripPreviewImage;
    private TextView stripCheckingNoteTxt;
    private Button retakePhotoBtn, proceedBtn;
    private ConstraintLayout preViewStripConstraint;
    byte[] imageData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        hideSystemUI();

        setContentView(R.layout.activity_crop);

        ratioWidth = getIntent().getIntExtra(CameraConfig.RATIO_WIDTH, CameraConfig.DEFAULT_RATIO_WIDTH);
        ratioHeight = getIntent().getIntExtra(CameraConfig.RATIO_HEIGHT, CameraConfig.DEFAULT_RATIO_HEIGHT);
        percentWidth = getIntent().getFloatExtra(CameraConfig.PERCENT_WIDTH, CameraConfig.DEFAULT_PERCENT_WIDTH);
        String noSupportHint = getIntent().getStringExtra(CameraConfig.NO_CAMERA_SUPPORT_HINT);
        imagePath = getIntent().getStringExtra(CameraConfig.IMAGE_PATH);
        int maskColor = getIntent().getIntExtra(CameraConfig.MASK_COLOR, CameraConfig.DEFAULT_MASK_COLOR);
        int rectCornerColor = getIntent().getIntExtra(CameraConfig.RECT_CORNER_COLOR, CameraConfig.DEFAULT_RECT_CORNER_COLOR);

        init();
        initFontAndText();
        setListener();

        if (!CameraUtils.checkCameraHardware(this)) {
            Toast.makeText(CropActivity.this, noSupportHint == null ? CameraConfig.DEFAULT_NO_CAMERA_SUPPORT_HINT : noSupportHint, Toast.LENGTH_SHORT).show();
            finish();
        }

    }
    private void init(){
        takingLayout = findViewById(R.id.takingLayout);
        spoonImageLayout = findViewById(R.id.spponImageLayout);
        spoonImageView = findViewById(R.id.spoonImageView);

        retakePhotoBtn = (Button) findViewById(R.id.retakePhotoBtn);
        proceedBtn = (Button) findViewById(R.id.proceedBtn);
        stripPreviewImage = (ImageView) findViewById(R.id.stripPreviewImage);
        stripCheckingNoteTxt = (TextView) findViewById(R.id.stripCheckingNoteTxt);
        takePhotoTxt = (TextView)findViewById(R.id.takePhotoTxt);
        takephotoNoteTxt = (TextView)findViewById(R.id.takePhotoNoteTxt);
        ivBack = (ImageView) findViewById(R.id.iv_back);
        ivFlash = (ImageView) findViewById(R.id.iv_flash);
        ivFlash.setBackgroundResource(R.drawable.flash_off);
        framelayout = (FrameLayout) findViewById(R.id.camera);
        ivTake = (ImageView) findViewById(R.id.iv_take);
        preViewStripConstraint = (ConstraintLayout) findViewById(R.id.preViewStripConstraint);
        preViewStripConstraint.setVisibility(View.GONE);

        rectView = (RectView) findViewById(R.id.rect);
        rectView.setMaskColor(Color.argb(150, 100, 100, 100));
        rectView.setCornerColor(0);
        rectView.setVisibility(View.GONE);
        String hint = getIntent().getStringExtra(CameraConfig.HINT_TEXT);
        rectView.setHintTextAndTextSize((hint == null || hint.length() == 0 ? hint : CameraConfig.DEFAULT_HINT_TEXT), 30);
        rectView.setRatioAndWidthPercentOfScreen(ratioWidth, ratioHeight, percentWidth);
    }

    private void initFontAndText(){
        retakePhotoBtn.setTypeface(FontUtility.getOfficinaSansCBold(getApplicationContext()));
        proceedBtn.setTypeface(FontUtility.getOfficinaSansCBold(getApplicationContext()));
        stripCheckingNoteTxt.setTypeface(FontUtility.getOfficinaSansCBook(getApplicationContext()));
        takePhotoTxt.setTypeface(FontUtility.getOfficinaSansCBold(getApplicationContext()));
        takephotoNoteTxt.setTypeface(FontUtility.getOfficinaSansCBook(getApplicationContext()));
    }

    private void setListener(){
        ivTake.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePhoto();
            }
        });
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        ivFlash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (flashOpen) {
                    ivFlash.setBackgroundResource(R.drawable.flash_off);
                    offLight();
                } else {
                    ivFlash.setBackgroundResource(R.drawable.flash_on);
                    openLight();
                }
                flashOpen = !flashOpen;
            }
        });
        spoonImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (camera != null) {
                    camera.autoFocus(new Camera.AutoFocusCallback() {
                        @Override
                        public void onAutoFocus(boolean success, Camera camera) {
                        }
                    });
                }
            }
        });
        rectView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (camera != null) {
                    camera.autoFocus(new Camera.AutoFocusCallback() {
                        @Override
                        public void onAutoFocus(boolean success, Camera camera) {
                        }
                    });
                }
            }
        });
        retakePhotoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                preViewStripConstraint.setVisibility(View.GONE);
                resumeCamera();
            }
        });

        final ArcOcrScannerListener ocrScannerListener = this;
        proceedBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DisplayMetrics displayMetrics = new DisplayMetrics();
                getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                int width = displayMetrics.heightPixels;
                int height = displayMetrics.widthPixels;

                BitmapUtils thread = new BitmapUtils(ocrScannerListener, CropActivity.this, imagePath, imageData, 0, 0, width, height, false);
                thread.execute();

            }
        });
    }
    private void hideSystemUI() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) return;
        // Set the IMMERSIVE flag.
        // Set the content to appear under the system bars so that the content
        // doesn't resize when the system bars hide and show.
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                        | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            hideSystemUI();
        }
    }

    @Override
    public void onOcrScanStarted(String filePath) {
        hud = KProgressHUD.create(CropActivity.this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("")
                .setDetailsLabel("Analyzing the photo…");
        hud.show();
    }

    @Override
    public void onOcrScanFinished(File file) {

        if (hud!= null)hud.dismiss();
        releaseCamera();

        Intent intent = new Intent();
        intent.putExtra(CameraConfig.IMAGE_PATH, imagePath);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (hud!= null)hud.dismiss();
    }

    private void takePhoto() {
        camera.takePicture(null, null, new Camera.PictureCallback() {
            @Override
            public void onPictureTaken(byte[] data, Camera camera) {
                imageData = new byte[data.length];
                imageData = data;

                File file = new File(imagePath);
                if (file.exists()) {
                    file.delete();
                }
                if (!file.getParentFile().exists()) {
                    file.getParentFile().mkdirs();
                }

                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                preViewStripConstraint.setVisibility(View.VISIBLE);
                float cornerRadius = 25.0f;
                RoundedBitmapDrawable roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(
                        getResources(),
                        ImageCropUtils.rotateBitmapIfNeeded(BitmapFactory.decodeByteArray(data, 0, data.length,options), ImageCropUtils.getRotate(CropActivity.this))
                );
                roundedBitmapDrawable.setCornerRadius(cornerRadius);
                roundedBitmapDrawable.setAntiAlias(true);
                stripPreviewImage.setImageDrawable(roundedBitmapDrawable);
                releaseCamera();
            }
        });
    }

    public void setRectRatio(int w, int h) {
        rectView.updateRatio(w, h);
    }


    @Override
    protected void onResume() {
        super.onResume();
        resumeCamera();
    }

    private void resumeCamera() {
        camera = CameraUtils.open();
        CameraView cameraView = new CameraView(this, camera);
        framelayout.removeAllViews();
        framelayout.addView(cameraView);
    }

    public synchronized void openLight() {
        if (camera != null) {
            try {
                Camera.Parameters parameters = camera.getParameters();
                parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                camera.setParameters(parameters);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public synchronized void offLight() {
        if (camera != null) {
            try {
                Camera.Parameters parameters = camera.getParameters();
                parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                camera.setParameters(parameters);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        releaseCamera();
    }

    private void releaseCamera() {
        if (camera != null) {
            camera.stopPreview();
            camera.release();
            camera = null;
        }
    }


}
