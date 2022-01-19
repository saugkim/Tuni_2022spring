package org.tuni.cameratest;

import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class SaveMediaExternally {

    final static String TAG = "SaveMedia...STORE_IMAGE";

    public static void STORE_IMAGE(Bitmap bitmap, String extension){
        File mediaFile = getOutputMediaFile(extension);
        if (mediaFile == null) {
            Log.d(TAG,"Error creating media file, check storage permissions: ");
            return;
        }

        try {
            FileOutputStream fileOutputStream = new FileOutputStream(mediaFile);
            bitmap.compress(Bitmap.CompressFormat.PNG, 90, fileOutputStream);
            Log.d(TAG, "File saved: " + mediaFile.getName() );
            fileOutputStream.close();
        } catch (FileNotFoundException e) {
            Log.d(TAG, "File not found: " + e.getMessage());
        } catch (IOException e) {
            Log.d(TAG, "Error accessing file: " + e.getMessage());
        }
    }

    private static File getOutputMediaFile(String fileExtension){
        File mediaStorageLocation = new File(Environment.getExternalStorageDirectory()
                + "/Android/data/"
                + BuildConfig.APPLICATION_ID );
        // This location works best if you want the created images to be shared between applications and persist after your app has been uninstalled.
        // Create the storage directory if it does not exist
        if (! mediaStorageLocation.exists()){
            if (! mediaStorageLocation.mkdirs()){
                return null;
            }
        }
        // Create a media file name
        String timeStamp = new SimpleDateFormat("dd-MM-yyyy_HHmm", Locale.getDefault()).format(new Date());
        String mediaFileName = "MI_" + timeStamp + "." + fileExtension;
        return new File(mediaStorageLocation.getPath() + File.separator + mediaFileName);
    }
}