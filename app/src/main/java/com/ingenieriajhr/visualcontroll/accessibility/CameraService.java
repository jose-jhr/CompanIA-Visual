package com.ingenieriajhr.visualcontroll.accessibility;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.view.View;

import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;

import com.ingenieriajhr.visualcontroll.R;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CameraService extends Service {

    private final IBinder binder = new LocalBinder();
    private final ExecutorService cameraExecutor = Executors.newSingleThreadExecutor();
    private ProcessCameraProvider cameraProvider;
    private boolean ifStartCamera = false;
    private ImageAnalysis imageAnalysis;
    private Preview imagePreview;

    private PreviewView previewView;



    public class LocalBinder extends Binder {
        CameraService getService() {
            return CameraService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        View root  = View.inflate(this, R.layout.cursor_l,null);
        previewView = root.findViewById(R.id.camera_x_preview);
        start(0,0,previewView,true);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Esto es un ejemplo, asegúrate de pasar los parámetros adecuados
        start(0, 0, null, false);
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        cameraExecutor.shutdown();
    }

    @SuppressLint("RestrictedApi")
    public void start(int selectorCamera, int aspectRatio, PreviewView view, boolean cameraPreview) {
        ProcessCameraProvider.getInstance(this).addListener(() -> {
            CameraSelector cameraSelector = new CameraSelector.Builder().requireLensFacing(selectorCamera).build();

            imageAnalysis = new ImageAnalysis.Builder()
                    .setImageQueueDepth(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_RGBA_8888)
                    .build();

            imageAnalysis.setAnalyzer(cameraExecutor, imageProxy -> {
                // Implementa aquí tu lógica de análisis de imagen
            });

            imagePreview = new Preview.Builder()
                    .setTargetAspectRatio(aspectRatio)
                    .build();

            ProcessCameraProvider cameraProvider = null;
            try {
                cameraProvider = ProcessCameraProvider.getInstance(this).get();
            } catch (ExecutionException e) {
                throw new RuntimeException(e);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            if (cameraPreview) {
                cameraProvider.bindToLifecycle((LifecycleOwner) this, cameraSelector, imagePreview, imageAnalysis);
                view.setImplementationMode(PreviewView.ImplementationMode.COMPATIBLE);
                imagePreview.setSurfaceProvider(view.getSurfaceProvider());
            } else {
                cameraProvider.bindToLifecycle((LifecycleOwner) this, cameraSelector, imageAnalysis);
            }
        }, ContextCompat.getMainExecutor(this));

        ifStartCamera = true;
    }
}
