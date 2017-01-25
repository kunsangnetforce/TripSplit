package com.netforceinfotech.tripsplit.mytrip;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.appyvet.rangebar.RangeBar;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.BitmapEncoder;
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
import com.google.gson.JsonParser;
import com.hedgehog.ratingbar.RatingBar;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.mukesh.countrypicker.fragments.CountryPicker;
import com.mukesh.countrypicker.interfaces.CountryPickerListener;
import com.netforceinfotech.tripsplit.R;
import com.netforceinfotech.tripsplit.general.ImageFilePath;
import com.netforceinfotech.tripsplit.general.UserSessionManager;
import com.netforceinfotech.tripsplit.posttrip.GoogleMapActivity;
import com.shehabic.droppy.DroppyClickCallbackInterface;
import com.shehabic.droppy.DroppyMenuItem;
import com.shehabic.droppy.DroppyMenuPopup;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Currency;
import java.util.Date;
import java.util.Locale;

import static com.facebook.FacebookSdk.getApplicationContext;

public class EditTripActivity extends AppCompatActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener, OnMapReadyCallback {
    private static final String IMAGE_DIRECTORY_NAME = "tripsplit";

    private static final int PICK_IMAGE = 1234;
    private static final int TAKE_PHOTO_CODE = 1235;
    private static final int DESTINATION = 420;
    private static final int SOURCE = 421;
    private static final int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;
    private static final String TAG = "tagresult";
    private Marker destinationMarker, sournceMarker;
    UserSessionManager userSessionManager;
    MaterialDialog dialog;
    private String timezone;

    JsonObject result;
    Button buttonDone, maleButton, femaleButton, mixedButton, buttonAddImage, buttonCurrency, increamentPass, decreamentPass, increamentSpace, decreamentSpace;
    ImageView imageViewDp, imageViewRating1, imageViewRating2, imageViewRating3, imageViewRating4, imageViewRating5, imageViewType, imageViewOneway, imageViewReturn, image;
    Context context;
    TextView textViewCountryCode, textViewDateCreated, textviewName, textviewAge, textviewAddress, textViewType, textViewPax, textViewSpace, textViewAgeGroup, textViewDepartureAddress, textViewDestinationAddress, textviewETD, textviewETA, textviewReturnETD, textviewReturnETA;
    RelativeLayout relativeLayoutTye;
    String type, group;
    RangeBar rangebar;
    LinearLayout linearLayoutReturn;
    RadioButton radioButtonOneway, radioButtonReturn;
    private GoogleMap mMap;

