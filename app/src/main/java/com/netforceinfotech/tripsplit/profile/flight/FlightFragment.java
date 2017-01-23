package com.netforceinfotech.tripsplit.profile.flight;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.appyvet.rangebar.RangeBar;
import com.netforceinfotech.tripsplit.R;
import com.netforceinfotech.tripsplit.general.UserSessionManager;
import com.shehabic.droppy.DroppyClickCallbackInterface;
import com.shehabic.droppy.DroppyMenuItem;
import com.shehabic.droppy.DroppyMenuPopup;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


public class FlightFragment extends Fragment implements View.OnClickListener, TimePickerDialog.OnTimeSetListener, DatePickerDialog.OnDateSetListener {

    private static final String IMAGE_DIRECTORY_NAME = "tripsplit";

    private static final int PICK_IMAGE = 1234;
    private static final int TAKE_PHOTO_CODE = 1235;
    FlightAdapter adapter;
    RecyclerView recyclerView;
    Context context;
    TextView textViewETD, pass_txt, space_txt, textviewETA, textviewAgeGroup;
    Button increamentPass, decreamentPass, increamentSpace, decreamentSpace, male, female, mixed, buttonPost, buttonAddImage;
    int pass_number = 1;
    int space_number = 1;
    boolean etdclicked = true;
    private Uri fileUri;
    private String filePath;
    private MaterialDialog dialog;
    Button buttonCurrency;
    RangeBar rangeBar;


    public FlightFragment() {
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
        setupLayout(view);
        return view;
    }

