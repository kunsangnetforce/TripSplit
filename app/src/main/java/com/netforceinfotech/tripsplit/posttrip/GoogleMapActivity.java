package com.netforceinfotech.tripsplit.posttrip;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.netforceinfotech.tripsplit.R;
import com.netforceinfotech.tripsplit.map.GetDirectionsAsyncTask4;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class GoogleMapActivity extends AppCompatActivity implements OnMapReadyCallback {
    static final LatLng TutorialsPoint = new LatLng(21, 57);
    private static final int PERMISSION_REQUEST_CODE_LOCATION1 = 2;
    private static final int MY_PERMISSIONS_REQUEST_CAMERA = 101;
    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 102;
    private static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 103;
    private static final int MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION = 104;
    private static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 105;
    private static final int PERMISSION_ALL = 101;
    LatLng currentLatLng;
    public GoogleMap mMap;
    Context context;
    static double source_latitude, source_lat;
    static double source_longitude, source_log;
    static double destination_latitude = 30.325558;
    static double destination_longitude = 77.9470939;
    private Polyline newPolyline;
    LatLng latLng;
    Button search, done;
    boolean source_place;
    String MY_PREFS_NAME = "preference_data";
    SharedPreferences.Editor editor;
    String address = "Address not found";
    Marker marker;
    AddressListner addressListner = null;
    private PlaceAutocompleteFragment autocompleteFragment;
    String TAG = "google_place";
    private float zoomLevel = 20;
    private LocationManager mLocationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_map);


        editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
        context = this;
        getPermission();
        source_place = getIntent().getExtras().getBoolean("choose_source");
        if (source_place) {
            setupToolBar(getString(R.string.choose_source));
        } else {
            setupToolBar(getString(R.string.choose_destination));
        }
        autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {

            @Override
            public void onPlaceSelected(Place place) {
                Log.i(TAG, "Place: " + place.getName());
                try {
                    try {
                        marker.remove();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    marker = mMap.addMarker(new MarkerOptions().position(place.getLatLng()).title(place.getAddress().toString()));
                    currentLatLng = place.getLatLng();
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(place.getLatLng(), zoomLevel));
                    address = place.getAddress().toString();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i(TAG, "An error occurred: " + status);
            }
        });

        System.out.println("gsdysugd============" + source_place);

        done = (Button) findViewById(R.id.done_button);

        MapFragment googleMap = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        googleMap.getMapAsync(GoogleMapActivity.this);


        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*
                * Intent intent = new Intent();
                intent.putExtra("friendsid", friendsidstring);
                intent.putExtra("frindstring", frindstring);
                setResult(RESULT_OK, intent);
                finish();
                * */
                try {
                    Intent intent = new Intent().putExtra("address", address).putExtra("lat", currentLatLng.latitude).putExtra("lon", currentLatLng.longitude);
                    setResult(RESULT_OK, intent);
                    finish();
                } catch (Exception ex) {
                    ex.printStackTrace();
                    Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }


        });


    }
    public static boolean hasPermissions(Context context, String... permissions) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    private void getPermission() {
        int PERMISSION_ALL = 1;
        String[] PERMISSIONS = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA};

        if (!hasPermissions(this, PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        Log.d(TAG, "Permission callback called-------");
        switch (requestCode) {
            case PERMISSION_ALL: {

                Map<String, Integer> perms = new HashMap<>();
                // Initialize the map with both permissions
                perms.put(Manifest.permission.CAMERA, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.READ_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.WRITE_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.ACCESS_FINE_LOCATION, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.ACCESS_COARSE_LOCATION, PackageManager.PERMISSION_GRANTED);

                // Fill with actual results from user
                if (grantResults.length > 0) {
                    for (int i = 0; i < permissions.length; i++)
                        perms.put(permissions[i], grantResults[i]);
                    // Check for both permissions
                    if (perms.get(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
                            && perms.get(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                            && perms.get(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                            && perms.get(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                            && perms.get(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED

                            ) {
                        Log.d(TAG, "sms & location services permission granted");
                        // process the normal flow
                        //else any one or both the permissions are not granted
                    } else {
                        Log.d(TAG, "Some permissions are not granted ask again ");
                        //permission is denied (this is the first time, when "never ask again" is not checked) so ask again explaining the usage of permission
//                        // shouldShowRequestPermissionRationale will return true
                        //show the dialog or snackbar saying its necessary and try again otherwise proceed with setup.
                        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA) || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)
                                || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                                || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_COARSE_LOCATION)

                                ) {
                            showDialogOK("Permission required for this app",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            switch (which) {
                                                case DialogInterface.BUTTON_POSITIVE:
                                                    checkAndRequestPermissions();
                                                    break;
                                                case DialogInterface.BUTTON_NEGATIVE:
                                                    // proceed with logic by disabling the related features or quit the app.
                                                    break;
                                            }
                                        }
                                    });
                        }
                        //permission is denied (and never ask again is  checked)
                        //shouldShowRequestPermissionRationale will return false
                        else {
                            Toast.makeText(this, "Go to settings and enable permissions", Toast.LENGTH_LONG)
                                    .show();
                            //                            //proceed with logic by disabling the related features or quit the app.
                        }
                    }
                }
            }
        }

    }

    private boolean checkAndRequestPermissions() {
        int cameraPermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA);
        int locationPermission1 = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        int writePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int locationPermission2 = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
        int readPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        List<String> listPermissionsNeeded = new ArrayList<>();
        if (cameraPermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.CAMERA);
        }
        if (locationPermission1 != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (writePermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (locationPermission2 != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        }
        if (readPermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }

        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), PERMISSION_ALL);
            return false;
        }
        return true;
    }

    private void showDialogOK(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", okListener)
                .create()
                .show();
    }

    private void showMessage(String s) {
        Toast.makeText(context, s, Toast.LENGTH_SHORT).show();
    }

    private void setupToolBar(String app_name) {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(app_name);


    }

    public void setAddressListner(AddressListner addressListner) {
        this.addressListner = addressListner;
    }

    public void onMapReady(GoogleMap map) {
        mMap = map;
        if (checkPermission(Manifest.permission.ACCESS_FINE_LOCATION, getApplicationContext(), GoogleMapActivity.this)) {

            mMap.getUiSettings().setRotateGesturesEnabled(false);
            // getLocation();

        } else {
            requestPermission(Manifest.permission.ACCESS_FINE_LOCATION, MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION, getApplicationContext(), GoogleMapActivity.this);
        }
        if (checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION, getApplicationContext(), GoogleMapActivity.this)) {

            mMap.getUiSettings().setRotateGesturesEnabled(false);
            // getLocation();

        } else {
            requestPermission(Manifest.permission.ACCESS_FINE_LOCATION, PERMISSION_REQUEST_CODE_LOCATION1, getApplicationContext(), GoogleMapActivity.this);
        }

        mMap.setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener() {

            @Override
            public void onCameraMove() {
                GoogleMapActivity.this.zoomLevel = mMap.getCameraPosition().zoom;
            }
        });
        mMap.setMyLocationEnabled(true);

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {

                try {
                    marker.remove();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                marker = mMap.addMarker(new MarkerOptions().position(latLng).title(getString(R.string.selected_place)));
                currentLatLng = latLng;
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoomLevel));
                setCompleteAddress(latLng);


            }
        });
        if (source_place) {

            mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);


            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000,
                    10, mLocationListener);
        }
        search = (Button) findViewById(R.id.search_button);


    }


    @Override
    protected void onPause() {
        super.onPause();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        try {
            mLocationManager.removeUpdates(mLocationListener);
            mLocationManager = null;

        } catch (Exception ex) {

        }
    }

    private final LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(final Location location) {
            //your code here
            try {
                marker.remove();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            latLng = new LatLng(location.getLatitude(), location.getLongitude());
            marker = mMap.addMarker(new MarkerOptions().position(latLng).title(getString(R.string.selected_place)));
            currentLatLng = latLng;
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18));
            zoomLevel = 18;
            setCompleteAddress(latLng);
            mMap.setMyLocationEnabled(false);

            if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            try {
                mLocationManager.removeUpdates(mLocationListener);
                mLocationManager = null;

            } catch (Exception ex) {

            }


        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {

        }

        @Override
        public void onProviderDisabled(String s) {

        }
    };

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setCompleteAddress(LatLng latLng) {

        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(this, Locale.getDefault());
        StringBuilder completeAddress = new StringBuilder();
        try {
            addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
        } catch (Exception ex) {
            return;
        }
        try {
            String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
            completeAddress.append(address);
        } catch (Exception ex) {
            completeAddress.append("");
        }
        try {
            String city = addresses.get(0).getLocality();
            completeAddress.append(city);

        } catch (Exception ex) {
            completeAddress.append("");

        }
        try {
            String state = addresses.get(0).getAdminArea();
            completeAddress.append(state);
        } catch (Exception ex) {
            completeAddress.append("");
        }
        try {
            String knownName = addresses.get(0).getFeatureName();
            completeAddress.append(knownName);
        } catch (Exception ex) {
            completeAddress.append("");
        }
        try {
            marker.remove();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        marker = mMap.addMarker(new MarkerOptions().position(latLng).title(completeAddress.toString()));
        currentLatLng = latLng;
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoomLevel));
        GoogleMapActivity.this.address = completeAddress.toString();
    }

    public LatLng getLocationFromAddress(Context context, String strAddress) {

        Geocoder coder = new Geocoder(context);
        List<Address> address;
        LatLng p1 = null;

        try {
            address = coder.getFromLocationName(strAddress, 5);
            if (address == null) {
                return null;
            }
            android.location.Address location = address.get(0);
            location.getLatitude();
            location.getLongitude();

            p1 = new LatLng(location.getLatitude(), location.getLongitude());

        } catch (Exception ex) {

            ex.printStackTrace();
        }

        return p1;
    }


    private void clearMarker() {
        try {
            mMap.clear();
        } catch (Exception ex) {

        }


    }


    public void requestPermission(String strPermission, int perCode, Context _c, Activity _a) {

        if (ActivityCompat.shouldShowRequestPermissionRationale(_a, strPermission)) {
            Toast.makeText(getApplicationContext(), "GPS permission allows us to access location data. Please allow in App Settings for additional functionality.", Toast.LENGTH_LONG).show();
        } else {

            ActivityCompat.requestPermissions(_a, new String[]{strPermission}, perCode);
        }
    }

    public static boolean checkPermission(String strPermission, Context _c, Activity _a) {
        int result = ContextCompat.checkSelfPermission(_c, strPermission);
        if (result == PackageManager.PERMISSION_GRANTED) {

            return true;

        } else {

            return false;

        }
    }


    public void findDirections(double fromPositionDoubleLat,
                               double fromPositionDoubleLong, double toPositionDoubleLat,
                               double toPositionDoubleLong, String mode) {

        Map<String, String> map = new HashMap<String, String>();
        map.put(GetDirectionsAsyncTask4.USER_CURRENT_LAT,
                String.valueOf(fromPositionDoubleLat));
        map.put(GetDirectionsAsyncTask4.USER_CURRENT_LONG,
                String.valueOf(fromPositionDoubleLong));
        map.put(GetDirectionsAsyncTask4.DESTINATION_LAT,
                String.valueOf(toPositionDoubleLat));
        map.put(GetDirectionsAsyncTask4.DESTINATION_LONG,
                String.valueOf(toPositionDoubleLong));
        map.put(GetDirectionsAsyncTask4.DIRECTIONS_MODE, mode);

        GetDirectionsAsyncTask4 asyncTask = new GetDirectionsAsyncTask4(GoogleMapActivity.this);
        asyncTask.execute(map);


    }

    public void handleGetDirectionsResult(ArrayList<LatLng> directionPoints) {

        PolylineOptions rectLine = new PolylineOptions().width(10).color(R.color.polyline_color);

        for (int i = 0; i < directionPoints.size(); i++) {
            rectLine.add(directionPoints.get(i));
        }

        if (newPolyline != null) {
            newPolyline.remove();
        }

        newPolyline = mMap.addPolyline(rectLine);

        MarkerOptions marker2 = new MarkerOptions().position(
                new LatLng(source_lat, source_log)).icon(BitmapDescriptorFactory.fromResource(R.drawable.car_location_green50)).title(
                "My Location");

        MarkerOptions marker3 = new MarkerOptions().position(
                new LatLng(latLng.latitude, latLng.longitude)).icon(BitmapDescriptorFactory.fromResource(R.drawable.car_location_red50)).title(
                "Dehradun");

        mMap.addMarker(marker2);
        mMap.addMarker(marker3);

    }

    public interface AddressListner {
        void gotAddress(String address, boolean source);
    }

}
