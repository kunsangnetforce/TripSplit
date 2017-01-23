package com.netforceinfotech.tripsplit.NavigationView.message.mysplit;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.netforceinfotech.tripsplit.R;

import java.util.ArrayList;


public class MySplitFragment extends Fragment
{

    private RecyclerView recyclerView;
    Context context;
    private LinearLayoutManager layoutManager;
    private MySplitAdapter adapter;
    FrameLayout messageLayout;
    int theme;
    View view;


  ArrayList<MySplitFragmentData>  mySplitFragmentsData = new ArrayList<>();

    public MySplitFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_my_split, container, false);

        context = getActivity();

        setuptoolbar(view);

        setupRecyclerView(view);

        return view;

    }



    private void setuptoolbar(View view)
    {
        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);

        ImageView home = (ImageView) getActivity().findViewById(R.id.homeButton);

        ImageView icon = (ImageView) getActivity().findViewById(R.id.image_appicon);
        home.setVisibility(View.VISIBLE);
        icon.setVisibility(View.VISIBLE);

        toolbar.setBackgroundColor(getResources().getColor(R.color.colorPrimary));

    }


    private void setupRecyclerView(View view)
    {
        messageLayout = (FrameLayout) view.findViewById(R.id.messagelayout);

        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerMyGroup);
        layoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);

        recyclerView.setLayoutManager(layoutManager);
        adapter = new MySplitAdapter(context, mySplitFragmentsData);
        recyclerView.setAdapter(adapter);
        setupFinsihedDatas();
        adapter.notifyDataSetChanged();


    }

    private void setupFinsihedDatas()
    {
        try
        {
            mySplitFragmentsData.clear();
        }
        catch (Exception ex) {

        }
        mySplitFragmentsData.add(new MySplitFragmentData("Tea", "imageurl"));
        mySplitFragmentsData.add(new MySplitFragmentData("Tea", "imageurl"));
        mySplitFragmentsData.add(new MySplitFragmentData("Tea", "imageurl"));
        mySplitFragmentsData.add(new MySplitFragmentData("Tea", "imageurl"));
        mySplitFragmentsData.add(new MySplitFragmentData("Tea", "imageurl"));
        mySplitFragmentsData.add(new MySplitFragmentData("Tea", "imageurl"));
        mySplitFragmentsData.add(new MySplitFragmentData("Tea", "imageurl"));
        mySplitFragmentsData.add(new MySplitFragmentData("Tea", "imageurl"));
        mySplitFragmentsData.add(new MySplitFragmentData("Tea", "imageurl"));
        mySplitFragmentsData.add(new MySplitFragmentData("Tea", "imageurl"));
        mySplitFragmentsData.add(new MySplitFragmentData("Tea", "imageurl"));
        mySplitFragmentsData.add(new MySplitFragmentData("Tea", "imageurl"));


    }



}
