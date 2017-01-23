package com.netforceinfotech.tripsplit.search;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.netforceinfotech.tripsplit.search.searchfragment.SearchFragment;

/**
 * Created by John on 8/29/2016.
 */

public class SearchPagerAdapter extends FragmentStatePagerAdapter {


    int mNumOfTabs;
    private SearchFragment aeriplaneFragment, carFragment, busFragment, shipFragment;
    public int currentpage = 0;
    private Bundle bundle;

    public SearchPagerAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }


    @Override
    public Fragment getItem(int position) {
        switch (position) {

            case 0:
                aeriplaneFragment = new SearchFragment();
                bundle = new Bundle();
                bundle.putString("type", "aeroplane");
                aeriplaneFragment.setArguments(bundle);
                return aeriplaneFragment;
            case 1:
                carFragment = new SearchFragment();
                bundle = new Bundle();
                bundle.putString("type", "car");
                carFragment.setArguments(bundle);
                return carFragment;
            case 2:
                busFragment = new SearchFragment();
                bundle = new Bundle();
                bundle.putString("type", "bus");
                busFragment.setArguments(bundle);
                return busFragment;
            case 3:
                shipFragment = new SearchFragment();
                bundle = new Bundle();
                bundle.putString("type", "ship");
                shipFragment.setArguments(bundle);
                return shipFragment;
            default:
                return null;

        }

    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }


}