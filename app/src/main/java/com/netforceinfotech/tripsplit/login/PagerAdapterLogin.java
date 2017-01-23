package com.netforceinfotech.tripsplit.login;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class PagerAdapterLogin extends FragmentStatePagerAdapter {
    int mNumOfTabs;

    public PagerAdapterLogin(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                SignUp1Fragment home = new SignUp1Fragment();
                return home;
            case 1:
                SignUp2Fragment currentBet = new SignUp2Fragment();
                return currentBet;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}