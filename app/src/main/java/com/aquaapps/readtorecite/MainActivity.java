package com.aquaapps.readtorecite;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.camera.video.OutputResults;
import androidx.camera.video.VideoRecordEvent;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity {
    public CameraController cameraController;
    public TextInputEditText textInputEditText;

    public boolean isShowingPreview = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = findViewById(R.id.fab);
        textInputEditText=findViewById(R.id.input_text);
        fab.setOnClickListener(view -> {
            if (cameraController.isRecording()) {
                cameraController.stopRecord();
                fab.setImageResource(android.R.drawable.ic_media_play); // start
            } else {
                cameraController.startRecord(videoRecordEvent -> {
                    if (videoRecordEvent instanceof VideoRecordEvent.Finalize) {
                        VideoRecordEvent.Finalize finalizeEvent = (VideoRecordEvent.Finalize) videoRecordEvent;
                        OutputResults outputResults = finalizeEvent.getOutputResults();
                        int errorCode = finalizeEvent.getError();
                        if (errorCode != VideoRecordEvent.Finalize.ERROR_NONE) {
                            Toast.makeText(this, "错误: " + errorCode, Toast.LENGTH_LONG).show();
                        } else {
                            shareVideo(outputResults.getOutputUri());
                        }
                    }
                });
                fab.setImageResource(android.R.drawable.ic_menu_save);
            }
        });
        // Instance Camera
        cameraController = new CameraController(this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        PermissionController.onRequestPermissionsResult(requestCode,permissions,grantResults);
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_preview) {
            if (isShowingPreview) {
                cameraController.previewView.setVisibility(View.INVISIBLE);
                item.setTitle(R.string.action_preview_enabled);
            } else {
                cameraController.previewView.setVisibility(View.VISIBLE);
                item.setTitle(R.string.action_preview_disabled);
            }
            isShowingPreview = !isShowingPreview;
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode){
            case KeyEvent.KEYCODE_VOLUME_UP:
                textInputEditText.scrollBy(0,-800);
                return true;
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                textInputEditText.scrollBy(0,800);
                return true;
            default:
                return super.onKeyDown(keyCode, event);
        }
    }

    public void shareVideo(Uri videoUri) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        String[] splitResult = videoUri.getPath().split("\\.");
        if (splitResult[splitResult.length - 1].equals("3gp")) {
            intent.setType("video/3gpp");
        } else {
            intent.setType("video/mp4");
        }

        intent.putExtra(Intent.EXTRA_STREAM, videoUri);
        startActivity(Intent.createChooser(intent, "分享背诵视频"));
    }

}
