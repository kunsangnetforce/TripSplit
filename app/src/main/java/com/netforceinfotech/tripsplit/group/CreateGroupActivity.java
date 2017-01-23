package com.netforceinfotech.tripsplit.group;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.mukesh.countrypicker.fragments.CountryPicker;
import com.mukesh.countrypicker.interfaces.CountryPickerListener;
import com.netforceinfotech.tripsplit.R;
import com.netforceinfotech.tripsplit.general.ImageFilePath;
import com.netforceinfotech.tripsplit.general.UserSessionManager;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class CreateGroupActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int PICK_IMAGE = 1234;
    private static final int TAKE_PHOTO_CODE = 1235;
    private static final String IMAGE_DIRECTORY_NAME = "tripsplit";
    UserSessionManager userSessionManager;
    Toolbar toolbar;
    Context context;
    ImageView image;
    Button buttonAddImage;
    TextView textViewCategory, textViewCountry;
    EditText editTextCity, editTextGroupTitle;
    ArrayList<Category> categories = new ArrayList<>();
    private String selectedCategoryId;
    private MaterialDialog progressDialog, dialog;
    private Uri fileUri;
    private String filePath;
    private String selectedCategoryString;
    private DatabaseReference _user_group;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);
        context = this;
        userSessionManager = new UserSessionManager(context);
        initView();
        getCategory();
        setupToolBar(getString(R.string.create_group));

    }

    private void getCategory() {
        progressDialog.show();
        //http://netforce.biz/tripesplit/mobileApp/api/services.php?opt=categories
        String baseUrl = getString(R.string.url);
        String url = baseUrl + "services.php?opt=categories";
        Log.i("url", url);
        Ion.with(context)
                .load(url)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        progressDialog.hide();
                        if (result == null) {
                            showMessage("Something went wrong. Please try again");
                        } else {
                            if (result.get("status").getAsString().equalsIgnoreCase("success")) {
                                JsonArray data = result.getAsJsonArray("data");
                                JsonObject object = data.get(0).getAsJsonObject();
                                JsonArray category = object.getAsJsonArray("category");
                                setupCategory(category);
                            }
                        }
                    }
                });

    }

    private void setupCategory(JsonArray category) {

        int size = category.size();
        for (int i = 0; i < size; i++) {
            JsonObject object = category.get(i).getAsJsonObject();
            Category category1 = new Category(object.get("name").getAsString(), object.get("id").getAsString());
            if (!categories.contains(category1)) {
                categories.add(category1);
            }
        }

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case TAKE_PHOTO_CODE:
                if (resultCode == RESULT_OK) {
                    Log.i("result picture", "clicked");
                    //  buttonAddImage.setText(filePath);
                    image.setVisibility(View.VISIBLE);
                    Glide.with(context).load(new File(filePath)).error(R.drawable.ic_error).into(image);
                }
                break;
            case PICK_IMAGE:
                if (resultCode == RESULT_OK) {
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
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void setupToolBar(String title) {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        String teams = title;
        getSupportActionBar().setTitle(teams);
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

    private void initView() {
        image = (ImageView) findViewById(R.id.image);
        buttonAddImage = (Button) findViewById(R.id.buttonAddImage);
        buttonAddImage.setOnClickListener(this);
        editTextGroupTitle = (EditText) findViewById(R.id.editTextGroupTitle);
        progressDialog = new MaterialDialog.Builder(context)
                .title(R.string.progress_dialog)
                .content(R.string.please_wait)
                .progress(true, 0).build();
        textViewCategory = (TextView) findViewById(R.id.textViewCategory);
        textViewCategory.setOnClickListener(this);
        editTextCity = (EditText) findViewById(R.id.editTextCity);
        textViewCountry = (TextView) findViewById(R.id.textViewCountry);
        textViewCountry.setOnClickListener(this);
        findViewById(R.id.buttonCreateGroup).setOnClickListener(this);
        //    setupRecyclerView(view);


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.linearLayoutPicture:
                takePictureIntent();
                dialog.dismiss();
                break;
            case R.id.linearLayoutGalary:
                pickPictureIntent();
                dialog.dismiss();
                break;
            case R.id.buttonAddImage:
                showEditPicPopup();
                break;
            case R.id.textViewCountry:
                setupCoutryPopUp();
                break;
            case R.id.textViewCategory:
                setupCategoryPopUp();
                break;

            case R.id.buttonCreateGroup:
                if (editTextGroupTitle.getText().toString().length() <= 0) {
                    showMessage("Set Group Title");
                    return;
                }
                if (textViewCountry.getText().toString().equalsIgnoreCase("country")) {
                    showMessage("Set Country");
                    return;
                }
                if (textViewCategory.getText().toString().equalsIgnoreCase("category")) {
                    showMessage("Set Category");
                    return;
                }
                if (editTextCity.getText().toString().equalsIgnoreCase("city")) {
                    showMessage("Set City");
                    return;
                }
                /*
                * 1) chat_title
2) city
3) country
4) category
                * */
                createGroup(editTextGroupTitle.getText().toString(), editTextCity.getText().toString(), textViewCountry.getText().toString(), selectedCategoryId);
                // searchGroup(textViewCountry.getText().toString(), textViewCity.getText().toString(), textViewCategory.getText().toString());
                break;

        }
    }

    private void createGroup(String groupName, String city, String country, String selectedCategoryId) {
        //services.php?opt=create_group
        String baseUrl = getString(R.string.url);
        String url = baseUrl + "services.php?opt=create_group";
        Log.i("url", url);
        progressDialog.show();
        if (filePath != null) {
            Log.i("filePath", "inside send image");
            Ion.with(getApplicationContext())
                    .load("POST", url)
                    .setMultipartParameter("user_id", userSessionManager.getUserId())
                    .setMultipartParameter("chat_title", groupName)
                    .setMultipartParameter("city", city)
                    .setMultipartParameter("country", country)
                    .setMultipartParameter("category", selectedCategoryId)
                    .setMultipartFile("image", new File(filePath))
                    .asJsonObject()
                    .setCallback(new FutureCallback<JsonObject>() {
                        @Override
                        public void onCompleted(Exception e, JsonObject result) {
                            progressDialog.dismiss();
                            if (result == null) {
                                e.printStackTrace();
                                showMessage("Error in creating group");
                            } else {
                                if (result.get("status").getAsString().equalsIgnoreCase("success")) {
                                    Log.i("result", result.toString());
                                    showMessage("Group Created Successfully");
                                    JsonArray data = result.getAsJsonArray("data");
                                    JsonObject jsonObject = data.get(0).getAsJsonObject();
                                    JsonObject group = jsonObject.getAsJsonObject("group");
                                    String group_id = group.get("group_id").getAsString();
                                    String image = "";
                                    if (!group.get("image").isJsonNull()) {
                                        image = group.get("image").getAsString();
                                    }
                                    setupFirebase(group_id, image);
                                    finish();
                                }

                            }
                        }
                    });
        } else {
            Log.i("filePath", "outside send image");
            Ion.with(getApplicationContext())
                    .load("POST", url)
                    .setMultipartParameter("chat_title", groupName)
                    .setMultipartParameter("city", city)
                    .setMultipartParameter("country", country)
                    .setMultipartParameter("category", selectedCategoryId)
                    .asJsonObject()
                    .setCallback(new FutureCallback<JsonObject>() {
                        @Override
                        public void onCompleted(Exception e, JsonObject result) {
                            progressDialog.dismiss();
                            if (result == null) {
                                e.printStackTrace();
                                showMessage("Error in creating group");
                            } else {
                                Log.i("result", result.toString());
                                if (result.get("status").getAsString().equalsIgnoreCase("success")) {
                                    JsonArray data = result.getAsJsonArray("data");
                                    JsonObject jsonObject = data.get(0).getAsJsonObject();
                                    String group_id = jsonObject.get("group_id").getAsString();
                                    String image = "";
                                    if (!jsonObject.get("image").isJsonNull()) {
                                        image = jsonObject.get("image").getAsString();
                                    }
                                    setupFirebase(group_id, image);
                                    showMessage("Group Created Successfully");
                                    finish();
                                }

                            }
                        }
                    });
        }

    }

    private void setupFirebase(final String group_id, final String image) {
        _user_group = FirebaseDatabase.getInstance().getReference().child("user_group");
        _user_group.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(userSessionManager.getUserId())) {
                    createUserChild(dataSnapshot, group_id, image);
                } else {
                    insertGrouptoUser(dataSnapshot, group_id, image);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    private void insertGrouptoUser(DataSnapshot dataSnapshot, final String group_id, final String image) {
        final DatabaseReference _user_id = _user_group.child(userSessionManager.getUserId());

        Map<String, Object> map = new HashMap<String, Object>();
        map.put(group_id, "");


        _user_id.updateChildren(map);
        _user_id.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                DatabaseReference _group_id = _user_id.child(group_id);
                Map<String, Object> map1 = new HashMap<String, Object>();
                map1.put("category", selectedCategoryString);
                map1.put("city", editTextCity.getText().toString());
                map1.put("country", textViewCountry.getText().toString());
                map1.put("user_id", userSessionManager.getUserId());
                map1.put("timestamp", ServerValue.TIMESTAMP);
                map1.put("image_url", image);
                map1.put("title", editTextGroupTitle.getText().toString());
                _group_id.updateChildren(map1);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void createUserChild(DataSnapshot dataSnapshot, final String group_id, final String image) {
        final HashMap<String, Object> user_id_map = new HashMap<String, Object>();
        user_id_map.put(userSessionManager.getUserId(), "");
        _user_group.updateChildren(user_id_map);
        _user_group.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                insertGrouptoUser(dataSnapshot, group_id, image);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

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

    private void setupCoutryPopUp() {
        final CountryPicker picker = CountryPicker.newInstance("Select Country");
        picker.show(getSupportFragmentManager(), "COUNTRY_PICKER");
        picker.setListener(new CountryPickerListener() {
            @Override
            public void onSelectCountry(String name, String code, String dialCode, int flagDrawableResID) {
                // Implement your code here
                textViewCountry.setText(name);
                picker.dismiss();

            }
        });
    }

    private void showMessage(String s) {
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }

    private void setupCategoryPopUp() {
        ArrayList<String> categoryNames = new ArrayList<>();
        for (int i = 0; i < categories.size(); i++) {
            categoryNames.add(categories.get(i).name);
        }
        new MaterialDialog.Builder(context)
                .title(R.string.choose_category)
                .titleColor(ContextCompat.getColor(context, R.color.colorPrimary))
                .items(categoryNames)
                .itemsColor(ContextCompat.getColor(context, R.color.colorPrimary))
                .itemsCallback(new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                        textViewCategory.setText(categories.get(which).name);
                        selectedCategoryId = categories.get(which).id;
                        selectedCategoryString = categories.get(which).name;
                    }
                })
                .show();
    }
}
