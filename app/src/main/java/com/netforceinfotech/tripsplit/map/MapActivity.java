/*
package com.netforceinfotech.tripsplit.map;

import java.util.ArrayList;

import java.util.HashMap;
import java.util.Map;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.netforceinfotech.tripsplit.R;
import com.netforceinfotech.tripsplit.posttrip.GMapV2Direction;
import com.seatgeek.placesautocomplete.OnPlaceSelectedListener;
import com.seatgeek.placesautocomplete.PlacesAutocompleteTextView;
import com.seatgeek.placesautocomplete.model.Place;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback, LocationListener , OnClickListener
{


    private static final int PERMISSION_REQUEST_CODE_LOCATION = 1;
    // Google Map
    private GoogleMap googleMap;
    static double source_latitude ;
    static double source_longitude ;
    static double destination_latitude = 30.325558;
    static double destination_longitude = 77.9470939;
    private Polyline newPolyline;
    private int width, height;


    private LatLngBounds latlngBounds;

    Button pickup_done,navigate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        pickup_done = (Button) findViewById(R.id.pick_done);
        navigate = (Button) findViewById(R.id.navigator);

        getSreenDimanstions();
        try {
            // Loading map
            initilizeMap();

            if (checkPermission(Manifest.permission.ACCESS_FINE_LOCATION,getApplicationContext(),MapActivity.this))
            {



            }
            else
            {
                requestPermission(Manifest.permission.ACCESS_FINE_LOCATION,PERMISSION_REQUEST_CODE_LOCATION,getApplicationContext(),MapActivity.this);
            }

            // Changing map type
            // googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            // googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
            // googleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
            // googleMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
            // googleMap.setMapType(GoogleMap.MAP_TYPE_NONE);

            // Showing / hiding your current location
            googleMap.setMyLocationEnabled(true);

            // Enable / Disable zooming controls
			*/
/*
			 * googleMap.getUiSettings().setZoomControlsEnabled(true);
			 *
			 * // Enable / Disable my location button
			 * googleMap.getUiSettings().setMyLocationButtonEnabled(true);
			 *
			 * // Enable / Disable Compass icon
			 * googleMap.getUiSettings().setCompassEnabled(true);
			 *
			 * // Enable / Disable Rotate gesture
			 * googleMap.getUiSettings().setRotateGesturesEnabled(true);
			 *
			 * // Enable / Disable zooming functionality
			 * googleMap.getUiSettings().setZoomGesturesEnabled(true);
			 *//*


            LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            Criteria criteria = new Criteria();
            String bestProvider = locationManager.getBestProvider(criteria,
                    true);
            Location location = locationManager
                    .getLastKnownLocation(bestProvider);

            if (location != null) {
                onLocationChanged(location);
            }

            locationManager
                    .requestLocationUpdates(bestProvider, 20000, 0, this);

            findDirections(source_latitude, source_longitude,
                    destination_latitude, destination_longitude,
                    GMapV2Direction.MODE_DRIVING);


        } catch (Exception e) {
            e.printStackTrace();
        }

        Location location1 = new Location("");
        location1.setLatitude(source_latitude);
        location1.setLongitude(source_longitude);

        Location location2 = new Location("");
        location2.setLatitude(destination_latitude);
        location2.setLongitude(destination_longitude);

        float distanceInMeters = location1.distanceTo(location2);
        System.out.println("Distance is ================="+distanceInMeters);
        pickup_done.setOnClickListener(this);
        navigate.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        LatLng source = new LatLng(source_latitude,
                source_longitude);

        LatLng destination = new LatLng(destination_latitude,
                destination_longitude);
        latlngBounds = createLatLngBoundsObject(source, destination);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(latlngBounds,
                width, height, 150));
    }

    */
/**
     * function to load map If map is not created it will create it for you
     * *//*

    private void initilizeMap() {
        if (googleMap == null) {
            googleMap = ((MapFragment) getFragmentManager().findFragmentById(
                    R.id.map)).getMapAsync(MapActivity.this);

            // check if map is created successfully or not
            if (googleMap == null) {
                Toast.makeText(getApplicationContext(),
                        "Sorry! unable to create maps", Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }

	*/
