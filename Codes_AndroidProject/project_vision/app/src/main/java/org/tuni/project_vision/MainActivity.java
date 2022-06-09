package org.tuni.project_vision;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import androidx.exifinterface.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.pytorch.IValue;
import org.pytorch.MemoryFormat;
import org.pytorch.Module;
import org.pytorch.Tensor;
import org.pytorch.torchvision.TensorImageUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    final static int MIN_INPUT_SIZE = 224;
    final static String TAG = "ZZ MainActivity: ";
    final static String MOBILE_NET = "mobilenet_scripted.pt";
    final static String REL_LOCATION = Environment.DIRECTORY_DCIM + File.separator + "PROJECT_OD";
    final static String IMAGE_TYPE = "image/png";
    public static String URI_EXTRA = "org.tuni.project_vision.putExtraKey";

    private final String[] REQUIRED_PERMISSIONS = new String[] {
            "android.permission.ACCESS_FINE_LOCATION",
            "android.permission.ACCESS_COARSE_LOCATION",
            "android.permission.ACCESS_NETWORK_STATE",
            "android.permission.INTERNET",
            "android.permission.CAMERA"
    };

    private final String[] REQUIRED_PERMISSIONS_28 = new String[] {
            "android.permission.ACCESS_FINE_LOCATION",
            "android.permission.ACCESS_COARSE_LOCATION",
            "android.permission.ACCESS_NETWORK_STATE",
            "android.permission.INTERNET",
            "android.permission.CAMERA",
            "android.permission.WRITE_EXTERNAL_STORAGE"
    };

    ImageView imageView;
    Button galleryButton, cameraButton, runButton, subButton, saveButton;
    TextView textView;

    RadioGroup radioGroup;
    RadioButton yesRadioButton, noRadioButton;

    Executor executor = Executors.newSingleThreadExecutor();

    LocationManager locationManager;
    LocationListener locationListener;
    Location currentLocation;

    ActivityResultLauncher<Intent> capturePhotoLauncher;
    ActivityResultLauncher<String> getContentLauncher;

    ActivityResultLauncher<String[]> requestMultiplePermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), isGranted -> {
                Log.d(TAG, "Launcher results: " + isGranted.toString());
                if (isGranted.containsValue(false)) {
                    Log.d(TAG, "Launcher, not granted");
                } else {
                    Log.d(TAG, "Launcher, all permission granted");
                }
            });

    Bitmap bitmapImage = null;

    String objectFilename = null;
    String objectUri = null;
    String objectLabel = null;
    float objectLongitude = -200;
    float objectLatitude = -200;

    Boolean isLabelCorrect = null;

    Boolean isDuplicated = false;
    Boolean isLocationPermissionGranted = false;

    ImageRepository imageRepository;

    CameraManager cameraManager;
    CameraCharacteristics characteristics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = findViewById(R.id.imageView);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

        galleryButton = findViewById(R.id.buttonSelect);
        cameraButton = findViewById(R.id.buttonTake);
        runButton = findViewById(R.id.buttonRun);

        subButton = findViewById(R.id.buttonStatistics);
        saveButton = findViewById(R.id.buttonSave);

        radioGroup = findViewById(R.id.radioGroup);
        yesRadioButton = findViewById(R.id.radioButtonYes);
        noRadioButton = findViewById(R.id.radioButtonNo);
        textView = findViewById(R.id.textViewObject);

        imageRepository = new ImageRepository(getApplication());

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationListener = location -> currentLocation = location;

        cameraManager = (CameraManager) this.getSystemService(CAMERA_SERVICE);

        try {
            String backId = null;
            for (String cameraId : cameraManager.getCameraIdList()) {
                CameraCharacteristics cameraCharacteristics = cameraManager.getCameraCharacteristics(cameraId);
                if (cameraCharacteristics.get(CameraCharacteristics.LENS_FACING) == CameraCharacteristics.LENS_FACING_BACK) {
                    backId = cameraId;
                    Log.d(TAG, "CameraId of lens facing back: " + cameraId);
                }
            }
            characteristics = cameraManager.getCameraCharacteristics(backId);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }

        checkAndRequestPermissions();

        resetUI();

        capturePhotoLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        assert data != null;
                        String uriString = data.getStringExtra(URI_EXTRA);
                        Log.d(TAG, "data getExtra uri string: " + uriString);
                        try {
                            //imageView.setImageURI(Uri.parse(uriString));
                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), Uri.parse(uriString));
                            Bitmap temp = getOrientatedBitmap(bitmap, Uri.parse(uriString));
                            imageView.setImageBitmap(temp);
                            bitmapImage = ImagePreprocessing.CENTER_CROP_AND_RESCALE(temp, MIN_INPUT_SIZE);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });

        getContentLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> {
                    try {
                       // imageView.setImageURI(uri);
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                        Bitmap temp = getOrientatedBitmap(bitmap, uri);
                        imageView.setImageBitmap(temp);
                        bitmapImage = ImagePreprocessing.CENTER_CROP_AND_RESCALE(temp, MIN_INPUT_SIZE);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Log.d(TAG,"uri from getContent launcher: " + uri);
                    String retrievedFilename = getFilenameFromUri(this, uri);
                    Log.d(TAG, "filename: " + retrievedFilename);
                    Toast.makeText(this,"filename from getContent: " + retrievedFilename, Toast.LENGTH_LONG).show();

                    if (retrievedFilename.startsWith("OD")) {
                        isDuplicated = true;
                    }
                });

        galleryButton.setOnClickListener(view -> {
            resetUI();
            getContentLauncher.launch("image/*");
        });

        cameraButton.setOnClickListener(view -> {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED) {
                Toast.makeText(this,"This feature need device camera, currently camera permission not accepted, you can take photo outside this app and use it", Toast.LENGTH_LONG).show();
                return;
            }
            resetUI();
            Intent captureIntent = new Intent(this, CaptureActivity.class);
            capturePhotoLauncher.launch(captureIntent);
            if (isLocationPermissionGranted) getLocation();
        });

        runButton.setOnClickListener(view -> detectObject(bitmapImage));

        radioGroup.setOnCheckedChangeListener((group, i) -> isLabelCorrect = i == R.id.radioButtonYes);

        saveButton.setOnClickListener(view -> {
            if (bitmapImage == null) {
                Toast.makeText(this, "Select Image and Run Image Recognition", Toast.LENGTH_LONG).show();
                return;
            }
            if (radioGroup.getCheckedRadioButtonId() == -1) {
                Toast.makeText(this, "Give Feedback first", Toast.LENGTH_LONG).show();
                return;
            }
            if (isDuplicated) {
                Toast.makeText(this, "This image is already in device", Toast.LENGTH_LONG).show();
                return;
            }

            try {
                storeImage(bitmapImage);
                saveImageDatabase();
                resetUI();
            } catch (IOException e) {
                Toast.makeText(this, "Failed to save image to the database", Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        });

        subButton.setOnClickListener(view-> {
            resetUI();
            startActivity(new Intent(this, StatisticsActivity.class));
        });
    }

    /**
     * Used to checking and asking permissions for camera action and getting location information
     * App working without those permissions but some features are not working for example taking photo inside app
     */
    private void checkAndRequestPermissions() {
        Log.d(TAG, "inside checkPermissions()");
        List<String> permissionNeeded = new ArrayList<>();

        for (String p : Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q ? REQUIRED_PERMISSIONS : REQUIRED_PERMISSIONS_28) {
            if (ContextCompat.checkSelfPermission(this, p) != PackageManager.PERMISSION_GRANTED) {
                permissionNeeded.add(p);
                Log.d(TAG, "not granted " + p);
            }
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                isLocationPermissionGranted = true;
                Log.d(TAG, "checkPermissions() FINE LOCATION granted");
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
            }
        }

        if (!permissionNeeded.isEmpty()) {
            Log.d(TAG, "asking permissions .....");
            requestMultiplePermissionLauncher.launch(permissionNeeded.toArray(new String[0]));
        }
    }

    /**
     * Used to get current location when taking photo.
     * App works without this permission.
     * If fine location permission is granted, current location info will be added.
     * location info is only available with taking photo not importing(loading) photo from gallery
     * even if photo in the gallery has location data in it, this app will not try to find the location info.
     *
     * if permission is denied, simply location information will not be added to the database.
     *
     * if permission granted, one can re-visit that place and take additional photos (different season, angle, and so on)
     * to re-train deep-learning model, in case the label of the image is incorrect
     */
    private void getLocation() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "access fine location permission granted");
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
            if (currentLocation != null) {
                objectLongitude = (float) currentLocation.getLongitude();
                objectLatitude = (float) currentLocation.getLatitude();

                Log.d(TAG, "longitude and latitude" + objectLongitude);
            }
        }
    }

    /**
     * Used to get filename from image uri and to check duplication of the image.
     * Getting file from uri is different for different uri scheme,
     * a. For "File Uri Scheme" - We will get file from uri.
     * b. For "Content Uri Scheme" - We will get the file by querying content resolver.
     * @param uri Uri.
     * @return String filename.
     */
    public String getFilenameFromUri(Context context, Uri uri) {
        String filename = null;
        if (uri != null) {
            // File Scheme.
            if (ContentResolver.SCHEME_FILE.equals(uri.getScheme())) {
                File file = new File(uri.getPath());
                filename = file.getName();
            }
            // Content Scheme.
            else if (ContentResolver.SCHEME_CONTENT.equals(uri.getScheme())) {
                Cursor returnCursor =
                        context.getContentResolver().query(uri, null, null, null, null);
                if (returnCursor != null && returnCursor.moveToFirst()) {
                    int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    filename = returnCursor.getString(nameIndex);
                    returnCursor.close();
                }
            }
        }
        return filename;
    }

    /**
     * used to initialize(Reset) main ui when necessary
     */
    private void resetUI() {
        radioGroup.clearCheck();
        for (int i = 0; i < radioGroup.getChildCount(); i++){
            radioGroup.getChildAt(i).setEnabled(false);
        }

        isDuplicated = false;
        bitmapImage = null;
        textView.setText(getResources().getString(R.string.hint));
        imageView.setImageDrawable(ContextCompat.getDrawable(this, android.R.drawable.ic_menu_help));

        objectFilename = null;
        objectLabel = null;
        objectUri = null;
        isLabelCorrect = null;
    }

    /**
     * used to activate Radiobutton only when image recognition process is finished
     * user cannot make interaction with this without performing the model
     */
    private void activateRadioButtons() {
        for (int i = 0; i < radioGroup.getChildCount(); i++){
            radioGroup.getChildAt(i).setEnabled(true);
        }
    }

    /**
     * Used to get Module of trained torch net (**.pt)
     * model should be in the assets folder
     * @return torch module
     */
    private Module getModule() {
        Module module = null;
        try {
            module = Module.load(assetFilePath(this, MOBILE_NET));
        } catch (IOException e) {
            Log.e("MODEL NOT FOUND", "Error loading model!", e);
            finish();
        }
        return module;
    }
    public String assetFilePath(Context context, String assetName) throws IOException {
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

    /**
     * Used to execute run model when user press button
     * @param bitmap image which has object to be classified by selecting taking photo
     */
    private void detectObject(Bitmap bitmap) {
        if (bitmap == null) {
            Toast.makeText(this,"SELECT PHOTO to classify", Toast.LENGTH_LONG).show();
            return;
        }
        activateRadioButtons();
        executor.execute(() -> runOnUiThread(() -> runModel(getModule(), bitmap)));
    }

    /**
     * Used to run model and get label(class of the object) of the bitmap
     * handling py-torch module here
     * @param module torch module
     * @param srcBitmap source image
     */
    public void runModel(Module module, Bitmap srcBitmap) {
        // preparing input tensor
        final Tensor inputTensor = TensorImageUtils.bitmapToFloat32Tensor(srcBitmap,
                TensorImageUtils.TORCHVISION_NORM_MEAN_RGB,
                TensorImageUtils.TORCHVISION_NORM_STD_RGB,
                MemoryFormat.CHANNELS_LAST);

        // running the model
        final Tensor outputTensor = module.forward(IValue.from(inputTensor)).toTensor();

        // getting tensor content as java array of floats
        final float[] scores = outputTensor.getDataAsFloatArray();

        // searching for the index with maximum score
        float maxScore = -Float.MAX_VALUE;
        int maxScoreIdx = -1;
        for (int i = 0; i < scores.length; i++) {
            if (scores[i] > maxScore) {
                maxScore = scores[i];
                maxScoreIdx = i;
            }
        }
        String output = ImageNetClasses.IMAGENET_CLASSES[maxScoreIdx];
        objectLabel = output;
        textView.setText(output);
    }

    /**
     * Used to save cropped-image used for image detection into the device
     * different location above and below Android version Q
     * Image selected from this location (already performed image) will not override and will not save again.
     * only fresh image from taken or selected from other location will be saved
     * @param bitmap oriented and center cropped image to save
     * @throws IOException exception
     */
    public void storeImage(Bitmap bitmap) throws IOException {

        Log.d(TAG, "store image into device...");

        String filename = getFilename();
        Toast.makeText(this, "Filename: "+ filename, Toast.LENGTH_LONG).show();
        objectFilename = filename;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
        {
            ContentResolver resolver = this.getContentResolver();
            ContentValues contentValues = new ContentValues();
            contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, filename);
            contentValues.put(MediaStore.MediaColumns.MIME_TYPE, IMAGE_TYPE);
            contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, REL_LOCATION);

            Uri imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
            objectUri = imageUri.toString();
            OutputStream fos = resolver.openOutputStream(imageUri);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            fos.close();
            Log.d(TAG, "image uri: " + objectUri);
        } else {
            Log.d(TAG, "version is low");
            File mediaStorageLocation = new File(Environment.getExternalStorageDirectory().toString()
                    + "/Images");
            // This location works best if you want the created images to be shared between applications and persist after your app has been uninstalled.
            // Create the storage directory if it does not exist
            if (! mediaStorageLocation.exists()) {
                if (! mediaStorageLocation.mkdirs()) return;
            }
            File mediaFile = new File(mediaStorageLocation.getPath() + File.separator + filename);
            try {
                FileOutputStream fileOutputStream = new FileOutputStream(mediaFile);
                bitmap.compress(Bitmap.CompressFormat.PNG, 90, fileOutputStream);
                Log.d(TAG, "File saved: " + mediaFile.getName());
                fileOutputStream.close();
                Log.d(TAG, "Abs path: " + mediaFile.getAbsolutePath());
                objectUri = Uri.fromFile(mediaFile).toString();
                Log.d(TAG, "saved file uri: " + objectUri);
            } catch (FileNotFoundException e) {
                Log.d(TAG, "File not found: " + e.getMessage());
            } catch (IOException e) {
                Log.d(TAG, "Error accessing file: " + e.getMessage());
            }
        }
    }

    /**
     * Used to create filename based on the date time
     * @return String filename.
     * filename start with "OD_"
     **/
    public String getFilename() {
        String timeStamp = new SimpleDateFormat("dd-MM-yyyy_HHmmss", Locale.getDefault()).format(new Date());
        return String.format("OD_%s.png", timeStamp);
    }

    /**
     * Used to save data (custom Image object) to the Room database
     */
    public void saveImageDatabase() {
        Log.d(TAG, "image uri: " + objectUri);
        Image image = new Image();
        image.setFilename(objectFilename);
        image.setLabel(objectLabel);
        image.setImageUri(objectUri);
        image.setIsCorrect(isLabelCorrect ? 1 : 0);
        image.setLatitude(objectLatitude);
        image.setLongitude(objectLongitude);

        imageRepository.insert(image);
    }

    /**
     * Device has own camera (sensor) orientation, this method rotate bitmap to the right angle(same as user's action)
     * ExifInterface library used to get correct orientation and rotate bitmap accord to it.
     * @param bitmap bitmap image of saved photo
     * @param uri uri of saved image
     * @return oriented bitmap for running(performing) torch model as a source image
     */
    public Bitmap getOrientatedBitmap(Bitmap bitmap, Uri uri){
        try (InputStream inputStream = this.getContentResolver().openInputStream(uri)) {
            ExifInterface exif = new ExifInterface(inputStream);
            int orientationTag = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

            int rotationAngle = 0;
            if (orientationTag == ExifInterface.ORIENTATION_ROTATE_90) rotationAngle = 90;
            if (orientationTag == ExifInterface.ORIENTATION_ROTATE_180) rotationAngle = 180;
            if (orientationTag == ExifInterface.ORIENTATION_ROTATE_270) rotationAngle = 270;

            Log.d(TAG, "orientation from exif: " + rotationAngle);
            Matrix mat = new Matrix();
            mat.postRotate(rotationAngle);
            return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), mat, true);
            //return ImagePreprocessing.CENTER_CROP_AND_RESCALE(orientedBitmap, MIN_INPUT_SIZE);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

//    /**
//     * This method NOT used
//     * @return display orientation
//     */
//    public int getDeviceOrientation() {
//        Display display = ((WindowManager)getSystemService(WINDOW_SERVICE)).getDefaultDisplay();
//        int rotation = display.getRotation();
//        String rotString;
//        switch(rotation) {
//            case Surface.ROTATION_0:
//                rotString="portrait";
//                break;
//            case Surface.ROTATION_90:
//                rotString="landscape left";
//                break;
//            case Surface.ROTATION_180:
//                rotString="flipped portrait";
//                break;
//            case Surface.ROTATION_270:
//                rotString="landscape right";
//                break;
//        }
//
//        int orientation = this.getResources().getConfiguration().orientation;
//        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
//            rotString = "Portrait mode";
//        } else {
//            rotString = "landscape mode";
//        }
//        return rotation;
//    }
}
