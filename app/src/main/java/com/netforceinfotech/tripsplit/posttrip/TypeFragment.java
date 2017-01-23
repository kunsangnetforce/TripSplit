package com.netforceinfotech.tripsplit.posttrip;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.appyvet.rangebar.RangeBar;
import com.bumptech.glide.Glide;
import com.directions.route.AbstractRouting;
import com.directions.route.Route;
import com.directions.route.RouteException;
import com.directions.route.Routing;
import com.directions.route.RoutingListener;
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
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.mukesh.countrypicker.fragments.CountryPicker;
import com.mukesh.countrypicker.interfaces.CountryPickerListener;
import com.netforceinfotech.tripsplit.home.HomeFragment;
import com.netforceinfotech.tripsplit.R;
import com.netforceinfotech.tripsplit.general.ImageFilePath;
import com.netforceinfotech.tripsplit.general.UserSessionManager;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Currency;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static com.facebook.FacebookSdk.getApplicationContext;


public class TypeFragment extends Fragment implements View.OnClickListener, TimePickerDialog.OnTimeSetListener, DatePickerDialog.OnDateSetListener, OnMapReadyCallback, GoogleMap.OnCameraIdleListener, GoogleMap.OnCameraMoveStartedListener, GoogleMap.OnCameraMoveListener, GoogleMap.OnCameraMoveCanceledListener, RoutingListener, CompoundButton.OnCheckedChangeListener {

    private static final String IMAGE_DIRECTORY_NAME = "tripsplit";

    private static final int PICK_IMAGE = 1234;
    private static final int TAKE_PHOTO_CODE = 1235;
    private static final int DESTINATION = 420;
    private static final int SOURCE = 421;
    private static final int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;
    AbstractRouting.TravelMode mode;
    String group = "mixed";
    float zoomlevel = 16f;
    private PolylineOptions polylineOptions = new PolylineOptions();
    private ArrayList<LatLng> arrayPoints = new ArrayList<>();
    LinearLayout linearLayoutReturn;
    Context context;
    TextView textViewETD, textviewPax, textviewSpace, textviewETA, textviewAgeGroup, textViewReturnETA, textViewReturnETD;
    Button increamentPass, decreamentPass, increamentSpace, decreamentSpace, male, female, mixed, buttonPost, buttonAddImage;
    int pass_number = 1;
    int space_number = 1;
    boolean etdclicked = true;
    private Uri fileUri;
    private String filePath;
    private MaterialDialog dialog;
    Button buttonCurrency;
    RangeBar rangeBar;
    RadioButton radioButtonOneway, radioButtonReturn;
    TextView textViewDepartureAddress, textViewDestinationAddress;
    private Intent google_intent;
    String TAG = "googleplace";
    private MapView mMapView;
    private boolean mapReady = false;
    private LatLng destinationLatLang, sourceLatLng;
    private boolean destinationFlag = false, sourceFlag = false;
    private Place place;
    private List<Polyline> polylines = new ArrayList<>();
    private static final int[] COLORS = new int[]{R.color.colorPrimaryDark, R.color.colorPrimary, R.color.colorPrimaryLight, R.color.colorAccent, R.color.primary_dark_material_light};
    private GoogleMap mMap;
    String type = "car";
    ImageView imageViewOneWay, imageViewReturn, image;
    private boolean returnFlag = true;
    UserSessionManager userSessionManager;
    private String upperLimit = "100", lowerLimit = "0";
    private String trip = "0";
    EditText editTextVehicleType, editTextItenarary, editTextTotalCost;
    private String currencyCode;
    private Calendar now;
    private DatePickerDialog dpd;
    private boolean returnetdclicked;
    private MaterialDialog progressDialog;
    private Marker destinationMarker, sournceMarker;
    private CameraUpdate cu;
    private MaterialDialog custompopup;
    private boolean imageFlag = false;
    private String timezone;
    private String sourceAddress = "", destinationAddress = "";

