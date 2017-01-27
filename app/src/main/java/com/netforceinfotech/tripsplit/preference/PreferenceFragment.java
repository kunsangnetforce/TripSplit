package com.netforceinfotech.tripsplit.preference;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.Glide;
import com.facebook.login.LoginManager;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.hedgehog.ratingbar.RatingBar;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.kyleduo.switchbutton.SwitchButton;
import com.netforceinfotech.tripsplit.dashboard.DashboardActivity;
import com.netforceinfotech.tripsplit.home.HomeFragment;
import com.netforceinfotech.tripsplit.R;
import com.netforceinfotech.tripsplit.general.UserSessionManager;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 */
public class PreferenceFragment extends Fragment implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {
    LinearLayout linearlayoutDone, linearLayoutDontLike, linearLayoutIrrelevant;
    RadioButton radioDone, radioDontlike, radioIrrelevant;
    ImageView imageViewDone, imageViewDontlike, imageViewIrrelevant;

    String TAG = "klog";
    Context context;
    SeekBar seekBar;

    TextView textViewRange;
    UserSessionManager userSessionManager;
    int range;
    Button buttonDeleteAccount;
    private MaterialDialog progressDialog;
    SwitchButton switchButtonLogout, switchbuttonLoop, switchbuttonMessage, switchbuttonEmail, switchbuttonVibration;
    private Intent intent;
    private float reviewRating = -1;
    private String deleteReview = "";

