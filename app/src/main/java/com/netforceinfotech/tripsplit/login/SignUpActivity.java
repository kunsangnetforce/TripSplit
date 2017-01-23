package com.netforceinfotech.tripsplit.login;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.mukesh.countrypicker.fragments.CountryPicker;
import com.mukesh.countrypicker.interfaces.CountryPickerListener;
import com.mukesh.countrypicker.models.Country;
import com.netforceinfotech.tripsplit.R;
import com.netforceinfotech.tripsplit.general.UserSessionManager;
import com.netforceinfotech.tripsplit.util.Validation;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener, DatePickerDialog.OnDateSetListener {

    private static final int MY_PERMISSIONS_REQUEST_CAMERA = 101;
    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 102;
    private static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 103;
    private static final int MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION = 104;
    private static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 105;
    private static final String TAG = "PERMISSION";
    private static final int PERMISSION_ALL = 101;
    UserSessionManager userSessionManager;
    Context context;
    Button buttonSignIn;
    EditText etname, etemail, etpassword, etconfirmpassword, etdob, etcountry, etaddress;
    String name, email, password, confirmpassword, dob, country, address, country_code = "";
    ArrayList<CountryData> countries = new ArrayList<>();
    private MaterialDialog progressDialog;
    Button buttonOk;
    EditText etDay, etMonth, etYear;
    private MaterialDialog dialogDoB;
    private String dobString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        context = this;
        userSessionManager = new UserSessionManager(context);
        initView();
        setupToolBar("Sign Up!");
        getPermission();
    }


    private void setupToolBar(String app_name) {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(app_name);


    }

    private void initView() {

        progressDialog = new MaterialDialog.Builder(this)
                .title(R.string.progress_dialog)
                .content(R.string.please_wait)
                .progress(true, 0).build();
        buttonSignIn = (Button) findViewById(R.id.buttonSignIn);
        etname = (EditText) findViewById(R.id.etname);
        etemail = (EditText) findViewById(R.id.etemail);
        etpassword = (EditText) findViewById(R.id.etpassword);
        etconfirmpassword = (EditText) findViewById(R.id.etconfirmpassword);
        etdob = (EditText) findViewById(R.id.etdob);
        etcountry = (EditText) findViewById(R.id.etcountry);
        etaddress = (EditText) findViewById(R.id.etaddress);
        buttonSignIn.setOnClickListener(this);
        etdob.setOnClickListener(this);
        etcountry.setOnClickListener(this);
        try {

            CountryPicker picker = CountryPicker.newInstance("Select CountryData");
            Country countryData = picker.getUserCountryInfo(this);
            etcountry.setText(countryData.getName());
        } catch (Exception ex) {

        }

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

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.etcountry:
                showCountryPopUp();
                break;
            case R.id.buttonSignIn:
                name = etname.getText().toString().trim();
                email = etemail.getText().toString().trim();
                password = etpassword.getText().toString();
                confirmpassword = etconfirmpassword.getText().toString();
                country = etcountry.getText().toString();
                dob = etdob.getText().toString();
                address = etaddress.getText().toString();

                if (!Validation.isEmailAddress(etemail, true)) {
                    showMessage("not valid email");
                    return;
                }
                if (name.length() <= 0 || email.length() <= 0 || password.length() <= 0) {
                    showMessage("Name or email or password cannot be blank");
                    return;

                }
                if (!password.equals(confirmpassword)) {
                    showMessage("Missmatch password");
                    return;
                }
                register(name, email, password, country, address, dob);
                break;
            case R.id.etdob:
                showEditDoBPopup();
                break;
            case R.id.buttonOk:
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                try {
                    Date test = sdf.parse(etYear.getText().toString() + "-" + etMonth.getText().toString() + "-" + etDay.getText().toString());
                } catch (ParseException pe) {
                    //Date is invalid, try next format
                    showMessage("Not valid date");
                    return;
                }

                dobString = etDay.getText().toString() + "/" + etMonth.getText().toString() + "/" + etYear.getText().toString();
                etdob.setText(dobString);
                dialogDoB.dismiss();
                break;

        }

    }

    private void showCountryPopUp() {
        final CountryPicker picker = CountryPicker.newInstance("Select Country");
        picker.show(getSupportFragmentManager(), "COUNTRY_PICKER");
        picker.setListener(new CountryPickerListener() {
            @Override
            public void onSelectCountry(String name, String code, String dialCode, int flagDrawableResID) {
                // Implement your code here
                etcountry.setText(name);
                country_code = code;
                country = name;
                picker.dismiss();
            }
        });
    }

    private void showMessage(String s) {
        Toast.makeText(context, s, Toast.LENGTH_SHORT).show();
    }

    private void register(String name, final String email, String password, String country, String address, String dob) {
        showProgressbar();
        try {
            name = URLEncoder.encode(name, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        try {
            address = URLEncoder.encode(address, "UTF-8");
        } catch (Exception ex) {

        }
        try {
            country = URLEncoder.encode(country, "UTF-8");
        } catch (Exception ex) {

        }
        password = password.trim();
        dob = getServerDateFormat(dob);
        String baseUrl = getString(R.string.url);
        String url = baseUrl + "services.php?opt=register&facebook=0&fb_token=&fb_id=&reg_id=" + userSessionManager.getRegId() + "&name=" + name
                + "&email=" + email + "&dob=" + dob + "&country=" + country + "&country_code=" + country_code + "&send_otp=true&password=" + password + "&address=" + address + "&send_otp=1";
        Log.i("kurl", url);
        Ion.with(context)
                .load(url)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        dismissProgressbar();
                        if (result != null) {
                            Log.i("kresult", result.toString());
                            if (result.get("status").getAsString().equalsIgnoreCase("success")) {
                                JsonArray data = result.getAsJsonArray("data");
                                JsonObject jsonObject = data.get(0).getAsJsonObject();
                                String user_id = jsonObject.get("user_id").getAsString();
                                userSessionManager.setUserId(user_id);
                                userSessionManager.setEmail(email);
                                Intent intent = new Intent(context, OTPActivity.class);
                                startActivity(intent);
                                finish();
                            }

                        } else {
                            showMessage("something went wrong. Please check internet connection");
                        }
                    }
                });
    }

    private void dismissProgressbar() {
        progressDialog.dismiss();
    }

    private void showProgressbar() {
        progressDialog.show();
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

    private void showEditDoBPopup() {

        boolean wrapInScrollView = true;
        dialogDoB = new MaterialDialog.Builder(context)
                .title(R.string.editdob)
                .customView(R.layout.editdob, wrapInScrollView)
                .negativeText(R.string.cancel)
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();
                    }
                })
                .show();
        buttonOk = (Button) dialogDoB.findViewById(R.id.buttonOk);
        etDay = (EditText) dialogDoB.findViewById(R.id.etDay);
        etMonth = (EditText) dialogDoB.findViewById(R.id.etMonth);
        etYear = (EditText) dialogDoB.findViewById(R.id.etYear);
        buttonOk.setOnClickListener(this);
        etDay.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (etDay.length() == 2) {
                    int day = Integer.parseInt(etDay.getText().toString());
                    if (day > 31) {
                        showMessage("Invalid date");
                        return;
                    }
                    etMonth.requestFocus();

                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        etMonth.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (etMonth.length() == 2) {
                    int day = Integer.parseInt(etMonth.getText().toString());
                    if (day > 12) {
                        showMessage("Invalid month");
                        return;
                    }
                    etYear.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        etYear.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (etYear.length() == 4) {
                    int dobYear = Integer.parseInt(etYear.getText().toString());
                    Calendar c = Calendar.getInstance();
                    int year = c.get(Calendar.YEAR);
                    if (dobYear > year) {
                        showMessage("DoB cannot be in future");
                        return;
                    }
                    buttonOk.performClick();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


    }

    private String getFormattedDate(String dob) {
        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;
        try {
            date = fmt.parse(dob);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        SimpleDateFormat fmtOut = new SimpleDateFormat("dd/MM/yyyy");
        return fmtOut.format(date);
    }

    private String getServerDateFormat(String s) {
        SimpleDateFormat fmt = new SimpleDateFormat("dd/MM/yyyy");
        Date date = null;
        try {
            date = fmt.parse(s);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        SimpleDateFormat fmtOut = new SimpleDateFormat("yyyy-MM-dd");
        return fmtOut.format(date);
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        Date date2 = new Date();
        monthOfYear = monthOfYear + 1;
        SimpleDateFormat date_format = new SimpleDateFormat("yyyy-MM-dd");
        try {
            date2 = date_format.parse(year + "-" + monthOfYear + "-" + dayOfMonth);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        SimpleDateFormat outDate = new SimpleDateFormat("yyyy-MM-dd");


        etdob.setText(outDate.format(date2));
        dob = etdob.getText().toString();

    }
}
