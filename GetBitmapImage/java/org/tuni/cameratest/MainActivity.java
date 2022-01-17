package org.tuni.cameratest;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;

import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;

import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity {

    ImageView imageView;
    Button galleryButton, cameraButton;

    ActivityResultLauncher<Intent> activityResultLauncher;
    ActivityResultLauncher<String> mGetContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = findViewById(R.id.imageView);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

        galleryButton = findViewById(R.id.buttonGallery);
        cameraButton = findViewById(R.id.buttonCamera);

        activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        assert data != null;
                        Bundle extras = data.getExtras();

                        Bitmap imageBitmap = (Bitmap) extras.get("data");
                        //imageView.setImageBitmap(imageBitmap);  //working
                        Uri uri = (Uri) getImageUri(this, imageBitmap);
                        Toast.makeText(this, "" + getRealPathFromURI(uri), Toast.LENGTH_SHORT).show();
                        Log.d("camera uri:",  uri.toString());
                        Log.d("camera uri real path:",  getRealPathFromURI(uri));
                        imageView.setImageURI(uri); //working
                    }
                });
        mGetContent = registerForActivityResult(new ActivityResultContracts.GetContent(),
                uri -> {
                    imageView.setImageURI(uri);
                    Toast.makeText(this, "" + uri, Toast.LENGTH_SHORT).show();
                    Log.d("gallery uri",  uri.toString());
                });

        galleryButton.setOnClickListener(v -> getImageFromDeviceGallery());
        cameraButton.setOnClickListener(v -> takePhotoFromCamera());
    }

    private void getImageFromDeviceGallery() {
        mGetContent.launch("image/*");
    }

    private void takePhotoFromCamera() {
        Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        activityResultLauncher.launch(takePhotoIntent);
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        //Bitmap OutImage = Bitmap.createScaledBitmap(inImage, 1000, 1000,true);
        Bitmap OutImage = cropBitmapImage(inImage);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), OutImage, "Title", null);
        return Uri.parse(path);
    }

    public String getRealPathFromURI(Uri uri) {
        String path = "";
        if (getContentResolver() != null) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            if(cursor!=null && cursor.getCount() > 0) {
                cursor.moveToFirst();
                int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                path = cursor.getString(idx);
                cursor.close();
            }
        }
        return path;
    }

    Bitmap cropBitmapImage(Bitmap srcBmp) {
        Bitmap dstBmp;
        if (srcBmp.getWidth() >= srcBmp.getHeight()){
            dstBmp = Bitmap.createBitmap(
                    srcBmp,
                    srcBmp.getWidth()/2 - srcBmp.getHeight()/2,
                    0,
                    srcBmp.getHeight(),
                    srcBmp.getHeight()
            );
        } else {
            dstBmp = Bitmap.createBitmap(
                    srcBmp,
                    0,
                    srcBmp.getHeight()/2 - srcBmp.getWidth()/2,
                    srcBmp.getWidth(),
                    srcBmp.getWidth()
            );
        }
        return Bitmap.createScaledBitmap(dstBmp, 100, 100, true);
    }
}