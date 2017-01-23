package com.netforceinfotech.tripsplit.group;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.mukesh.countrypicker.fragments.CountryPicker;
import com.mukesh.countrypicker.interfaces.CountryPickerListener;
import com.netforceinfotech.tripsplit.dashboard.NavigationFragment;
import com.netforceinfotech.tripsplit.home.HomeFragment;
import com.netforceinfotech.tripsplit.R;
import com.netforceinfotech.tripsplit.general.UserSessionManager;
import com.netforceinfotech.tripsplit.group.mygroup.MyAdapter;
import com.netforceinfotech.tripsplit.group.mygroup.MyData;
import com.netforceinfotech.tripsplit.group.mygroup.MyGroupFragment;
import com.netforceinfotech.tripsplit.group.searchgroup.SearchGroupFragment;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class GroupFragment extends Fragment implements View.OnClickListener {


    RecyclerView recyclerView;
    Context context;
    UserSessionManager userSessionManager;
    ArrayList<MyData> myDatas = new ArrayList<>();
    RelativeLayout relativeLayoutSearch;
    TextView textViewCountry, textViewCategory;
    EditText editTextCity;
    private String currencyCode = "";
    private MaterialDialog progressDialog;
    ArrayList<Category> categories = new ArrayList<>();
    private String selectedCategoryId;
    public static int POSITION = 1;
    static final int MYGROUP = 1;
    static final int SEARCHGROUP = 2;


    public GroupFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_group, container, false);
        context = getActivity();
        NavigationFragment.POSITION = 10;
        userSessionManager = new UserSessionManager(context);
        setuptoolbar();
        initView(view);
        getCategory();
        return view;
    }

    private void getCategory() {
        progressDialog.show();
        //http://netforce.biz/tripesplit/mobileApp/api/services.php?opt=categories
        String baseUrl = getString(R.string.url);
        String url = baseUrl + "services.php?opt=categories";
        Log.i("url", url);
        Ion.with(context)
                .load(url)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        progressDialog.hide();
                        if (result == null) {
                            showMessage("Something went wrong. Please try again");
                        } else {
                            if (result.get("status").getAsString().equalsIgnoreCase("success")) {
                                JsonArray data = result.getAsJsonArray("data");
                                JsonObject object = data.get(0).getAsJsonObject();
                                JsonArray category = object.getAsJsonArray("category");
                                setupCategory(category);
                            }
                        }
                    }
                });

    }

    private void setupCategory(JsonArray category) {
        int size = category.size();
        for (int i = 0; i < size; i++) {
            JsonObject object = category.get(i).getAsJsonObject();
            Category category1 = new Category(object.get("name").getAsString(), object.get("id").getAsString());
            if (!categories.contains(category1)) {
                categories.add(category1);
            }
        }

    }

    private void replaceFragment(Fragment newFragment, String tag) {
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame, newFragment, tag);
        transaction.commit();
    }

    @Override
    public void onResume() {
        setupMyGroupFragment();
        super.onResume();
    }

    public void setupHomeFragment() {
        HomeFragment dashboardFragment = new HomeFragment();
        String tagName = dashboardFragment.getClass().getName();
        replaceFragment(dashboardFragment, tagName);
    }

    public void setupMyGroupFragment() {
        POSITION = MYGROUP;
        MyGroupFragment myGroupFragment = new MyGroupFragment();
        String tagName = myGroupFragment.getClass().getName();
        replaceInnerFragment(myGroupFragment, tagName);
    }

    private void replaceInnerFragment(Fragment newFragment, String tag) {
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.groupFrame, newFragment, tag);
        transaction.commit();
    }

    private void setuptoolbar() {
        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        ImageView home = (ImageView) toolbar.findViewById(R.id.homeButton);
        ImageView icon = (ImageView) toolbar.findViewById(R.id.image_appicon);
        TextView textViewLogout = (TextView) toolbar.findViewById(R.id.textviewLogout);
        textViewLogout.setVisibility(View.GONE);
        home.setVisibility(View.VISIBLE);
        icon.setVisibility(View.VISIBLE);
        toolbar.setBackgroundColor(ContextCompat.getColor(context, R.color.colorPrimary));
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setupHomeFragment();
            }
        });
    }


    private void initView(View view) {
        view.findViewById(R.id.imageViewClose).setOnClickListener(this);
        progressDialog = new MaterialDialog.Builder(context)
                .title(R.string.progress_dialog)
                .content(R.string.please_wait)
                .progress(true, 0).build();
        relativeLayoutSearch = (RelativeLayout) view.findViewById(R.id.relativeLayoutSearch);
        textViewCategory = (TextView) view.findViewById(R.id.textViewCategory);
        textViewCategory.setOnClickListener(this);
        editTextCity = (EditText) view.findViewById(R.id.editTextCity);
        textViewCountry = (TextView) view.findViewById(R.id.textViewCountry);
        textViewCountry.setOnClickListener(this);
        view.findViewById(R.id.buttonSearchGroup).setOnClickListener(this);
        view.findViewById(R.id.textviewSearch).setOnClickListener(this);
        view.findViewById(R.id.imageViewCreateGroup).setOnClickListener(this);
        //    setupRecyclerView(view);
        getGroupChat();
        relativeLayoutSearch.setVisibility(View.GONE);


    }


    private void getGroupChat() {

    }

    private void setupRecyclerView(View view) {
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerMyGroup);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        MyAdapter adapter = new MyAdapter(context, myDatas);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.imageViewClose:
                if (POSITION == SEARCHGROUP) {
                    POSITION = MYGROUP;
                    setupMyGroupFragment();
                }
                relativeLayoutSearch.setVisibility(View.GONE);
                break;
            case R.id.textViewCountry:
                setupCoutryPopUp();
                break;
            case R.id.textViewCategory:
                setupCategoryPopUp();
                break;
            case R.id.textviewSearch:
                relativeLayoutSearch.setVisibility(View.VISIBLE);
                break;
            case R.id.buttonSearchGroup:
                if (textViewCountry.getText().toString().equalsIgnoreCase("country")) {
                    showMessage("Set Country");
                    return;
                }
                if (textViewCategory.getText().toString().equalsIgnoreCase("category") && editTextCity.getText().toString().equalsIgnoreCase("")) {
                    showMessage("Set Category or city");
                    return;
                }

                searchGroup(textViewCountry.getText().toString(), editTextCity.getText().toString(), selectedCategoryId);
                break;
            case R.id.imageViewCreateGroup:
                Intent intent = new Intent(context, CreateGroupActivity.class);
                startActivity(intent);
                break;
        }

    }

    private void searchGroup(String country, String city, String selectedCategoryId) {
        setupSearchGroupFragment(country, city, selectedCategoryId);
    }

    private void setupSearchGroupFragment(String country, String city, String selectedCategoryId) {
        POSITION = SEARCHGROUP;
        SearchGroupFragment searchGroupFragment = new SearchGroupFragment();
        String tagName = searchGroupFragment.getClass().getName();
        Bundle bundle = new Bundle();
        bundle.putString("country", country);
        bundle.putString("city", city);
        bundle.putString("category", selectedCategoryId);
        searchGroupFragment.setArguments(bundle);
        replaceInnerFragment(searchGroupFragment, tagName);
    }


    private void setupCategoryPopUp() {
        ArrayList<String> categoryNames = new ArrayList<>();
        for (int i = 0; i < categories.size(); i++) {
            categoryNames.add(categories.get(i).name);
        }
        new MaterialDialog.Builder(context)
                .title(R.string.choose_category)
                .titleColor(ContextCompat.getColor(context, R.color.colorPrimary))
                .items(categoryNames)
                .itemsColor(ContextCompat.getColor(context, R.color.colorPrimary))
                .negativeText(getString(R.string.cancel))
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        textViewCategory.setText("Category");
                        dialog.dismiss();
                    }
                })
                .itemsCallback(new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                        textViewCategory.setText(categories.get(which).name);
                        selectedCategoryId = categories.get(which).id;
                    }
                })
                .show();
    }

    private void setupCoutryPopUp() {
        final CountryPicker picker = CountryPicker.newInstance("Select Country");
        picker.show(getChildFragmentManager(), "COUNTRY_PICKER");
        picker.setListener(new CountryPickerListener() {
            @Override
            public void onSelectCountry(String name, String code, String dialCode, int flagDrawableResID) {
                // Implement your code here
                currencyCode = code;
                textViewCountry.setText(name);
                picker.dismiss();

            }
        });
    }

    private void showMessage(String string) {
        Toast.makeText(context, string, Toast.LENGTH_SHORT).show();
    }
}
