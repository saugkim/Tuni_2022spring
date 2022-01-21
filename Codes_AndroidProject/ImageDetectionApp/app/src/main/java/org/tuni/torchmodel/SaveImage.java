package org.tuni.torchmodel;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class SaveImage {

    final static String TAG = "SaveImage/External...";
    final static String TAG1 = "SaveImage/Media...";

    private static void SaveExternalStorage(Context context, Bitmap bitmap){
        File mediaFile;
        File mediaStorageLocation = new File(Environment.getExternalStorageDirectory()
                + "/Android/data/" + context.getString(R.string.app_name));
              //  + BuildConfig.APPLICATION_ID );
        if (! mediaStorageLocation.exists()){
            if (! mediaStorageLocation.mkdirs()){
                return;
            }
        }
        String mediaFileName = createMediaFilename("png");
        mediaFile = new File(mediaStorageLocation.getPath() + File.separator + mediaFileName);

        try {
            FileOutputStream fileOutputStream = new FileOutputStream(mediaFile);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
            Log.d(TAG, "File saved: " + mediaFile.getName() );
            fileOutputStream.close();
        } catch (FileNotFoundException e) {
            Log.d(TAG, "File not found: " + e.getMessage());
        } catch (IOException e) {
            Log.d(TAG, "Error accessing file: " + e.getMessage());
        }
    }

    private static String createMediaFilename(String extension){
        String timeStamp = new SimpleDateFormat("dd-MM-yyyy_HHmm", Locale.getDefault()).format(new Date());
        return "ID_" + timeStamp + "." + extension;
    }

    public static void SAVE(Context context, Bitmap imageBitmap) {

        OutputStream outputStream ;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
        {
            ContentResolver resolver = context.getContentResolver();
            ContentValues contentValues = new ContentValues();
            String mediaFileName = createMediaFilename("jpg");
            contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, mediaFileName);
            contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg");
            contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + File.separator + "ID");
            Uri imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,contentValues);
            try {
                outputStream =  resolver.openOutputStream(Objects.requireNonNull(imageUri) );
                imageBitmap.compress(Bitmap.CompressFormat.JPEG,100,outputStream);
                Objects.requireNonNull(outputStream);
                Log.d(TAG1, "Image saved to " + imageUri.toString());
                Toast.makeText(context, "Image Saved", Toast.LENGTH_LONG).show();
            } catch (Exception e) {
                Toast.makeText(context, "Image Not Saved: \n "+e, Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        } else {
            SaveExternalStorage(context, imageBitmap);
        }
    }
}