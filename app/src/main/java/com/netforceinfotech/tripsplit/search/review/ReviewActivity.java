package com.netforceinfotech.tripsplit.search.review;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.Glide;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.hedgehog.ratingbar.RatingBar;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.netforceinfotech.tripsplit.R;
import com.netforceinfotech.tripsplit.profile.myprofile.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import de.hdodenhof.circleimageview.CircleImageView;

public class ReviewActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    private MaterialDialog progressDialog;
    TextView textViewNoReview, textViewDob, textviewName;
    ArrayList<MyData> myDatas = new ArrayList<>();
    private MyAdapter myAdapter;
    CircleImageView imageViewDp;
    private String dob;
    String image_url;
    RatingBar ratingbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);
        Bundle bundle = getIntent().getExtras();
        setupToolBar("Reviews");
        initView();
        setupRecyclerView();
        image_url = bundle.getString("image_url");
        String userId = bundle.getString("user_id");
        getUserInfo(userId);
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

    private void initView() {
        ratingbar = (RatingBar) findViewById(R.id.ratingbar);
        textviewName = (TextView) findViewById(R.id.textviewName);
        textViewDob = (TextView) findViewById(R.id.textViewDob);
        imageViewDp = (CircleImageView) findViewById(R.id.imageViewDp);
        textViewNoReview = (TextView) findViewById(R.id.textViewNoReview);

        progressDialog = new MaterialDialog.Builder(this)
                .title(R.string.progress_dialog)
                .content(R.string.please_wait)
                .progress(true, 0).build();
    }

    private void setupToolBar(String app_name) {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(app_name);


    }

    private void setupRecyclerView() {
        recyclerView = (RecyclerView) findViewById(R.id.recycler);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        myAdapter = new MyAdapter(this, myDatas);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(myAdapter);
        recyclerView.setNestedScrollingEnabled(false);

    }

    private void getUserInfo(String userId) {
//netforce.biz/tripesplit/mobileApp/api/services.php?opt=aboutme
        String url = getString(R.string.url) + "services.php?opt=aboutme";
        progressDialog.show();
        //services.php?opt=forgotpassword&email=dileep@netforceinfotech.com
        Log.i("kurl", url);
        Ion.with(this)
                .load(url)
                .setBodyParameter("user_id", userId)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                                 @Override
                                 public void onCompleted(Exception e, JsonObject result) {
                                     progressDialog.dismiss();
                                     if (result != null) {
                                         if (result.get("status").getAsString().equalsIgnoreCase("success")) {
                                             Log.i("result", result.toString());
                                             JsonObject data = result.getAsJsonObject("data");
                                             JsonArray user = data.getAsJsonArray("user");
                                             JsonObject object = user.get(0).getAsJsonObject();
                                             JsonArray review = data.getAsJsonArray("review");
                                             setupReviewData(review);
                                             setupUser(object);
                                             String no_of_trip = data.get("no_of_trip").getAsString();

                                         }


                                     } else {
                                         e.printStackTrace();
                                         showMessage("Something went wrong");
                                     }
                                 }
                             }

                );

    }

    private void setupUser(JsonObject object) {
        String user_id = object.get("id").getAsString();
        String firstname = object.get("firstname").getAsString();
        String dob = object.get("dob").getAsString();
        this.dob = dob;
        String address = object.get("address").getAsString();
        String country_code = object.get("country_code").getAsString();
        String aboutme = object.get("aboutme").getAsString();
        String created_date = object.get("created_date").getAsString();
        try {
            String rating = object.get("rating").getAsString();
            float ratingFloat = Float.parseFloat(rating);
            ratingbar.setStar(ratingFloat);
        } catch (Exception ex) {


        }
        textViewDob.setText(setAge(dob));
        textviewName.setText(firstname);
        Glide.with(this).load(image_url).error(R.drawable.ic_error).into(imageViewDp);
    }

    private String setAge(String dob) {
        if (dob.equalsIgnoreCase("0000-00-00")) {
            return "NA";
        } else {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            try {
                Calendar cal = Calendar.getInstance();
                cal.setTime(simpleDateFormat.parse(dob));// all done
                String age = getAge(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
                return age;
            } catch (ParseException e) {
                e.printStackTrace();
                return "NA";
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

    private void setupReviewData(JsonArray review) {
        int size = review.size();
        if (size <= 0) {
            textViewNoReview.setVisibility(View.VISIBLE);
            return;
        }
        textViewNoReview.setVisibility(View.GONE);
        Log.i("review", review.toString());
        for (int i = 0; i < size; i++) {
            JsonObject object = review.get(i).getAsJsonObject();
            String id = object.get("id").getAsString();
            String image_url = object.get("profile_image").getAsString();
            String name = object.get("name").getAsString();
            String date = object.get("created").getAsString();
            String review1 = object.get("comment").getAsString();
            float rating = object.get("rating").getAsFloat();
            MyData myData = new MyData(id, image_url, name, date, review1, rating);
            if (!myDatas.contains(myData)) {
                myDatas.add(myData);
            }
        }
        myAdapter.notifyDataSetChanged();
    }

    private void showMessage(String s) {
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }
}