/*
	 * creating random postion around a location for testing purpose only
	 *//*

	*/
/*
	 * private double[] createRandLocation(double latitude, double longitude) {
	 *
	 * return new double[] { latitude + ((Math.random() - 0.5) / 500), longitude
	 * + ((Math.random() - 0.5) / 500), 150 + ((Math.random() - 0.5) * 10) }; }
	 *//*


    public void onLocationChanged(Location location) {
        // TODO Auto-generated method stub
        source_latitude = location.getLatitude();
        source_longitude = location.getLongitude();

    }

    public void onStatusChanged(String provider, int status, Bundle extras) {
        // TODO Auto-generated method stub

    }

    public void onProviderEnabled(String provider) {
        // TODO Auto-generated method stub

    }

    public void onProviderDisabled(String provider) {
        // TODO Auto-generated method stub

    }

    private void getSreenDimanstions() {
        Display display = getWindowManager().getDefaultDisplay();
        width = display.getWidth();
        height = display.getHeight();
    }

    private LatLngBounds createLatLngBoundsObject(LatLng firstLocation,
                                                  LatLng secondLocation) {
        if (firstLocation != null && secondLocation != null) {
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            builder.include(firstLocation).include(secondLocation);

            return builder.build();
        }
        return null;
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

        GetDirectionsAsyncTask4 asyncTask = new GetDirectionsAsyncTask4(MapActivity.this);
        asyncTask.execute(map);



    }

    public void handleGetDirectionsResult(ArrayList<LatLng> directionPoints) {
        PolylineOptions rectLine = new PolylineOptions().width(5).color(
                Color.RED);

        for (int i = 0; i < directionPoints.size(); i++) {
            rectLine.add(directionPoints.get(i));
        }
        if (newPolyline != null) {
            newPolyline.remove();
        }
        newPolyline = googleMap.addPolyline(rectLine);
        source_latitude = 31.1048;
        source_longitude=77.1734;
        destination_latitude = 30.325558;
        destination_longitude = 77.9470939;


        MarkerOptions marker2 = new MarkerOptions().position(
                new LatLng(source_latitude, source_longitude)).title(
                "My Location");

        MarkerOptions marker3 = new MarkerOptions().position(
                new LatLng(destination_latitude, destination_longitude)).title(
                "Dehradun");

        googleMap.addMarker(marker2);
        googleMap.addMarker(marker3);

    }

    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.navigator:

                final Intent intent = new Intent(Intent.ACTION_VIEW,
                        */
/** Using the web based turn by turn directions url. *//*

                        Uri.parse("http://maps.google.com/maps?" + "saddr="
                                + source_latitude + "," + source_longitude
                                + "&daddr=28.6527766,77.2890384"));

                intent.setClassName("com.google.android.apps.maps",
                        "com.google.android.maps.MapsActivity");
                startActivity(intent);

                break;
            case R.id.pick_done:
                finish();
                break;
            default:
                break;
        }
    }

    public  void requestPermission(String strPermission,int perCode,Context _c,EditTripActivity _a)
    {

        if (ActivityCompat.shouldShowRequestPermissionRationale(_a, strPermission))
        {
            Toast.makeText(getApplicationContext(),"GPS permission allows us to access location data. Please allow in App Settings for additional functionality.",Toast.LENGTH_LONG).show();
        }
        else
        {

            ActivityCompat.requestPermissions(_a, new String[]{strPermission}, perCode);
        }
    }

    public static boolean checkPermission(String strPermission,Context _c,EditTripActivity _a){
        int result = ContextCompat.checkSelfPermission(_c, strPermission);
        if (result == PackageManager.PERMISSION_GRANTED){

            return true;

        }
        else {

            return false;

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {

            case PERMISSION_REQUEST_CODE_LOCATION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {


                }
                else
                {

                    Toast.makeText(getApplicationContext(),"Permission Denied, You cannot access location data.", Toast.LENGTH_LONG).show();

                }
                break;

        }
    }

    public void onMapReady(GoogleMap map)
    {


    }


}*/
