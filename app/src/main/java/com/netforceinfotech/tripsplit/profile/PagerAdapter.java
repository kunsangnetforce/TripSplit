package com.netforceinfotech.tripsplit.profile;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.netforceinfotech.tripsplit.profile.flight.FlightFragment;


public class PagerAdapter extends FragmentStatePagerAdapter {
    int mNumOfTabs;
    private FlightFragment flight;

    public PagerAdapter(FragmentManager fm, int NumOfTabs)
    {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }



    @Override
    public Fragment getItem(int position) {

        switch (position)
        {
            case 0:
                flight = new FlightFragment();
                return flight;
            case 1:
                flight = new FlightFragment();
                return flight;
            default:
                flight = new FlightFragment();
                return flight;
        }

    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}