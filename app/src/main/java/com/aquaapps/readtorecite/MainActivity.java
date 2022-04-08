package com.aquaapps.readtorecite;

import static androidx.camera.video.VideoRecordEvent.Finalize.*;

import android.Manifest;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.video.FallbackStrategy;
import androidx.camera.video.FileOutputOptions;
import androidx.camera.video.MediaStoreOutputOptions;
import androidx.camera.video.OutputOptions;
import androidx.camera.video.Quality;
import androidx.camera.video.QualitySelector;
import androidx.camera.video.Recorder;
import androidx.camera.video.Recording;
import androidx.camera.video.VideoCapture;
import androidx.camera.video.VideoRecordEvent;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {
    public ProcessCameraProvider cameraProvider;
    public Recorder recorder;
    public Recording recording=null;
    public VideoCapture<Recorder> videoCapture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> {
            if(recording==null) {
                // Start record
                ContentValues values = new ContentValues();
                values.put(MediaStore.Video.Media.DISPLAY_NAME,
                        "ReadToRecite-" + new SimpleDateFormat("yyyyMMdd-HHmmss", Locale.ENGLISH).format(new Date()) + ".mp4");
                MediaStoreOutputOptions options = new MediaStoreOutputOptions.Builder(
                        this.getContentResolver(), MediaStore.Video.Media.EXTERNAL_CONTENT_URI)
                        .setContentValues(values)
                        .build();
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, 114514);
                }

                recording = videoCapture.getOutput().prepareRecording(this, options)
                        .withAudioEnabled()
                        .start(ContextCompat.getMainExecutor(this), videoRecordEvent -> {
                            if(videoRecordEvent instanceof VideoRecordEvent.Finalize){
                                VideoRecordEvent.Finalize finalizeEvent= (VideoRecordEvent.Finalize) videoRecordEvent;
                                Log.e("Debug", String.valueOf(finalizeEvent.getError()));
                                OutputOptions outputOptions=finalizeEvent.getOutputOptions();
                                if(outputOptions instanceof FileOutputOptions)
                                    Log.e("Debug",((FileOutputOptions) outputOptions).getFile().getAbsolutePath());
                            }
                        });

                fab.setImageResource(android.R.drawable.ic_menu_save);

            } else { // Stop record
                recording.stop();
                recording=null;
                fab.setImageResource(android.R.drawable.ic_media_play); // start
            }
        });
        // Instance Camera
        try{
            cameraProvider=ProcessCameraProvider.getInstance(this).get();
            Preview preview=new Preview.Builder().build();
            PreviewView previewView=findViewById(R.id.previewView);
            CameraSelector cameraSelector=new CameraSelector.Builder()
                    .requireLensFacing(CameraSelector.LENS_FACING_FRONT)
                    .build();
            preview.setSurfaceProvider(previewView.getSurfaceProvider());

            QualitySelector qs=QualitySelector.fromOrderedList(
                    QualitySelector.getSupportedQualities(
                            cameraSelector.filter(cameraProvider.getAvailableCameraInfos()).get(0)),
                    FallbackStrategy.higherQualityOrLowerThan(Quality.SD));
            recorder=new Recorder.Builder()
                    .setQualitySelector(qs)
                    .build();
            videoCapture=VideoCapture.withOutput(recorder);
            Camera camera=cameraProvider.bindToLifecycle(this,cameraSelector,videoCapture,preview);

        }
        catch(ExecutionException | InterruptedException e){
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
