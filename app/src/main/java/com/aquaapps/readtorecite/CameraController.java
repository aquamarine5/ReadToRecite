package com.aquaapps.readtorecite;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;

import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.video.FallbackStrategy;
import androidx.camera.video.MediaStoreOutputOptions;
import androidx.camera.video.Quality;
import androidx.camera.video.QualitySelector;
import androidx.camera.video.Recorder;
import androidx.camera.video.Recording;
import androidx.camera.video.VideoCapture;
import androidx.camera.video.VideoRecordEvent;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import androidx.core.util.Consumer;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutionException;


public class CameraController {
    public PreviewView previewView;
    public ProcessCameraProvider cameraProvider;
    public Recorder recorder;
    public Recording recording = null;
    public VideoCapture<Recorder> videoCapture;
    public AppCompatActivity activity;

    public CameraController(AppCompatActivity activity) {
        this.activity = activity;
        try {

            PermissionController.requestPermissions(activity, new String[]{
                    Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.CAMERA
            });

            cameraProvider = ProcessCameraProvider.getInstance(activity).get();
            Preview preview = new Preview.Builder().build();
            previewView = activity.findViewById(R.id.previewView);
            CameraSelector cameraSelector = new CameraSelector.Builder()
                    .requireLensFacing(CameraSelector.LENS_FACING_FRONT)
                    .build();
            preview.setSurfaceProvider(previewView.getSurfaceProvider());

            QualitySelector qs = QualitySelector.fromOrderedList(
                    QualitySelector.getSupportedQualities(
                            cameraSelector.filter(cameraProvider.getAvailableCameraInfos()).get(0)),
                    FallbackStrategy.higherQualityOrLowerThan(Quality.SD));
            recorder = new Recorder.Builder()
                    .setQualitySelector(qs)
                    .build();
            videoCapture = VideoCapture.withOutput(recorder);
            cameraProvider.bindToLifecycle(activity, cameraSelector, videoCapture, preview);

        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public boolean isRecording() {
        return recording != null;
    }


    @SuppressLint("MissingPermission")
    public void startRecord(Consumer<VideoRecordEvent> videoRecordListener) {
        ContentValues values = new ContentValues();
        values.put(MediaStore.MediaColumns.DISPLAY_NAME,
                "ReadToRecite-" + new SimpleDateFormat("yyyyMMdd-HHmmss", Locale.ENGLISH).format(new Date()) + ".3gp");
        Uri videoCollection;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            videoCollection = MediaStore.Video.Media
                    .getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY);
        } else {
            videoCollection = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        }
        MediaStoreOutputOptions options = new MediaStoreOutputOptions.Builder(
                activity.getContentResolver(), videoCollection)
                .setContentValues(values)
                .build();

        recording = videoCapture.getOutput()
                .prepareRecording(activity, options)
                .withAudioEnabled()
                .start(ContextCompat.getMainExecutor(activity), videoRecordListener);
    }

    public void stopRecord() {
        recording.stop();
        recording = null;
    }

}