    public TypeFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_type, container, false);
        context = getActivity();
        userSessionManager = new UserSessionManager(context);
        mMapView = (MapView) view.findViewById(R.id.mapView);
        context = getActivity();
        Calendar cal = Calendar.getInstance();
        timezone = cal.getTimeZone().getID();
        try {
            type = this.getArguments().getString("type");
            ////aeroplane,car,bus,ship

        } catch (Exception ex) {
            showMessage("bundle error");
            Log.i("kunsang_exception", "paramenter not yet set");
        }
        mMapView.onCreate(savedInstanceState);

        mMapView.onResume(); // needed to get the map to display immediately

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        mMapView.getMapAsync(this);
        initView(view);
        return view;
    }

    private void initView(View view) {

        image = (ImageView) view.findViewById(R.id.image);
        image.setVisibility(View.GONE);
        progressDialog = new MaterialDialog.Builder(context)
                .title(R.string.progress_dialog)
                .content(R.string.please_wait)
                .progress(true, 0).build();
        editTextTotalCost = (EditText) view.findViewById(R.id.et_totalCost);
        editTextItenarary = (EditText) view.findViewById(R.id.et_itenarary);
        editTextVehicleType = (EditText) view.findViewById(R.id.et_vehicleType);
        linearLayoutReturn = (LinearLayout) view.findViewById(R.id.linearlayoutReturn);
        textViewReturnETA = (TextView) view.findViewById(R.id.textviewReturnETA);
        textViewReturnETD = (TextView) view.findViewById(R.id.textviewReturnETD);
        textViewDepartureAddress = (TextView) view.findViewById(R.id.textViewDepartureAddress);
        textViewDestinationAddress = (TextView) view.findViewById(R.id.textViewDestinationAddress);
        textViewDestinationAddress.setOnClickListener(this);
        textViewDepartureAddress.setOnClickListener(this);
        textViewReturnETA.setOnClickListener(this);
        textViewReturnETD.setOnClickListener(this);
        imageViewOneWay = (ImageView) view.findViewById(R.id.imageViewOneway);
        imageViewReturn = (ImageView) view.findViewById(R.id.imageViewReturn);
        imageViewOneWay.setOnClickListener(this);
        imageViewReturn.setOnClickListener(this);
        radioButtonOneway = (RadioButton) view.findViewById(R.id.radioButtonOneway);
        radioButtonReturn = (RadioButton) view.findViewById(R.id.radioButtonReturn);
        radioButtonOneway.setOnCheckedChangeListener(this);
        radioButtonReturn.setOnCheckedChangeListener(this);
        radioButtonOneway.setChecked(false);
        radioButtonOneway.setChecked(true);

        rangeBar = (RangeBar) view.findViewById(R.id.rangebar);
        textviewAgeGroup = (TextView) view.findViewById(R.id.textViewAgeGroup);
        textviewAgeGroup.setText(getString(R.string.age_group) + " : " + lowerLimit + "-" + upperLimit);

        buttonCurrency = (Button) view.findViewById(R.id.buttonCurrency);
        buttonCurrency.setOnClickListener(this);

        rangeBar.setOnRangeBarChangeListener(new RangeBar.OnRangeBarChangeListener() {
            @Override
            public void onRangeChangeListener(RangeBar rangeBar, int leftPinIndex,
                                              int rightPinIndex,
                                              String leftPinValue, String rightPinValue) {
                textviewAgeGroup.setText(getString(R.string.age_group) + " : " + leftPinValue + "-" + rightPinValue);
                lowerLimit = leftPinValue;
                upperLimit = rightPinValue;
            }
        });
        buttonAddImage = (Button) view.findViewById(R.id.buttonAddImage);
        buttonAddImage.setOnClickListener(this);
        buttonPost = (Button) view.findViewById(R.id.buttonPost);
        buttonPost.setOnClickListener(this);
        increamentPass = (Button) view.findViewById(R.id.increament_pass);
        decreamentPass = (Button) view.findViewById(R.id.decreament_pass);
        increamentSpace = (Button) view.findViewById(R.id.increament_Space);
        decreamentSpace = (Button) view.findViewById(R.id.decreament_Space);
        male = (Button) view.findViewById(R.id.maleButton);
        female = (Button) view.findViewById(R.id.femaleButton);
        mixed = (Button) view.findViewById(R.id.mixedButton);
        mixed.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.colorPrimary));
        mixed.setTextColor(ContextCompat.getColor(getActivity(), R.color.white));
        textviewPax = (TextView) view.findViewById(R.id.textViewPax);
        textviewSpace = (TextView) view.findViewById(R.id.textViewSpace);
        textviewETA = (TextView) view.findViewById(R.id.textviewETA);
        textViewETD = (TextView) view.findViewById(R.id.textviewETD);
        Typeface custom_font = Typeface.createFromAsset(context.getAssets(), "fonts/GothamRoundedBook.ttf");
        textViewETD.setTypeface(custom_font);
        textviewETA.setOnClickListener(this);
        textViewETD.setOnClickListener(this);
        increamentPass.setOnClickListener(this);
        decreamentPass.setOnClickListener(this);
        increamentSpace.setOnClickListener(this);
        decreamentSpace.setOnClickListener(this);
        male.setOnClickListener(this);
        female.setOnClickListener(this);
        mixed.setOnClickListener(this);

    }


    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.textViewDestinationAddress:
                try {
                    Intent intent = new Intent(context, GoogleMapActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putBoolean("choose_source", false);
                    intent.putExtras(bundle);
                    startActivityForResult(intent, DESTINATION);
                } catch (Exception ex) {
                    showMessage("google map exception");
                    ex.printStackTrace();
                }
                break;
            case R.id.textViewDepartureAddress:
                try {
                    Intent intent = new Intent(context, GoogleMapActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putBoolean("choose_source", true);
                    intent.putExtras(bundle);
                    startActivityForResult(intent, SOURCE);
                } catch (Exception ex) {
                    showMessage("google map exception");
                    ex.printStackTrace();
                }
              /*  try {
                    Intent intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                            .build(getActivity());
                    startActivityForResult(intent, SOURCE);
                } catch (GooglePlayServicesRepairableException e) {
                    // TODO: Handle the error.
                } catch (GooglePlayServicesNotAvailableException e) {
                    // TODO: Handle the error.
                }*/
                break;
            case R.id.buttonAddImage:
                showEditPicPopup();
                break;
            case R.id.buttonPost:
                //  setupDashboardFragment();
                postTrip();
                break;
            case R.id.textviewETD:
                etdclicked = true;
                returnetdclicked = false;

                now = Calendar.getInstance();
                dpd = DatePickerDialog.newInstance(
                        TypeFragment.this,
                        now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH)
                );
                dpd.show(getActivity().getFragmentManager(), "Datepickerdialog");


                break;
            case R.id.textviewETA:
                etdclicked = false;
                returnetdclicked = false;

                now = Calendar.getInstance();
                dpd = DatePickerDialog.newInstance(
                        TypeFragment.this,
                        now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH)
                );
                dpd.show(getActivity().getFragmentManager(), "Datepickerdialog");


                break;
            case R.id.textviewReturnETA:
                etdclicked = false;
                returnetdclicked = true;
                now = Calendar.getInstance();
                dpd = DatePickerDialog.newInstance(
                        TypeFragment.this,
                        now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH)
                );
                dpd.show(getActivity().getFragmentManager(), "Datepickerdialog");

                break;
            case R.id.textviewReturnETD:
                etdclicked = true;
                returnetdclicked = true;

                now = Calendar.getInstance();
                dpd = DatePickerDialog.newInstance(
                        TypeFragment.this,
                        now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH)
                );
                dpd.show(getActivity().getFragmentManager(), "Datepickerdialog");

                break;

            case R.id.increament_pass:

                pass_number = pass_number + 1;
                String p_n = String.valueOf(pass_number);
                textviewPax.setText(p_n);


                break;

            case R.id.decreament_pass:

                if (pass_number >= 1) {
                    pass_number = pass_number - 1;
                    String dp_n = String.valueOf(pass_number);
                    textviewPax.setText(dp_n);
                }
                break;

            case R.id.increament_Space:

                space_number = space_number + 1;
                String sp_n = String.valueOf(space_number);
                textviewSpace.setText(sp_n);

                break;

            case R.id.decreament_Space:
                if (space_number >= 1) {
                    space_number = space_number - 1;
                    String dsp_n = String.valueOf(space_number);
                    textviewSpace.setText(dsp_n);
                }

                break;

            case R.id.maleButton:

                group = "male";
                female.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.border_image));
                mixed.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.border_image2));
                male.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.colorPrimary));

                male.setTextColor(ContextCompat.getColor(getActivity(), R.color.white));
                female.setTextColor(ContextCompat.getColor(getActivity(), R.color.colorPrimary));
                mixed.setTextColor(ContextCompat.getColor(getActivity(), R.color.colorPrimary));

                break;

            case R.id.femaleButton:
                group = "female";
                male.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.border_image));
                mixed.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.border_image2));
                female.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.colorPrimary));

                female.setTextColor(ContextCompat.getColor(getActivity(), R.color.white));
                male.setTextColor(ContextCompat.getColor(getActivity(), R.color.colorPrimary));
                mixed.setTextColor(ContextCompat.getColor(getActivity(), R.color.colorPrimary));


                break;

            case R.id.mixedButton:
                group = "mixed";

                male.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.border_image));
                female.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.border_image));
                mixed.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.colorPrimary));

                mixed.setTextColor(ContextCompat.getColor(getActivity(), R.color.white));
                male.setTextColor(ContextCompat.getColor(getActivity(), R.color.colorPrimary));
                female.setTextColor(ContextCompat.getColor(getActivity(), R.color.colorPrimary));
                break;

            case R.id.linearLayoutPicture:
                takePictureIntent();
                dialog.dismiss();
                break;
            case R.id.linearLayoutGalary:
                pickPictureIntent();
                dialog.dismiss();
                break;
            case R.id.buttonCurrency:
                showCountryPopUp();
                break;
            case R.id.imageViewOneway:
                radioButtonOneway.performClick();
                break;
            case R.id.imageViewReturn:
                radioButtonReturn.performClick();
                break;
        }


    }

    private void postTrip() {
        if (validateDate()) {
            String baseUrl = getString(R.string.url);
            // https://netforcesales.com/tripesplit/mobileApp/api/services.php?opt=posttrip
            String url = baseUrl + "services.php?opt=posttrip";
            Log.i("url", url);
            Log.i("type", type);

            progressDialog.show();
            if (filePath != null) {
                Log.i("filePath", "inside send image");
                Ion.with(getApplicationContext())
                        .load("POST", url)
                        .setMultipartParameter("user_id", userSessionManager.getUserId())
                        .setMultipartParameter("type", type)
                        .setMultipartParameter("pax", textviewPax.getText().toString().trim())
                        .setMultipartParameter("space", textviewSpace.getText().toString().trim())
                        .setMultipartParameter("group", group)
                        .setMultipartParameter("age_group_upper", upperLimit)
                        .setMultipartParameter("age_group_lower", lowerLimit)
                        .setMultipartParameter("trip", trip)
                        .setMultipartParameter("vehicle_type", editTextVehicleType.getText().toString().trim())
                        .setMultipartParameter("depart_address", textViewDepartureAddress.getText().toString())
                        .setMultipartParameter("dest_address", textViewDestinationAddress.getText().toString())
                        .setMultipartParameter("depart_lat", sourceLatLng.latitude + "")
                        .setMultipartParameter("depart_lon", sourceLatLng.longitude + "")
                        .setMultipartParameter("dest_lat", destinationLatLang.latitude + "")
                        .setMultipartParameter("dest_lon", destinationLatLang.longitude + "")
                        .setMultipartParameter("etd", getServerFormatDate(textViewETD.getText().toString()))
                        .setMultipartParameter("eta", getServerFormatDate(textviewETA.getText().toString()))
                        .setMultipartParameter("return_etd", getServerFormatDate(textViewReturnETD.getText().toString()))
                        .setMultipartParameter("return_eta", getServerFormatDate(textViewReturnETA.getText().toString()))
                        .setMultipartParameter("iteinerary", editTextItenarary.getText().toString().trim())
                        .setMultipartParameter("totalcost", editTextTotalCost.getText().toString())
                        .setMultipartParameter("timezone", timezone)
                        .setMultipartParameter("currency", currencyCode)
                        .setMultipartFile("image", new File(filePath))
                        .asJsonObject()
                        .setCallback(new FutureCallback<JsonObject>() {
                            @Override
                            public void onCompleted(Exception e, JsonObject result) {
                                progressDialog.dismiss();
                                if (result == null) {
                                    e.printStackTrace();
                                    showMessage("Error in Posting new Trip!!! Try again");
                                } else {
                                    Log.i("result", result.toString());
                                    if (result.get("status").getAsString().equalsIgnoreCase("success")) {
                                        /*result.toString();
                                        showMessage("New Trip Posted Successfully");
                                        setupDashboardFragment();*/

                                        JsonArray data = result.getAsJsonArray("data");
                                        JsonObject jsonObject = data.get(0).getAsJsonObject();
                                        JsonObject trip_id = jsonObject.getAsJsonObject("data");
                                        setupDialog(trip_id);
                                    } else {

                                    }

                                }
                            }
                        });
            } else {
                Log.i("filePath", "outside send image");
                Ion.with(getApplicationContext())
                        .load("POST", url)
                        .setMultipartParameter("user_id", userSessionManager.getUserId())
                        .setMultipartParameter("type", type)
                        .setMultipartParameter("pax", textviewPax.getText().toString().trim())
                        .setMultipartParameter("group", group)
                        .setMultipartParameter("age_group_upper", upperLimit)
                        .setMultipartParameter("age_group_lower", lowerLimit)
                        .setMultipartParameter("trip", trip)
                        .setMultipartParameter("vehicle_type", editTextVehicleType.getText().toString().trim())
                        .setMultipartParameter("depart_address", textViewDepartureAddress.getText().toString())
                        .setMultipartParameter("dest_address", textViewDestinationAddress.getText().toString())
                        .setMultipartParameter("depart_lat", sourceLatLng.latitude + "")
                        .setMultipartParameter("depart_lon", sourceLatLng.longitude + "")
                        .setMultipartParameter("dest_lat", destinationLatLang.latitude + "")
                        .setMultipartParameter("dest_lon", destinationLatLang.longitude + "")
                        .setMultipartParameter("etd", getServerFormatDate(textViewETD.getText().toString()))
                        .setMultipartParameter("eta", getServerFormatDate(textviewETA.getText().toString()))
                        .setMultipartParameter("return_etd", getServerFormatDate(textViewReturnETD.getText().toString()))
                        .setMultipartParameter("return_eta", getServerFormatDate(textViewReturnETA.getText().toString()))
                        .setMultipartParameter("iteinerary", editTextItenarary.getText().toString().trim())
                        .setMultipartParameter("totalcost", editTextTotalCost.getText().toString())
                        //currency
                        .setMultipartParameter("currency", currencyCode)
                        .asJsonObject()
                        .setCallback(new FutureCallback<JsonObject>() {
                            @Override
                            public void onCompleted(Exception e, JsonObject result) {
                                progressDialog.dismiss();
                                if (result == null) {
                                    e.printStackTrace();
                                    showMessage("Error in Posting new Trip!!! Try again");
                                } else {
                                    if (result.get("status").getAsString().equalsIgnoreCase("success")) {
                                        /*result.toString();
                                        showMessage("New Trip Posted Successfully");
                                        setupDashboardFragment();*/
                                        Log.i("result", result.toString());
                                        JsonArray data = result.getAsJsonArray("data");
                                        JsonObject jsonObject = data.get(0).getAsJsonObject();
                                        JsonObject trip_id = jsonObject.getAsJsonObject("data");
                                        setupDialog(trip_id);
                                    }
                                }
                            }
                        });
            }

        }
    }

    private String getServerFormatDate(String dateString) {
        //Fri 27 Jan 2017 12:31
        //2017-01-16 16:43:00
        try{
            SimpleDateFormat fmt = new SimpleDateFormat("EEE dd MMM yyyy HH:mm");
            Date date = null;
            try {
                date = fmt.parse(dateString);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            SimpleDateFormat fmtOut = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            return fmtOut.format(date);
        }catch (Exception ex){
            return "";
        }

    }


    private void setupDialog(JsonObject jsonObject) {
        boolean wrapInScrollView = true;
        /*
        *  textviewETD_date  textviewETD_time  textViewSource  textViewDestination
                textViewETA  textViewJourneyTime  textCarType  textViewPax

        * */
        custompopup = new MaterialDialog.Builder(context)
                .title(R.string.postdetail)
                .customView(R.layout.tripdetail, wrapInScrollView)
                .positiveText(R.string.ok)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        setupDashboardFragment();
                        custompopup.dismiss();
                    }
                })
                .show();

        TextView textViewETD_date, textViewETD_time, textViewSource, textViewDestination, textViewETA, textViewJourneyTime,
                textViewCarType, textViewPax, textViewPrice, textViewETD_date_return, textViewETD_time_return,
                textViewSource_return, textViewDestination_return, textViewETA_return, textViewJourneyTime_return;
        LinearLayout linearLayoutReturn = (LinearLayout) custompopup.findViewById(R.id.linearLayoutReturn);
        textViewETD_date_return = (TextView) custompopup.findViewById(R.id.textviewETD_dateReturn);
        textViewETD_time_return = (TextView) custompopup.findViewById(R.id.textviewETD_timeReturn);
        textViewSource_return = (TextView) custompopup.findViewById(R.id.textViewSourceReturn);
        textViewDestination_return = (TextView) custompopup.findViewById(R.id.textViewDestinationReturn);
        textViewETA_return = (TextView) custompopup.findViewById(R.id.textViewETAReturn);
        textViewJourneyTime_return = (TextView) custompopup.findViewById(R.id.textViewJourneyTimeReturn);
        textViewETD_date = (TextView) custompopup.findViewById(R.id.textviewETD_date);
        textViewETD_time = (TextView) custompopup.findViewById(R.id.textviewETD_time);
        textViewSource = (TextView) custompopup.findViewById(R.id.textViewSource);
        textViewDestination = (TextView) custompopup.findViewById(R.id.textViewDestination);
        textViewETA = (TextView) custompopup.findViewById(R.id.textViewETA);
        textViewJourneyTime = (TextView) custompopup.findViewById(R.id.textViewJourneyTime);
        textViewCarType = (TextView) custompopup.findViewById(R.id.textCarType);
        textViewPax = (TextView) custompopup.findViewById(R.id.textViewPax);
        textViewPrice = (TextView) custompopup.findViewById(R.id.textViewPrice);
        String pax = jsonObject.get("pax").getAsString();
        String vehical_type = jsonObject.get("vehical_type").getAsString();
        String depart_address = jsonObject.get("depart_address").getAsString();
        String dest_address = jsonObject.get("dest_address").getAsString();
        String etd = jsonObject.get("etd").getAsString();
        String eta = jsonObject.get("eta").getAsString();
        String start_price = jsonObject.get("start_price").getAsString();
        String currency = jsonObject.get("currency").getAsString();
        String trip = jsonObject.get("trip").getAsString();
        if (trip.equalsIgnoreCase("1")) {
            linearLayoutReturn.setVisibility(View.VISIBLE);
            textViewDestination_return.setText(depart_address);
            textViewSource_return.setText(dest_address);
            String return_eta = jsonObject.get("return_eta").getAsString();
            String return_etd = jsonObject.get("return_etd").getAsString();
            textViewETD_date_return.setText(getFormatedDate(return_etd));
            textViewETD_time_return.setText(getFormetedTime(return_etd));
            textViewETA_return.setText(getFormetedTime(return_eta));
            textViewJourneyTime_return.setText(getFormattedTimeDiff(return_eta, return_etd));

        } else {
            linearLayoutReturn.setVisibility(View.GONE);
        }

        textViewPrice.setText(currency + " " + start_price);
        textViewPax.setText(pax);
        textViewCarType.setText(vehical_type);
        textViewSource.setText(depart_address);
        textViewDestination.setText(dest_address);
        String etd_date = getFormatedDate(etd);
        textViewETD_date.setText(etd_date);
        String etd_time = getFormetedTime(etd);
        textViewETD_time.setText(etd_time);
        String eta_time = getFormetedTime(eta);
        textViewETA.setText(eta_time);
        String timeDiff = getFormattedTimeDiff(eta, etd);
        textViewJourneyTime.setText(timeDiff);

    }

    private String getFormattedTimeDiff(String eta, String etd) {
        Date d1 = null;
        Date d2 = null;
        //        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        try {
            d1 = format.parse(etd);
            d2 = format.parse(eta);

            //in milliseconds
            long diff = d2.getTime() - d1.getTime();

            long diffSeconds = diff / 1000 % 60;
            long diffMinutes = diff / (60 * 1000) % 60;
            long diffHours = diff / (60 * 60 * 1000) % 24;
            long diffDays = diff / (24 * 60 * 60 * 1000);
            String timeDiff = diffDays + "D," + diffHours + "H " + diffMinutes + "m";
            return timeDiff;
        } catch (Exception e) {
            e.printStackTrace();
            return "NA";
        }
    }

    private String getFormetedTime(String etd) {
        //2017-01-16 16:55:00
        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = null;
        try {
            date = fmt.parse(etd);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        SimpleDateFormat fmtOut = new SimpleDateFormat("HH:mm");
        return fmtOut.format(date);
    }

    private String getFormatedDate(String etd) {
        //2017-01-16 16:43:00
        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = null;
        try {
            date = fmt.parse(etd);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        SimpleDateFormat fmtOut = new SimpleDateFormat("EEE dd, MMM yyyy");
        return fmtOut.format(date);
    }

    private boolean validateDate() {
        Date etd, eta, returnetd, returneta;
        String date1 = textViewETD.getText().toString();
        String date2 = textviewETA.getText().toString();
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE dd MMM yyyy HH:mm");
        Log.i("datecheck", date1 + ":" + date2);
        try {
            etd = dateFormat.parse(date1);
        } catch (ParseException e) {
            showMessage("Departure date not set");
            return false;
        }
        try {
            eta = dateFormat.parse(date2);
        } catch (ParseException e) {
            showMessage("Arival date not set");
            return false;
        }
        Log.i("datecheck", etd.toString() + ":" + eta.toString());
        if (etd.after(eta)) {

            showMessage("Arival time should be after Departure time");
            return false;
        }

        if (returnFlag) {
            String date3 = textViewReturnETD.getText().toString();
            String date4 = textViewReturnETA.getText().toString();
            try {
                returnetd = dateFormat.parse(date3);
            } catch (ParseException e) {
                showMessage("Departure date not set");
                return false;
            }
            try {
                returneta = dateFormat.parse(date4);
            } catch (ParseException e) {
                showMessage("Arival date not set");
                return false;
            }
            if (eta.after(returnetd)) {
                showMessage("Return journy cannot be set before!!!");
                return false;
            }
            Log.i("datecheck", returnetd.toString() + ":" + returneta.toString());
            if (returnetd.after(returneta)) {

                showMessage("Arival time should be after Departure time");
                return false;
            }


        }
        if (textViewDepartureAddress.getText().toString().equalsIgnoreCase(getString(R.string.departure_address))) {
            showMessage("Select departure address");
            return false;
        }
        if (textViewDestinationAddress.getText().toString().equalsIgnoreCase(getString(R.string.destination_address))) {
            showMessage("Select destination address");
            return false;
        }
        return true;


    }

    private void showMessage(String s) {
        Toast.makeText(context, s, Toast.LENGTH_SHORT).show();
    }

    public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute, int second) {
        String hourString = hourOfDay < 10 ? "0" + hourOfDay : "" + hourOfDay;
        String minuteString = minute < 10 ? "0" + minute : "" + minute;
        String secondString = second < 10 ? "0" + second : "" + second;
        String time = "You picked the following time: " + hourString + "h" + minuteString + "m" + secondString + "s";
        if (etdclicked && !returnetdclicked) {
            textViewETD.append(" " + hourString + ":" + minuteString);
        } else if (!etdclicked && !returnetdclicked) {
            textviewETA.append(" " + hourString + ":" + minuteString);
        } else if (etdclicked && returnetdclicked) {
            textViewReturnETD.append(" " + hourString + ":" + minuteString);
        } else {
            textViewReturnETA.append(" " + hourString + ":" + minuteString);
        }
    }


    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        Date date2 = new Date();
        SimpleDateFormat date_format = new SimpleDateFormat("yyyy-MM-dd");
        try {
            monthOfYear = monthOfYear + 1;
            date2 = date_format.parse(year + "-" + monthOfYear + "-" + dayOfMonth);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        SimpleDateFormat outDate = new SimpleDateFormat("EEE dd MMM yyyy");

        if (isDateSelectedPast(outDate.format(date2))) {
            showMessage("Cannot post Trip on Past!!!");
            return;
        }
        if (etdclicked && !returnetdclicked) {
            textViewETD.setText(outDate.format(date2));
        } else if (!etdclicked && !returnetdclicked) {
            textviewETA.setText(outDate.format(date2));
        } else if (etdclicked && returnetdclicked) {
            textViewReturnETD.setText(outDate.format(date2));
        } else {
            textViewReturnETA.setText(outDate.format(date2));
        }
        Calendar now = Calendar.getInstance();
        TimePickerDialog tpd = TimePickerDialog.newInstance(
                this,
                now.get(Calendar.HOUR_OF_DAY),
                now.get(Calendar.MINUTE), true
        );
        tpd.show(getActivity().getFragmentManager(), "Timepickerdialog");
    }

    private boolean isDateSelectedPast(String format) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_YEAR, -1);
        Date oneDayBefore = cal.getTime();
        //EEE dd MMM yyyy HH:mm
        try {
            if (new SimpleDateFormat("EEE dd MMM yyyy").parse(format).before(oneDayBefore)) {
                return true;
            } else {
                return false;
            }
        } catch (ParseException e) {
            e.printStackTrace();
            Log.e("Dateparse", "Failed " + format);
            return false;

        }
    }

    private void pickPictureIntent() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
    }

    private void takePictureIntent() {
        UserSessionManager userSessionManager = new UserSessionManager(context);
        String name = userSessionManager.getName();
        Intent cameraIntent = new Intent(
                MediaStore.ACTION_IMAGE_CAPTURE);
        fileUri = getOutputMediaFileUri();
        filePath = fileUri.getPath();

        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
        startActivityForResult(cameraIntent, TAKE_PHOTO_CODE);
    }

    private void showEditPicPopup() {
        boolean wrapInScrollView = true;
        dialog = new MaterialDialog.Builder(context)
                .title(R.string.editpic)
                .customView(R.layout.editpic, wrapInScrollView)
                .negativeText(R.string.cancel)
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();
                    }
                })
                .show();
        dialog.findViewById(R.id.linearLayoutGalary).setOnClickListener(this);
        dialog.findViewById(R.id.linearLayoutPicture).setOnClickListener(this);

    }

    public String getPath(Uri uri) {
        // just some safety built in
        if (uri == null) {
            showMessage("null uri");
            // TODO perform some logging or show user feedback
            return null;
        }
        // try to retrieve the image from the media store first
        // this will only work for images selected from gallery
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = getActivity().managedQuery(uri, projection, null, null, null);
        if (cursor != null) {
            int column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        }
        // this is our fallback here
        return uri.getPath();
    }

    private Bitmap decodeUri(Uri selectedImage) throws FileNotFoundException {
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(
                context.getContentResolver().openInputStream(selectedImage), null, o);

        final int REQUIRED_SIZE = 100;

        int width_tmp = o.outWidth, height_tmp = o.outHeight;
        int scale = 1;
        while (true) {
            if (width_tmp / 2 < REQUIRED_SIZE || height_tmp / 2 < REQUIRED_SIZE) {
                break;
            }
            width_tmp /= 2;
            height_tmp /= 2;
            scale *= 2;
        }

        BitmapFactory.Options o2 = new BitmapFactory.Options();
        o2.inSampleSize = scale;
        return BitmapFactory.decodeStream(
                context.getContentResolver().openInputStream(selectedImage), null, o2);
    }

    private void getPermission() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            String[] permission = {
                    Manifest.permission.CAMERA,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE

            };

            ActivityCompat.requestPermissions(getActivity(),
                    permission, 1);


        }
    }

    private static File getOutputMediaFile() {

        // External sdcard location
        File mediaStorageDir = new File(
                Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                IMAGE_DIRECTORY_NAME);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d(IMAGE_DIRECTORY_NAME, "Oops! Failed create "
                        + IMAGE_DIRECTORY_NAME + " directory");
                return null;
            } else {
                Log.d(IMAGE_DIRECTORY_NAME, mediaStorageDir.getAbsolutePath());
            }

        } else {
            Log.d(IMAGE_DIRECTORY_NAME, mediaStorageDir.getAbsolutePath());
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                Locale.getDefault()).format(new Date());
        File mediaFile = null;

        mediaFile = new File(mediaStorageDir.getPath() + File.separator
                + "IMG_" + timeStamp + ".jpg");
        Log.i("result imagepath", mediaFile.getAbsolutePath());


        return mediaFile;
    }

    public Uri getOutputMediaFileUri() {
        return Uri.fromFile(getOutputMediaFile());
    }

    private void setupSourceDestinationMarker() {
        switch (type) {
            case "aeroplane":
                mode = Routing.TravelMode.TRANSIT;
                break;
            case "car":
                mode = Routing.TravelMode.DRIVING;
                break;
            case "ship":
                mode = Routing.TravelMode.TRANSIT;
                break;
            case "bus":
                mode = Routing.TravelMode.TRANSIT;
                break;
        }
        Routing routing = new Routing.Builder()
                .travelMode(mode)
                .withListener(this)
                .waypoints(sourceLatLng, destinationLatLang)
                .build();
        routing.execute();

        MarkerOptions options = new MarkerOptions();
        options.position(sourceLatLng);
        options.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_round_black));
        mMap.addMarker(options);
        options.position(destinationLatLang);
        options.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker_black));
        mMap.addMarker(options);
        CameraUpdate center =
                CameraUpdateFactory.newLatLng(sourceLatLng);
        CameraUpdate zoom = CameraUpdateFactory.zoomTo(15);

        mMap.moveCamera(center);
        mMap.animateCamera(zoom);
        showMessage(center.toString());
        double dummy_radius = distance(sourceLatLng.latitude, sourceLatLng.longitude, destinationLatLang.latitude, destinationLatLang.longitude);
        dummy_radius = dummy_radius / 2;
        double circleRad = dummy_radius * 1000;//multiply by 1000 to make units in KM
        float zoomLevel = getZoomLevel(circleRad);
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(sourceLatLng, zoomLevel));

    }

    private int getZoomLevel(double radius) {
        double scale = radius / 500;
        return ((int) (16 - Math.log(scale) / Math.log(2)));
    }

    private double distance(double lat1, double lon1, double lat2, double lon2) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        return (dist);
    }

    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case TAKE_PHOTO_CODE:
                if (resultCode == getActivity().RESULT_OK) {
                    imageFlag = true;
                    Log.i("result picture", "clicked");
                    //  buttonAddImage.setText(filePath);
                    image.setVisibility(View.VISIBLE);
                    Glide.with(context).load(new File(filePath)).error(R.drawable.ic_error).into(image);
                }
                break;
            case PICK_IMAGE:
                if (resultCode == getActivity().RESULT_OK) {
                    imageFlag = true;
                    Uri uri = data.getData();
                    filePath = getPath(uri);
                    if (filePath == null) {
                        filePath = ImageFilePath.getPath(getActivity(), data.getData());
                        if (filePath == null) {
                            showMessage("File path still null :(");
                            return;
                        }
                    }
                    try {
                        //  buttonAddImage.setText(filePath);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    image.setVisibility(View.VISIBLE);
                    Glide.with(context).load(new File(filePath)).error(R.drawable.ic_error).into(image);

                    Log.i("result picture", "picked");
                }
                break;
            case DESTINATION:
                destinationFlag = true;
                if (resultCode == RESULT_OK) {
                    double lat = data.getDoubleExtra("lat", 0);
                    double lon = data.getDoubleExtra("lon", 0);
                    String address = data.getStringExtra("address");
                    this.destinationAddress = destinationAddress;
                    textViewDestinationAddress.setText(address);
                    destinationLatLang = new LatLng(lat, lon);
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
                    double lat = data.getDoubleExtra("lat", 0);
                    double lon = data.getDoubleExtra("lon", 0);
                    String address = data.getStringExtra("address");
                    this.sourceAddress = address;
                    textViewDepartureAddress.setText(address);
                    sourceLatLng = new LatLng(lat, lon);
                    try {
                        sournceMarker.remove();
                    } catch (Exception ex) {

                    }
                    BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.ic_marker_black);

                    sournceMarker = mMap.addMarker(new MarkerOptions().icon(icon).snippet(address).title(getString(R.string.source)).position(sourceLatLng));
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

    public void createDashedLine(GoogleMap map, LatLng latLngOrig, LatLng latLngDest, int color) {
        try {
            map.clear();
        } catch (Exception ex) {

        }
        BitmapDescriptor sourceIcon = BitmapDescriptorFactory.fromResource(R.drawable.ic_marker_black);

        sournceMarker = mMap.addMarker(new MarkerOptions().icon(sourceIcon).snippet(sourceAddress).title(getString(R.string.source)).position(sourceLatLng));
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


    private void drawPolyLine() {
        arrayPoints.clear();
        arrayPoints.add(sourceLatLng);
        arrayPoints.add(destinationLatLang);
        polylineOptions.color(Color.RED);
        polylineOptions.width(5);
        polylineOptions.addAll(arrayPoints);
        mMap.addPolyline(polylineOptions);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(arrayPoints.get(0), 15));
        // Zoom in, animating the camera.
        mMap.animateCamera(CameraUpdateFactory.zoomIn());
        // Zoom out to zoom level 10, animating with a duration of 2 seconds.
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15), 2000, null);


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
    public void onCameraIdle() {

    }

    @Override
    public void onCameraMoveStarted(int i) {

    }

    @Override
    public void onCameraMove() {

    }

    @Override
    public void onCameraMoveCanceled() {

    }

    @Override
    public void onRoutingFailure(RouteException e) {
        if (e != null) {
            Toast.makeText(context, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(context, "Something went wrong, Try again", Toast.LENGTH_SHORT).show();
        }
        drawPolyLine();
    }

    @Override
    public void onRoutingStart() {

    }

    @Override
    public void onRoutingSuccess(ArrayList<Route> route, int shortestRouteIndex) {


        if (polylines.size() > 0) {
            for (Polyline poly : polylines) {
                poly.remove();
            }
        }


        for (int i = 0; i < route.size(); i++) {

            //In case of more than 5 alternative routes
            int colorIndex = i % COLORS.length;

            PolylineOptions polyOptions = new PolylineOptions();
            polyOptions.color(ContextCompat.getColor(context, COLORS[colorIndex]));
            polyOptions.width(10 + i * 3);
            polyOptions.addAll(route.get(i).getPoints());
            Polyline polyline = mMap.addPolyline(polyOptions);
            polylines.add(polyline);

            Toast.makeText(getApplicationContext(), "Route " + (i + 1) + ": distance - " + route.get(i).getDistanceValue() + ": duration - " + route.get(i).getDurationValue(), Toast.LENGTH_SHORT).show();

        }
    }

    @Override
    public void onRoutingCancelled() {

    }

    public LatLng midPoint(double lat1, double lon1, double lat2, double lon2) {

        double dLon = Math.toRadians(lon2 - lon1);

        //convert to radians
        lat1 = Math.toRadians(lat1);
        lat2 = Math.toRadians(lat2);
        lon1 = Math.toRadians(lon1);

        double Bx = Math.cos(lat2) * Math.cos(dLon);
        double By = Math.cos(lat2) * Math.sin(dLon);
        double lat3 = Math.atan2(Math.sin(lat1) + Math.sin(lat2), Math.sqrt((Math.cos(lat1) + Bx) * (Math.cos(lat1) + Bx) + By * By));
        double lon3 = lon1 + Math.atan2(By, Math.cos(lat1) + Bx);

        return new LatLng(lat3, lon3);
    }

    private void showCountryPopUp() {
        final CountryPicker picker = CountryPicker.newInstance("Select Country");
        picker.show(getChildFragmentManager(), "COUNTRY_PICKER");
        picker.setListener(new CountryPickerListener() {
            @Override
            public void onSelectCountry(String name, String code, String dialCode, int flagDrawableResID) {
                // Implement your code here
                try {
                    Currency currency = picker.getCurrencyCode(code);
                    currencyCode = currency.getCurrencyCode();
                    String currencySymbol = currency.getSymbol();
                    buttonCurrency.setText(currencySymbol + "   " + currencyCode);
                } catch (Exception ex) {
                    showMessage(getString(R.string.currency_not_found));
                }
                picker.dismiss();
            }
        });
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        switch (compoundButton.getId()) {
            case R.id.radioButtonOneway:
                if (b) {
                    returnFlag = false;
                    trip = "0";
                    linearLayoutReturn.setVisibility(View.GONE);
                    imageViewOneWay.setImageResource(R.drawable.checked);
                    imageViewReturn.setImageResource(R.drawable.unchecked);
                } else {
                    trip = "1";
                    returnFlag = true;
                    linearLayoutReturn.setVisibility(View.VISIBLE);
                    imageViewOneWay.setImageResource(R.drawable.unchecked);
                    imageViewReturn.setImageResource(R.drawable.checked);
                }
                break;
            case R.id.radioButtonReturn:

                if (b) {
                    trip = "1";
                    linearLayoutReturn.setVisibility(View.VISIBLE);
                    imageViewOneWay.setImageResource(R.drawable.unchecked);
                    imageViewReturn.setImageResource(R.drawable.checked);
                    returnFlag = true;
                } else {
                    trip = "0";
                    returnFlag = false;
                    linearLayoutReturn.setVisibility(View.GONE);
                    imageViewOneWay.setImageResource(R.drawable.checked);
                    imageViewReturn.setImageResource(R.drawable.unchecked);
                }
                break;

        }
    }

    private void replaceFragment(Fragment newFragment, String tag) {
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame, newFragment, tag);
        transaction.commit();
    }

    private void setupDashboardFragment() {
        HomeFragment dashboardFragment = new HomeFragment();
        String tagName = dashboardFragment.getClass().getName();
        replaceFragment(dashboardFragment, tagName);

    }

    public String getRealPathFromURI(Uri contentURI, Activity context) {
        String[] projection = {MediaStore.Images.Media.DATA};
        @SuppressWarnings("deprecation")
        Cursor cursor = context.managedQuery(contentURI, projection, null,
                null, null);
        if (cursor == null)
            return null;
        int column_index = cursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        if (cursor.moveToFirst()) {
            String s = cursor.getString(column_index);
            // cursor.close();
            return s;
        }
        // cursor.close();
        return null;
    }
}
