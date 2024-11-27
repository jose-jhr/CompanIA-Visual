package com.ingenieriajhr.visualcontroll.camara;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.Matrix;

import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;

import com.ingenieriajhr.visualcontroll.interfaces.BitmapResponse;

import java.nio.ByteBuffer;

public class LuminosityAnalyzer implements ImageAnalysis.Analyzer {

    public boolean isFrontCamera = false;
    private BitmapResponse bitmapResponse;
    public boolean returnBitmap = true;
    public boolean returnImageProxy = false;

    public void addListener(BitmapResponse bitmapResponse) {
        this.bitmapResponse = bitmapResponse;
    }


    @SuppressLint("UnsafeOptInUsageError")
    @Override
    public void analyze(ImageProxy imageProxy) {
        try {
            if (returnBitmap && !returnImageProxy) {
                Bitmap bitmap = convertionAllBitmapOrImageProxy(imageProxy);
                bitmapResponse.bitmapReturn(bitmap);
            }
            imageProxy.close();
        } catch (IllegalStateException e) {
            // Handle the exception here
            System.out.println("Error in conversion");
        }
    }

    private Bitmap convertionAllBitmapOrImageProxy(ImageProxy imageProxy) {
        ByteBuffer buffer = imageProxy.getPlanes()[0].getBuffer();
        Bitmap bitmapBuffer = Bitmap.createBitmap(imageProxy.getWidth(), imageProxy.getHeight(), Bitmap.Config.ARGB_8888);
        bitmapBuffer.copyPixelsFromBuffer(buffer);

        Matrix matrix = new Matrix();
        matrix.postRotate(imageProxy.getImageInfo().getRotationDegrees());

        if (isFrontCamera) {
            matrix.postScale(-1f, 1f, imageProxy.getWidth() / 2f, imageProxy.getHeight() / 2f);
        }

        Bitmap rotatedBitmap = Bitmap.createBitmap(bitmapBuffer, 0, 0, bitmapBuffer.getWidth(), bitmapBuffer.getHeight(), matrix, true);
        return rotatedBitmap;
    }
}
