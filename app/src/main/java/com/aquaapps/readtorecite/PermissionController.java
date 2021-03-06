package com.aquaapps.readtorecite;

import android.content.pm.PackageManager;
import android.util.Log;

import androidx.annotation.CheckResult;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresPermission;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.PermissionChecker;

public class PermissionController {
    public static void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        // TODO:
    }

    public static void requestPermissions(AppCompatActivity activity, @NonNull String[] permissions) {
        ActivityCompat.requestPermissions(activity, permissions, 114514);
        for (String permission :
                permissions) {
            //requestPermission(activity, permission);
        }
    }

    public static void requestPermission(AppCompatActivity activity, String permission) {
        if (ActivityCompat.checkSelfPermission(activity, permission) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(activity, new String[]{permission}, 114514);
            Log.d("a", "requestPermission: "+permission);
        }
    }
}
