package org.tuni.cameraproject;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.core.UseCaseGroup;
import androidx.camera.core.ViewPort;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.util.Rational;
import android.util.Size;
import android.view.Surface;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

//https://stackoverflow.com/questions/66851770/android-camerax-returns-success-but-not-saving-picture

public class MainActivity extends AppCompatActivity {
    public static String TAG = "ZZ Main";

    public static String SHARED_KEY = "org.tuni.cameraproject.sharedPreferences";
    public static String URI_STRING_KEY = "org.tuni.cameraproject.uri_string.key";

    public static String REL_PATH = Environment.DIRECTORY_DCIM + File.separator + "TEMP";

    Executor executor = Executors.newSingleThreadExecutor();
    private final String[] REQUIRED_PERMISSIONS = new String[] {
            "android.permission.CAMERA",
        };
    private final String[] REQUIRED_PERMISSIONS_28 = new String[] {
            "android.permission.CAMERA",
            "android.permission.WRITE_EXTERNAL_STORAGE"
        };
    public static int REQUEST_CODE_PERMISSIONS = 1001;

    SharedPreferences sharedPreferences;

    PreviewView previewView;
    ImageView displayView;

    ProgressBar progressBar;
    Button captureButton;
    Button showListButton;

    ImageCapture imageCaptureInstance;

    List<String> file_uris;
    int rotationAngle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        previewView = findViewById(R.id.previewView);
        displayView = findViewById(R.id.savedImageView);
        progressBar = findViewById(R.id.progressBar);
        captureButton = findViewById(R.id.takePhotoButton);
        showListButton = findViewById(R.id. showPhotosButton);

        sharedPreferences = this.getSharedPreferences(SHARED_KEY, MODE_PRIVATE);

        file_uris = getUriList();
        Log.d(TAG, "number of saved files: " + file_uris.size());

        resetUI();

        if (allPermissionsGranted()) {
            startCamera();
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS);
            } else {
                ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS_28, REQUEST_CODE_PERMISSIONS);
            }
        }

        showListButton.setOnClickListener(view -> {
            updateSharedPreferences();
            startActivity(new Intent(this, ImagelistActivity.class));
        });

        captureButton.setOnClickListener(view-> {
            if (imageCaptureInstance == null) {
                Log.d(TAG, "imageCapture instance is null");
                return;
            }
            displayView.setImageResource(android.R.color.transparent);
            progressBar.setVisibility(View.VISIBLE);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                ContentResolver resolver = this.getContentResolver();
                ContentValues contentValues = new ContentValues();
                contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, createDisplayName());
                contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/png");
                contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, REL_PATH);

                ImageCapture.OutputFileOptions outputFileOptions =
                        new ImageCapture.OutputFileOptions
                                .Builder(resolver, MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
                                .build();
                imageCaptureInstance.takePicture(outputFileOptions, executor, new ImageCapture.OnImageSavedCallback() {
                    @Override
                    public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
                        executor.execute(() -> runOnUiThread(() -> displaySavedImageFromUri(outputFileResults.getSavedUri())));
                    }
                    @Override
                    public void onError(@NonNull ImageCaptureException exception) {
                        exception.printStackTrace();
                    }
                });

