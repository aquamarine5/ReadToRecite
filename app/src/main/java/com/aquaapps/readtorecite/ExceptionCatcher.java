package com.aquaapps.readtorecite;

import android.app.AlertDialog;

import androidx.appcompat.app.AppCompatActivity;

public class ExceptionCatcher {
    public static void CatchException(Exception exception, AppCompatActivity activity) {
        new AlertDialog.Builder(activity)
                .setTitle(exception.getMessage())
                .setMessage(exception.getStackTrace()[0].toString())
                .setPositiveButton("确定", (dialogInterface, i) -> {
                    // TODO: write to log
                })
                .create()
                .show();

    }
}