    private void setupLayout(View view) {
        rangeBar = (RangeBar) view.findViewById(R.id.rangebar);
        textviewAgeGroup = (TextView) view.findViewById(R.id.textViewAgeGroup);
        buttonCurrency = (Button) view.findViewById(R.id.buttonCurrency);
        buttonCurrency.setOnClickListener(this);
        final String currency[] = {"INR", "USD", "EURO", "YEN"};
        DroppyMenuPopup.Builder droppyBuilder = new DroppyMenuPopup.Builder(context, buttonCurrency);
        droppyBuilder.addMenuItem(new DroppyMenuItem("INR"))
                .addMenuItem(new DroppyMenuItem("USD"))
                .addMenuItem(new DroppyMenuItem("EURO"))
                .addMenuItem(new DroppyMenuItem("YEN"));
        droppyBuilder.setOnClick(new DroppyClickCallbackInterface() {
            @Override
            public void call(View v, int id) {
                buttonCurrency.setText(currency[id]);
            }
        });
        droppyBuilder.build();
        rangeBar.setOnRangeBarChangeListener(new RangeBar.OnRangeBarChangeListener() {
            @Override
            public void onRangeChangeListener(RangeBar rangeBar, int leftPinIndex,
                                              int rightPinIndex,
                                              String leftPinValue, String rightPinValue) {
                textviewAgeGroup.setText(leftPinValue + "-" + rightPinValue);
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
        pass_txt = (TextView) view.findViewById(R.id.textViewPax);
        space_txt = (TextView) view.findViewById(R.id.textViewSpace);
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
            case R.id.buttonAddImage:
                showEditPicPopup();
                break;
            case R.id.buttonPost:
                postTrip();
                break;
            case R.id.textviewETD:
                etdclicked = true;
                Calendar now = Calendar.getInstance();
                DatePickerDialog dpd = DatePickerDialog.newInstance(
                        FlightFragment.this,
                        now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH)
                );
                dpd.show(getActivity().getFragmentManager(), "Datepickerdialog");


                break;
            case R.id.textviewETA:
                etdclicked = false;
                Calendar now1 = Calendar.getInstance();
                DatePickerDialog dpd1 = DatePickerDialog.newInstance(
                        FlightFragment.this,
                        now1.get(Calendar.YEAR),
                        now1.get(Calendar.MONTH),
                        now1.get(Calendar.DAY_OF_MONTH)
                );
                dpd1.show(getActivity().getFragmentManager(), "Datepickerdialog");


                break;

            case R.id.increament_pass:

                pass_number = pass_number + 1;
                String p_n = String.valueOf(pass_number);
                pass_txt.setText(p_n);


                break;

            case R.id.decreament_pass:

                if (pass_number >= 1) {
                    pass_number = pass_number - 1;
                    String dp_n = String.valueOf(pass_number);
                    pass_txt.setText(dp_n);
                }
                break;

            case R.id.increament_Space:

                space_number = space_number + 1;
                String sp_n = String.valueOf(space_number);
                space_txt.setText(sp_n);

                break;

            case R.id.decreament_Space:
                if (space_number >= 1) {
                    space_number = space_number - 1;
                    String dsp_n = String.valueOf(space_number);
                    space_txt.setText(dsp_n);
                }

                break;

            case R.id.maleButton:

                female.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.border_image));
                mixed.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.border_image2));
                male.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.colorPrimary));

                male.setTextColor(ContextCompat.getColor(getActivity(), R.color.white));
                female.setTextColor(ContextCompat.getColor(getActivity(), R.color.colorPrimary));
                mixed.setTextColor(ContextCompat.getColor(getActivity(), R.color.colorPrimary));

                break;

            case R.id.femaleButton:

                male.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.border_image));
                mixed.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.border_image2));
                female.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.colorPrimary));

                female.setTextColor(ContextCompat.getColor(getActivity(), R.color.white));
                male.setTextColor(ContextCompat.getColor(getActivity(), R.color.colorPrimary));
                mixed.setTextColor(ContextCompat.getColor(getActivity(), R.color.colorPrimary));


                break;

            case R.id.mixedButton:

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
        }


    }

    private void postTrip() {
        validateDate();
    }

    private void validateDate() {
        Date etd, eta;
        String date1 = textViewETD.getText().toString();
        String date2 = textviewETA.getText().toString();
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE dd MMM yy HH:mm");
        Log.i("datecheck", date1 + ":" + date2);
        try {
            etd = dateFormat.parse(date1);
        } catch (ParseException e) {
            showMessage("Departure date not set");
            return;
        }
        try {
            eta = dateFormat.parse(date2);
        } catch (ParseException e) {
            showMessage("Arival date not set");
            return;
        }
        Log.i("datecheck", etd.toString() + ":" + eta.toString());
        if (etd.after(eta)) {

            showMessage("Arival time should be after Departure time");
        }
    }

    private void showMessage(String s) {
        Toast.makeText(context, s, Toast.LENGTH_SHORT).show();
    }

    public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute, int second) {
        String hourString = hourOfDay < 10 ? "0" + hourOfDay : "" + hourOfDay;
        String minuteString = minute < 10 ? "0" + minute : "" + minute;
        String secondString = second < 10 ? "0" + second : "" + second;
        String time = "You picked the following time: " + hourString + "h" + minuteString + "m" + secondString + "s";
        if (etdclicked) {
            textViewETD.append(" " + hourString + ":" + minuteString);
        } else {
            textviewETA.append(" " + hourString + ":" + minuteString);
        }
    }


    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        Date date2 = new Date();
        SimpleDateFormat date_format = new SimpleDateFormat("yyyy-MM-dd");
        try {
            date2 = date_format.parse(year + "-" + monthOfYear + "-" + dayOfMonth);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        SimpleDateFormat outDate = new SimpleDateFormat("EEE dd MMM yy");

        if (etdclicked) {
            textViewETD.setText(outDate.format(date2));
        } else {
            textviewETA.setText(outDate.format(date2));
        }
        Calendar now = Calendar.getInstance();
        TimePickerDialog tpd = TimePickerDialog.newInstance(
                this,
                now.get(Calendar.HOUR_OF_DAY),
                now.get(Calendar.MINUTE), true
        );
        tpd.show(getActivity().getFragmentManager(), "Timepickerdialog");
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case TAKE_PHOTO_CODE:
                if (resultCode == getActivity().RESULT_OK) {
                    Log.i("result picture", "clicked");
                    buttonAddImage.setText(filePath);
                }
                break;
            case PICK_IMAGE:
                if (resultCode == getActivity().RESULT_OK) {
                    Uri uri = data.getData();
                    String[] filePathColumn = {MediaStore.Images.Media.DATA};
                    Cursor cursor = context.getContentResolver().query(uri, filePathColumn, null, null, null);
                    cursor.moveToFirst();

                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    filePath = getPath(uri);
                    try {
                        buttonAddImage.setText(filePath);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Log.i("result picture", "picked");
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
