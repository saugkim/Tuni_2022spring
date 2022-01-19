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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity {

    ImageView imageView;
    Button galleryButton, cameraButton;
    EditText multiText;

    ActivityResultLauncher<Intent> activityResultLauncher;
    ActivityResultLauncher<String> mGetContent;

    final static String TAG1 = "Main.TakePhoto";
    final static String TAG2 = "Main.SelectPhoto";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = findViewById(R.id.imageView);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        multiText = findViewById(R.id.editTextTextMultiLine);

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
                        imageView.setImageBitmap(imageBitmap);  //working

                        Uri uri = getImageUri(this, imageBitmap);
                        Toast.makeText(this, "" + getRealPathFromURI(uri), Toast.LENGTH_LONG).show();
                        Log.d(TAG1, "uri: " + uri.toString());
                        Log.d(TAG1, "uri real path: " + getRealPathFromURI(uri));
                        //imageView.setImageURI(uri); //working
                    }
                });

        mGetContent = registerForActivityResult(new ActivityResultContracts.GetContent(),
                uri -> {
                   /* try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                        SaveMediaExternally.STORE_IMAGE(bitmap, "png");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }*/

                    imageView.setImageURI(uri);
                    Log.d(TAG2,"uri: " + uri.toString());
                    SaveAndLoadTextInternally.SAVE_TEXT(this, uri.toString());
                });

        galleryButton.setOnClickListener(v -> mGetContent.launch("image/*") );
        cameraButton.setOnClickListener(v -> takePhotoFromCamera());
    }

    private void takePhotoFromCamera() {
        //multiText.setText(SaveAndLoadTextInternally.LOAD_TEXT(this));
        Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        activityResultLauncher.launch(takePhotoIntent);
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        //Bitmap OutImage = Bitmap.createScaledBitmap(inImage, 1000, 1000,true);
        Bitmap outImage = ImagePreprocessing.CENTER_CROP_AND_RESCALE(inImage, 400);
        //Bitmap outImage = cropBitmapImage(inImage);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), outImage, "Title", null);
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

}