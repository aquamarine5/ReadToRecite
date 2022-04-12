package com.aquaapps.readtorecite;

import android.app.AlertDialog;

import androidx.appcompat.app.AppCompatActivity;

public class ExceptionCatcher {
    public static void CatchException(Exception exception, String function, AppCompatActivity activity) {
        new AlertDialog.Builder(activity)
                .setTitle(function)
                .setMessage(exception.getMessage())
                .setPositiveButton("确定", (dialogInterface, i) -> {
                    // TODO: write to log
                })
                .create()
                .show();

    }
}
