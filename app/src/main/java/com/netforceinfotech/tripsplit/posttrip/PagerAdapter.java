package com.netforceinfotech.tripsplit.posttrip;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.ViewGroup;

import xyz.santeri.wvp.WrappingViewPager;

public class PagerAdapter extends FragmentStatePagerAdapter {
    int mNumOfTabs;
    private int mCurrentPosition = -1; // Keep track of the current position

    private TypeFragment aeroplaneFragment, carFragment, busFragment, shipFragment;
    Bundle bundle;

    public PagerAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }

    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        super.setPrimaryItem(container, position, object);

        if (!(container instanceof WrappingViewPager)) {
            return; // Do nothing if it's not a compatible ViewPager
        }

        if (position != mCurrentPosition) { // If the position has changed, tell WrappingViewPager
            Fragment fragment = (Fragment) object;
            WrappingViewPager pager = (WrappingViewPager) container;
            if (fragment != null && fragment.getView() != null) {
                mCurrentPosition = position;
                pager.onPageChanged(fragment.getView());
            }
        }
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                aeroplaneFragment = new TypeFragment();
                bundle = new Bundle();
                bundle.putString("type", "aeroplane");//aeroplane,car,bus,ship
                aeroplaneFragment.setArguments(bundle);
                return aeroplaneFragment;
            case 1:
                carFragment = new TypeFragment();
                bundle = new Bundle();
                bundle.putString("type", "car");
                carFragment.setArguments(bundle);
                return carFragment;
            case 2:
                busFragment = new TypeFragment();
                bundle = new Bundle();
                bundle.putString("type", "bus");
                busFragment.setArguments(bundle);
                return busFragment;
            case 3:
                shipFragment = new TypeFragment();
                bundle = new Bundle();
                bundle.putString("type", "ship");
                shipFragment.setArguments(bundle);
                return shipFragment;

            default:
                carFragment = new TypeFragment();
                bundle.putString("type", "car");
                carFragment.setArguments(bundle);
                return carFragment;
        }

    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}