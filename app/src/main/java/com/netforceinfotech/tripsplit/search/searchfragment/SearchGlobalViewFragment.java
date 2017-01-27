package com.netforceinfotech.tripsplit.search.searchfragment;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.directions.route.Route;
import com.directions.route.RouteException;
import com.directions.route.RoutingListener;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.netforceinfotech.tripsplit.R;
import com.netforceinfotech.tripsplit.general.UserSessionManager;
import com.netforceinfotech.tripsplit.search.TripDetailActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 */
public class SearchGlobalViewFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnCameraIdleListener, GoogleMap.OnCameraMoveStartedListener, GoogleMap.OnCameraMoveListener, GoogleMap.OnCameraMoveCanceledListener, RoutingListener {

    UserSessionManager userSessionManager;
    private GoogleMap mMap;
    private MaterialDialog progressDialog;
    Context context;
    ArrayList<CarData> carDatas = new ArrayList<>();
    private MapView mMapView;
    private boolean mapReady = false;
    private float zoomlevel;
    private CameraUpdate cu;
    double dest_lat, dest_lon, source_lat, source_lon;
    String etd, type, sort;
    private CameraPosition cameraPosition;
    private String timezone;

    public SearchGlobalViewFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_search_global_view, container, false);
        userSessionManager = new UserSessionManager(getActivity());
        Calendar cal = Calendar.getInstance();
        timezone = cal.getTimeZone().getID();
        dest_lat = getArguments().getDouble("dest_lat");
        dest_lon = getArguments().getDouble("dest_lon");
        source_lat = getArguments().getDouble("source_lat");
        source_lon = getArguments().getDouble("source_lon");
        Log.i("lat", source_lat + "   " + source_lon);
        etd = getArguments().getString("etd");
        type = getArguments().getString("type");
        sort = getArguments().getString("sort");
        cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(source_lat, source_lon))      // Sets the center of the map to Mountain View
                .zoom(16)                   // Sets the zoom
                .tilt(30)                   // Sets the tilt of the camera to 30 degrees
                .build();                   // Creates a CameraPosition from the builder
        mMapView = (MapView) view.findViewById(R.id.mapView);
        try {
            mMapView.onCreate(savedInstanceState);

            mMapView.onResume(); // needed to get the map to display immediately

        } catch (Exception e) {
        }

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        mMapView.getMapAsync(this);

        context = getActivity();
        initView(view);
        return view;
    }

    private void initView(View view) {
        progressDialog = new MaterialDialog.Builder(context)
                .title(R.string.progress_dialog)
                .content(R.string.please_wait)
                .progress(true, 0).build();
    }

    private void searchTrip() {
        /*
        * source_lat=55.946302847171445&source_lon=-3.1891679763793945
        * &dest_lat=28.599072519302414&dest_lon=77.32198219746351&etd=2016-05-11&range=4000&type=car
        * */
        progressDialog.show();

        String baseUrl = getString(R.string.url);
        //http://netforce.biz/tripesplit/mobileApp/api/services.php?opt=search_trip
        String url = baseUrl + "services.php?opt=search_trip";
        Log.i("url", url);
        Log.i("type1", "type");
        Ion.with(context)
                .load("POST", url)
                .setBodyParameter("dest_lat", dest_lat + "")
                .setBodyParameter("dest_lon", dest_lon + "")
                .setBodyParameter("source_lat", source_lat + "")
                .setBodyParameter("source_lon", source_lon + "")
                .setBodyParameter("etd", getDateWithTimezone(etd))
                .setBodyParameter("range", userSessionManager.getSearchRadius() + "")
                .setBodyParameter("sort", sort)
                .setBodyParameter("type", type)
                .setBodyParameter("timezone", timezone)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        progressDialog.dismiss();
                        // do stuff with the result or error
                        if (result == null) {
                            showMessage("something wrong");
                        } else {
                            if (result.get("status").getAsString().equalsIgnoreCase("success")) {
                                Log.i("result", result.toString());

                                JsonArray data = result.getAsJsonArray("data");
                                setupData(data);
                            }

                        }
                    }
                });
    }

    private String getDateWithTimezone(String date1) {
        //Mon 02 Jan 2017 16:04
        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;
        try {
            date = fmt.parse(date1);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        SimpleDateFormat fmtOut = new SimpleDateFormat("yyyy-MM-dd");
        return fmtOut.format(date);
    }

    private void setupData(JsonArray data) {
        int size = data.size();
        if (size == 0) {
            showMessage(getString(R.string.no_split_found));
            return;
        }
        for (int i = 0; i < size; i++) {
            try {
                JsonObject object = data.get(i).getAsJsonObject();
                final String tour_id = object.get("tour_id").getAsString();
                String start_price = object.get("start_price").getAsString();
                String date_created = object.get("date_created").getAsString();
                String user_id = object.get("user_id").getAsString();
                String cartype = object.get("cartype").getAsString();
                String pax = object.get("pax").getAsString();
                String space = object.get("space").getAsString();
                String trip_group = object.get("trip_group").getAsString();
                String age_group_lower = object.get("age_group_lower").getAsString();
                String age_group_upper = object.get("age_group_upper").getAsString();
                final String trip = object.get("trip").getAsString();
                String vehical_type = object.get("vehical_type").getAsString();
                String depart_address = object.get("depart_address").getAsString();
                String country_code = object.get("country_code").getAsString();
                String etd = object.get("etd").getAsString();
                String eta = object.get("eta").getAsString();
                String iteinerary = object.get("iteinerary").getAsString();
                String image = object.get("image").getAsString();
                String currency = object.get("currency").getAsString();
                String depart_lat = object.get("depart_lat").getAsString();
                String depart_lon = object.get("depart_lon").getAsString();
                String return_eta = object.get("return_eta").getAsString();
                String return_etd = object.get("return_etd").getAsString();
                String dest_address = object.get("dest_address").getAsString();
                String dest_lat = object.get("dest_lat").getAsString();
                String dest_lon = object.get("dest_lon").getAsString();
                CarData carData = new CarData(tour_id, start_price, date_created, user_id, cartype, pax, space, trip_group, age_group_lower, age_group_upper, trip, vehical_type, depart_address, country_code, etd, eta, iteinerary, image, currency, depart_lat, depart_lon, return_eta, return_etd, dest_address, dest_lat, dest_lon);
                BitmapDrawable bitmapdraw;
                if (!carDatas.contains(carData)) {
                    int height = 90;
                    int width = 70;
                    if (type.equalsIgnoreCase("aeroplane")) {
                        bitmapdraw = (BitmapDrawable) ContextCompat.getDrawable(context, R.drawable.ic_plane_marker);

                    } else if (type.equalsIgnoreCase("car")) {
                        bitmapdraw = (BitmapDrawable) ContextCompat.getDrawable(context, R.drawable.ic_car_marker);

                    } else if (type.equalsIgnoreCase("bus")) {
                        bitmapdraw = (BitmapDrawable) ContextCompat.getDrawable(context, R.drawable.ic_bus_marker);

                    } else {
                        bitmapdraw = (BitmapDrawable) ContextCompat.getDrawable(context, R.drawable.ic_ship_marker);

                    }

                    Bitmap b = bitmapdraw.getBitmap();
                    Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);

                    mMap.addMarker(new MarkerOptions().snippet(depart_address).icon(BitmapDescriptorFactory.fromBitmap(smallMarker)).title("Source").position(new LatLng(Double.parseDouble(depart_lat), Double.parseDouble(depart_lon))));
                    mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                        @Override
                        public void onInfoWindowClick(Marker marker) {
                            if (marker.equals(marker)) {
                                Intent intent = new Intent(context, TripDetailActivity.class);
                                Bundle bundle = new Bundle();
                                bundle.putString("trip_id", tour_id);
                                intent.putExtras(bundle);
                                startActivity(intent);
                            }
                        }
                    });
                    carDatas.add(carData);
                }
            } catch (Exception ex) {
                Log.i("position", i + "");
                ex.printStackTrace();
            }
        }
        setupbound(carDatas);

    }

    private void setupbound(ArrayList<CarData> carDatas) {
        mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {

                LatLng searchLatLng = new LatLng(source_lat, source_lon);

                BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.ic_round_black);

                mMap.addMarker(new MarkerOptions().icon(icon).title("Source").position(searchLatLng));
                int radius = userSessionManager.getSearchRadius();
                float radiusDivideBy3 = radius / 3;
                mMap.addCircle(new CircleOptions()
                        .center(searchLatLng)
                        .radius(radiusDivideBy3 * 1000)
                        .strokeWidth(0f)
                        .fillColor(ContextCompat.getColor(context, R.color.grey_transparent)));
                mMap.addCircle(new CircleOptions()
                        .center(searchLatLng)
                        .radius(radiusDivideBy3 * 2 * 1000)
                        .strokeWidth(0f)
                        .fillColor(ContextCompat.getColor(context, R.color.grey_transparent)));
                Circle circle = mMap.addCircle(new CircleOptions()
                        .center(searchLatLng)
                        .radius(radiusDivideBy3 * 3 * 1000)
                        .strokeWidth(0f)
                        .fillColor(ContextCompat.getColor(context, R.color.grey_transparent)));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(searchLatLng, getZoomLevel(circle)));
            }
        });


    }

    public int getZoomLevel(Circle circle) {
        int zoomLevel = 11;
        if (circle != null) {
            double radius = circle.getRadius() + circle.getRadius() / 2;
            double scale = radius / 500;
            zoomLevel = (int) (16 - Math.log(scale) / Math.log(2));
        }
        return zoomLevel;
    }

    private void showMessage(String s) {
        Toast.makeText(context, s, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRoutingFailure(RouteException e) {

    }

    @Override
    public void onRoutingStart() {

    }

    @Override
    public void onRoutingSuccess(ArrayList<Route> arrayList, int i) {

    }

    @Override
    public void onRoutingCancelled() {

    }

    @Override
    public void onCameraIdle() {

    }

    @Override
    public void onCameraMoveCanceled() {

    }

    @Override
    public void onCameraMove() {

    }

    @Override
    public void onCameraMoveStarted(int i) {

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mapReady = true;
        mMap = googleMap;
        // Add a marker in Sydney and move the camera
        mMap.getUiSettings().setRotateGesturesEnabled(false);
        mMap.getUiSettings().setAllGesturesEnabled(true);
        mMap.getUiSettings().setMapToolbarEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);

        mMap.setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener() {
            @Override
            public void onCameraMove() {
                zoomlevel = mMap.getCameraPosition().zoom;
            }
        });
        if (isAdded()) {
            searchTrip();
        }
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }
}
