package com.netforceinfotech.tripsplit.posttrip;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.Glide;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.netforceinfotech.tripsplit.dashboard.NavigationFragment;
import com.netforceinfotech.tripsplit.home.HomeFragment;
import com.netforceinfotech.tripsplit.R;
import com.netforceinfotech.tripsplit.general.UserSessionManager;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import xyz.santeri.wvp.WrappingViewPager;

public class PostTripFragment extends Fragment {

    WrappingViewPager viewPager;
    Context context;
    private MaterialDialog progressDialog;
    UserSessionManager userSessionManager;
    ImageView imageViewDp, imageViewRating1, imageViewRating2, imageViewRating3, imageViewRating4, imageViewRating5;
    TextView textViewCountryCode, textViewName, textViewAge, textViewAddress, textViewDateCreated;
    private String countryCode = "";


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_profile, container, false);
        context = getActivity();
        userSessionManager = new UserSessionManager(context);
        NavigationFragment.POSITION = 5;
        initView(view);
        setuptoolbar();
        setupTab(view);
        getUserInfo();
        return view;

    }



    private void initView(View view) {
        progressDialog = new MaterialDialog.Builder(context)
                .title(R.string.progress_dialog)
                .content(R.string.please_wait)
                .progress(true, 0).build();
        imageViewDp = (ImageView) view.findViewById(R.id.imageViewDp);
        textViewCountryCode = (TextView) view.findViewById(R.id.textViewCountryCode);
        textViewName = (TextView) view.findViewById(R.id.textviewName);
        textViewAddress = (TextView) view.findViewById(R.id.textviewAddress);
        textViewAge = (TextView) view.findViewById(R.id.textviewAge);
        imageViewRating1 = (ImageView) view.findViewById(R.id.imageViewRating1);
        imageViewRating2 = (ImageView) view.findViewById(R.id.imageViewRating2);
        imageViewRating3 = (ImageView) view.findViewById(R.id.imageViewRating3);
        imageViewRating4 = (ImageView) view.findViewById(R.id.imageViewRating4);
        imageViewRating5 = (ImageView) view.findViewById(R.id.imageViewRating5);
        textViewDateCreated = (TextView) view.findViewById(R.id.textViewDateCreated);
    }

    private void getUserInfo() {
        String baseUrl = getString(R.string.url);
        String viewProfile = "services.php?opt=viewprofile&user_id=" + userSessionManager.getUserId();
        String url = baseUrl + viewProfile;
        Log.i("url", url);
        Ion.with(context)
                .load(url)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        progressDialog.dismiss();
                        // do stuff with the result or error
                        if (result != null) {
                            Log.i("kresult", result.toString());
                            setupUserData(result);
                        } else {
                            showMessage("Something went wrong");
                        }
                    }
                });
    }

    private void showMessage(String s) {
        Toast.makeText(context, s, Toast.LENGTH_SHORT).show();
    }

    private void setupUserData(JsonObject result) {
        if (result.get("status").getAsString().equalsIgnoreCase("success")) {
            JsonArray data = result.getAsJsonArray("data");
            String name = "", email = "", profile_image = "", dob = "", country = "", address = "";
            JsonObject jsonObject = data.get(0).getAsJsonObject();
            if (!jsonObject.get("firstname").isJsonNull()) {
                name = jsonObject.get("firstname").getAsString();
            }
            if (!jsonObject.get("email").isJsonNull()) {
                email = jsonObject.get("email").getAsString();
            }
            if (!jsonObject.get("profile_image").isJsonNull()) {
                profile_image = jsonObject.get("profile_image").getAsString();
            }
            if (!jsonObject.get("dob").isJsonNull()) {
                dob = jsonObject.get("dob").getAsString();
            }
            if (!jsonObject.get("country").isJsonNull()) {
                country = jsonObject.get("country").getAsString();
            }
            if (!jsonObject.get("country_code").isJsonNull()) {
                countryCode = jsonObject.get("country_code").getAsString();
            }
            if (!jsonObject.get("address").isJsonNull()) {
                address = jsonObject.get("address").getAsString();
            }

            try {
                Glide.with(context).load(profile_image).error(R.drawable.ic_error).into(imageViewDp);
            } catch (Exception ex) {
                showMessage("some error in pic");
                ex.printStackTrace();
            }
            try {
                String rating = jsonObject.get("rating").getAsString();
                float ratingFloat = Float.parseFloat(rating);
                setupRatingImage(ratingFloat);
            } catch (Exception ex) {
                setupRatingImage(3);

            }
            textViewName.setText(name);
            String created_date = jsonObject.get("created_date").getAsString();

            textViewDateCreated.setText(created_date);
            String fortmatedDate = getFormattedDate(dob);
            textViewAddress.setText(address);
            textViewCountryCode.setText(countryCode);
            setAge(dob);

        }
    }

    private void setupRatingImage(float ratingFloat) {
        int ratingInt = (int) ratingFloat;
        float ramainingFloat = ratingFloat -ratingInt;
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

    private void setAge(String dob) {
        if (dob.equalsIgnoreCase("0000-00-00")) {
            textViewAge.setText("NA");
        } else {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            try {
                Calendar cal = Calendar.getInstance();
                cal.setTime(simpleDateFormat.parse(dob));// all done
                String age = getAge(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
                textViewAge.setText(age);
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

    private void setuptoolbar() {
        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        ImageView home = (ImageView) toolbar.findViewById(R.id.homeButton);
        ImageView icon = (ImageView) toolbar.findViewById(R.id.image_appicon);
        TextView textViewLogout = (TextView) toolbar.findViewById(R.id.textviewLogout);
        textViewLogout.setVisibility(View.GONE);
        home.setVisibility(View.VISIBLE);
        icon.setVisibility(View.VISIBLE);
        toolbar.setBackgroundColor(ContextCompat.getColor(context, R.color.colorPrimary));
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setupHomeFragment();
            }
        });
    }

    private void replaceFragment(Fragment newFragment, String tag) {
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame, newFragment, tag);
        transaction.commit();
    }

    public void setupHomeFragment() {
        HomeFragment dashboardFragment = new HomeFragment();
        String tagName = dashboardFragment.getClass().getName();
        replaceFragment(dashboardFragment, tagName);
    }

    private void setupTab(View v) {

        viewPager = (WrappingViewPager) v.findViewById(R.id.pager);

        TabLayout tabLayout = (TabLayout) v.findViewById(R.id.tab_layout);

        tabLayout.addTab(tabLayout.newTab().setIcon(ContextCompat.getDrawable(getActivity(), R.drawable.ic_plane)));
        tabLayout.addTab(tabLayout.newTab().setIcon(ContextCompat.getDrawable(getActivity(), R.drawable.ic_car)));
        tabLayout.addTab(tabLayout.newTab().setIcon(ContextCompat.getDrawable(getActivity(), R.drawable.ic_bus)));
        tabLayout.addTab(tabLayout.newTab().setIcon(ContextCompat.getDrawable(getActivity(), R.drawable.ic_build)));

        final int tabIconColor = ContextCompat.getColor(getActivity(), R.color.black);
        final int tabIconSelectedColor = ContextCompat.getColor(getActivity(), R.color.red);


        tabLayout.getTabAt(0).getIcon().setColorFilter(tabIconSelectedColor, PorterDuff.Mode.SRC_IN);
        tabLayout.getTabAt(1).getIcon().setColorFilter(tabIconColor, PorterDuff.Mode.SRC_IN);
        tabLayout.getTabAt(2).getIcon().setColorFilter(tabIconColor, PorterDuff.Mode.SRC_IN);
        tabLayout.getTabAt(3).getIcon().setColorFilter(tabIconColor, PorterDuff.Mode.SRC_IN);

        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        final PagerAdapter adapter = new PagerAdapter(getChildFragmentManager(), tabLayout.getTabCount());

        viewPager.setAdapter(adapter);

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        viewPager.setCurrentItem(0);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {

            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
                tab.getIcon().setColorFilter(tabIconSelectedColor, PorterDuff.Mode.SRC_IN);

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                tab.getIcon().setColorFilter(tabIconColor, PorterDuff.Mode.SRC_IN);

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case android.R.id.home:
                ///  finish();

                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return super.onOptionsItemSelected(item);
    }


}
