package com.netforceinfotech.tripsplit.preference;


import android.content.Context;
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
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.facebook.login.LoginManager;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.kyleduo.switchbutton.SwitchButton;
import com.netforceinfotech.tripsplit.home.HomeFragment;
import com.netforceinfotech.tripsplit.R;
import com.netforceinfotech.tripsplit.general.UserSessionManager;

/**
 * A simple {@link Fragment} subclass.
 */
public class PreferenceFragment extends Fragment implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    String TAG = "klog";
    Context context;
    SeekBar seekBar;

    TextView textViewRange;
    UserSessionManager userSessionManager;
    int range;
    Button buttonDeleteAccount;
    private MaterialDialog progressDialog;
    SwitchButton switchButtonLogout, switchbuttonLoop, switchbuttonMessage, switchbuttonEmail, switchbuttonVibration;

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

        progressDialog = new MaterialDialog.Builder(getActivity())
                .title(R.string.progress_dialog)
                .content(R.string.please_wait)
                .progress(true, 0).build();
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
        }
    }

    private void deleteAccount() {
        progressDialog.show();
        //services.php?opt=deleteprofile&user_id=11
        String url = getResources().getString(R.string.url);
        String pushurl = "services.php?opt=deleteprofile&user_id=" + userSessionManager.getUserId();
        Log.i(TAG, pushurl);
        Ion.with(context)
                .load(url + pushurl)
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
        new MaterialDialog.Builder(context)
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
                .show();
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        switch (compoundButton.getId()) {
            case R.id.switchbuttonVibration:
                userSessionManager.setInAppVibration(b);
                break;
            case R.id.switchbuttonEmail:
                userSessionManager.setEmailNotification(b);
                break;
            case R.id.switchbuttonMessage:
                userSessionManager.setMessageNotification(b);
                break;
            case R.id.switchbuttonLoop:
                userSessionManager.setKeepMeInLoop(b);
                break;
        }
    }
}
