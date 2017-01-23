package com.netforceinfotech.tripsplit.tutorial;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.ViewGroup;

import com.afollestad.materialdialogs.MaterialDialog;
import com.netforceinfotech.tripsplit.dashboard.DashboardActivity;
import com.netforceinfotech.tripsplit.general.UserSessionManager;

import tyrantgit.explosionfield.ExplosionField;

public final class DefaultIntro extends BaseIntro {

    Bitmap icon;
    MaterialDialog dailog;
    ExplosionField mExplosionField;
    UserSessionManager userSessionManager;
    private String from = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            Bundle bundle = getIntent().getExtras();
            from = bundle.getString("from");

        } catch (Exception ex) {

        }
        userSessionManager = new UserSessionManager(getApplicationContext());
        AppIntrofirst appIntrofirst=new AppIntrofirst();
        AppIntroFourth appIntroFourth =new AppIntroFourth();
        AppIntroThird appIntroThird=new AppIntroThird();
        AppIntroSecond appIntroSecond =new AppIntroSecond();
        addSlide(appIntrofirst);
        addSlide(appIntroSecond);
        addSlide(appIntroThird);
        addSlide(appIntroFourth);

        showSkipButton(false);

        setSeparatorColor(Color.parseColor("#EE4039"));


    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);

/*
        mExplosionField = ExplosionField.attach2Window(this);

        mExplosionField.expandExplosionBound(200, 300);
*/

        // loadMainActivity();

        if (from.equalsIgnoreCase("menu")) {
            finish();
            return;
        }
        userSessionManager.setIsFirstTime(false);
        userSessionManager.setIsLoggedIn(true);
        Intent dashboard = new Intent(DefaultIntro.this, DashboardActivity.class);
        startActivity(dashboard);
        finish();


    }

    @Override
    public void onSkipPressed(Fragment currentFragment) {
        super.onSkipPressed(currentFragment);
        loadMainActivity();
        //Toast.makeText(getApplicationContext(), getString(R.string.skip), Toast.LENGTH_SHORT).show();
    }

    public void getStarted(View v) {
        loadMainActivity();
    }


    private void addListener(View root) {
        if (root instanceof ViewGroup) {
            ViewGroup parent = (ViewGroup) root;
            for (int i = 0; i < parent.getChildCount(); i++) {
                addListener(parent.getChildAt(i));
            }
        } else {
            root.setClickable(true);
            root.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mExplosionField.explode(v);
                    v.setOnClickListener(null);
                }
            });
        }
    }
}

