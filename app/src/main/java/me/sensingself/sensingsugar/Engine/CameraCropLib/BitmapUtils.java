package me.sensingself.sensingsugar.Engine.CameraCropLib;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.AsyncTask;


import me.sensingself.sensingsugar.Engine.CameraCropLib.Manager.ArcOcrScannerListener;
import me.sensingself.sensingsugar.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * image tools
 * Created by zhouzhuo810 on 2017/6/16.
 */

public class BitmapUtils  extends AsyncTask<Void, Void, Void> {
    private ArcOcrScannerListener arcOcrScannerListener;
    private String filePath;
    private String scannedText;
    private Context context;
    private int rectLeft;
    private int rectWidth;
    private int rectTop;
    private int rectHeight;
    private boolean isNeedCut;
    private byte[] data;
    private File changedFile;

    public BitmapUtils(ArcOcrScannerListener ocrScannerListener, Context context, String filePath, byte[] data, int rectLeft, int rectTop, int rectWidth, int rectHeight, boolean isNeedCut) {

        this.arcOcrScannerListener = ocrScannerListener;
        this.filePath = filePath;
        this.context = context;
        this.data = data;
        this.rectLeft = rectLeft;
        this.rectTop = rectTop;
        this.rectWidth = rectWidth;
        this.rectHeight = rectHeight;
        this.isNeedCut = isNeedCut;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        arcOcrScannerListener.onOcrScanStarted(this.filePath);
    }

    @Override
    protected Void doInBackground(Void... params) {
        try {
            this.changedFile = saveBitMap(this.context, this.filePath, this.data, this.rectLeft, this.rectTop, this.rectWidth, this.rectHeight, this.isNeedCut);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        arcOcrScannerListener.onOcrScanFinished(this.changedFile);
    }



    public static Bitmap setBitmapSize(Bitmap bitmap, int newWidth, int newHeight) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        float scaleWidth = (newWidth * 1.0f) / width;
        float scaleHeight = (newHeight * 1.0f) / height;
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        return Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
    }

    public static File saveBitMap(Context context, String filePath, final byte[] data, int rectLeft, int rectTop, int rectWidth, int rectHeight, boolean isNeedCut) throws IOException {
        File file = new File(filePath);
        try {
            FileOutputStream fos = new FileOutputStream(file, true);
            fos.write(data);
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        int degrees = getExifRotateDegree(filePath);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        Bitmap photo = BitmapFactory.decodeByteArray(data, 0, data.length,options);
//        photo = rotateBitmap(photo,degrees);
//
//        int scH = ScreenUtils.getScreenWidth(context);
//        int scW = ScreenUtils.getScreenHeight(context);
//
//        float ratio = scW  * 1.0f / photo.getWidth();
//        int widthImg = (int)(photo.getWidth() * ratio);
//        int heightImg = (int)(photo.getHeight() * ratio);
//        photo = Bitmap.createScaledBitmap(photo, widthImg, heightImg, true);

        if (isNeedCut) {
//            photo = Bitmap.createBitmap(photo, rectLeft*photo.getWidth()/scW, rectTop* photo.getHeight() / scH , rectWidth*photo.getWidth()/scW, rectHeight* photo.getHeight() / scH);
        }
        if (photo != null) {
//            Bitmap maskBitMap = BitmapFactory.decodeResource(context.getResources(), R.drawable.spoonback_mask_rect);
//            Bitmap scaledMaskBitMap = Bitmap.createScaledBitmap(maskBitMap, widthImg,  heightImg, true);
//            if(maskBitMap!=null)
//            {
//                maskBitMap.recycle();
//                maskBitMap=null;
//            }
//            Bitmap resultPhoto = Bitmap.createBitmap(widthImg,  heightImg, Bitmap.Config.ARGB_8888);
//
//            Canvas canvas = new Canvas(resultPhoto);
//            canvas.drawRGB(255,255,255);
//
//            int r = 0;
//            int g = 0;
//            int b = 0;
//            for (int i = 0; i < widthImg; i ++){
//                for (int j = 0; j < heightImg; j ++){
//                    int pMask = scaledMaskBitMap.getPixel(i, j);
//                    r = Color.red(pMask);
//                    g = Color.green(pMask);
//                    b = Color.blue(pMask);
//                    if (r > 0 || g > 0 || b > 0){
//                        if (r == b && r != g && r != 255 && g >= 200){
//                            resultPhoto.setPixel(i, j, photo.getPixel(i, j));
//                        }else{
//                            r = 255 - r + 35;
//                            if (r > 255) r = 255;
//                            g = 255 - g + 75;
//                            if (g > 255) g = 255;
//                            b = 255 - b + 145;
//                            if (b > 255) b = 255;
//                            resultPhoto.setPixel(i, j,  Color.rgb(r, g, b));
//                        }
//                    }else{
//                        resultPhoto.setPixel(i, j, Color.rgb(255, 255, 255));
//                    }
//                }
//            }
            compressImageToFile(photo, file);

            photo.recycle();
            photo = null;
//            scaledMaskBitMap.recycle();
//            scaledMaskBitMap = null;

            return file;
        } else {
            return null;
        }

    }

    private static int getExifRotateDegree(String path){
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            int degrees = getExifRotateDegrees(orientation);
            return degrees;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private static int getExifRotateDegrees(int exifOrientation) {
        int degrees = 0;
        switch (exifOrientation) {
            case ExifInterface.ORIENTATION_NORMAL:
                degrees = 0;
                break;
            case ExifInterface.ORIENTATION_ROTATE_90:
                degrees = 90;
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                degrees = 180;
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                degrees = 270;
                break;
        }
        return degrees;
    }

    private static Bitmap rotateBitmap(Bitmap origin, float alpha) {
        if (origin == null) {
            return null;
        }
        int width = origin.getWidth();
        int height = origin.getHeight();
        Matrix matrix = new Matrix();
        matrix.setRotate(alpha);
        Bitmap newBM = Bitmap.createBitmap(origin, 0, 0, width, height, matrix, false);
        if (newBM.equals(origin)) {
            return newBM;
        }
        origin.recycle();
        return newBM;
    }


    public static void compressImageToFile(Bitmap bmp, File file) {
        int options = 100;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, options, baos);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(baos.toByteArray());
            fos.flush();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