    private String lowerLimit, upperLimit;
    private boolean returnFlag;
    private String trip;
    EditText et_vehicleType, et_itenarary, et_totalCost;
    private MapView mMapView;
    private boolean mapReady = false;
    private float zoomlevel = 12f;
    private LatLng destinationLatLang, sourceLatLng;
    private boolean imageFlag = false;
    private String filePath;
    private boolean destinationFlag = false;
    private boolean sourceFlag = false;
    private CameraUpdate cu;
    private String sourceAddress = "", destinationAddress = "";
    private Place place;
    private int pass_number = 0;
    private int space_number = 0;
    private Uri fileUri;
    private MaterialDialog progressDialog;
    private MaterialDialog custompopup;
    private String currencyCode;
    private String tour_id="";

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupToolBar(String app_name) {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(app_name);


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edittrip);
        context = this;
        userSessionManager = new UserSessionManager(context);
        Calendar cal = Calendar.getInstance();
        timezone = cal.getTimeZone().getID();
        Bundle bundle = getIntent().getExtras();
        String jsonResult = bundle.getString("jsonResult");
        tour_id=bundle.getString("tour_id");
        JsonParser parser = new JsonParser();
        result = parser.parse(jsonResult).getAsJsonObject();
        mMapView = (MapView) findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);

        mMapView.onResume(); // needed to get the map to display immediately

        try {
            MapsInitializer.initialize(this);
        } catch (Exception e) {
            e.printStackTrace();
        }

        mMapView.getMapAsync(this);
        initView();
        setupToolBar("Edit Trip");
        Log.i("resultjson", result.toString());

    }

    private void initView() {
        progressDialog = new MaterialDialog.Builder(context)
                .title(R.string.progress_dialog)
                .content(R.string.please_wait)
                .progress(true, 0).build();
        JsonObject data = result.getAsJsonObject("data");
        JsonObject my_splitz = data.getAsJsonObject("my_splitz");
        String profile_image = my_splitz.get("profile_image").getAsString();
        String country_code = my_splitz.get("country_code").getAsString();
        String created_date = my_splitz.get("created_date").getAsString();
        String firstname = my_splitz.get("firstname").getAsString();
        String address = my_splitz.get("address").getAsString();
        String pax = my_splitz.get("pax").getAsString();
        String space = my_splitz.get("space").getAsString();
        space_number = Integer.parseInt(space);
        pass_number = Integer.parseInt(pax);
        type = my_splitz.get("cartype").getAsString();
        String dob = my_splitz.get("dob").getAsString();
        String vehical_type = my_splitz.get("vehical_type").getAsString();
        group = my_splitz.get("trip_group").getAsString();
        String age_group_lower = my_splitz.get("age_group_lower").getAsString();
        String age_group_upper = my_splitz.get("age_group_upper").getAsString();
        String depart_address = my_splitz.get("depart_address").getAsString();
        String dest_address = my_splitz.get("dest_address").getAsString();
        double dest_lat = Double.parseDouble(my_splitz.get("dest_lat").getAsString());
        double dest_lon = Double.parseDouble(my_splitz.get("dest_lon").getAsString());
        double depart_lat = Double.parseDouble(my_splitz.get("depart_lat").getAsString());
        double depart_lon = Double.parseDouble(my_splitz.get("depart_lon").getAsString());
        String etd = my_splitz.get("etd").getAsString();
        String eta = my_splitz.get("eta").getAsString();
        String trip = my_splitz.get("trip").getAsString();
        String iteinerary = my_splitz.get("iteinerary").getAsString();
        String return_eta = my_splitz.get("return_eta").getAsString();
        String return_etd = my_splitz.get("return_etd").getAsString();
        String img_name = my_splitz.get("img_name").getAsString();
        String start_price = my_splitz.get("start_price").getAsString();
        String currency = my_splitz.get("currency").getAsString();
        currencyCode = currency;
        destinationLatLang = new LatLng(dest_lat, dest_lon);
        sourceLatLng = new LatLng(depart_lat, depart_lon);
        try {
            rangebar.setLeft(Integer.parseInt(age_group_lower));
            rangebar.setRight(Integer.parseInt(age_group_upper));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        rangebar = (RangeBar) findViewById(R.id.rangebar);
        rangebar.setOnRangeBarChangeListener(new RangeBar.OnRangeBarChangeListener() {
            @Override
            public void onRangeChangeListener(RangeBar rangeBar, int leftPinIndex,
                                              int rightPinIndex,
                                              String leftPinValue, String rightPinValue) {
                textViewAgeGroup.setText(getString(R.string.age_group) + " : " + leftPinValue + "-" + rightPinValue);
                lowerLimit = leftPinValue;
                upperLimit = rightPinValue;
            }
        });
        buttonCurrency = (Button) findViewById(R.id.buttonCurrency);
        buttonCurrency.setOnClickListener(this);
        buttonCurrency.setText(currency);

        et_totalCost = (EditText) findViewById(R.id.et_totalCost);
        et_totalCost.setText(start_price);
        image = (ImageView) findViewById(R.id.image);
        Glide.with(context).load(img_name).error(R.drawable.ic_picture_box).into(image);
        buttonAddImage = (Button) findViewById(R.id.buttonAddImage);
        buttonAddImage.setOnClickListener(this);
        et_itenarary = (EditText) findViewById(R.id.et_itenarary);
        et_itenarary.setText(iteinerary);
        textviewReturnETA = (TextView) findViewById(R.id.textviewReturnETA);
        textviewReturnETD = (TextView) findViewById(R.id.textviewReturnETD);
        try {
            textviewReturnETA.setText(getFormattedDateTime(return_eta));
            textviewReturnETD.setText(getFormattedDateTime(return_etd));
        } catch (Exception ex) {

        }

        increamentPass = (Button) findViewById(R.id.increament_pass);
        decreamentPass = (Button) findViewById(R.id.decreament_pass);
        increamentSpace = (Button) findViewById(R.id.increament_Space);
        decreamentSpace = (Button) findViewById(R.id.decreament_Space);
        increamentPass.setOnClickListener(this);
        decreamentPass.setOnClickListener(this);
        increamentSpace.setOnClickListener(this);
        decreamentSpace.setOnClickListener(this);

        textviewETA = (TextView) findViewById(R.id.textviewETA);
        textviewETD = (TextView) findViewById(R.id.textviewETD);
        textviewETD.setText(getFormattedDateTime(etd));
        textviewETA.setText(getFormattedDateTime(eta));
        textViewDestinationAddress = (TextView) findViewById(R.id.textViewDestinationAddress);
        textViewDestinationAddress.setText(dest_address);
        textViewDepartureAddress = (TextView) findViewById(R.id.textViewDepartureAddress);
        textViewDepartureAddress.setText(depart_address);
        et_vehicleType = (EditText) findViewById(R.id.et_vehicleType);
        et_vehicleType.setText(vehical_type);
        linearLayoutReturn = (LinearLayout) findViewById(R.id.linearlayoutReturn);
        imageViewOneway = (ImageView) findViewById(R.id.imageViewOneway);
        imageViewReturn = (ImageView) findViewById(R.id.imageViewReturn);
        imageViewOneway.setOnClickListener(this);
        imageViewReturn.setOnClickListener(this);
        textViewDestinationAddress.setOnClickListener(this);
        textViewDepartureAddress.setOnClickListener(this);
        radioButtonOneway = (RadioButton) findViewById(R.id.radioButtonOneway);
        radioButtonReturn = (RadioButton) findViewById(R.id.radioButtonReturn);
        radioButtonOneway.setOnCheckedChangeListener(this);
        radioButtonReturn.setOnCheckedChangeListener(this);

        maleButton = (Button) findViewById(R.id.maleButton);
        femaleButton = (Button) findViewById(R.id.femaleButton);
        mixedButton = (Button) findViewById(R.id.mixedButton);
        maleButton.setOnClickListener(this);
        femaleButton.setOnClickListener(this);
        mixedButton.setOnClickListener(this);
        textViewAgeGroup = (TextView) findViewById(R.id.textViewAgeGroup);
        textViewAgeGroup.setText("Age Group : " + age_group_lower + "-" + age_group_upper);
        textViewSpace = (TextView) findViewById(R.id.textViewSpace);
        textViewSpace.setText(space);
        textViewPax = (TextView) findViewById(R.id.textViewPax);
        textViewPax.setText(pax);
        textViewType = (TextView) findViewById(R.id.textViewType);
        relativeLayoutTye = (RelativeLayout) findViewById(R.id.relativeLayoutTye);
        buttonDone = (Button) findViewById(R.id.buttonDone);
        imageViewType = (ImageView) findViewById(R.id.imageViewType);
        imageViewDp = (ImageView) findViewById(R.id.imageViewDp);
        imageViewRating1 = (ImageView) findViewById(R.id.imageViewRating1);
        imageViewRating2 = (ImageView) findViewById(R.id.imageViewRating2);
        imageViewRating3 = (ImageView) findViewById(R.id.imageViewRating3);
        imageViewRating4 = (ImageView) findViewById(R.id.imageViewRating4);
        imageViewRating5 = (ImageView) findViewById(R.id.imageViewRating5);
        textViewCountryCode = (TextView) findViewById(R.id.textViewCountryCode);
        textViewDateCreated = (TextView) findViewById(R.id.textViewDateCreated);
        textviewName = (TextView) findViewById(R.id.textviewName);
        textviewAge = (TextView) findViewById(R.id.textviewAge);
        textviewAddress = (TextView) findViewById(R.id.textviewAddress);
        if (trip.equalsIgnoreCase("0")) {
            imageViewOneway.performClick();
            linearLayoutReturn.setVisibility(View.GONE);
        } else {
            imageViewReturn.performClick();
            linearLayoutReturn.setVisibility(View.VISIBLE);
        }
        DroppyMenuPopup.Builder droppyBuilder = new DroppyMenuPopup.Builder(this, relativeLayoutTye);
        droppyBuilder.addMenuItem(new DroppyMenuItem("Aeroplane"));
        droppyBuilder.addMenuItem(new DroppyMenuItem("Car"));
        droppyBuilder.addMenuItem(new DroppyMenuItem("Bus"));
        droppyBuilder.addMenuItem(new DroppyMenuItem("Boat"));
        droppyBuilder.setOnClick(new DroppyClickCallbackInterface() {
            @Override
            public void call(View v, int id) {
                switch (id) {
                    case 0:
                        type = "aeroplane";
                        textViewType.setText("Aeroplane");
                        Glide.with(context)
                                .fromResource()
                                .asBitmap()
                                .encoder(new BitmapEncoder(Bitmap.CompressFormat.PNG, 100))
                                .load(R.drawable.ic_redplane).into(imageViewType);
                        break;
                    case 1:
                        type = "car";
                        textViewType.setText("Car");
                        Glide.with(context)
                                .fromResource()
                                .asBitmap()
                                .encoder(new BitmapEncoder(Bitmap.CompressFormat.PNG, 100))
                                .load(R.drawable.ic_car_primary).into(imageViewType);
                        break;
                    case 2:
                        type = "bus";
                        textViewType.setText("Bus");
                        Glide.with(context)
                                .fromResource()
                                .asBitmap()
                                .encoder(new BitmapEncoder(Bitmap.CompressFormat.PNG, 100))
                                .load(R.drawable.ic_bus_primary).into(imageViewType);
                        break;
                    case 3:
                        type = "ship";
                        textViewType.setText("Boat");
                        Glide.with(context)
                                .fromResource()
                                .asBitmap()
                                .encoder(new BitmapEncoder(Bitmap.CompressFormat.PNG, 100))
                                .load(R.drawable.ic_ship_primary).into(imageViewType);
                        break;
                }
            }
        });

        DroppyMenuPopup droppyMenu = droppyBuilder.build();
        switch (group) {
            case "male":
                femaleButton.setBackground(ContextCompat.getDrawable(context, R.drawable.border_image));
                mixedButton.setBackground(ContextCompat.getDrawable(context, R.drawable.border_image2));
                maleButton.setBackgroundColor(ContextCompat.getColor(context, R.color.colorPrimary));

                maleButton.setTextColor(ContextCompat.getColor(context, R.color.white));
                femaleButton.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary));
                mixedButton.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary));

                break;
            case "female":
                maleButton.setBackground(ContextCompat.getDrawable(context, R.drawable.border_image));
                mixedButton.setBackground(ContextCompat.getDrawable(context, R.drawable.border_image2));
                femaleButton.setBackgroundColor(ContextCompat.getColor(context, R.color.colorPrimary));

                femaleButton.setTextColor(ContextCompat.getColor(context, R.color.white));
                maleButton.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary));
                mixedButton.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary));


                break;
            default:

                maleButton.setBackground(ContextCompat.getDrawable(context, R.drawable.border_image));
                femaleButton.setBackground(ContextCompat.getDrawable(context, R.drawable.border_image));
                mixedButton.setBackgroundColor(ContextCompat.getColor(context, R.color.colorPrimary));

                mixedButton.setTextColor(ContextCompat.getColor(context, R.color.white));
                maleButton.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary));
                femaleButton.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary));

                break;
        }
        textviewName.setText(firstname);
        textviewAddress.setText(address);
        textViewDateCreated.setText(created_date);
        setAge(dob);

        Glide.with(context).load(profile_image).error(R.drawable.ic_picture_box).into(imageViewDp);
        textViewCountryCode.setText(country_code);
        try {
            Float rating = my_splitz.get("rating").getAsFloat();
            setupRatingImage(rating);
        } catch (Exception ex) {
            setupRatingImage(3);
        }
        buttonDone.setOnClickListener(this);
    }

    private String getFormattedDateTime(String etd) {
        //2017-01-16 16:55:00
        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = null;
        try {
            date = fmt.parse(etd);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        SimpleDateFormat fmtOut = new SimpleDateFormat("EEE dd MMM yyyy HH:mm");
        return fmtOut.format(date);
    }

    private void showCountryPopUp() {
        final CountryPicker picker = CountryPicker.newInstance("Select Country");
        picker.show(getSupportFragmentManager(), "COUNTRY_PICKER");
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

    private void setAge(String dob) {
        if (dob.equalsIgnoreCase("0000-00-00")) {
            textviewAge.setText("NA");
        } else {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            try {
                Calendar cal = Calendar.getInstance();
                cal.setTime(simpleDateFormat.parse(dob));// all done
                String age = getAge(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
                textviewAge.setText(age);
            } catch (ParseException e) {
                e.printStackTrace();
            }


        }
        switch (type) {
            case "aeroplane":
                textViewType.setText("Aeroplane");
                Glide.with(context)
                        .fromResource()
                        .asBitmap()
                        .encoder(new BitmapEncoder(Bitmap.CompressFormat.PNG, 100))
                        .load(R.drawable.ic_redplane).into(imageViewType);
                break;
            case "car":
                textViewType.setText("Car");
                Glide.with(context)
                        .fromResource()
                        .asBitmap()
                        .encoder(new BitmapEncoder(Bitmap.CompressFormat.PNG, 100))
                        .load(R.drawable.ic_car_primary).into(imageViewType);
                break;
            case "bus":
                textViewType.setText("Bus");
                Glide.with(context)
                        .fromResource()
                        .asBitmap()
                        .encoder(new BitmapEncoder(Bitmap.CompressFormat.PNG, 100))
                        .load(R.drawable.ic_bus_primary).into(imageViewType);
                break;
            case "boat":
                textViewType.setText("Boat");
                Glide.with(context)
                        .fromResource()
                        .asBitmap()
                        .encoder(new BitmapEncoder(Bitmap.CompressFormat.PNG, 100))
                        .load(R.drawable.ic_ship_primary).into(imageViewType);
                break;
            default:
                textViewType.setText("Car");
                Glide.with(context)
                        .fromResource()
                        .asBitmap()
                        .encoder(new BitmapEncoder(Bitmap.CompressFormat.PNG, 100))
                        .load(R.drawable.ic_car_primary).into(imageViewType);
                break;
        }

    }

    private String getAge(int year, int month, int day) {
        Calendar dob = Calendar.getInstance();
        Calendar today = Calendar.getInstance();

        dob.set(year, month, day);

        int age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR);

        if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)) {
            age--;
        }

        Integer ageInt = new Integer(age);
        String ageS = ageInt.toString();

        return ageS;
    }

    private void setupRatingImage(float ratingFloat) {
        int ratingInt = (int) ratingFloat;
        float ramainingFloat = ratingFloat - ratingInt;
        switch (ratingInt) {
            case 1:
                imageViewRating1.setImageResource(R.drawable.ic_star_full);
                imageViewRating2.setImageResource(R.drawable.ic_star_outline);
                imageViewRating3.setImageResource(R.drawable.ic_star_outline);
                imageViewRating4.setImageResource(R.drawable.ic_star_outline);
                imageViewRating5.setImageResource(R.drawable.ic_star_outline);
                break;
            case 2:
                imageViewRating1.setImageResource(R.drawable.ic_star_full);
                imageViewRating2.setImageResource(R.drawable.ic_star_full);
                imageViewRating3.setImageResource(R.drawable.ic_star_outline);
                imageViewRating4.setImageResource(R.drawable.ic_star_outline);
                imageViewRating5.setImageResource(R.drawable.ic_star_outline);
                break;
            case 3:
                imageViewRating1.setImageResource(R.drawable.ic_star_full);
                imageViewRating2.setImageResource(R.drawable.ic_star_full);
                imageViewRating3.setImageResource(R.drawable.ic_star_full);
                imageViewRating4.setImageResource(R.drawable.ic_star_outline);
                imageViewRating5.setImageResource(R.drawable.ic_star_outline);
                break;
            case 4:
                imageViewRating1.setImageResource(R.drawable.ic_star_full);
                imageViewRating2.setImageResource(R.drawable.ic_star_full);
                imageViewRating3.setImageResource(R.drawable.ic_star_full);
                imageViewRating4.setImageResource(R.drawable.ic_star_full);
                imageViewRating5.setImageResource(R.drawable.ic_star_outline);
                break;
            case 5:
                imageViewRating1.setImageResource(R.drawable.ic_star_full);
                imageViewRating2.setImageResource(R.drawable.ic_star_full);
                imageViewRating3.setImageResource(R.drawable.ic_star_full);
                imageViewRating4.setImageResource(R.drawable.ic_star_full);
                imageViewRating5.setImageResource(R.drawable.ic_star_full);
                break;
        }
        if (ramainingFloat >= 0.5) {
            switch (ratingInt) {
                case 1:
                    imageViewRating2.setImageResource(R.drawable.ic_half_star);

                    break;
                case 2:
                    imageViewRating3.setImageResource(R.drawable.ic_half_star);
                    break;
                case 3:
                    imageViewRating4.setImageResource(R.drawable.ic_half_star);
                    break;
                case 4:
                    imageViewRating5.setImageResource(R.drawable.ic_half_star);
                    break;
                case 5:
                    break;
            }
        }
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

    private void pickPictureIntent() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
    }

    public Uri getOutputMediaFileUri() {
        return Uri.fromFile(getOutputMediaFile());
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

    @Override
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
                break;
            case R.id.linearLayoutPicture:
                takePictureIntent();
                dialog.dismiss();
                break;
            case R.id.linearLayoutGalary:
                pickPictureIntent();
                dialog.dismiss();
                break;
            case R.id.increament_pass:

                pass_number = pass_number + 1;
                String p_n = String.valueOf(pass_number);
                textViewPax.setText(p_n);


                break;

            case R.id.decreament_pass:

                if (pass_number >= 1) {
                    pass_number = pass_number - 1;
                    String dp_n = String.valueOf(pass_number);
                    textViewPax.setText(dp_n);
                }
                break;

            case R.id.increament_Space:

                space_number = space_number + 1;
                String sp_n = String.valueOf(space_number);
                textViewSpace.setText(sp_n);

                break;

            case R.id.decreament_Space:
                if (space_number >= 1) {
                    space_number = space_number - 1;
                    String dsp_n = String.valueOf(space_number);
                    textViewSpace.setText(dsp_n);
                }

                break;

            case R.id.buttonAddImage:
                showEditPicPopup();
                break;

            case R.id.buttonDone:
                showConfirmationPopUp();
                break;
            case R.id.buttonCurrency:
                showCountryPopUp();
                break;

            case R.id.maleButton:

                group = "male";
                femaleButton.setBackground(ContextCompat.getDrawable(context, R.drawable.border_image));
                mixedButton.setBackground(ContextCompat.getDrawable(context, R.drawable.border_image2));
                maleButton.setBackgroundColor(ContextCompat.getColor(context, R.color.colorPrimary));

                maleButton.setTextColor(ContextCompat.getColor(context, R.color.white));
                femaleButton.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary));
                mixedButton.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary));

                break;

            case R.id.femaleButton:
                group = "female";
                maleButton.setBackground(ContextCompat.getDrawable(context, R.drawable.border_image));
                mixedButton.setBackground(ContextCompat.getDrawable(context, R.drawable.border_image2));
                femaleButton.setBackgroundColor(ContextCompat.getColor(context, R.color.colorPrimary));

                femaleButton.setTextColor(ContextCompat.getColor(context, R.color.white));
                maleButton.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary));
                mixedButton.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary));


                break;

            case R.id.mixedButton:
                group = "mixed";

                maleButton.setBackground(ContextCompat.getDrawable(context, R.drawable.border_image));
                femaleButton.setBackground(ContextCompat.getDrawable(context, R.drawable.border_image));
                mixedButton.setBackgroundColor(ContextCompat.getColor(context, R.color.colorPrimary));

                mixedButton.setTextColor(ContextCompat.getColor(context, R.color.white));
                maleButton.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary));
                femaleButton.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary));
                break;
            case R.id.imageViewOneway:
                radioButtonOneway.performClick();
                break;
            case R.id.imageViewReturn:
                radioButtonReturn.performClick();
                break;
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        switch (compoundButton.getId()) {
            case R.id.radioButtonOneway:
                if (b) {
                    returnFlag = false;
                    trip = "0";
                    linearLayoutReturn.setVisibility(View.GONE);
                    imageViewOneway
                            .setImageResource(R.drawable.checked);
                    imageViewReturn.setImageResource(R.drawable.unchecked);
                } else {
                    trip = "1";
                    returnFlag = true;
                    linearLayoutReturn.setVisibility(View.VISIBLE);
                    imageViewOneway.setImageResource(R.drawable.unchecked);
                    imageViewReturn.setImageResource(R.drawable.checked);
                }
                break;
            case R.id.radioButtonReturn:

                if (b) {
                    trip = "1";
                    linearLayoutReturn.setVisibility(View.VISIBLE);
                    imageViewOneway.setImageResource(R.drawable.unchecked);
                    imageViewReturn.setImageResource(R.drawable.checked);
                    returnFlag = true;
                } else {
                    trip = "0";
                    returnFlag = false;
                    linearLayoutReturn.setVisibility(View.GONE);
                    imageViewOneway.setImageResource(R.drawable.checked);
                    imageViewReturn.setImageResource(R.drawable.unchecked);
                }
                break;

        }
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
        zoomInTwoPoint();

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case TAKE_PHOTO_CODE:
                if (resultCode == RESULT_OK) {
                    imageFlag = true;
                    Log.i("result picture", "clicked");
                    //  buttonAddImage.setText(filePath);
                    image.setVisibility(View.VISIBLE);
                    Glide.with(context).load(new File(filePath)).error(R.drawable.ic_error).into(image);
                }
                break;
            case PICK_IMAGE:
                if (resultCode == RESULT_OK) {
                    imageFlag = true;
                    Uri uri = data.getData();
                    filePath = getPath(uri);
                    if (filePath == null) {
                        filePath = ImageFilePath.getPath(context, data.getData());
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
                    place = PlaceAutocomplete.getPlace(this, data);
                    Log.i(TAG, "Place: " + place.getName());
                } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                    Status status = PlaceAutocomplete.getStatus(this, data);
                    // TODO: Handle the error.
                    Log.i(TAG, status.getStatusMessage());

                } else if (resultCode == RESULT_CANCELED) {
                    // The user canceled the operation.
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
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
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        if (cursor != null) {
            int column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        }
        // this is our fallback here
        return uri.getPath();
    }

    private void showMessage(String s) {
        Toast.makeText(context, s, Toast.LENGTH_SHORT).show();
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


    private boolean validateDate() {
        Date etd, eta, returnetd, returneta;
        String date1 = textviewETD.getText().toString();
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
            String date3 = textviewReturnETD.getText().toString();
            String date4 = textviewReturnETA.getText().toString();
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

    private String getServerFormatDate(String dateString) {
        //Fri 27 Jan 2017 12:31
        //2017-01-16 16:43:00
        try {
            SimpleDateFormat fmt = new SimpleDateFormat("EEE dd MMM yyyy HH:mm");
            Date date = null;
            try {
                date = fmt.parse(dateString);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            SimpleDateFormat fmtOut = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            return fmtOut.format(date);
        } catch (Exception ex) {
            return "";
        }

    }

    private void updateTrip() {
        if (validateDate()) {
            String baseUrl = getString(R.string.url);
            // https://netforcesales.com/tripesplit/mobileApp/api/services.php?opt=posttrip
            String url = baseUrl + "services.php?opt=updatetrip";
            Log.i("url", url);
            Log.i("type", type);

            progressDialog.show();
            if (filePath != null) {
                Log.i("filePath", "inside send image");
                Ion.with(getApplicationContext())
                        .load("POST", url)
                        .setMultipartParameter("tour_id",tour_id)
                        .setMultipartParameter("user_id", userSessionManager.getUserId())
                        .setMultipartParameter("type", type)
                        .setMultipartParameter("pax", textViewPax.getText().toString().trim())
                        .setMultipartParameter("space", textViewSpace.getText().toString().trim())
                        .setMultipartParameter("group", group)
                        .setMultipartParameter("age_group_upper", upperLimit)
                        .setMultipartParameter("age_group_lower", lowerLimit)
                        .setMultipartParameter("trip", trip)
                        .setMultipartParameter("vehicle_type", et_vehicleType.getText().toString().trim())
                        .setMultipartParameter("depart_address", textViewDepartureAddress.getText().toString())
                        .setMultipartParameter("dest_address", textViewDestinationAddress.getText().toString())
                        .setMultipartParameter("depart_lat", sourceLatLng.latitude + "")
                        .setMultipartParameter("depart_lon", sourceLatLng.longitude + "")
                        .setMultipartParameter("dest_lat", destinationLatLang.latitude + "")
                        .setMultipartParameter("dest_lon", destinationLatLang.longitude + "")
                        .setMultipartParameter("etd", getServerFormatDate(textviewETD.getText().toString()))
                        .setMultipartParameter("eta", getServerFormatDate(textviewETA.getText().toString()))
                        .setMultipartParameter("return_etd", getServerFormatDate(textviewReturnETD.getText().toString()))
                        .setMultipartParameter("return_eta", getServerFormatDate(textviewReturnETA.getText().toString()))
                        .setMultipartParameter("iteinerary", et_itenarary.getText().toString().trim())
                        .setMultipartParameter("totalcost", et_totalCost.getText().toString())
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
                        .setMultipartParameter("tour_id",tour_id)
                        .setMultipartParameter("user_id", userSessionManager.getUserId())
                        .setMultipartParameter("type", type)
                        .setMultipartParameter("pax", textViewPax.getText().toString().trim())
                        .setMultipartParameter("space", textViewSpace.getText().toString().trim())
                        .setMultipartParameter("group", group)
                        .setMultipartParameter("age_group_upper", upperLimit)
                        .setMultipartParameter("age_group_lower", lowerLimit)
                        .setMultipartParameter("trip", trip)
                        .setMultipartParameter("vehicle_type", et_vehicleType.getText().toString().trim())
                        .setMultipartParameter("depart_address", textViewDepartureAddress.getText().toString())
                        .setMultipartParameter("dest_address", textViewDestinationAddress.getText().toString())
                        .setMultipartParameter("depart_lat", sourceLatLng.latitude + "")
                        .setMultipartParameter("depart_lon", sourceLatLng.longitude + "")
                        .setMultipartParameter("dest_lat", destinationLatLang.latitude + "")
                        .setMultipartParameter("dest_lon", destinationLatLang.longitude + "")
                        .setMultipartParameter("timezone", timezone)
                        .setMultipartParameter("etd", getServerFormatDate(textviewETD.getText().toString()))
                        .setMultipartParameter("eta", getServerFormatDate(textviewETA.getText().toString()))
                        .setMultipartParameter("return_etd", getServerFormatDate(textviewReturnETD.getText().toString()))
                        .setMultipartParameter("return_eta", getServerFormatDate(textviewReturnETA.getText().toString()))
                        .setMultipartParameter("iteinerary", et_itenarary.getText().toString().trim())
                        .setMultipartParameter("totalcost", et_totalCost.getText().toString())
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

    private void showConfirmationPopUp() {
        new MaterialDialog.Builder(context)
                .title("Edit Trip!!!")
                .content("Are you sure you want to Edit trip?")
                .positiveText("Confirm")
                .negativeText(R.string.cancel)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        updateTrip();
                    }
                })
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }
}
