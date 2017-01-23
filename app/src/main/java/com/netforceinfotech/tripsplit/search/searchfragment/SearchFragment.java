package com.netforceinfotech.tripsplit.search.searchfragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.directions.route.AbstractRouting;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.netforceinfotech.tripsplit.R;
import com.netforceinfotech.tripsplit.general.UserSessionManager;
import com.netforceinfotech.tripsplit.posttrip.GoogleMapActivity;
import com.shehabic.droppy.DroppyClickCallbackInterface;
import com.shehabic.droppy.DroppyMenuItem;
import com.shehabic.droppy.DroppyMenuPopup;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;


public class SearchFragment extends Fragment implements View.OnClickListener, DatePickerDialog.OnDateSetListener, GoogleMapActivity.AddressListner, OnMapReadyCallback, CompoundButton.OnCheckedChangeListener {
    private static final String IMAGE_DIRECTORY_NAME = "tripsplit";

    private static final int PICK_IMAGE = 1234;
    private static final int TAKE_PHOTO_CODE = 1235;
    private static final int DESTINATION = 420;
    private static final int SOURCE = 421;
    private static final int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;
    AbstractRouting.TravelMode mode;
    String group = "mixed";
    float zoomlevel = 16f;
    private Marker destinationMarker, sourceMarker;
    String TAG = "tag";
    String sort = "Best match";
    private static final int TRAVEL_TO = 2;
    private static final int TRAVEL_FROM = 1;
    private Calendar calendar;
    RecyclerView recyclerView;
    LinearLayoutManager layoutManager;
    public TextView textViewDate, textViewSource, textViewDestination;
    RelativeLayout relativeLayoutSort, relativeLayoutGlobe;
    LinearLayout linearLayoutRefine, linearlayoutSearch;

    Context context;
    private double dest_lat, dest_lon, source_lat, source_lon;
    UserSessionManager userSessionManager;
    TextView textViewSort;
    String type;
    private MaterialDialog progressDialog;
    private MapView mMapView;
    private boolean mapReady = false;
    private GoogleMap mMap;
    private boolean destinationFlag = false, sourceFlag = false;
    private LatLng destinationLatLang, sourceLatLng;
    private Place place;
    private CameraUpdate cu;
    private SearchListViewFragment dashboardFragment;
    private SearchGlobalViewFragment globeviewFragment;
    private static int SELECTED_VIEW = 0;
    private static int LIST_VIEW = 0;
    private static int GLOBE_VIEW = 1;
    private String etd = "0000-00-00";
    CheckBox checkBoxGlobe;
    TextView textViewGlobe;
    private String sourceAddress, destinationAddress;

