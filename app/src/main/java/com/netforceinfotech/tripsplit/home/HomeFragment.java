package com.netforceinfotech.tripsplit.home;


import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.BitmapEncoder;
import com.netforceinfotech.tripsplit.R;
import com.netforceinfotech.tripsplit.search.SearchSplitFragment;
import com.netforceinfotech.tripsplit.posttrip.PostTripFragment;


public class HomeFragment extends Fragment {
    Context context;
    ImageView post_trip, search_trip, imageViewLogo;
    //RecyclerAdapterDrawer adapterDrawer;

    public HomeFragment() {
        // this.adapterDrawer = adapter;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.activity_home_content, container, false);
        context = getActivity();
        setuptoolbar(view);
        setuplayout(view);

        return view;
    }

    private void setuptoolbar(View view) {
        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        ImageView home = (ImageView) toolbar.findViewById(R.id.homeButton);
        ImageView icon = (ImageView) toolbar.findViewById(R.id.image_appicon);
        TextView textView = (TextView) toolbar.findViewById(R.id.textviewLogout);
        textView.setVisibility(View.VISIBLE);
        home.setVisibility(View.GONE);
        icon.setVisibility(View.GONE);
        toolbar.setBackgroundColor(ContextCompat.getColor(context, R.color.colorPrimaryTransparent));
    }

    public void setuplayout(View v) {
        post_trip = (ImageView) v.findViewById(R.id.post_trip_image);
        imageViewLogo = (ImageView) v.findViewById(R.id.imageviewLogo);
        search_trip = (ImageView) v.findViewById(R.id.search_split_image);
        post_trip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setupCreateTripFragment();
            }
        });
        search_trip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setupSearchSplitFramgent();
            }
        });
        Glide.with(context)
                .fromResource()
                .asBitmap()
                .encoder(new BitmapEncoder(Bitmap.CompressFormat.PNG, 100))
                .load(R.drawable.trip_splitz_logo).into(imageViewLogo);

    }

    private void setupCreateTripFragment() {
        PostTripFragment searchSplitFragment = new PostTripFragment();
        String tag = searchSplitFragment.getClass().getName();
        replaceFragment(searchSplitFragment, tag);
    }

    private void setupSearchSplitFramgent() {
        SearchSplitFragment searchSplitFragment = new SearchSplitFragment();
        String tag = searchSplitFragment.getClass().getName();
        replaceFragment(searchSplitFragment, tag);
    }

    private void replaceFragment(Fragment newFragment, String tag) {
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame, newFragment, tag);
        transaction.commit();
    }
}
