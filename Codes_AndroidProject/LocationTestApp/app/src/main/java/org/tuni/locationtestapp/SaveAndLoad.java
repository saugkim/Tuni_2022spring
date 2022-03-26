package org.tuni.locationtestapp;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class SaveAndLoad {

    final static String TAG1 = "SaveAnd...SAVE_TEXT";
    final static String TAG2 = "SaveAnd...LOAD_TEXT";
    final private static String FILE_NAME = "org.tuni.locationtestapp.txt";

    public static void SAVE_DATA(Context context, String inputText) {
        FileOutputStream fileOutputStream = null;

        try {
            fileOutputStream = context.openFileOutput(FILE_NAME, MODE_PRIVATE);
            fileOutputStream.write(inputText.getBytes());

            Log.d(TAG1, "data saved to " + FILE_NAME);
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

    public static List<String> LOAD_DATA(Context context) {

        FileInputStream fileInputStream = null;
        List<String> output = new ArrayList<>();

        try {
            fileInputStream = context.openFileInput(FILE_NAME);
            BufferedReader br = new BufferedReader(new InputStreamReader(fileInputStream));
            String line;
            while ( (line = br.readLine()) != null) output.add(line);
            Log.d(TAG2, "loading text from saved file... " + output.size());
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
