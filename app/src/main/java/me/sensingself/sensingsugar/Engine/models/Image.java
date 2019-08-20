package me.sensingself.sensingsugar.Engine.models;

import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by liujie on 12/15/17.
 */

public class Image {
    private Map<String , Object> data = new HashMap<String, Object>();
    public String storeFilePath;

    public void  init(String jpgFile, int width, int height)
    {
        storeFilePath = jpgFile;
        Uri myUri = Uri.parse(jpgFile);
        String basename    = myUri.getLastPathSegment();
        String[] filenames   = basename.split(".jpg");

        this.data.put("prefix", filenames);
        this.data.put("filePath", jpgFile);
        this.data.put("width", width);
        this.data.put("height", height);
        this.data.put("rotate", -1);
        Map<String , Object> raw = new HashMap<String, Object>();
        this.data.put("raw", raw);
    }

    private Object get(String key)
    {
        return this.data.containsKey(key)?this.data.get(key):null;
    }

    public String filename(String suffix)
    {
        if (!suffix.isEmpty()) {
            suffix = "-" + suffix;
        }
        return  this.get("prefix") + suffix+".jpg";
    }

    public String getStoreFilePath()
    {
        return this.storeFilePath;
    }
    public void setStoreFilePath(String filePath)
    {
         this.storeFilePath = filePath;
    }
    public String filePath()
    {
        return this.get("filePath").toString();
    }

    public int width()
    {
        return (int)this.get("width");
    }

    public int height()
    {
        return (int)this.get("height");
    }

    public Map<String , Object> getRawData()
    {
        return (Map<String , Object>)this.data.get("raw");
    }

    public void setRawData(Map<String , Object> rawData)
    {
        Map<String , Object> raw = new HashMap<String, Object>();
        raw = (Map<String , Object>)this.data.get("raw");

        for ( Map.Entry<String, Object > entry : rawData.entrySet() ) {
            String key = entry.getKey();
            Object value = entry.getValue();
            raw.put(key, value);
        }
        this.data.put("raw", raw);
    }

    public Object getRotation() {
        return this.data.get("rotate");
    }

    public void setRotation(Object rotate) {
        this.data.put("rotate", rotate);
    }

    public Bitmap resize(Dimension src, Dimension dest, Bitmap currentImage)
    {
        Map<String, Object> tmpRaw = new HashMap<String, Object>();
        tmpRaw.put("raw_src_x",src.x);
        tmpRaw.put("raw_src_y",src.y);
        tmpRaw.put("raw_src_h",src.height);
        tmpRaw.put("raw_src_w",src.width);
        tmpRaw.put("raw_dest_x",dest.x);
        tmpRaw.put("raw_dest_y",dest.y);
        tmpRaw.put("raw_dest_h",dest.height);
        tmpRaw.put("raw_dest_w",dest.width);
        this.setRawData(tmpRaw);

        Bitmap croppedBmp = Bitmap.createBitmap(currentImage, src.x, src.y,src.width, src.height);
        Bitmap croppedBmpFit = Bitmap.createScaledBitmap(croppedBmp, dest.width, dest.height, true);
        storeImage(croppedBmpFit);

        if (croppedBmp != null){
            croppedBmp.recycle();
            croppedBmp = null;
        }
        return croppedBmpFit;
    }
    private Bitmap JPGtoRGB888(Bitmap img){
        Bitmap result = null;
        int numPixels = img.getWidth() * img.getHeight();
        int[] pixels = new int[numPixels];
        img.getPixels(pixels,0,img.getWidth(),0,0,img.getWidth(),img.getHeight());
        result = Bitmap.createBitmap(img.getWidth(),img.getHeight(), Bitmap.Config.ARGB_8888);
        result.setPixels(pixels, 0, result.getWidth(), 0, 0, result.getWidth(), result.getHeight());

        return result;
    }
    private Boolean storeImage(Bitmap image) {
        File pictureFile = getOutputMediaFile();
        if (pictureFile == null) {
            Log.d("Jella__",
                    "Error creating media file, check storage permissions: ");// e.getMessage());
            return false;
        }
        try {
            FileOutputStream fos = new FileOutputStream(pictureFile);
            image.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.close();
            return true;
        } catch (FileNotFoundException e) {
            Log.d("Jella__", "File not found: " + e.getMessage());
            return false;
        } catch (IOException e) {
            Log.d("Jella__", "Error accessing file: " + e.getMessage());
            return false;
        }
    }
    private  File getOutputMediaFile(){
        File mediaFile;
        mediaFile = new File(storeFilePath);
        return mediaFile;
    }
}
