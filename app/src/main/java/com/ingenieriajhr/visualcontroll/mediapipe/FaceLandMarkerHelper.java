package com.ingenieriajhr.visualcontroll.mediapipe;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.SystemClock;
import android.util.Log;
import androidx.annotation.VisibleForTesting;
import androidx.camera.core.ImageProxy;
import com.google.mediapipe.framework.image.BitmapImageBuilder;
import com.google.mediapipe.framework.image.MPImage;
import com.google.mediapipe.tasks.core.BaseOptions;
import com.google.mediapipe.tasks.core.Delegate;
import com.google.mediapipe.tasks.vision.core.RunningMode;
import com.google.mediapipe.tasks.vision.facelandmarker.FaceLandmarker;
import com.google.mediapipe.tasks.vision.facelandmarker.FaceLandmarkerResult;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class FaceLandMarkerHelper {

    private static final String TAG = "FaceLandmarkerHelper";
    private static final String MP_FACE_LANDMARKER_TASK = "face_landmarker.task";

    public static final int DELEGATE_CPU = 0;
    public static final int DELEGATE_GPU = 1;
    public static final float DEFAULT_FACE_DETECTION_CONFIDENCE = 0.5F;
    public static final float DEFAULT_FACE_TRACKING_CONFIDENCE = 0.5F;
    public static final float DEFAULT_FACE_PRESENCE_CONFIDENCE = 0.5F;
    public static final int DEFAULT_NUM_FACES = 1;

    private static final int OTHER_ERROR = 0;
    private static final int GPU_ERROR = 1;

    private float minFaceDetectionConfidence = DEFAULT_FACE_DETECTION_CONFIDENCE;
    private float minFaceTrackingConfidence = DEFAULT_FACE_TRACKING_CONFIDENCE;
    private float minFacePresenceConfidence = DEFAULT_FACE_PRESENCE_CONFIDENCE;
    private int maxNumFaces = DEFAULT_NUM_FACES;
    private int currentDelegate = DELEGATE_GPU;
    private RunningMode runningMode = RunningMode.LIVE_STREAM;
    private final Context context;
    private final LandmarkerListener faceLandmarkerHelperListener;
    private FaceLandmarker faceLandmarker;

    /**Time recognition **/
    private static final int MAX_FRAME_PROCESSING_INTERVAL_MS = 150; // Intervalo máximo de procesamiento en milisegundos
    private long lastProcessedFrameTime = 0;

    private MPImage cachedMPImage; // Almacena la última imagen procesada



    public FaceLandMarkerHelper(Context context, LandmarkerListener faceLandmarkerHelperListener) {
        this.context = context;
        this.faceLandmarkerHelperListener = faceLandmarkerHelperListener;
        setupFaceLandmarker();
    }

    public void clearFaceLandmarker() {
        if (faceLandmarker != null) {
            faceLandmarker.close();
            faceLandmarker = null;
        }
    }

    public boolean isClose() {
        return faceLandmarker == null;
    }

    public void setupFaceLandmarker() {
        BaseOptions baseOptions = switch (currentDelegate) {
            case DELEGATE_CPU ->
                    BaseOptions.builder().setDelegate(Delegate.CPU).setModelAssetPath(MP_FACE_LANDMARKER_TASK).build();
            case DELEGATE_GPU ->
                    BaseOptions.builder().setDelegate(Delegate.GPU).setModelAssetPath(MP_FACE_LANDMARKER_TASK).build();
            default -> throw new IllegalArgumentException("Invalid delegate: " + currentDelegate);
        };

        FaceLandmarker.FaceLandmarkerOptions options = FaceLandmarker.FaceLandmarkerOptions.builder()
                .setBaseOptions(baseOptions)
                .setMinFaceDetectionConfidence(minFaceDetectionConfidence)
                .setMinTrackingConfidence(minFaceTrackingConfidence)
                .setMinFacePresenceConfidence(minFacePresenceConfidence)
                .setNumFaces(maxNumFaces)
                .setOutputFaceBlendshapes(true)
                .setRunningMode(runningMode)
                .setResultListener(this::returnLivestreamResult)
                .setErrorListener(this::returnLivestreamError)
                .build();

        try {
            faceLandmarker = FaceLandmarker.createFromOptions(context, options);
        } catch (IllegalStateException e) {
            faceLandmarkerHelperListener.onError("Face Landmarker failed to initialize. See error logs for details",1);
            Log.e(TAG, "MediaPipe failed to load the task with error: " + e.getMessage());
        } catch (RuntimeException e) {
            faceLandmarkerHelperListener.onError("Face Landmarker failed to initialize. See error logs for details", GPU_ERROR);
            Log.e(TAG, "Face Landmarker failed to load model with error: " + e.getMessage());
        }
    }

    public void detectLiveStream(Bitmap bitmapBuffer, boolean isFrontCamera) {
        if (runningMode != RunningMode.LIVE_STREAM) {
            throw new IllegalArgumentException("Attempting to call detectLiveStream while not using RunningMode.LIVE_STREAM");
        }
        long frameTime = SystemClock.uptimeMillis();

        long currentTime = SystemClock.uptimeMillis();
        //if (currentTime - lastProcessedFrameTime < MAX_FRAME_PROCESSING_INTERVAL_MS) {
            // Si no ha pasado suficiente tiempo desde el último procesamiento, ignorar este frame
        //    return;
        //}
        lastProcessedFrameTime = currentTime;
        //Bitmap bitmapBuffer = Bitmap.createBitmap(imageProxy.getWidth(), imageProxy.getHeight(), Bitmap.Config.ARGB_8888);
        //imageProxy.getImage()
        //imageProxy.use(bitmapBuffer::copyPixelsFromBuffer);
        //imageProxy.close();
        //Matrix matrix = new Matrix();
        //matrix.postRotate(imageProxy.getImageInfo().getRotationDegrees());
        //if (isFrontCamera) {
        //    matrix.postScale(-1f, 1f, imageProxy.getWidth(), imageProxy.getHeight());
        // }
        //Bitmap rotatedBitmap = Bitmap.createBitmap(bitmapBuffer, 0, 0, bitmapBuffer.getWidth(), bitmapBuffer.getHeight(), matrix, true);


        MPImage mpImage = new BitmapImageBuilder(bitmapBuffer).build();
        detectAsync(mpImage, frameTime);
    }

    @VisibleForTesting
    public void detectAsync(MPImage mpImage, long frameTime) {
        if (faceLandmarker != null) {
            faceLandmarker.detectAsync(mpImage, frameTime);
        }
    }


    /*public VideoResultBundle detectVideoFile(Uri videoUri, long inferenceIntervalMs) {
        if (runningMode != RunningMode.VIDEO) {
            throw new IllegalArgumentException("Attempting to call detectVideoFile while not using RunningMode.VIDEO");
        }
        long startTime = SystemClock.uptimeMillis();
        boolean didErrorOccurred = false;
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(context, videoUri);
        String videoLengthMs = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        if (videoLengthMs == null) return null;
        long numberOfFrameToRead = Long.parseLong(videoLengthMs) / inferenceIntervalMs;
        List<FaceLandmarkerResult> resultList = new java.util.ArrayList<>();
        for (int i = 0; i <= numberOfFrameToRead; i++) {
            long timestampMs = i * inferenceIntervalMs;
            Bitmap frame = retriever.getFrameAtTime(timestampMs * 1000, MediaMetadataRetriever.OPTION_CLOSEST);
            if (frame != null) {
                Bitmap argb8888Frame = (frame.getConfig() == Bitmap.Config.ARGB_8888) ? frame : frame.copy(Bitmap.Config.ARGB_8888, false);
                MPImage mpImage = new BitmapImageBuilder(argb8888Frame).build();
                FaceLandmarkerResult detectionResult = faceLandmarker.detectForVideo(mpImage, timestampMs);
                if (detectionResult != null) {
                    resultList.add(detectionResult);
                } else {
                    didErrorOccurred = true;
                    faceLandmarkerHelperListener.onError("ResultBundle could not be returned in detectVideoFile");
                }
            } else {
                didErrorOccurred = true;
                faceLandmarkerHelperListener.onError("Frame at specified time could not be retrieved when detecting in video.");
            }
        }
        retriever.release();
        long inferenceTimePerFrameMs = (SystemClock.uptimeMillis() - startTime) / numberOfFrameToRead;
        if (didErrorOccurred) {
            return null;
        } else {
            //return new VideoResultBundle(resultList, inferenceTimePerFrameMs, frame.getHeight(), frame.getWidth());
        }
    }*/

    public ResultBundle detectImage(Bitmap image) {
        if (runningMode != RunningMode.IMAGE) {
            throw new IllegalArgumentException("Attempting to call detectImage while not using RunningMode.IMAGE");
        }
        long startTime = SystemClock.uptimeMillis();
        MPImage mpImage = new BitmapImageBuilder(image).build();
        FaceLandmarkerResult landmarkResult = faceLandmarker.detect(mpImage);
        if (landmarkResult != null) {
            long inferenceTimeMs = SystemClock.uptimeMillis() - startTime;
            return new ResultBundle(landmarkResult, inferenceTimeMs, image.getHeight(), image.getWidth());
        } else {
            faceLandmarkerHelperListener.onError("Face Landmarker failed to detect.",0);
            return null;
        }
    }

    private void returnLivestreamResult(@NotNull FaceLandmarkerResult result, @NotNull MPImage input) {
        if (result.faceLandmarks().size() > 0) {
            long finishTimeMs = SystemClock.uptimeMillis();
            long inferenceTime = finishTimeMs - result.timestampMs();
            faceLandmarkerHelperListener.onResults(new ResultBundle(result, inferenceTime, input.getHeight(), input.getWidth()));
        } else {
            faceLandmarkerHelperListener.onEmpty();
        }
    }

    private void returnLivestreamError(@NotNull RuntimeException error) {
        faceLandmarkerHelperListener.onError(error.getMessage() != null ? error.getMessage() : "An unknown error has occurred",0);
    }


    public class FaceLandmarkerHelper {
        public static final String TAG = "FaceLandmarkerHelper";
        private static final String MP_FACE_LANDMARKER_TASK = "face_landmarker.task";

        public static final int DELEGATE_CPU = 0;
        public static final int DELEGATE_GPU = 1;
        public static final float DEFAULT_FACE_DETECTION_CONFIDENCE = 0.5F;
        public static final float DEFAULT_FACE_TRACKING_CONFIDENCE = 0.5F;
        public static final float DEFAULT_FACE_PRESENCE_CONFIDENCE = 0.5F;
        public static final int DEFAULT_NUM_FACES = 1;
        public static final int OTHER_ERROR = 0;
        public static final int GPU_ERROR = 1;
    }



    public interface LandmarkerListener {
        void onError(String error, int errorCode);

        void onResults(ResultBundle resultBundle);

        default void onEmpty() {}
    }

    public static class ResultBundle {
        public final FaceLandmarkerResult result;
        public final long inferenceTime;
        public final int inputImageHeight;
        public final int inputImageWidth;

        public ResultBundle(FaceLandmarkerResult result, long inferenceTime, int inputImageHeight, int inputImageWidth) {
            this.result = result;
            this.inferenceTime = inferenceTime;
            this.inputImageHeight = inputImageHeight;
            this.inputImageWidth = inputImageWidth;
        }
    }

    public static class VideoResultBundle {
        public final List<FaceLandmarkerResult> results;
        public final long inferenceTime;
        public final int inputImageHeight;
        public final int inputImageWidth;

        public VideoResultBundle(List<FaceLandmarkerResult> results, long inferenceTime, int inputImageHeight, int inputImageWidth) {
            this.results = results;
            this.inferenceTime = inferenceTime;
            this.inputImageHeight = inputImageHeight;
            this.inputImageWidth = inputImageWidth;
        }
    }
}