    public PreferenceFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_preference, container, false);
        context = getActivity();
        userSessionManager = new UserSessionManager(context);
        initview(view);
        setuptoolbar();
        return view;
    }

    private void initview(View view) {
        progressDialog = new MaterialDialog.Builder(getActivity())
                .title(R.string.progress_dialog)
                .content(R.string.please_wait)
                .progress(true, 0).build();
        view.findViewById(R.id.relativeLayoutShare).setOnClickListener(this);
        view.findViewById(R.id.relativeLayoutTnC).setOnClickListener(this);
        view.findViewById(R.id.relativeLayoutPrivacyPolicy).setOnClickListener(this);
        switchbuttonEmail = (SwitchButton) view.findViewById(R.id.switchbuttonEmail);
        switchbuttonLoop = (SwitchButton) view.findViewById(R.id.switchbuttonLoop);
        switchbuttonMessage = (SwitchButton) view.findViewById(R.id.switchbuttonMessage);
        switchbuttonVibration = (SwitchButton) view.findViewById(R.id.switchbuttonVibration);
        switchbuttonEmail.setOnCheckedChangeListener(this);
        switchbuttonLoop.setOnCheckedChangeListener(this);
        switchbuttonMessage.setOnCheckedChangeListener(this);
        switchbuttonVibration.setOnCheckedChangeListener(this);
        switchbuttonEmail.setChecked(userSessionManager.getEmailNotification());
        switchbuttonLoop.setChecked(userSessionManager.getKeepMeInLoop());
        switchbuttonVibration.setChecked(userSessionManager.getInAppVibration());
        switchbuttonMessage.setChecked(userSessionManager.getMessageNotification());
        switchButtonLogout = (SwitchButton) view.findViewById(R.id.switchbuttonLogout);
        switchButtonLogout.setChecked(true);
        switchButtonLogout.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (!b) {
                    showLogoutpopup();
                }
            }
        });


        range = userSessionManager.getSearchRadius();
        buttonDeleteAccount = (Button) view.findViewById(R.id.buttonDeleteAccount);
        buttonDeleteAccount.setOnClickListener(this);
        textViewRange = (TextView) view.findViewById(R.id.textviewRange);
        seekBar = (SeekBar) view.findViewById(R.id.seekbar);
        seekBar.setProgress(0); // call these two methods before setting progress.
        seekBar.setMax(11);
        if (range == 0) {
            textViewRange.setText("Anywhere");
        } else {
            textViewRange.setText(range + " km");
        }
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                switch (i) {
                    case 0:
                        range = 1;
                        break;
                    case 1:
                        range = 5;
                        break;
                    case 2:
                        range = 10;
                        break;
                    case 3:
                        range = 20;
                        break;
                    case 4:
                        range = 30;
                        break;
                    case 5:
                        range = 40;
                        break;
                    case 6:
                        range = 50;
                        break;
                    case 7:
                        range = 100;
                        break;
                    case 8:
                        range = 200;
                        break;
                    case 9:
                        range = 300;
                        break;
                    case 10:
                        range = 500;
                        break;
                    case 11:
                        range = 1000;
                        break;
                    case 12:
                        range = 0;
                        break;
                }
                if (range == 0) {
                    textViewRange.setText("Anywhere");
                } else {
                    textViewRange.setText(range + " km");
                }
                userSessionManager.setSearchRaius(range);

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        userSessionManager.setSearchRaius(range);
        switch (userSessionManager.getSearchRadius()) {
            case 0:
                seekBar.setProgress(12);
                break;
            case 1:
                seekBar.setProgress(0);
                break;
            case 5:
                seekBar.setProgress(1);
                break;
            case 10:
                seekBar.setProgress(2);
                break;
            case 20:
                seekBar.setProgress(3);
                break;
            case 30:
                seekBar.setProgress(4);
                break;
            case 40:
                seekBar.setProgress(5);
                break;
            case 50:
                seekBar.setProgress(6);
                break;
            case 100:
                seekBar.setProgress(7);
                break;
            case 200:
                seekBar.setProgress(8);
                break;
            case 300:
                seekBar.setProgress(9);
                break;
            case 500:
                seekBar.setProgress(10);
                break;
            case 1000:
                seekBar.setProgress(11);
                break;

        }
    }

    private void logout() {
        userSessionManager.setIsLoggedIn(false);
        userSessionManager.clearData();
        try {
            LoginManager.getInstance().logOut();
            getActivity().finish();
        } catch (Exception ex) {

        }
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

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.buttonDeleteAccount:
                showDeleteAccountPopUp();
                break;
            case R.id.relativeLayoutPrivacyPolicy:
                intent = new Intent(context, PrivacyPolicyActivity.class);
                startActivity(intent);
                break;
            case R.id.relativeLayoutTnC:
                intent = new Intent(context, TermsAndConditionActivity.class);
                startActivity(intent);
                break;
            case R.id.relativeLayoutShare:
                shareData();
                break;
            case R.id.linearlayoutIrrelevant:
                radioIrrelevant.performClick();
                break;
            case R.id.linearlayoutDone:
                radioDone.performClick();
                break;

            case R.id.linearlayoutDidnotLike:
                radioDontlike.performClick();
                break;
        }
    }

    private void shareData() {
        String shareBody = getString(R.string.download_tripsplit);
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
        sendIntent.setType("text/plain");
        startActivity(Intent.createChooser(sendIntent, getResources().getText(R.string.shareit)));
    }

    private void deleteAccount() {
        progressDialog.show();
        //services.php?opt=deleteprofile&user_id=11
        String url = getResources().getString(R.string.url);
        String pushurl = "services.php?opt=deleteprofile&user_id=" + userSessionManager.getUserId();
        Log.i(TAG, pushurl);
        Ion.with(context)
                .load(url + pushurl)
                .setBodyParameter("review", deleteReview)
                .setBodyParameter("rating", reviewRating + "")
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        progressDialog.dismiss();
                        if (result == null) {
                            showMessage("Sorry!!! Something went wrong");
                            Log.i(TAG, "not sending");
                        } else {

                            String status = result.get("status").getAsString().toLowerCase();
                            if (status.equalsIgnoreCase("success")) {
                                Log.i(TAG, "successfully Deleted");
                                userSessionManager.setIsLoggedIn(false);
                                showMessage("Account Successfully Deleted");
                                try {
                                    LoginManager.getInstance().logOut();
                                    getActivity().finish();
                                } catch (Exception ex) {

                                }
                                getActivity().finish();

                            }
                        }

                    }
                });
    }

    private void showMessage(String s) {
        Toast.makeText(context, s, Toast.LENGTH_SHORT).show();
    }

    private void showLogoutpopup() {
        new MaterialDialog.Builder(context)
                .title("Log out")
                .content("Are you sure you want to Log out?")
                .positiveText("Yes")
                .negativeText("Cancel")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        logout();
                    }
                })
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();
                        switchButtonLogout.setChecked(true);
                    }
                })
                .show();
    }


    private void showDeleteAccountPopUp() {
        final MaterialDialog deleteAccount = new MaterialDialog.Builder(getActivity())
                .customView(R.layout.delete_account, false)
                .show();

        deleteAccount.setCanceledOnTouchOutside(false);
        RatingBar ratingBar = (RatingBar) deleteAccount.findViewById(R.id.ratingbar);
        ratingBar.setOnRatingChangeListener(new RatingBar.OnRatingChangeListener() {
            @Override
            public void onRatingChange(float RatingCount) {
                reviewRating = RatingCount;
            }
        });
        imageViewDone = (ImageView) deleteAccount.findViewById(R.id.imageViewDone);
        imageViewDontlike = (ImageView) deleteAccount.findViewById(R.id.imageViewDontlike);
        imageViewIrrelevant = (ImageView) deleteAccount.findViewById(R.id.imageViewIrrelevant);
        radioDone = (RadioButton) deleteAccount.findViewById(R.id.radioDone);
        radioDontlike = (RadioButton) deleteAccount.findViewById(R.id.radioDidnotLike);
        radioIrrelevant = (RadioButton) deleteAccount.findViewById(R.id.radioIrrelevent);
        radioDone.setOnCheckedChangeListener(this);
        radioDontlike.setOnCheckedChangeListener(this);
        radioIrrelevant.setOnCheckedChangeListener(this);
        linearlayoutDone = (LinearLayout) deleteAccount.findViewById(R.id.linearlayoutDone);
        linearLayoutDontLike = (LinearLayout) deleteAccount.findViewById(R.id.linearlayoutDidnotLike);
        linearLayoutIrrelevant = (LinearLayout) deleteAccount.findViewById(R.id.linearlayoutIrrelevant);
        linearlayoutDone.setOnClickListener(this);
        linearLayoutDontLike.setOnClickListener(this);
        linearLayoutIrrelevant.setOnClickListener(this);
        linearlayoutDone.performClick();
        deleteAccount.findViewById(R.id.buttonSubmit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (reviewRating == -1) {
                    showMessage("Kindly rate the app!");
                    return;
                }
                deleteAccount();
                deleteAccount.dismiss();
            }
        });
        deleteAccount.findViewById(R.id.buttonCancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteAccount.dismiss();
            }
        });
      /*  new MaterialDialog.Builder(context)
                .title("Delete Account")
                .content("Are you sure you want to Delete Account?")
                .positiveText("Yes")
                .negativeText("Cancel")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        deleteAccount();
                    }
                })
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();
                    }
                })
                .show();*/
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        switch (compoundButton.getId()) {
            case R.id.switchbuttonVibration:
                if (b) {
                    updatePreference("1", "appvibration");
                } else {
                    updatePreference("0", "appvibration");
                }
                break;
            case R.id.switchbuttonEmail:
                if (b) {
                    updatePreference("1", "keepmail");
                } else {
                    updatePreference("0", "keepmail");
                }

                break;
            case R.id.switchbuttonMessage:
                if (b) {
                    updatePreference("1", "keepmessage");
                } else {
                    updatePreference("0", "keepmessage");
                }
                break;

            case R.id.switchbuttonLoop:
                if (b) {
                    updatePreference("1", "keeploop");
                } else {
                    updatePreference("0", "keeploop");
                }
                break;

            case R.id.radioDone:
                if (b) {
                    imageViewDone.setImageResource(R.drawable.ic_radio_checked);
                    imageViewDontlike.setImageResource(R.drawable.ic_radio_uncheck);
                    imageViewIrrelevant.setImageResource(R.drawable.ic_radio_uncheck);
                    deleteReview = getString(R.string.appdone);
                }
                break;
            case R.id.radioDidnotLike:
                if (b) {
                    imageViewDone.setImageResource(R.drawable.ic_radio_uncheck);
                    imageViewDontlike.setImageResource(R.drawable.ic_radio_checked);
                    imageViewIrrelevant.setImageResource(R.drawable.ic_radio_uncheck);
                    deleteReview = getString(R.string.dontlike);
                }
                break;
            case R.id.radioIrrelevent:
                if (b) {
                    imageViewDone.setImageResource(R.drawable.ic_radio_uncheck);
                    imageViewDontlike.setImageResource(R.drawable.ic_radio_uncheck);
                    imageViewIrrelevant.setImageResource(R.drawable.ic_radio_checked);
                    deleteReview = getString(R.string.irrelevant);
                }
                break;
        }
    }

    private void updatePreference(String status, final String option) {
        //  http://netforce.biz/tripesplit/mobileApp/api/services.php?opt=keeploop&user_id=1&status=1
        progressDialog.show();
        String baseUrl = getString(R.string.url);
        String url = baseUrl + "services.php?opt=" + option;
        Ion.with(context)
                .load("POST", url)
                .setBodyParameter("user_id", userSessionManager.getUserId())
                .setBodyParameter("status", status)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        progressDialog.dismiss();
                        // do stuff with the result or error
                        if (result == null) {
                        } else {
                            if (result.get("status").getAsString().equalsIgnoreCase("success")) {
                                JsonArray data=result.getAsJsonArray("data");
                                JsonObject jsonObject=data.get(0).getAsJsonObject();
                                switch (option){
                                    case "keeploop":
                                        String keeploop=jsonObject.get("keeploop").getAsString();
                                        if(keeploop.equalsIgnoreCase("0")){
                                            userSessionManager.setKeepMeInLoop(false);
                                        }else {
                                            userSessionManager.setKeepMeInLoop(true);
                                        }
                                        break;
                                    case "keepmail":
                                        String keepmail=jsonObject.get("keepmail").getAsString();
                                        if(keepmail.equalsIgnoreCase("0")){
                                            userSessionManager.setEmailNotification(false);
                                        }else {
                                            userSessionManager.setEmailNotification(true);
                                        }
                                        break;
                                    case "keepmessage":
                                        String keepmessage=jsonObject.get("keepmessage").getAsString();
                                        if(keepmessage.equalsIgnoreCase("0")){
                                            userSessionManager.setMessageNotification(false);
                                        }else {
                                            userSessionManager.setMessageNotification(true);
                                        }
                                        break;
                                    case "appvibration":
                                        String appvibration=jsonObject.get("appvibration").getAsString();
                                        if(appvibration.equalsIgnoreCase("0")){
                                            userSessionManager.setInAppVibration(false);
                                        }else {
                                            userSessionManager.setInAppVibration(true);
                                        }
                                        break;
                                }
                            } else {
                                showMessage("Something went wrong");
                            }
                            Log.i("result", result.toString());
                        }
                    }
                });
    }
}