//                imageCaptureInstance.takePicture(executor, new ImageCapture.OnImageCapturedCallback() {
//                    @Override
//                    public void onCaptureSuccess(@NonNull ImageProxy image) {
//                        super.onCaptureSuccess(image);
//                        int rot = image.getImageInfo().getRotationDegrees();
//                        Bitmap bitmap = convertImageProxyToBitmap(image);
//                        image.close();
//                        rotationAngle = rot;
//                        Log.d(TAG, "image rotation: " + rot);
//                        executor.execute(() -> runOnUiThread(() -> displayCapturedImage(bitmap, rot)));
//                        try {
//                            saveImageToMedia(bitmap);
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                    @Override
//                    public void onError(@NonNull ImageCaptureException exception) {
//                        super.onError(exception);
//                    }
//                });
            } else {
                File file = getImageFile();
                ImageCapture.OutputFileOptions outputFileOptions =
                        new ImageCapture.OutputFileOptions
                                .Builder(file)
                                .build();
                imageCaptureInstance.takePicture(outputFileOptions, executor, new ImageCapture.OnImageSavedCallback () {
                    @Override
                    public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
                        executor.execute(() -> runOnUiThread(() -> displaySavedImageFromUri(outputFileResults.getSavedUri())));
                       // Log.d(TAG, "image saved as " + file.getName());
                    }
                    @Override
                    public void onError(@NonNull ImageCaptureException error) {
                        error.printStackTrace();
                    }
                });
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        updateSharedPreferences();
        resetUI();
    }

    private void resetUI() {
        displayView.setImageResource(android.R.color.transparent);
        displayView.destroyDrawingCache();

        progressBar.setVisibility(View.INVISIBLE);
    }

    private void startCamera() {
        final ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(this);

        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                bindPreview(cameraProvider);
            } catch (ExecutionException | InterruptedException e) {
                // No errors need to be handled for this Future.
                // This should never be reached.
            }
        }, ContextCompat.getMainExecutor(this));
    }

    void bindPreview(@NonNull ProcessCameraProvider cameraProvider) {
        int width = previewView.getWidth();
        int height = previewView.getHeight();

        Preview preview = new Preview.Builder()
                .setTargetResolution(new Size(width, height))
                .setTargetRotation(Surface.ROTATION_0)
                .build();

        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build();

        ImageAnalysis imageAnalysis = new ImageAnalysis.Builder()
                .build();

        ImageCapture.Builder builder = new ImageCapture.Builder();

        final ImageCapture imageCapture = builder
                .setTargetRotation(this.getWindowManager().getDefaultDisplay().getRotation())
                .setTargetResolution(new Size(width, height))
                .build();

        preview.setSurfaceProvider(previewView.getSurfaceProvider());

        ViewPort viewPort =
                new ViewPort.Builder(new Rational(width, height), preview.getTargetRotation()).build();

        UseCaseGroup useCaseGroup = new UseCaseGroup.Builder()
                .addUseCase(preview)
                .addUseCase(imageAnalysis)
                .addUseCase(imageCapture)
                .setViewPort(viewPort)
                .build();

        cameraProvider.unbindAll();
  //      cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageAnalysis, imageCapture);
        cameraProvider.bindToLifecycle(this, cameraSelector,useCaseGroup);
        imageCaptureInstance = imageCapture;
    }

    public void displaySavedImageFromUri(Uri uri) {
        displayView.setImageURI(uri);
        progressBar.setVisibility(View.GONE);
        file_uris.add(uri.toString());
    }

    public File getImageFile() {
        File dir = new File(Environment.getExternalStorageDirectory().toString() + "/images");
        if (!dir.exists() && !dir.mkdirs()) {
            return null;
        }
        //SimpleDateFormat mDateFormat = new SimpleDateFormat("yyyy-MM-dd-HHmmss", Locale.getDefault());
        //return new File(dir.getPath(), mDateFormat.format(new Date())+ ".png");
        return new File(dir.getPath(), createDisplayName());
    }

    private void updateSharedPreferences(){
        Gson gson = new Gson();
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(URI_STRING_KEY, gson.toJson(file_uris));
        editor.apply();
    }

    public String createDisplayName() {
        String timeStamp = new SimpleDateFormat("yyyy-MM-dd-HHmmss", Locale.getDefault()).format(new Date());
        return String.format("CameraTest_%s.png", timeStamp);
    }

    private boolean allPermissionsGranted() {
        for(String permission : Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q ? REQUIRED_PERMISSIONS : REQUIRED_PERMISSIONS_28){
            Log.d(TAG, "permission is " + permission);
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED){
                Log.d(TAG, "false denied");
                return false;
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                startCamera();
            } else {
                Toast.makeText(this, "Permissions not granted by the user.", Toast.LENGTH_SHORT).show();
                //this.finish();
            }
        }
    }

    private List<String> getUriList() {
        Gson gson = new Gson();
        if (sharedPreferences.getString(URI_STRING_KEY, null) == null) {
            return new ArrayList<>();
        }
        String json = sharedPreferences.getString(URI_STRING_KEY, null);
        Type type = new TypeToken<List<String>>(){}.getType();
        return gson.fromJson(json, type);
    }


    @RequiresApi(api = Build.VERSION_CODES.Q)
    private void saveImageToMedia(Bitmap bitmap) throws IOException {
        Matrix matrix = new Matrix();
        matrix.postRotate(rotationAngle);
        Bitmap rotatedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);

        ContentResolver resolver = this.getContentResolver();
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, createDisplayName());
        contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/png");
        contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, REL_PATH);

        Uri imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);

        file_uris.add(imageUri.toString());

        OutputStream fos = resolver.openOutputStream(imageUri);
        rotatedBitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
        fos.flush();
        fos.close();
    }
    private Bitmap convertImageProxyToBitmap(ImageProxy image) {
        ByteBuffer byteBuffer = image.getPlanes()[0].getBuffer();
        byteBuffer.rewind();
        byte[] bytes = new byte[byteBuffer.capacity()];
        byteBuffer.get(bytes);
        byte[] clonedBytes = bytes.clone();
        return BitmapFactory.decodeByteArray(clonedBytes, 0, clonedBytes.length);
    }
    public void displayCapturedImage(Bitmap bitmap, int rot) {
        displayView.setRotation(rot);
        displayView.setImageBitmap(bitmap);
        progressBar.setVisibility(View.GONE);
    }
    public void displaySavedImage(File file) {
        if (file.exists()) {
            Uri uri = Uri.fromFile(file);
            displayView.setImageURI(uri);
            progressBar.setVisibility(View.GONE);

            Log.d(TAG, "uri: " + uri.toString());
            file_uris.add(uri.toString());
        }
    }
}