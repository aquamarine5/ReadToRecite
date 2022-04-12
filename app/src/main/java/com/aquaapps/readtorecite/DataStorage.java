package com.aquaapps.readtorecite;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class DataStorage {
    public static String readFile(File file)
            throws IOException {
        FileInputStream fis = new FileInputStream(file.getCanonicalFile());
        StringBuilder stringBuilder = new StringBuilder();
        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fis, StandardCharsets.UTF_8))) {
            String line = bufferedReader.readLine();
            while (line != null) {
                stringBuilder.append(line).append('\n');
                line = bufferedReader.readLine();
            }
        }
        return stringBuilder.toString();
    }

    public static void writeFile(File file, String contents)
            throws IOException {
        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(contents.getBytes(StandardCharsets.UTF_8));
        }
    }

    public static File getFile(AppCompatActivity activity, String dataLabels) {
        return new File(activity.getExternalFilesDir(null), dataLabels);
    }
}
