package com.netforceinfotech.tripsplit.general;

import android.app.Application;

/**
 * Created by Netforce on 10/25/2016.
 */

public class TripSplitz extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        TypefaceUtil.overrideFont(getApplicationContext(), "SERIF", "fonts/GothamRoundedBook.ttf");
        // TypefaceUtil.overrideFont(getApplicationContext(), "SERIF", "fonts/Pacifico.ttf");
    }
}
