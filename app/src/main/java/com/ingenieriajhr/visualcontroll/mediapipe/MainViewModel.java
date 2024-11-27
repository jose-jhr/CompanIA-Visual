package com.ingenieriajhr.visualcontroll.mediapipe;
import androidx.lifecycle.ViewModel;

/**
 * This ViewModel is used to store face landmarker helper settings
 */
public class MainViewModel extends ViewModel {


    private int _delegate = FaceLandMarkerHelper.FaceLandmarkerHelper.DELEGATE_GPU;
    private float _minFaceDetectionConfidence = FaceLandMarkerHelper.FaceLandmarkerHelper.DEFAULT_FACE_DETECTION_CONFIDENCE;
    private float _minFaceTrackingConfidence = FaceLandMarkerHelper.FaceLandmarkerHelper.DEFAULT_FACE_TRACKING_CONFIDENCE;
    private float _minFacePresenceConfidence = FaceLandMarkerHelper.FaceLandmarkerHelper.DEFAULT_FACE_PRESENCE_CONFIDENCE;
    private int _maxFaces = FaceLandMarkerHelper.FaceLandmarkerHelper.DEFAULT_NUM_FACES;

    public int getCurrentDelegate() {
        return _delegate;
    }

    public float getCurrentMinFaceDetectionConfidence() {
        return _minFaceDetectionConfidence;
    }

    public float getCurrentMinFaceTrackingConfidence() {
        return _minFaceTrackingConfidence;
    }

    public float getCurrentMinFacePresenceConfidence() {
        return _minFacePresenceConfidence;
    }

    public int getCurrentMaxFaces() {
        return _maxFaces;
    }

    public void setDelegate(int delegate) {
        _delegate = delegate;
    }

    public void setMinFaceDetectionConfidence(float confidence) {
        _minFaceDetectionConfidence = confidence;
    }

    public void setMinFaceTrackingConfidence(float confidence) {
        _minFaceTrackingConfidence = confidence;
    }

    public void setMinFacePresenceConfidence(float confidence) {
        _minFacePresenceConfidence = confidence;
    }

    public void setMaxFaces(int maxResults) {
        _maxFaces = maxResults;
    }
}
