package com.aquaapps.readtorecite;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

public class Properties {
    private static Properties properties = null;

    private JSONObject propertiesContent;

    public File propertiesFile;

    // TODO: remove static
    public static AppCompatActivity activity;


    public static void initialize(AppCompatActivity activity) {
        properties = new Properties();
        properties.activity = activity;
        properties.propertiesFile = DataStorage.getFile(activity, DataLabels.PROPERTIES);
        try {
            if (properties.propertiesFile.exists()) {
                properties.propertiesContent = new JSONObject(DataStorage.readFile(properties.propertiesFile));
            } else {
                properties.propertiesContent = Properties.setup();
            }
        } catch (IOException | JSONException e) {
            ExceptionCatcher.CatchException(e, activity);
        }
    }

    public static String readString(String propertiesKey) {
        try {
            return properties.propertiesContent.getString(propertiesKey);
        } catch (JSONException e) {
            ExceptionCatcher.CatchException(e, activity);
            return null;
        }
    }

    public static int readInt(String propertiesKey) {
        try {
            return properties.propertiesContent.getInt(propertiesKey);
        } catch (JSONException e) {
            ExceptionCatcher.CatchException(e, activity);
            return 0;
        }
    }

    public static void write(String propertiesKey, Object values) {
        try {
            properties.propertiesContent.put(propertiesKey, values);
        } catch (JSONException e) {
            ExceptionCatcher.CatchException(e, activity);
        }
    }

    public static JSONObject setup()
            throws JSONException, IOException {
        JSONObject object = new JSONObject();
        object.put(PropertiesKeys.LAST_RECITE_CONTENT, "");

        object.put(PropertiesKeys.SCROLL_DISTANCE, 800);
        DataStorage.writeFile(properties.propertiesFile, object.toString());
        return object;
    }

    public static void save() {
        try {
            DataStorage.writeFile(properties.propertiesFile, properties.propertiesContent.toString());
        } catch (IOException e) {
            ExceptionCatcher.CatchException(e, activity);
        }
    }
}
