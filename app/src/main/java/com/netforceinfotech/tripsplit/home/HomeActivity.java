package com.netforceinfotech.tripsplit.home;

import android.content.Intent;

import android.support.design.widget.NavigationView;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;

import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;

import android.widget.ImageView;
import android.widget.Toast;

import com.netforceinfotech.tripsplit.NavigationView.message.MessageFragment;

import com.netforceinfotech.tripsplit.R;

import com.netforceinfotech.tripsplit.posttrip.PostTripFragment;

public class HomeActivity extends AppCompatActivity
{

    MessageFragment messageFragment;
    HomeFragment homeFragment;

    ImageView post_trip,search_trip;
    private String imageURL, tagName;
    DrawerLayout drawer;
    NavigationView navigationView;



    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_home2);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);


        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

         navigationView = (NavigationView) findViewById(R.id.nav_view);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            // This method will trigger on item Click of navigation menu
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {

                //Checking if the item is in checked state or not, if not make it in checked state
                if (menuItem.isChecked()) menuItem.setChecked(false);
                else menuItem.setChecked(true);

                //Closing drawer on item click
                drawer.closeDrawers();

                //Check to see which item was being clicked and perform appropriate action
                switch (menuItem.getItemId()) {

/*

                    case R.id.nav_preferences:
                        setupDashboardFragment();
                        return true;
                    case R.id.nav_message:
                        setupMessageFragment();
                        return true;
*/

                    default:
                        Toast.makeText(getApplicationContext(), "Somethings Wrong", Toast.LENGTH_SHORT).show();
                        return true;

                }
            }
        });


        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            @Override
            public void onDrawerClosed(View v) {

                super.onDrawerClosed(v);
            }

            @Override
            public void onDrawerOpened(View v) {
                super.onDrawerOpened(v);
            }

        };

        drawer.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        setupDashboardFragment();

    }



    private void showMessage(String s) {
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }

    private void setupDashboardFragment()
    {
        if (homeFragment == null)
        {
            homeFragment = new HomeFragment();
        }

        tagName = homeFragment.getClass().getName();
        replaceFragment(homeFragment, tagName);

    }

    private void setupMessageFragment()
    {
        messageFragment = new MessageFragment();
        tagName = messageFragment.getClass().getName();
        replaceFragment(messageFragment, tagName);

    }



    private void replaceFragment(Fragment newFragment, String tag)
    {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.drawer_layout, newFragment, tag);
        transaction.commit();
    }

    private void setuplayout()
    {

        post_trip = (ImageView) findViewById(R.id.post_trip_image);

        search_trip = (ImageView) findViewById(R.id.search_split_image);

        post_trip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(HomeActivity.this, PostTripFragment.class);

                startActivity(i);
            }
        });

        search_trip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(HomeActivity.this, PostTripFragment.class);

                startActivity(i);
            }
        });
    }



}
