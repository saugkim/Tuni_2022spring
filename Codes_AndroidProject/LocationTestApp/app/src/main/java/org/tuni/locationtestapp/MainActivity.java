package org.tuni.locationtestapp;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.preference.PreferenceManager;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.osmdroid.config.Configuration;
import org.osmdroid.library.BuildConfig;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Overlay;
import org.osmdroid.views.overlay.compass.CompassOverlay;
import org.osmdroid.views.overlay.compass.InternalCompassOrientationProvider;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    public static String TAG = "ZZ MainActivity";

    private final String[] REQUIRED_PERMISSIONS = new String[] {
            "android.permission.ACCESS_FINE_LOCATION",
            "android.permission.ACCESS_COARSE_LOCATION",
            "android.permission.WRITE_EXTERNAL_STORAGE",
            "android.permission.INTERNET"
    };
//    public static final int PERMISSIONS_REQUEST_CODE = 101;
//    public static final int PERMISSIONS_REQUEST_LOCATION = 98;

    LocationManager locationManager;
    LocationListener locationListener;
    Location currentLocation;

    MapView mapView = null;
    MapController mapController;

    TextView textLocation;
    Button saveButton, getButton;
    FloatingActionButton showAllButton;

    List<Double> longitudes;
    List<Double> latitudes;
    MyLocationNewOverlay myLocationOverlay;
    CompassOverlay compassOverlay;

    ActivityResultLauncher<String[]> multiplePermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), isGranted -> {
                Log.d(TAG, "Launcher results: " + isGranted.toString());
                if (isGranted.containsValue(false)) {
                    Log.d(TAG, "Launcher, not granted");
                } else {
                    Log.d(TAG, "Launcher, all permission granted");
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        Context ctx = getApplicationContext();
//        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));
//        Configuration.getInstance().setUserAgentValue(BuildConfig.APPLICATION_ID);

        setContentView(R.layout.activity_main);

        textLocation = findViewById(R.id.textLocation);
        String tmp = "location will be displayed here... ";
        textLocation.setText(tmp);

        saveButton = findViewById(R.id.saveButton);
        showAllButton = findViewById(R.id.showAll);
        getButton = findViewById(R.id.getButton);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationListener = location -> currentLocation = location;

        checkAllPermissions();

        getSavedLocations();

        mapView = findViewById(R.id.mapview);
        mapView.setTileSource(TileSourceFactory.MAPNIK);
        mapView.setMultiTouchControls(true);
        mapController = (MapController) mapView.getController();
        mapController.setZoom(18);
        initializeMap();

        GeoPoint startPoint = new GeoPoint(60.16054, 24.88311);
        if (longitudes.size() >= 1) {
            int len = longitudes.size();
            Log.d(TAG, "latitude and longitude: " + latitudes.get(len-1) + ", " + longitudes.get(len-1));
            startPoint = new GeoPoint(latitudes.get(len-1), longitudes.get(len-1));
        }
        mapController.setCenter(startPoint);

        this.myLocationOverlay = new MyLocationNewOverlay(new GpsMyLocationProvider(this),mapView);
        this.myLocationOverlay.enableMyLocation();
        mapView.getOverlays().add(this.myLocationOverlay);

        this.compassOverlay = new CompassOverlay(this, new InternalCompassOrientationProvider(this), mapView);
        this.compassOverlay.enableCompass();
        mapView.getOverlays().add(this.compassOverlay);

        getButton.setOnClickListener(view -> checkPermission());
        saveButton.setOnClickListener(view -> saveCurrentLocation(currentLocation.getLongitude(), currentLocation.getLatitude()));
        showAllButton.setOnClickListener(view -> showAllLocations());
    }

    private void getLocationAndShowOnMap() {
        try {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
            String ret;
            if (currentLocation != null) {
                ret = String.format(Locale.getDefault(), "Longitude: %.5f \nLatitude: %.5f", currentLocation.getLongitude(), currentLocation.getLatitude());
                GeoPoint newPoint = new GeoPoint(currentLocation.getLatitude(), currentLocation.getLongitude());
                mapController.setCenter(newPoint);
            } else {
                ret = "location not available at the moment, try again";
            }
            textLocation.setText(ret);

        } catch (SecurityException e) {
            Log.d("ZZ", "error message: " + e);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }


    private void checkAllPermissions() {
        Log.d(TAG, "inside checkAllPermissions()");
        List<String> permissionNeeded = new ArrayList<>();

        for (String p : REQUIRED_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(this, p) != PackageManager.PERMISSION_GRANTED) {
                permissionNeeded.add(p);
                Log.d(TAG, "not granted " + p);
            }
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "checkAllPermissions() FINE LOCATION granted");
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
                //getLocationAndShowOnMap();
            }
        }
        if (!permissionNeeded.isEmpty()) {
            Log.d(TAG, "size of array " + permissionNeeded.toArray(new String[0]).length);
            Log.d(TAG, "asking permissions .....");
            multiplePermissionLauncher.launch(permissionNeeded.toArray(new String[0]));
        }
    }

    private void checkPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "checkPermission() granted");
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
            getLocationAndShowOnMap();
        } else if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.ACCESS_FINE_LOCATION)) {
            Log.d(TAG, "app works with gps....");
        } else {
            Log.d(TAG, "ask help");
            // The registered ActivityResultCallback gets the result of this request.
            multiplePermissionLauncher.launch(new String[]{Manifest.permission.ACCESS_FINE_LOCATION});
        }
    }

    public void getSavedLocations(){
        longitudes = new ArrayList<>();
        latitudes = new ArrayList<>();

        try {
            List<String> saved = SaveAndLoad.LOAD_DATA(this);
            for (int i = 0; i < saved.size(); i++) {
                Log.d(TAG, "saved file content: " + saved.get(i));
                longitudes.add(Double.valueOf(saved.get(i).split(" ")[0]));
                latitudes.add(Double.valueOf(saved.get(i).split(" ")[1]));
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    public void saveCurrentLocation(double lon, double lat) {
        if (longitudes.size() >= 12) {
            longitudes.remove(0);
            latitudes.remove(0);
        }
        longitudes.add(lon);
        latitudes.add(lat);
        Log.d(TAG, "length of saved location " + longitudes.size());
    }

    public void initializeMap() {
        for (int i=0; i<mapView.getOverlays().size(); i++){
            Overlay overlay = mapView.getOverlays().get(i);
            if(overlay instanceof Marker && ((Marker)overlay).getId().equals("HISTORY_MARKER")){
                mapView.getOverlays().remove(overlay);
            }
        }
    }

    public void showAllLocations() {
        initializeMap();
        for (int i=0; i<longitudes.size(); i++){
            GeoPoint point = new GeoPoint(latitudes.get(i), longitudes.get(i));
            Marker marker = new Marker(mapView);
            marker.setPosition(point);
            marker.setId("HISTORY_MARKER");
            marker.setIcon(getResources().getDrawable(org.osmdroid.library.R.drawable.ic_menu_mylocation));
            mapView.getOverlays().add(marker);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
        //addSamples();
        //clearData();
        save();
    }

    public void clearData() {
        SaveAndLoad.SAVE_DATA(this, "");
    }

    public void addSamples() {
        StringBuilder sb = new StringBuilder();
        sb.append("24.885638 60.161238\n");
        sb.append("24.882545 60.161207\n");
        sb.append("24.882964 60.159877\n");
        sb.append("24.883025 60.158734\n");
        sb.append("24.880733 60.161666\n");
        sb.append("24.881667 60.158865\n");
        SaveAndLoad.SAVE_DATA(this, sb.toString());
    }

    public void save() {
        StringBuilder sb = new StringBuilder();
        for (int i=0; i<longitudes.size(); i++){
            sb.append(longitudes.get(i));
            sb.append(" ");
            sb.append(latitudes.get(i));
            sb.append("\n");
        }
        Log.d(TAG, "save()");
        SaveAndLoad.SAVE_DATA(this, sb.toString());
    }

    /*
    private boolean isAllPermissionGranted() {
        List<String> permissionNeeded = new ArrayList<>();
        for (String p : REQUIRED_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(this, p) != PackageManager.PERMISSION_GRANTED) {
                permissionNeeded.add(p);
            } else {
                Log.d(TAG, p + " is granted");
            }
        }
        if (!permissionNeeded.isEmpty()) {
            multiplePermissionLauncher.launch(permissionNeeded.toArray(new String[0]));
            return false;
        }
        return true;
    }
    private boolean askPermission(Context context) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                Log.d(TAG, "Permission once denied, no more request");
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSIONS_REQUEST_CODE) {
            List<String> permissionsToRequest = new ArrayList<>();
            Log.d(TAG, "how many result?" + grantResults.length);
            boolean allGranted = true;
            for (int i=0; i<grantResults.length; i++){
                boolean checking = grantResults[i] == PackageManager.PERMISSION_GRANTED;
                if (!checking) {
                    allGranted = false;
                }
                if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                    permissionsToRequest.add(permissions[i]);
                }
            }
            if (allGranted) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "onRequest... Getting location....");
                }
            } else {
                Log.d(TAG, "onRequest... permission denied....");
            }
        }
        if (requestCode == PERMISSIONS_REQUEST_LOCATION) {
            // If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "onRequest... permission granted");
                if (ContextCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {
                    //Request location updates:
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,0,0,locationListener);
                }
            } else {
                // permission denied, boo! Disable the functionality that depends on this permission.
                Log.d(TAG, "onRequest... permission denied");
            }
        }
    }
    */
}