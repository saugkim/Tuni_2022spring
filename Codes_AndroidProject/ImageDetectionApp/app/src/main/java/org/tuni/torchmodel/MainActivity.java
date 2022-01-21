package org.tuni.torchmodel;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;

import org.pytorch.Module;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class MainActivity extends AppCompatActivity {

    final static String MODEL_OD = "mobilenet_scripted.pt";
    final static String MODEL_IS = "deeplabv3_scripted.pt";

    final static String DEFAULT_IMAGE_FILENAME = "default_image.jpg";

    ImageView imageView;
    Button galleryButton, runButtonOD, runButtonIS;

    Module module_od = null;
    Module module_is = null;
    Bitmap mBitmap = null;

    String mObjectName = "";
    Bitmap segmentedImage = null;

    ActivityResultLauncher<String> mGetContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = findViewById(R.id.imageView);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

        galleryButton = findViewById(R.id.galleryButton);
        runButtonOD = findViewById(R.id.runButtonOD);
        runButtonIS = findViewById(R.id.runButtonIS);

        try {
            mBitmap = BitmapFactory.decodeStream(getAssets().open(DEFAULT_IMAGE_FILENAME));
        } catch (IOException e) {
            Log.e("IMAGE NOT FOUND", "Error open image!", e);
            finish();
        }
        //imageView.setImageBitmap(mBitmap);

        mGetContent = registerForActivityResult(new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
                        imageView.setImageURI(uri);
                        //Log.d("gallery uri", uri.toString());
                        mBitmap = ImagePreprocessing.GET_BITMAP_FROM_URI(this, uri);
                    }
                    imageView.setImageBitmap(mBitmap);
                });

        galleryButton.setOnClickListener(v -> getImageFromDevice());

        runButtonOD.setOnClickListener(v-> {
            module_od = getModule(MODEL_OD);
            mObjectName = ObjectDetection.RUN_MODEL(module_od, mBitmap);
            getSupportFragmentManager().beginTransaction().replace(R.id.container,
                    ODFragment.newInstance(mObjectName)).commit();
        });


        runButtonIS.setOnClickListener(v-> {
            module_is = getModule(MODEL_IS);
            segmentedImage = ImageSegmentation.RUN_MODEL(module_is, mBitmap);
            getSupportFragmentManager().beginTransaction().replace(R.id.container,
                    ISFragment.newInstance(segmentedImage)).commit();
        });
    }

    private void getImageFromDevice() {
        mGetContent.launch("image/*");
    }

    public Module getModule(String model_name) {
        Module module = null;
        try {
            module = Module.load(assetFilePath(this, model_name));
        } catch (IOException e) {
            Log.e("MODEL NOT FOUND", "Error loading model!", e);
            finish();
        }
        return module;
    }

    public static String assetFilePath(Context context, String assetName) throws IOException {
        File file = new File(context.getFilesDir(), assetName);
        if (file.exists() && file.length() > 0) {
            return file.getAbsolutePath();
        }
        try (InputStream is = context.getAssets().open(assetName)) {
            try (OutputStream os = new FileOutputStream(file)) {
                byte[] buffer = new byte[4 * 1024];
                int read;
                while ((read = is.read(buffer)) != -1) {
                    os.write(buffer, 0, read);
                }
                os.flush();
            }
            return file.getAbsolutePath();
        }
    }
/*
    private Bitmap getBitmapFromUri(Uri imageUri) {
        Bitmap bitmap;
        Bitmap output = null;
        ContentResolver contentResolver = getContentResolver();
        try {
            if(Build.VERSION.SDK_INT < 28) {
                bitmap = MediaStore.Images.Media.getBitmap(contentResolver, imageUri);
            } else {
                ImageDecoder.Source source = ImageDecoder.createSource(contentResolver, imageUri);
                bitmap = ImageDecoder.decodeBitmap(source).copy(Bitmap.Config.RGBA_F16, true);
            }
            output = ImagePreprocessing.CENTER_CROP_AND_RESCALE(bitmap, 224);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return output;
    }
    */
}