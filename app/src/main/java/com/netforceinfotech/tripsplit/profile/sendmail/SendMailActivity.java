package com.netforceinfotech.tripsplit.profile.sendmail;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.Glide;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.netforceinfotech.tripsplit.R;
import com.netforceinfotech.tripsplit.general.ImageFilePath;
import com.netforceinfotech.tripsplit.general.UserSessionManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class SendMailActivity extends AppCompatActivity implements View.OnClickListener {

    Context context;
    MaterialDialog dialog;

    private static final String IMAGE_DIRECTORY_NAME = "tripsplit";

    private static final int PICK_IMAGE = 1234;
    private static final int TAKE_PHOTO_CODE = 1235;
    private Uri fileUri;
    private String filePath;
    ImageView image;
    private boolean imageFlag = false;
    private File file;
    EditText editText;
    UserSessionManager userSessionManager;
    private String id, name, image_url, reg_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ;
        setContentView(R.layout.activity_send_mail);

        context = this;
        userSessionManager = new UserSessionManager(context);
        editText = (EditText) findViewById(R.id.editText);
        image = (ImageView) findViewById(R.id.image);
        Bundle bundle = getIntent().getExtras();
        name = bundle.getString("name");
        id = bundle.getString("id");
        image_url = bundle.getString("image_url");
        reg_id = bundle.getString("reg_id");
        findViewById(R.id.buttonAddImage).setOnClickListener(this);
        findViewById(R.id.buttonSend).setOnClickListener(this);
        setupToolBar("Send Email");
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
                        filePath = ImageFilePath.getPath(this, data.getData());
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

        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.buttonAddImage:
                showEditPicPopup();
                break;
            case R.id.linearLayoutPicture:
                takePictureIntent();
                dialog.dismiss();
                break;
            case R.id.linearLayoutGalary:
                pickPictureIntent();
                dialog.dismiss();
                break;
            case R.id.buttonSend:
                if (editText.getText().length() <= 0) {
                    showMessage("Empty body!!!");
                    return;
                }
                sendEmail();
                break;
        }
    }

    private File savebitmap(String filePath) {
        File file = new File(filePath);
        String extension = filePath.substring(filePath.lastIndexOf(".") + 1, filePath.length());
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        Bitmap bitmap = BitmapFactory.decodeFile(filePath, bmOptions);
        OutputStream outStream = null;
        try {
            // make a new bitmap from your file
            outStream = new FileOutputStream(file);
            if (extension.equalsIgnoreCase("png")) {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 20, outStream);
            } else if (extension.equalsIgnoreCase("jpg") || extension.equalsIgnoreCase("jpeg")) {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 20, outStream);
            } else {
                return null;
            }
            outStream.flush();
            outStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return file;


    }



    private void showMessage(String s) {
        Toast.makeText(context, s, Toast.LENGTH_SHORT).show();
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

    public Uri getOutputMediaFileUri() {
        return Uri.fromFile(getOutputMediaFile());
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

    private void sendEmail() {
        String baseUrl = getString(R.string.url);
        String url = baseUrl + "services.php?opt=send_mail";
        Log.i("kurl", url);
        if (filePath != null) {
            file = savebitmap(filePath);
            if (file == null) {
                showMessage("Image not valid");
                return;
            }
            Ion.with(this)
                    .load(url)
                    .setHeader("ENCTYPE", "multipart/form-data")
                    .setTimeout(60 * 60 * 1000)
                    .setMultipartParameter("writer_id", userSessionManager.getUserId())
                    .setMultipartParameter("id", id)
                    .setMultipartParameter("body", editText.getText().toString())
                    .setMultipartFile("upload", "image/*", file)

                    .asJsonObject()
                    .setCallback(new FutureCallback<JsonObject>() {
                                     @Override
                                     public void onCompleted(Exception e, JsonObject result) {

                                         if (result != null) {
                                             Log.i("kresult", result.toString());
                                             if (result.get("status").getAsString().equalsIgnoreCase("success")) {
                                                 showMessage("Sent successfully");
                                             }

                                         } else {
                                             showMessage("Something went wrong...");
                                             e.printStackTrace();
                                         }
                                     }
                                 }

                    );
        } else {
            Ion.with(this)
                    .load(url)
                    .setHeader("ENCTYPE", "multipart/form-data")
                    .setTimeout(60 * 60 * 1000)
                    .setMultipartParameter("writer_id", userSessionManager.getUserId())
                    .setMultipartParameter("id", id)
                    .setMultipartParameter("body", editText.getText().toString())
                    .asJsonObject()
                    .setCallback(new FutureCallback<JsonObject>() {
                                     @Override
                                     public void onCompleted(Exception e, JsonObject result) {

                                         if (result != null) {
                                             Log.i("kresult", result.toString());
                                             if (result.get("status").getAsString().equalsIgnoreCase("success")) {
                                                 showMessage("Sent successfully");
                                             }

                                         } else {
                                             showMessage("Something went wrong...");
                                             e.printStackTrace();
                                         }
                                     }
                                 }

                    );
        }
        //services.php?opt=forgotpassword&email=dileep@netforceinfotech.com


    }


}
