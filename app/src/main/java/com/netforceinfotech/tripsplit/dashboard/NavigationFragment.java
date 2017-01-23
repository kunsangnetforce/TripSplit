package com.netforceinfotech.tripsplit.dashboard;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.hedgehog.ratingbar.RatingBar;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.netforceinfotech.tripsplit.home.HomeFragment;
import com.netforceinfotech.tripsplit.mysplitz.MySplitzFragment;
import com.netforceinfotech.tripsplit.profile.editprofile.EditPofileFragment;
import com.netforceinfotech.tripsplit.R;
import com.netforceinfotech.tripsplit.search.SearchSplitFragment;
import com.netforceinfotech.tripsplit.general.UserSessionManager;
import com.netforceinfotech.tripsplit.group.GroupFragment;
import com.netforceinfotech.tripsplit.message.message_title.MessageTitleFragment;
import com.netforceinfotech.tripsplit.mytrip.MyTripFragment;
import com.netforceinfotech.tripsplit.posttrip.PostTripFragment;
import com.netforceinfotech.tripsplit.preference.PreferenceFragment;
import com.netforceinfotech.tripsplit.tutorial.DefaultIntro;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 */
public class NavigationFragment extends Fragment implements RecyclerAdapterDrawer.clickListner, View.OnClickListener {

    public static final String preFile = "textFile";
    public static final String userKey = "key";
    private static final String TAG = "gcm_tag";
    public static int POSITION = 0;
    public static ActionBarDrawerToggle mDrawerToggle;
    public static DrawerLayout mDrawerLayout;
    boolean mUserLearnedDrawer;
    boolean mFromSavedInstance;
    View view;
    RecyclerView recyclerView;
    public static RecyclerAdapterDrawer adapter;
    public static final String PREFS_NAME = "call_recorder";
    private SharedPreferences loginPreferences;
    public static SharedPreferences.Editor loginPrefsEditor;
    public static List<RowDataDrawer> list = new LinkedList<>();
    private Context context;
    CircleImageView imageViewDp;
    TextView textViewName, textviewCountry;
    UserSessionManager userSessionManager;
    RatingBar ratingBar;
    private DatabaseReference _unseen;
    private Boolean newMessage = false;
    private ValueEventListener unseenListner;


