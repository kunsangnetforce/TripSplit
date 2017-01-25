package com.netforceinfotech.tripsplit.search;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.Glide;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.netforceinfotech.tripsplit.R;
import com.netforceinfotech.tripsplit.general.UserSessionManager;
import com.netforceinfotech.tripsplit.message.message_detail.MessageDetailActivity;
import com.netforceinfotech.tripsplit.mytrip.EditTripActivity;
import com.netforceinfotech.tripsplit.profile.myprofile.MyProfileActivity;
import com.netforceinfotech.tripsplit.profile.sendmail.SendMailActivity;
import com.netforceinfotech.tripsplit.search.review.ReviewActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Currency;
import java.util.Date;

public class TripDetailActivity extends AppCompatActivity implements View.OnClickListener {

    Context context;
    RelativeLayout reviewlayout;
    Button buttonBookIt, buttonEditTrip;
    LinearLayout linearLayoutReturn;
    TextView textViewETDReturn, textViewETAReturn, textViewCountryCode, textViewDateCreated, textViewName, textViewAge, textViewAddress, textViewSource, textViewDestination, textViewAboutMe, textViewETD, textViewETA, textViewSpace, textViewDate, textViewPax, textViewAgeGroup, textViewTripSplit, textViewItenerary, textViewTotalCost, textViewYourShare;
    ImageView imageViewDp, imageViewStar1, imageViewStar2, imageViewStar3, imageViewStar4, imageViewStar5, imageViewEmail, imageViewMessage, imageViewType, imageViewTrip, imageViewRating1, imageViewRating2, imageViewRating3, imageViewRating4, imageViewRating5;
    private String tripcreator_id;
    private String username;
    UserSessionManager userSessionManager;
    private String trip_image, profile_image;
    private String reg_id = "";
    private MaterialDialog progressDialog;
    private String trip_id;
    private String userId;
    private String etd = null;
    private String dob = "0000-00-00";
    private boolean mysplit = false;
    private RelativeLayout relativeLayoutYourshare;
    String jsonResult;