    public SearchFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.search_fragment, container, false);
        context = getActivity();
        SELECTED_VIEW = LIST_VIEW;
        mMapView = (MapView) view.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);

        mMapView.onResume(); // needed to get the map to display immediately

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        mMapView.getMapAsync(this);
        userSessionManager = new UserSessionManager(context);
        type = this.getArguments().getString("type");
        Log.i("type", type);
        initView(view);
        return view;


    }

    public void createDashedLine(GoogleMap map, LatLng latLngOrig, LatLng latLngDest, int color) {
        try {
            map.clear();
        } catch (Exception ex) {

        }
        BitmapDescriptor sourceIcon = BitmapDescriptorFactory.fromResource(R.drawable.ic_marker_black);

        sourceMarker = mMap.addMarker(new MarkerOptions().icon(sourceIcon).snippet(sourceAddress).title(getString(R.string.source)).position(sourceLatLng));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sourceLatLng, zoomlevel));
        int height = 20;
        int width = 20;
        BitmapDrawable bitmapdraw = (BitmapDrawable) ContextCompat.getDrawable(context, R.drawable.ic_round_black);
        ;

        Bitmap b = bitmapdraw.getBitmap();
        Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);

        destinationMarker = mMap.addMarker(new MarkerOptions().icon((BitmapDescriptorFactory.fromBitmap(smallMarker))).snippet(destinationAddress).title(getString(R.string.destination)).position(destinationLatLang));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sourceLatLng, zoomlevel));

        Polyline line = map.addPolyline(new PolylineOptions()
                .add(sourceLatLng, destinationLatLang)
                .width(5)
                .color(Color.BLACK));
    }

    private void initView(View view) {
        textViewGlobe = (TextView) view.findViewById(R.id.textViewGlobe);
        checkBoxGlobe = (CheckBox) view.findViewById(R.id.checkBoxGlobe);
        checkBoxGlobe.setOnCheckedChangeListener(this);
        progressDialog = new MaterialDialog.Builder(context)
                .title(R.string.progress_dialog)
                .content(R.string.please_wait)
                .progress(true, 0).build();
        textViewSort = (TextView) view.findViewById(R.id.textViewSort);
        linearlayoutSearch = (LinearLayout) view.findViewById(R.id.linearlayoutSearch);
        linearlayoutSearch.setOnClickListener(this);
        textViewSource = (TextView) view.findViewById(R.id.travel_from);
        textViewSource.setOnClickListener(this);
        textViewDestination = (TextView) view.findViewById(R.id.travel_to);
        textViewDestination.setOnClickListener(this);
        textViewDate = (TextView) view.findViewById(R.id.textviewETD);
        textViewDate.setOnClickListener(this);
        relativeLayoutGlobe = (RelativeLayout) view.findViewById(R.id.relativeLayoutGlobe);
        relativeLayoutGlobe.setOnClickListener(this);
        relativeLayoutSort = (RelativeLayout) view.findViewById(R.id.relativeLayoutSort);
        linearLayoutRefine = (LinearLayout) view.findViewById(R.id.linearlayoutRefine);
        linearLayoutRefine.setOnClickListener(this);
        DroppyMenuPopup.Builder droppyBuilder = new DroppyMenuPopup.Builder(getActivity(), relativeLayoutSort);
        final ArrayList<String> sorts = new ArrayList<>();
        sorts.add(getString(R.string.best_match));
        sorts.add(getString(R.string.low_cost));
        sorts.add(getString(R.string.highest_cost));
        sorts.add(getString(R.string.date));
        sort = sorts.get(0);
        droppyBuilder.addMenuItem(new DroppyMenuItem(getString(R.string.best_match)));
        droppyBuilder.addMenuItem(new DroppyMenuItem(getString(R.string.low_cost)));
        droppyBuilder.addMenuItem(new DroppyMenuItem(getString(R.string.highest_cost)));
        droppyBuilder.addMenuItem(new DroppyMenuItem(getString(R.string.date)));
        droppyBuilder.setOnClick(new DroppyClickCallbackInterface() {
            @Override
            public void call(View v, int id) {
                sort = sorts.get(id);
                textViewSort.setText(sort);
            }
        });
        droppyBuilder.build();
        checkBoxGlobe.setChecked(false);
        if (isAdded()) {
            textViewGlobe.setText(getString(R.string.globe));
        }


    }


    public void setupListViewFragment() {
        dashboardFragment = new SearchListViewFragment();
        String tagName = dashboardFragment.getClass().getName();
        /*
        * .setBodyParameter("dest_lat", dest_lat + "")
                .setBodyParameter("dest_lon", dest_lon + "")
                .setBodyParameter("source_lat", source_lat + "")
                .setBodyParameter("source_lon", source_lon + "")
                .setBodyParameter("etd", etd)
                .setBodyParameter("range",userSessionManager.getSearchRadius() )
                .setBodyParameter("sort", sort)
                .setBodyParameter("type", type)
        * */
        Bundle bundle = new Bundle();
        bundle.putDouble("dest_lat", dest_lat);
        bundle.putDouble("dest_lon", dest_lon);
        bundle.putDouble("source_lat", source_lat);
        bundle.putDouble("source_lon", source_lon);
        bundle.putString("etd", etd);
        bundle.putString("sort", sort);
        bundle.putString("type", type);
        dashboardFragment.setArguments(bundle);
        replaceFragment(dashboardFragment, tagName);
    }

    public void setupGlobeViewFragment() {
        globeviewFragment = new SearchGlobalViewFragment();
        String tagName = globeviewFragment.getClass().getName();
        Bundle bundle = new Bundle();
        bundle.putDouble("dest_lat", dest_lat);
        bundle.putDouble("dest_lon", dest_lon);
        bundle.putDouble("source_lat", source_lat);
        bundle.putDouble("source_lon", source_lon);
        bundle.putString("etd", etd);
        bundle.putString("sort", sort);
        bundle.putString("type", type);
        globeviewFragment.setArguments(bundle);
        replaceFragment(globeviewFragment, tagName);
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.relativeLayoutGlobe:
                checkBoxGlobe.performClick();

                break;
            case R.id.linearlayoutSearch:
                if (textViewSource.getText().length() <= 0 || textViewDestination.getText().length() <= 0 || textViewDate.getText().length() <= 0) {
                    showMessage(getString(R.string.cantbeempy));
                    return;
                }
                if (SELECTED_VIEW == LIST_VIEW) {
                    setupListViewFragment();
                } else {
                    setupGlobeViewFragment();
                }
                break;
            case R.id.linearlayoutRefine:
                clearAllData();
                break;
            case R.id.textviewETD:
                Calendar now = Calendar.getInstance();
                DatePickerDialog dpd = DatePickerDialog.newInstance(SearchFragment.this, now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH));

                dpd.show(getActivity().getFragmentManager(), "Datepickerdialog");
                break;
            case R.id.travel_from:
                Intent google_intent = new Intent(getActivity(), GoogleMapActivity.class);
                google_intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                google_intent.putExtra("choose_source", true);
                startActivityForResult(google_intent, SOURCE);
                break;
            case R.id.travel_to:
                Intent google_intent2 = new Intent(getActivity(), GoogleMapActivity.class);
                google_intent2.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                google_intent2.putExtra("choose_source", false);
                startActivityForResult(google_intent2, DESTINATION);
                break;
        }


    }


    private void clearAllData() {
        try {
            SELECTED_VIEW = LIST_VIEW;
            relativeLayoutGlobe.setBackgroundResource(R.drawable.roundrect_tranparent_unselected);

            textViewSource.setText("Travel From");
            textViewDestination.setText("Travel To");
            textViewDate.setText("Select date and time");
            if (dashboardFragment != null) {
                dashboardFragment.clearData();
            }
        } catch (Exception ex) {

        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case DESTINATION:
                destinationFlag = true;
                if (resultCode == RESULT_OK) {
                    dest_lat = data.getDoubleExtra("lat", 0);
                    dest_lon = data.getDoubleExtra("lon", 0);
                    String address = data.getStringExtra("address");
                    this.destinationAddress = address;
                    textViewDestination.setText(address);
                    destinationLatLang = new LatLng(dest_lat, dest_lon);
                    try {
                        destinationMarker.remove();
                    } catch (Exception ex) {

                    }
                    int height = 20;
                    int width = 20;
                    BitmapDrawable bitmapdraw = (BitmapDrawable) ContextCompat.getDrawable(context, R.drawable.ic_round_black);
                    ;
                    Bitmap b = bitmapdraw.getBitmap();
                    Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);

                    destinationMarker = mMap.addMarker(new MarkerOptions().icon((BitmapDescriptorFactory.fromBitmap(smallMarker))).snippet(address).title(getString(R.string.destination)).position(destinationLatLang));
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(destinationLatLang, zoomlevel));
                    if (sourceFlag) {
                        zoomInTwoPoint();
                    }

                }
                break;
            case SOURCE:
                sourceFlag = true;
                if (resultCode == RESULT_OK) {
                    source_lat = data.getDoubleExtra("lat", 0);
                    source_lon = data.getDoubleExtra("lon", 0);
                    String address = data.getStringExtra("address");
                    this.sourceAddress = address;
                    textViewSource.setText(address);
                    sourceLatLng = new LatLng(source_lat, source_lon);
                    try {
                        sourceMarker.remove();
                    } catch (Exception ex) {

                    }
                    BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.ic_marker_black);

                    sourceMarker = mMap.addMarker(new MarkerOptions().icon(icon).snippet(address).title(getString(R.string.source)).position(sourceLatLng));
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sourceLatLng, zoomlevel));

                    if (destinationFlag) {
                        zoomInTwoPoint();
                    }
                }
                break;
            case PLACE_AUTOCOMPLETE_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    place = PlaceAutocomplete.getPlace(getActivity(), data);
                    Log.i(TAG, "Place: " + place.getName());
                } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                    Status status = PlaceAutocomplete.getStatus(getActivity(), data);
                    // TODO: Handle the error.
                    Log.i(TAG, status.getStatusMessage());

                } else if (resultCode == RESULT_CANCELED) {
                    // The user canceled the operation.
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    private void zoomInTwoPoint() {
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        builder.include(sourceLatLng);
        builder.include(destinationLatLang);
        LatLngBounds bounds = builder.build();
        int padding = 120; // offset from edges of the map in pixels
        cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
        mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                mMap.animateCamera(cu);
            }
        });
        createDashedLine(mMap, sourceLatLng, destinationLatLang, ContextCompat.getColor(context, R.color.black));

    }

    public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute, int second) {
        String hourString = hourOfDay < 10 ? "0" + hourOfDay : "" + hourOfDay;
        String minuteString = minute < 10 ? "0" + minute : "" + minute;
        String secondString = second < 10 ? "0" + second : "" + second;
        String time = "You picked the following time: " + hourString + "h" + minuteString + "m" + secondString + "s";
        textViewDate.append(" " + hourString + ":" + minuteString);
    }


    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        Date date2 = new Date();
        SimpleDateFormat date_format = new SimpleDateFormat("yyyy-MM-dd");
        try {
            monthOfYear++;
            date2 = date_format.parse(year + "-" + monthOfYear + "-" + dayOfMonth);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        SimpleDateFormat outDate = new SimpleDateFormat("EEE dd MMM yyyy");


        textViewDate.setText(outDate.format(date2));
        etd = date_format.format(date2);
    }

    @Override
    public void gotAddress(String address, boolean source) {
        if (source) {
            textViewSource.setText(address);
        } else {
            textViewDestination.setText(address);
        }

    }

    private void replaceFragment(Fragment newFragment, String tag) {
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.searchFrame, newFragment, tag);
        transaction.commit();
    }


    private void showMessage(String clicked) {
        Toast.makeText(context, clicked, Toast.LENGTH_SHORT).show();
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

    }


    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        switch (compoundButton.getId()) {
            case R.id.checkBoxGlobe:
                if (b) {
                    textViewGlobe.setText(getString(R.string.list));
                    SELECTED_VIEW = GLOBE_VIEW;
                    setupGlobeViewFragment();
                } else {
                    setupListViewFragment();
                    SELECTED_VIEW = LIST_VIEW;
                    textViewGlobe.setText(getString(R.string.globe));
                }
                break;

        }
    }
}