    public NavigationFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_navigation, container, false);
        initView();
        getUserInfo();
        setupHomeFragment();
        return view;
    }

    private void getUserInfo() {
        //services.php?opt=viewprofile&user_id=11
        String baseUrl = getString(R.string.url);
        String viewProfile = "services.php?opt=viewprofile&user_id=" + userSessionManager.getUserId();
        String url = baseUrl + viewProfile;
        Ion.with(context)
                .load(url)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        // do stuff with the result or error
                        if (result != null) {
                            Log.i("kresult", result.toString());
                            setupUserData(result);
                        }
                    }
                });
    }

    private void setupUserData(JsonObject result) {
        if (result.get("status").getAsString().equalsIgnoreCase("success")) {
            JsonArray data = result.getAsJsonArray("data");
            JsonObject jsonObject = data.get(0).getAsJsonObject();
            String name = jsonObject.get("firstname").getAsString();
            String email = jsonObject.get("email").getAsString();
            String profile_image = jsonObject.get("profile_image").getAsString();
            userSessionManager.setProfileImage(profile_image);
            userSessionManager.setName(name);
            String dob = jsonObject.get("dob").getAsString();
            String country = jsonObject.get("country").getAsString();
            String rating;
            try{
                rating = jsonObject.get("rating").getAsString();
            }catch (Exception ex){
                rating="3";
            }

            String country_code = jsonObject.get("country_code").getAsString();
            try {
                Glide.with(context).load(profile_image).error(R.drawable.ic_error).into(imageViewDp);
            } catch (Exception ex) {
            }
            if (country_code != null) {
                textviewCountry.setText(country_code);
            } else {
                textviewCountry.setText("");
            }

            textViewName.setText(name);
            float ratingDoube = Float.parseFloat(rating);
            ratingBar.setStar(ratingDoube);

        }
    }

    private void initView() {
        ratingBar = (RatingBar) view.findViewById(R.id.ratingbar);
        ratingBar.setStar(3.5f);
        userSessionManager = new UserSessionManager(context);
        imageViewDp = (CircleImageView) view.findViewById(R.id.imageViewDp);
        textviewCountry = (TextView) view.findViewById(R.id.textviewCountry);
        textViewName = (TextView) view.findViewById(R.id.textviewName);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerMyGroup);
        list = setDrawer();
        adapter = new RecyclerAdapterDrawer(context, list, newMessage);
        adapter.setClickListner(this);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        recyclerView.setNestedScrollingEnabled(true);
        //sharedprefrance
        loginPreferences = getActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        loginPrefsEditor = loginPreferences.edit();
        getUnseenMessage();
    }

    private void getUnseenMessage() {
        _unseen = FirebaseDatabase.getInstance().getReference().child("unseen");
        _unseen.addValueEventListener(setupUnseen());
    }

    private ValueEventListener setupUnseen() {
        return unseenListner = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(userSessionManager.getUserId()).exists()) {
                    newMessage = dataSnapshot.child(userSessionManager.getUserId()).getValue(Boolean.class);
                    Log.i("testing", newMessage + "");
                    adapter.newMessage = newMessage;
                    adapter.notifyDataSetChanged();
                } else {
                    Log.i("testing", dataSnapshot.toString());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
    }


    private List<RowDataDrawer> setDrawer() {
        List<RowDataDrawer> list = new ArrayList<>();

        String title[] = {getString(R.string.home), getString(R.string.preferences), getString(R.string.edit_profile), getString(R.string.invite_friend), getString(R.string.search_spliz), getString(R.string.post_trip), getString(R.string.my_splitz), getString(R.string.my_trip), getString(R.string.messages), getString(R.string.dummy), getString(R.string.group), getString(R.string.how_it_works), getString(R.string.support)};
        int drawableId[];


        drawableId = new int[]
                {
                        R.drawable.ic_home_menu, R.drawable.ic_prefrence, R.drawable.ic_edit_profile, R.drawable.ic_invite_frnd, R.drawable.ic_search_custom, R.drawable.ic_create_trip, R.drawable.my_splitz, R.drawable.my_trip, R.drawable.ic_message,
                        R.drawable.ic_group, R.drawable.ic_group, R.drawable.ic_help, R.drawable.ic_community
                };


        for (int i = 0; i < title.length; i++) {
            RowDataDrawer current = new RowDataDrawer();
            current.text = title[i];
            current.id = drawableId[i];
            list.add(current);

        }


        Log.i("TAG count", list.size() + "");
        return list;
    }


    public static void openDrawer() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
        mUserLearnedDrawer = Boolean.valueOf(readFromPreferences(getActivity(), userKey, "false"));
        if (savedInstanceState != null) {
            mFromSavedInstance = true;
        }
    }

    public void setup(int id, final DrawerLayout drawer, Toolbar toolbar) {
        view = getActivity().findViewById(id);

        mDrawerLayout = drawer;
        mDrawerToggle = new ActionBarDrawerToggle(getActivity(), drawer, toolbar, R.string.drawer_open, R
                .string.drawer_close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                hideSoftKeyboard(getActivity());

                super.onDrawerOpened(drawerView);
                if (!mUserLearnedDrawer) {
                    mUserLearnedDrawer = true;
                    savedInstances(getActivity(), userKey, mUserLearnedDrawer + "");
                }
                getActivity().invalidateOptionsMenu();
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                getActivity().invalidateOptionsMenu();

            }

        };

        mDrawerLayout.addDrawerListener(mDrawerToggle);
        mDrawerLayout.post(new Runnable() {
            @Override
            public void run() {
                mDrawerToggle.syncState();
            }
        });


    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mDrawerLayout.closeDrawers();

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    public static void savedInstances(Context context, String preferenceName, String preferenceValue) {
        SharedPreferences sharePreference = context.getSharedPreferences(preFile, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharePreference.edit();
        editor.putString(preferenceName, preferenceValue);
        editor.apply();
    }

    public static String readFromPreferences(Context context, String preferenceName, String defaultValue) {
        SharedPreferences sharePreference = context.getSharedPreferences(preFile, Context.MODE_PRIVATE);
        return sharePreference.getString(preferenceName, defaultValue);
    }

    @Override
    public void itemClicked(int position) {
        POSITION = position;
        RecyclerAdapterDrawer.selected_item = position;
        adapter.notifyDataSetChanged();
        mDrawerLayout.closeDrawers();
        switch (RecyclerAdapterDrawer.selected_item) {
            case 0:
                setupHomeFragment();
                break;
            case 1:
                setupPreferenceFragment();
                break;
            case 2:
                setupEditProfileFragment();
                break;
            case 3:
                setupInviteFriendFragment();
                break;
            case 4:
                setupSearchSplitFramgent();
                break;
            case 5:
                setupCreateTripFragment();
                break;
            case 6:
                setupMySplitz();
                break;
            case 7:
                setupMyPost();
                break;

            case 8:
                setupMessageFragment();
                break;
            case 9:
                break;
            case 10:
                setupGroupFragment();
                break;
            case 11:
                setupHiWFragment();
                break;
            case 12:
                setupSupportFragment();
                break;
        }


    }

    private void setupMyPost() {
        MyTripFragment groupFragment = new MyTripFragment();
        String tag = groupFragment.getClass().getName();
        replaceFragment(groupFragment, tag);
    }

    private void setupMySplitz() {

        MySplitzFragment groupFragment = new MySplitzFragment();
        String tag = groupFragment.getClass().getName();
        replaceFragment(groupFragment, tag);
    }

    private void setupSupportFragment() {
        showMessage("set up support");
    }

    private void setupHiWFragment() {
        Intent intent = new Intent(context, DefaultIntro.class);
        Bundle bundle = new Bundle();
        bundle.putString("from", "menu");
        intent.putExtras(bundle);
        startActivity(intent);
    }

    private void setupGroupFragment() {
        GroupFragment groupFragment = new GroupFragment();
        String tag = groupFragment.getClass().getName();
        replaceFragment(groupFragment, tag);

    }

    private void setupMessageFragment() {
        MessageTitleFragment searchSplitFragment = new MessageTitleFragment();
        String tag = searchSplitFragment.getClass().getName();
        replaceFragment(searchSplitFragment, tag);
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

    private void setupInviteFriendFragment() {
        shareData();
    }

    private void setupEditProfileFragment() {
        EditPofileFragment editPofileFragment = new EditPofileFragment();
        String tagName = editPofileFragment.getClass().getName();
        replaceFragment(editPofileFragment, tagName);
    }

    public static void hideSoftKeyboard(Activity activity)

    {
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.header:
                showMessage("header");
                break;
        }
    }

    private void showMessage(String clicked) {
        Toast.makeText(context, clicked, Toast.LENGTH_SHORT).show();
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

    private void setupPreferenceFragment() {
        PreferenceFragment preferenceFragment = new PreferenceFragment();
        String tagName = preferenceFragment.getClass().getName();
        replaceFragment(preferenceFragment, tagName);
    }


    private void shareData() {
        String shareBody = "Trip split lorem ispsum";
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
        sendIntent.setType("text/plain");
        startActivity(Intent.createChooser(sendIntent, getResources().getText(R.string.shareit)));
    }
}