package org.tuni.cameratest;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import static android.content.Context.*;
import static android.content.Context.MODE_PRIVATE;

public class SaveAndLoadTextInternally {

    final static String TAG1 = "SaveAnd...SAVE_TEXT";
    final static String TAG2 = "SaveAnd...LOAD_TEXT";
    final private static String FILE_NAME = BuildConfig.APPLICATION_ID + ".txt";

    public static void SAVE_TEXT(Context context, String inputText) {

        FileOutputStream fileOutputStream = null;

        try {
            fileOutputStream = context.openFileOutput(FILE_NAME, MODE_APPEND | MODE_PRIVATE);
            fileOutputStream.write(inputText.getBytes());
            fileOutputStream.write(10);

            Log.d(TAG1, "text appended to " + FILE_NAME);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fileOutputStream != null) {
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static String LOAD_TEXT(Context context) {

        FileInputStream fileInputStream = null;
        String output = null;

        try {
            fileInputStream = context.openFileInput(FILE_NAME);
            BufferedReader br = new BufferedReader(new InputStreamReader(fileInputStream));

            StringBuilder sb = new StringBuilder();
            String line;

            while ((line = br.readLine()) != null) {
                sb.append(line).append("\n");
            }
            output = sb.toString();
            Log.d(TAG2, "loading text from saved file... " + output);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fileInputStream != null) {
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return output;
    }

    public static void REMOVE_ALL_TEXT(Context context) {
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = context.openFileOutput(FILE_NAME, MODE_PRIVATE);
            fileOutputStream.write("".getBytes());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fileOutputStream != null) {
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