    private void initView() {
        buttonEditTrip = (Button) findViewById(R.id.buttonEditTrip);
        imageViewRating1 = (ImageView) findViewById(R.id.imageViewRating1);
        imageViewRating2 = (ImageView) findViewById(R.id.imageViewRating2);
        imageViewRating3 = (ImageView) findViewById(R.id.imageViewRating3);
        imageViewRating4 = (ImageView) findViewById(R.id.imageViewRating4);
        imageViewRating5 = (ImageView) findViewById(R.id.imageViewRating5);

        findViewById(R.id.linearLayoutUser).setOnClickListener(this);
        progressDialog = new MaterialDialog.Builder(context)
                .title(R.string.progress_dialog)
                .content(R.string.please_wait)
                .progress(true, 0).build();
        textViewSource = (TextView) findViewById(R.id.textViewSource);
        textViewDestination = (TextView) findViewById(R.id.textViewDestination);
        buttonBookIt = (Button) findViewById(R.id.buttonBookIt);
        if (mysplit) {
            buttonBookIt.setVisibility(View.GONE);
        }
        buttonBookIt.setOnClickListener(this);
        buttonEditTrip.setOnClickListener(this);
        relativeLayoutYourshare = (RelativeLayout) findViewById(R.id.relativeLayoutYourshare);
        linearLayoutReturn = (LinearLayout) findViewById(R.id.linearLayoutReturn);
        textViewETDReturn = (TextView) findViewById(R.id.textViewETDReturn);
        textViewETAReturn = (TextView) findViewById(R.id.textViewETAReturn);
        linearLayoutReturn.setVisibility(View.GONE);
        imageViewTrip = (ImageView) findViewById(R.id.imageViewTrip);
        reviewlayout = (RelativeLayout) findViewById(R.id.reviewlayout);
        textViewCountryCode = (TextView) findViewById(R.id.textViewCountryCode);
        textViewDateCreated = (TextView) findViewById(R.id.textViewDateCreated);
        textViewName = (TextView) findViewById(R.id.textViewName);
        textViewAge = (TextView) findViewById(R.id.textViewAge);
        textViewAddress = (TextView) findViewById(R.id.textViewAddress);
        textViewAboutMe = (TextView) findViewById(R.id.textViewAboutMe);
        textViewETD = (TextView) findViewById(R.id.textViewETD);
        textViewETA = (TextView) findViewById(R.id.textViewETA);
        textViewSpace = (TextView) findViewById(R.id.textViewSpace);
        textViewDate = (TextView) findViewById(R.id.textViewDate);
        textViewPax = (TextView) findViewById(R.id.textViewPax);
        textViewAgeGroup = (TextView) findViewById(R.id.textViewAgeGroup);
        textViewTripSplit = (TextView) findViewById(R.id.textViewTripSplit);
        textViewItenerary = (TextView) findViewById(R.id.textViewItenerary);
        textViewTotalCost = (TextView) findViewById(R.id.textViewTotalCost);
        textViewYourShare = (TextView) findViewById(R.id.textViewYourShare);
        imageViewDp = (ImageView) findViewById(R.id.imageViewDp);
        imageViewStar1 = (ImageView) findViewById(R.id.imageViewRating1);
        imageViewStar2 = (ImageView) findViewById(R.id.imageViewRating2);
        imageViewStar3 = (ImageView) findViewById(R.id.imageViewRating3);
        imageViewStar4 = (ImageView) findViewById(R.id.imageViewRating4);
        imageViewStar5 = (ImageView) findViewById(R.id.imageViewRating5);
        imageViewEmail = (ImageView) findViewById(R.id.imageViewEmail);
        imageViewMessage = (ImageView) findViewById(R.id.imageViewMessage);
        imageViewType = (ImageView) findViewById(R.id.imageViewType);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_detail);
        context = this;
        Bundle bundle = getIntent().getExtras();
        trip_id = bundle.getString("trip_id");
        try {
            mysplit = bundle.getBoolean("mysplit");
        } catch (Exception ex) {

        }
        setupToolBar("Trip Details");
        userSessionManager = new UserSessionManager(context);
        initView();
        try {
            if (bundle.getBoolean("edit")) {
                buttonBookIt.setVisibility(View.GONE);
                buttonEditTrip.setVisibility(View.VISIBLE);
            }
        } catch (Exception ex) {

        }

    }


    @Override
    protected void onResume() {
        super.onResume();
        getTripDetail(trip_id);

    }

    private void setupToolBar(String app_name) {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(app_name);


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void getTripDetail(String trip_id) {
        progressDialog.show();
        //http://netforce.biz/tripesplit/mobileApp/api/services.php?opt=splitz_detail
        String baseUrl = getString(R.string.url);
        String url = baseUrl + "services.php?opt=splitz_detail";
        Ion.with(context)
                .load("POST", url)
                .setBodyParameter("trip_id", trip_id)
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
                                JsonObject data = result.getAsJsonObject("data");
                                JsonObject my_splitz = data.getAsJsonObject("my_splitz");
                                setupTripDetail(my_splitz);
                                jsonResult = result.toString();
                            }

                        }
                    }
                });

    }

    private void setupTripDetail(JsonObject my_splitz) {
        String tour_id, start_price, date_created, user_id, cartype, pax, space, trip_group, age_group_lower, age_group_upper, trip, vehical_type,
                depart_address, country_code, etd, eta, iteinerary, image_name, currency,
                return_eta, return_etd, dest_address, id, username, email, dob, address, country, aboutme, your_share, created_date, reg_id;

        user_id = my_splitz.get("user_id").getAsString();
        this.tripcreator_id = user_id;
        tour_id = my_splitz.get("tour_id").getAsString();
        start_price = my_splitz.get("start_price").getAsString();
        date_created = my_splitz.get("date_created").getAsString();
        cartype = my_splitz.get("cartype").getAsString();
        pax = my_splitz.get("pax").getAsString();
        space = my_splitz.get("space").getAsString();
        trip_group = my_splitz.get("trip_group").getAsString();
        age_group_lower = my_splitz.get("age_group_lower").getAsString();
        age_group_upper = my_splitz.get("age_group_upper").getAsString();
        trip = my_splitz.get("trip").getAsString();
        vehical_type = my_splitz.get("vehical_type").getAsString();
        depart_address = my_splitz.get("depart_address").getAsString();
        dest_address = my_splitz.get("dest_address").getAsString();
        reg_id = my_splitz.get("reg_id").getAsString();
        this.reg_id = reg_id;
        country_code = my_splitz.get("country_code").getAsString();
        etd = my_splitz.get("etd").getAsString();
        this.etd = etd;
        eta = my_splitz.get("eta").getAsString();
        iteinerary = my_splitz.get("iteinerary").getAsString();
        image_name = my_splitz.get("img_name").getAsString();
        currency = my_splitz.get("currency").getAsString();
        return_eta = my_splitz.get("return_eta").getAsString();
        return_etd = my_splitz.get("return_etd").getAsString();
        id = my_splitz.get("id").getAsString();
        this.userId = id;
        if (!mysplit) {

            if (id.equalsIgnoreCase(userSessionManager.getUserId())) {
                relativeLayoutYourshare.setVisibility(View.GONE);
                buttonBookIt.setVisibility(View.GONE);


            } else {
                buttonBookIt.setVisibility(View.VISIBLE);
                relativeLayoutYourshare.setVisibility(View.VISIBLE);

            }
        }
        username = my_splitz.get("username").getAsString();
        this.username = username;
        email = my_splitz.get("email").getAsString();
        trip_image = my_splitz.get("img_name").getAsString();
        profile_image = my_splitz.get("profile_image").getAsString();
        dob = my_splitz.get("dob").getAsString();
        this.dob = dob;
        address = my_splitz.get("address").getAsString();
        country = my_splitz.get("country").getAsString();
        aboutme = my_splitz.get("aboutme").getAsString();
        try {
            String rating = my_splitz.get("rating").getAsString();
            float ratingFloat = Float.parseFloat(rating);
            setupRatingImage(ratingFloat);
        } catch (Exception ex) {
            setupRatingImage(3);

        }

        your_share = my_splitz.get("your_share").getAsString();
        created_date = my_splitz.get("created_date").getAsString();
        Glide.with(context).load(image_name).error(R.drawable.ic_blank).into(imageViewTrip);
        textViewCountryCode.setText(country_code);
        textViewDateCreated.setText(getFormatedDate(created_date, 1));
        Glide.with(context).load(profile_image).error(R.drawable.ic_error).into(imageViewDp);
        textViewName.setText(username);
        setAge(dob, textViewAge);
        textViewAddress.setText(address);
        imageViewEmail.setOnClickListener(this);
        imageViewMessage.setOnClickListener(this);
        switch (cartype) {
            case "car":
                Glide.with(context).load(R.drawable.ic_car_primary).into(imageViewType);
                break;
            case "aeroplane":
                Glide.with(context).load(R.drawable.ic_redplane).into(imageViewType);

                break;
            case "bus":
                Glide.with(context).load(R.drawable.ic_bus_primary).into(imageViewType);

                break;
            case "ship":
                Glide.with(context).load(R.drawable.ic_ship_primary).into(imageViewType);

                break;
        }
        textViewSource.setText(depart_address);
        textViewDestination.setText(dest_address);
        textViewAboutMe.setText(aboutme);
        reviewlayout.setOnClickListener(this);
        textViewETD.setText(getFormetedTime(etd));
        textViewETA.setText(getFormetedTime(eta));
        textViewSpace.setText(space);
        textViewDate.setText(getFormatedDate(etd));
        textViewPax.setText(pax);
        textViewAgeGroup.setText(age_group_lower + " - " + age_group_upper);
        //final CountryPicker picker = CountryPicker.newInstance("Select Country");
        Currency currency1 = Currency.getInstance(currency);
        String currencySymbol = currency1.getSymbol();
        textViewTripSplit.setText(currency + " " + currencySymbol + " " + start_price);
        textViewItenerary.setText(iteinerary);
        textViewTotalCost.setText(currency + " " + currencySymbol + " " + start_price);
        textViewYourShare.setText(currency + " " + currencySymbol + " " + your_share);
        if (trip.equalsIgnoreCase("1")) {
            linearLayoutReturn.setVisibility(View.VISIBLE);
            textViewETAReturn.setText(return_eta);
            textViewETDReturn.setText(return_etd);
        }


    }

    private void setupRatingImage(float ratingFloat) {
        int ratingInt = (int) ratingFloat;
        float ramainingFloat = ratingFloat = ratingInt;
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

    private void showMessage(String s) {
        Toast.makeText(context, s, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.linearLayoutUser:
                Intent intent = new Intent(context, MyProfileActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("image_url", profile_image);
                bundle.putString("dob", dob);
                bundle.putString("name", username);
                bundle.putString("user_id", userId);
                intent.putExtras(bundle);
                startActivity(intent);
                break;
            case R.id.imageViewEmail:
                if (!tripcreator_id.equalsIgnoreCase(userSessionManager.getUserId())) {
                    intent = new Intent(context, SendMailActivity.class);
                    bundle = new Bundle();
                    bundle.putString("id", tripcreator_id);
                    bundle.putString("name", username);
                    bundle.putString("image_url", trip_image);
                    bundle.putString("reg_id", reg_id);
                    intent.putExtras(bundle);
                    startActivity(intent);
                } else {
                    showMessage("Cannot Email to self");
                }

                break;
            case R.id.imageViewMessage:
                if (!tripcreator_id.equalsIgnoreCase(userSessionManager.getUserId())) {
                    intent = new Intent(context, MessageDetailActivity.class);
                    bundle = new Bundle();
                    bundle.putString("id", tripcreator_id);
                    bundle.putString("name", username);
                    bundle.putString("image_url", trip_image);
                    bundle.putString("reg_id", reg_id);
                    intent.putExtras(bundle);
                    startActivity(intent);
                } else {
                    showMessage("Cannot message to self");
                }
                break;
            case R.id.buttonBookIt:
                if (userId.equalsIgnoreCase(userSessionManager.getUserId())) {
                    showMessage("Cannot book your own trip");
                    return;
                }
                if (etd != null && !validateDate(etd)) {
                    showMessage("Cannot book it because the Trip already ended");
                    return;
                }
                showConfirmationPopUp();
                break;
            case R.id.reviewlayout:
                intent = new Intent(context, ReviewActivity.class);
                bundle = new Bundle();
                bundle.putString("image_url", profile_image);
                bundle.putString("dob", dob);
                bundle.putString("name", username);
                bundle.putString("user_id", userId);
                intent.putExtras(bundle);
                startActivity(intent);
                break;
            case R.id.buttonEditTrip:
                if (jsonResult == null) {
                    showMessage("No data available... please load trip again!!!");
                    return;
                }
                intent = new Intent(context, EditTripActivity.class);
                bundle = new Bundle();
                bundle.putString("jsonResult", jsonResult);
                bundle.putString("tour_id", trip_id);
                intent.putExtras(bundle);
                startActivity(intent);
                break;


        }

    }

    private boolean validateDate(String etd) {
        //EEE dd MMM yyyy HH:mm
        try {
            if (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(etd).before(new Date())) {
                return false;
            } else {
                return true;
            }
        } catch (ParseException e) {
            e.printStackTrace();
            Log.e("Dateparse", "Failed " + etd);
            return false;

        }
    }

    private void showConfirmationPopUp() {

        boolean wrapInScrollView = true;
        new MaterialDialog.Builder(context)
                .title("Booking confirmation!!!")
                .content("Are you sure you want to Book it?")
                .positiveText("Confirm")
                .negativeText(R.string.cancel)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        bookRide();
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

    private void bookRide() {
        //http://netforce.biz/tripesplit/mobileApp/api/services.php?opt=book_trip
        String baseUrl = getResources().getString(R.string.url);
        String url = baseUrl + "services.php?opt=book_trip";

        Log.i("result_url", url);

        Ion.with(context)
                .load("POST", url)
                .setBodyParameter("trip_id", trip_id)
                .setBodyParameter("user_id", userSessionManager.getUserId())
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        progressDialog.dismiss();
                        if (result == null) {
                            showMessage("nothing is happening");
                        } else {
                            Log.i("result_kunsang", result.toString());
                            String status = result.get("status").getAsString();
                            if (status.equalsIgnoreCase("success")) {
                                finish();
                                showMessage("Ride booked");
                            } else {

                                if (status.equalsIgnoreCase("Already Booked")) {
                                    showMessage("Trip already booked");

                                } else if (status.equalsIgnoreCase("Failed")) {
                                    JsonArray data = result.getAsJsonArray("data");
                                    JsonObject jsonObject = data.get(0).getAsJsonObject();
                                    String msg = jsonObject.get("msg").getAsString();
                                    showMessage(msg);
                                    return;

                                } else {
                                    showMessage("something went wrong");
                                }
                            }
                        }
                    }
                });

    }

    private void setAge(String dob, TextView textView) {
        if (dob.equalsIgnoreCase("0000-00-00")) {
            textView.setText("NA");
        } else {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            try {
                Calendar cal = Calendar.getInstance();
                cal.setTime(simpleDateFormat.parse(dob));// all done
                String age = getAge(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
                textView.setText(age);
            } catch (ParseException e) {
                e.printStackTrace();
            }


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

    private String getFormatedDate(String created_date, int i) {
        //2016-11-08
        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;
        try {
            date = fmt.parse(created_date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        SimpleDateFormat fmtOut = new SimpleDateFormat("dd MMM yyyy");
        return fmtOut.format(date);
    }

    private String getFormatedDate(String etd) {
        //Wed 21 Dec 2016 19:02
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

    private String getFormetedTime(String etd) {
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

}
