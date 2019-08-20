package me.sensingself.sensingsugar.Engine.CameraCropLib.Manager;

import android.graphics.Bitmap;

import java.io.File;

public interface ArcOcrScannerListener {

    public void onOcrScanStarted(String filePath);

    public void onOcrScanFinished(File file);
}
