package com.netforceinfotech.tripsplit.profile.myprofile;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.netforceinfotech.tripsplit.general.UserSessionManager;
import com.netforceinfotech.tripsplit.message.message_detail.MessageDetailActivity;
import com.netforceinfotech.tripsplit.profile.myprofile.triplist.TripListActivity;
import com.netforceinfotech.tripsplit.profile.sendmail.SendMailActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import de.hdodenhof.circleimageview.CircleImageView;

public class MyProfileActivity extends AppCompatActivity implements View.OnClickListener {

    String userId;
    Context context;
    UserSessionManager userSessionManager;
    private RecyclerView recyclerView;
    private MyAdapter myAdapter;
    ArrayList<MyData> myDatas = new ArrayList<>();
    private float reviewRating = -1;
    String userName, dob = "0000-00-00", image_url;
    Button buttonWriteReview, buttonSeeMore;
    private MaterialDialog progressDialog;
    ImageView imageViewTrip, imageViewEmail, imageViewMessage, imageViewRating1, imageViewRating2, imageViewRating3, imageViewRating4, imageViewRating5;
    CircleImageView imageViewDp;
    TextView textViewCountryCode, textViewName, textViewAge, textViewAddress, textViewAboutMe, textViewNoOfTrip, textViewDateCreated, textViewNoReview;
    private String writer_id = "";
    private Intent intent;
    Bundle bundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);
        Bundle bundle = getIntent().getExtras();
        context = this;
        userSessionManager = new UserSessionManager(context);
        userName = bundle.getString("name");
        userId = bundle.getString("user_id");
        image_url = bundle.getString("image_url");
        try {
            dob = bundle.getString("dob");
        } catch (Exception ex) {
            dob = "0000-00-00";

        }
        setupToolBar(userName);

        setupRecyclerView();
        initView();
        if (userId.equalsIgnoreCase(userSessionManager.getUserId())) {
            buttonWriteReview.setVisibility(View.GONE);
        }
        getUserInfo(userId);
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
                                            /**/
                                             setupUser(object);
                                             String no_of_trip = data.get("no_of_trip").getAsString();
                                             textViewNoOfTrip.setText(no_of_trip + " Trips");
                                         }


                                     } else {
                                         e.printStackTrace();
                                         showMessage("Something went wrong");
                                     }
                                 }
                             }

                );

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

    private void setupUser(JsonObject object) {
        String user_id = object.get("id").getAsString();
        this.writer_id = user_id;
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
            setupRatingImage(ratingFloat);
        } catch (Exception ex) {
            setupRatingImage(4);

        }
        textViewAboutMe.setText(aboutme);
        textViewAddress.setText(address);
        textViewAge.setText(setAge(dob));
        textViewCountryCode.setText(country_code);
        textViewName.setText(firstname);
        textViewDateCreated.setText(created_date);
        Glide.with(context).load(image_url).into(imageViewDp);
        Glide.with(context).load(image_url).into(imageViewTrip);
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

    private void initView() {
        imageViewRating1 = (ImageView) findViewById(R.id.imageViewRating1);
        imageViewRating2 = (ImageView) findViewById(R.id.imageViewRating2);
        imageViewRating3 = (ImageView) findViewById(R.id.imageViewRating3);
        imageViewRating4 = (ImageView) findViewById(R.id.imageViewRating4);
        imageViewRating5 = (ImageView) findViewById(R.id.imageViewRating5);
        textViewNoReview = (TextView) findViewById(R.id.textViewNoReview);
        imageViewDp = (CircleImageView) findViewById(R.id.imageViewDp);
        textViewDateCreated = (TextView) findViewById(R.id.textViewDateCreated);
        buttonSeeMore = (Button) findViewById(R.id.buttonSeeMore);
        buttonSeeMore.setOnClickListener(this);
        textViewNoOfTrip = (TextView) findViewById(R.id.textViewNoOfTrip);
        textViewAboutMe = (TextView) findViewById(R.id.textViewAboutMe);
        imageViewMessage = (ImageView) findViewById(R.id.imageViewMessage);
        imageViewEmail = (ImageView) findViewById(R.id.imageViewEmail);
        imageViewEmail.setOnClickListener(this);
        imageViewMessage.setOnClickListener(this);
        textViewAddress = (TextView) findViewById(R.id.textViewAddress);
        textViewAge = (TextView) findViewById(R.id.textViewAge);
        textViewName = (TextView) findViewById(R.id.textViewName);
        textViewCountryCode = (TextView) findViewById(R.id.textViewCountryCode);
        imageViewTrip = (ImageView) findViewById(R.id.imageViewTrip);
        progressDialog = new MaterialDialog.Builder(this)
                .title(R.string.progress_dialog)
                .content(R.string.please_wait)
                .progress(true, 0).build();
        buttonWriteReview = (Button) findViewById(R.id.buttonWriteReview);
        buttonWriteReview.setOnClickListener(this);
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
        recyclerView.setNestedScrollingEnabled(false);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        myAdapter = new MyAdapter(context, myDatas);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(myAdapter);
        recyclerView.smoothScrollToPosition(myDatas.size());
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.imageViewEmail:
                if (userId.equalsIgnoreCase(userSessionManager.getUserId())) {
                    showMessage("Cannot email to self...");
                    return;
                }
                intent = new Intent(context, SendMailActivity.class);
                bundle = new Bundle();
                bundle.putString("id", userId);
                bundle.putString("name", userName);
                bundle.putString("image_url", image_url);
                bundle.putString("reg_id", "");
                intent.putExtras(bundle);
                startActivity(intent);
                break;
            case R.id.imageViewMessage:
                intent = new Intent(context, MessageDetailActivity.class);
                bundle = new Bundle();
                bundle.putString("name", userName);
                bundle.putString("image_url", image_url);
                bundle.putString("id", userId);
                intent.putExtras(bundle);
                context.startActivity(intent);
                break;

            case R.id.buttonWriteReview:
                showReviewPopUp(userName, dob, image_url);
                break;
            case R.id.buttonSeeMore:
                intent = new Intent(context, TripListActivity.class);
                bundle = new Bundle();
                bundle.putString("id", userId);
                intent.putExtras(bundle);
                startActivity(intent);
                break;

        }
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

    private void showReviewPopUp(String name, String dob, String imageUrl) {
        final RatingBar ratingBar;

        final MaterialDialog reviewBox = new MaterialDialog.Builder(this)
                .customView(R.layout.review_pop_up1, false)
                .show();

        reviewBox.setCanceledOnTouchOutside(false);

        reviewBox.findViewById(R.id.imageViewClose).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reviewBox.clearSelectedIndices();
            }
        });
        final EditText editText = (EditText) reviewBox.findViewById(R.id.editText);
        CircleImageView imageViewDp = (CircleImageView) reviewBox.findViewById(R.id.imageViewDp);
        TextView textViewName, textViewDob, textViewLable;
        textViewLable = (TextView) reviewBox.findViewById(R.id.textViewLable);
        textViewLable.setText("Rate the User");
        textViewDob = (TextView) reviewBox.findViewById(R.id.textViewDob);
        textViewName = (TextView) reviewBox.findViewById(R.id.textViewName);
        ratingBar = (RatingBar) reviewBox.findViewById(R.id.ratingbar);
        Glide.with(getApplicationContext()).load(imageUrl).error(R.drawable.ic_error).into(imageViewDp);
        textViewDob.setText(setAge(dob));
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
                writeReview(reviewRating, editText.getText().toString());
                reviewBox.dismiss();
            }
        });
    }

    private void writeReview(float reviewRating, String s) {
        //http://netforce.biz/tripesplit/mobileApp/api/services.php?opt=profile_review
        String url = getString(R.string.url) + "services.php?opt=profile_review";
        progressDialog.show();
        //services.php?opt=forgotpassword&email=dileep@netforceinfotech.com
        Log.i("kurl", url);
        Ion.with(this)
                .load(url)
                .setBodyParameter("writer_id", userSessionManager.getUserId())
                .setBodyParameter("user_id", writer_id)
                .setBodyParameter("comment", s)
                .setBodyParameter("rating", reviewRating + "")
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                                 @Override
                                 public void onCompleted(Exception e, JsonObject result) {
                                     progressDialog.dismiss();
                                     if (result != null) {
                                         Log.i("result", result.toString());
                                         if (result.get("status").getAsString().equalsIgnoreCase("success")) {
                                             showMessage("Successfully reviewed");
                                         }

                                     } else {
                                         e.printStackTrace();
                                         showMessage("Something went wrong");
                                     }
                                 }
                             }

                );
    }

    private void showMessage(String s) {
        Toast.makeText(context, s, Toast.LENGTH_SHORT).show();
    }

    private String getFormattedDob(String dob) {
        return dob;
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
}
