package org.tuni.project_vision;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.Preview;
import androidx.camera.core.UseCaseGroup;
import androidx.camera.core.ViewPort;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Rational;
import android.util.Size;
import android.view.Surface;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.common.util.concurrent.ListenableFuture;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class CaptureActivity extends AppCompatActivity {

    public static String URI_EXTRA = "org.tuni.project_vision.putExtraKey";

    public static String REL_PATH = Environment.DIRECTORY_DCIM + File.separator + "TEMP";

    Executor executor = Executors.newSingleThreadExecutor();

    PreviewView previewView;
    ImageView imageView;
    ProgressBar progressBar;
    Button captureButton;
    FloatingActionButton acceptButton;

    ImageCapture imageCaptureInstance;
    String imageUriString = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_capture);

        previewView = findViewById(R.id.previewView);
        imageView = findViewById(R.id.savedImageView);
        progressBar = findViewById(R.id.progressBar);
        captureButton = findViewById(R.id.captureButton);
        acceptButton = findViewById(R.id.acceptButton);

        resetUI();
        startCamera();

        captureButton.setOnClickListener(view -> {
            if (imageCaptureInstance == null) {
                Toast.makeText(this, "ImageCapture instance is null, clear memory and try again", Toast.LENGTH_LONG).show();
                return;
            }
            imageView.setImageResource(android.R.color.transparent);
            progressBar.setVisibility(View.VISIBLE);

            ImageCapture.OutputFileOptions outputFileOptions;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                ContentResolver resolver = this.getContentResolver();
                ContentValues contentValues = new ContentValues();
                contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, createDisplayName());
                contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/png");
                contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, REL_PATH);

                outputFileOptions = new ImageCapture.OutputFileOptions
                                .Builder(resolver, MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
                                .build();

            } else {
                File dir = new File(Environment.getExternalStorageDirectory().toString() + "/TemporaryFolder");
                if (!dir.exists() && !dir.mkdirs()) {
                    return;
                }
                File file = new File(dir.getPath(), createDisplayName());
                outputFileOptions = new ImageCapture.OutputFileOptions
                                .Builder(file)
                                .build();
            }

            imageCaptureInstance.takePicture(outputFileOptions, executor, new ImageCapture.OnImageSavedCallback() {
                @Override
                public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
                    executor.execute(() -> runOnUiThread(() -> displaySavedImageFromUri(Objects.requireNonNull(outputFileResults.getSavedUri()))));
                    imageUriString = Objects.requireNonNull(outputFileResults.getSavedUri()).toString();
                }
                @Override
                public void onError(@NonNull ImageCaptureException exception) {
                    exception.printStackTrace();
                }
            });
        });

        acceptButton.setOnClickListener(view -> {
            Intent data = new Intent();
            data.putExtra(URI_EXTRA, imageUriString);
            setResult(RESULT_OK, data);
            finish();
        });
    }

    private void resetUI() {
        imageView.setImageResource(android.R.color.transparent);
        imageView.destroyDrawingCache();
        progressBar.setVisibility(View.INVISIBLE);
        acceptButton.setVisibility(View.INVISIBLE);
    }

    /**
     * used to start camera to bind view to the previewView
     */
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

    /**
     * used to bind view to previewView
     * to get ImageCapture instance here
     * @param cameraProvider CameraProvider instance
     */
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
        cameraProvider.bindToLifecycle(this, cameraSelector,useCaseGroup);
        imageCaptureInstance = imageCapture;
    }

    /**
     * display capture and saved image to the ImageView UI
     * when image is displayed, the accept button shows up in bottom right corner
     * User can retake photo or accept to the main activity
     * @param uri saved uri of the image
     */
    public void displaySavedImageFromUri(Uri uri) {
        imageUriString = uri.toString();
        imageView.setImageURI(uri);
        acceptButton.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
    }

    /**
     * get temporary filename to save image
     * @return created filename based on the date-time
     */
    public String createDisplayName() {
        String timeStamp = new SimpleDateFormat("yyyy-MM-dd-HHmmss", Locale.getDefault()).format(new Date());
        return String.format("Temp_%s.png", timeStamp);
    }
}