package com.netforceinfotech.tripsplit.dashboard;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.BitmapEncoder;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.google.gson.JsonObject;
import com.hedgehog.ratingbar.RatingBar;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.netforceinfotech.tripsplit.R;
import com.netforceinfotech.tripsplit.general.UserSessionManager;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class DashboardActivity extends AppCompatActivity implements View.OnClickListener {
    private static final int MY_PERMISSIONS_REQUEST_CAMERA = 101;
    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 102;
    private static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 103;
    private static final int MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION = 104;
    private static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 105;
    private static final String TAG = "PERMISSION";
    //Defining Variables
    private Toolbar toolbar;
    Context context;
    private DrawerLayout drawerLayout;
    NavigationFragment drawer;
    UserSessionManager userSessionManager;
    String[] PERMISSIONS = {Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_NETWORK_STATE};
    private static final int PERMISSION_ALL = 1;
    private float reviewRating = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(getApplication());
        setContentView(R.layout.activity_dashboard);
        // Initializing Toolbar and setting it as the actionbar
        context = this;
        getPermission();
        userSessionManager = new UserSessionManager(context);
        setupToolbar();
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer);
        setupNavigationCustom();

        getReviewStatus();

        /*if (!hasPermissions(this, PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);

        }*/

    }

    private void getReviewStatus() {
        //netforce.biz/tripesplit/mobileApp/api/services.php?opt=review_status
        String baseUrl = getString(R.string.url);
        String url = baseUrl + "services.php?opt=review_status";

        //showReviewPopUp("kunsang", "12 dec 1990", userSessionManager.getProfileImage(), "3");

        Ion.with(context)
                .load("POST", url)
                .setBodyParameter("user_id", userSessionManager.getUserId())
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        // do stuff with the result or error
                        if (result == null) {
                        } else {
                            Log.i("result", result.toString());
                            if (result.get("status").getAsString().equalsIgnoreCase("success")) {
                                JsonObject data = result.getAsJsonObject("data");
                                try {
                                    JsonObject request_review = data.getAsJsonObject("request_review");

                                    if (request_review.get("status").getAsBoolean()) {

                                        JsonObject detail = request_review.getAsJsonObject("detail");
                                        String source = detail.get("depart_address").getAsString();
                                        String destination = detail.get("dest_address").getAsString();
                                        String etd = detail.get("etd").getAsString();
                                        String image = detail.get("img_name").getAsString();
                                        String trip_id = detail.get("tour_id").getAsString();
                                        showRequiestReviewPopUp(source, destination, etd, image, trip_id);
                                        return;
                                    }
                                } catch (Exception ex) {

                                }
                                try {
                                    JsonObject fill_review = data.getAsJsonObject("fill_review");
                                    if (fill_review.get("status").getAsBoolean()) {
                                        JsonObject detail = fill_review.getAsJsonObject("detail");
                                        String name = detail.get("firstname").getAsString();
                                        String dob = detail.get("dob").getAsString();
                                        String image = detail.get("profile_image").getAsString();
                                        String trip_id = detail.get("tour_id").getAsString();
                                        String etd = detail.get("etd").getAsString();
                                        String destination = detail.get("dest_address").getAsString();
                                        String source = detail.get("depart_address").getAsString();
                                        showReviewPopUp(name, dob, image, trip_id, etd, destination, source);
                                        return;
                                    }
                                } catch (Exception ex) {

                                }


                            }

                        }
                    }
                });

    }

    private void setupToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        TextView textViewLogout = (TextView) toolbar.findViewById(R.id.textviewLogout);
        ImageView homeButton = (ImageView) toolbar.findViewById(R.id.homeButton);
        ImageView image_appicon = (ImageView) toolbar.findViewById(R.id.image_appicon);
        image_appicon.setVisibility(View.GONE);
        Glide.with(this)
                .fromResource()
                .asBitmap()
                .encoder(new BitmapEncoder(Bitmap.CompressFormat.PNG, 100))
                .load(R.drawable.trip_splitz_logo_red_bg).into(image_appicon);
        homeButton.setVisibility(View.GONE);
        textViewLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopUp();


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

    private void showPopUp() {
        new MaterialDialog.Builder(this)
                .title("Log out")
                .content("Are you sure you want to Log out?")
                .positiveText("Yes")
                .negativeText("Cancel")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        userSessionManager.setIsLoggedIn(false);
                        userSessionManager.clearData();
                        try {
                            LoginManager.getInstance().logOut();
                            finish();
                        } catch (Exception ex) {

                        }
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

    private void showRequiestReviewPopUp(String source, String destination, String etd, String image_url, final String trip_id) {

        final MaterialDialog requestReviewBox = new MaterialDialog.Builder(this)
                .customView(R.layout.request_review_popup, false)
                .show();

        requestReviewBox.setCanceledOnTouchOutside(false);
        requestReviewBox.findViewById(R.id.buttonSendRequest).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showMessage("sending request");
                requestReviewBox.dismiss();
                sendReviewRequest(trip_id);
            }
        });
        requestReviewBox.findViewById(R.id.buttonLater).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestReviewBox.dismiss();
            }
        });
        TextView textViewSource = (TextView) requestReviewBox.findViewById(R.id.textViewSource);
        TextView textViewDestination = (TextView) requestReviewBox.findViewById(R.id.textViewDestination);
        TextView textViewTripDate = (TextView) requestReviewBox.findViewById(R.id.textViewTripDate);
        CircleImageView circleImageView = (CircleImageView) requestReviewBox.findViewById(R.id.imageViewTrip);
        Glide.with(context).load(image_url).error(R.drawable.ic_error).into(circleImageView);
        textViewSource.setText(source);
        textViewDestination.setText(destination);
        textViewTripDate.setText(etd);


    }

    private void sendReviewRequest(String trip_id) {
        //http://netforce.biz/tripesplit/mobileApp/api/services.php?opt=ask_review
        String url = getString(R.string.url) + "services.php?opt=ask_review";
        Ion.with(context)
                .load("POST", url)
                .setBodyParameter("trip_id", trip_id)
                .setBodyParameter("user_id", userSessionManager.getUserId())
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        // do stuff with the result or error
                        if (result == null) {
                        } else {

                        }
                    }
                });

    }

    private void showReviewPopUp(String name, String dob, String imageUrl, final String trip_id, String etd, String destination, String source) {
        final RatingBar ratingBar;

        final MaterialDialog reviewBox = new MaterialDialog.Builder(this)
                .customView(R.layout.review_pop_up, false)
                .show();

        reviewBox.setCanceledOnTouchOutside(false);

        reviewBox.findViewById(R.id.imageViewClose).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        final EditText editText = (EditText) reviewBox.findViewById(R.id.editText);
        CircleImageView imageViewDp = (CircleImageView) reviewBox.findViewById(R.id.imageViewDp);
        TextView textViewName, textViewDob, textViewDestination, textViewSource, textViewDate;
        textViewDob = (TextView) reviewBox.findViewById(R.id.textViewDob);
        textViewName = (TextView) reviewBox.findViewById(R.id.textViewName);
        textViewDestination = (TextView) reviewBox.findViewById(R.id.textViewDestination);
        textViewSource = (TextView) reviewBox.findViewById(R.id.textViewSource);
        textViewDate = (TextView) reviewBox.findViewById(R.id.textViewDate);
        textViewDate.setText(getFormattedDate(etd));
        textViewDestination.setText(destination);
        textViewSource.setText(source);
        ratingBar = (RatingBar) reviewBox.findViewById(R.id.ratingbar);
        Glide.with(getApplicationContext()).load(imageUrl).error(R.drawable.ic_error).into(imageViewDp);
        textViewDob.setText(getFormattedDob(dob));
        textViewName.setText(name);
        ratingBar.setOnRatingChangeListener(new RatingBar.OnRatingChangeListener() {
            @Override
            public void onRatingChange(float RatingCount) {
                reviewRating = RatingCount;
            }
        });
        reviewBox.findViewById(R.id.imageViewClose).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reviewBox.dismiss();
            }
        });
        reviewBox.findViewById(R.id.buttonSubmit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (reviewRating == -1) {
                    showMessage("Please Rate this trip first!!!");
                    return;
                }
                if (editText.length() <= 0) {
                    showMessage("Please write few word about trip !!!");
                    return;
                }
                reviewBox.dismiss();
                sendReview(trip_id, reviewRating, editText.getText().toString());
            }
        });
    }

    private String getFormattedDate(String etd) {
        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = null;
        try {
            date = fmt.parse(etd);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        SimpleDateFormat fmtOut = new SimpleDateFormat("dd MMM yyyy HH:mm");
        return fmtOut.format(date);
    }

    private void sendReview(String trip_id, float reviewRating, String review) {

//http://netforce.biz/tripesplit/mobileApp/api/services.php?opt=review
        String url = getString(R.string.url) + "services.php?opt=review";
        Ion.with(context)
                .load("POST", url)
                .setBodyParameter("trip_id", trip_id)
                .setBodyParameter("star_rating", reviewRating + "")
                .setBodyParameter("comment", review)
                .setBodyParameter("user_id", userSessionManager.getUserId())
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        // do stuff with the result or error
                        if (result == null) {
                        } else {
                            Log.i("result", result.toString());
                        }
                    }
                });

    }

    private String getFormattedDob(String dob) {
        return dob;
    }

    private void showMessage(String s) {
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }

    private void setupNavigationCustom() {
        drawer = (NavigationFragment) getSupportFragmentManager().findFragmentById(R.id.fragment);
        drawer.setup(R.id.fragment, (DrawerLayout) findViewById(R.id.drawer), toolbar);
    }

    @Override
    public void onBackPressed() {
        if (NavigationFragment.POSITION != 0) {
            drawerLayout.closeDrawers();
            drawer.setupHomeFragment();
            NavigationFragment.POSITION = 0;
        } else {
            ActivityCompat.finishAffinity(this);
            super.onBackPressed();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

        }
    }
}
