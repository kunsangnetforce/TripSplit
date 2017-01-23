package com.netforceinfotech.tripsplit.group.searchgroup;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.netforceinfotech.tripsplit.R;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class SearchGroupFragment extends Fragment {


    Context context;
    ArrayList<MyData> myDatas = new ArrayList<>();
    String country = "", city = "", category = "";
    private MyAdapter myAdapter;
    private MaterialDialog progressDialog;

    public SearchGroupFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_search_group, container, false);
        context = getActivity();
        progressDialog = new MaterialDialog.Builder(context)
                .title(R.string.progress_dialog)
                .content(R.string.please_wait)
                .progress(true, 0).build();
        setupRecyclerView(view);
        try {
            country = this.getArguments().getString("country");
            city = this.getArguments().getString("city");
            category = this.getArguments().getString("category");
            searchGroup(country, city, category);

        } catch (Exception ex) {
            showMessage("bundle error");
            Log.i("kunsang_exception", "paramenter not yet set");

        }


        return view;
    }

    private void setupRecyclerView(View view) {
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recycler);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        myAdapter = new MyAdapter(context, myDatas);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(myAdapter);

    }

    private void searchGroup(String country, String city, String category) {
        //http://netforce.biz/tripesplit/mobileApp/api/services.php?opt=search_group
        progressDialog.show();
        String baseURl = getString(R.string.url);
        String url = baseURl + "/services.php?opt=search_group";
        Log.i("url", url);
        Ion.with(getContext())
                .load(url)
                .setBodyParameter("country", country)
                .setBodyParameter("city", city)
                .setBodyParameter("category", category)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        progressDialog.dismiss();
                        if (result == null) {
                            showMessage("Something went wrong. Please Try again");
                        } else {
                            Log.i("result", result.toString());
                            if (result.get("status").getAsString().equalsIgnoreCase("success")) {
                                JsonArray data = result.getAsJsonArray("data");
                                JsonObject jsonObject = data.get(0).getAsJsonObject();
                                JsonArray group = jsonObject.getAsJsonArray("group");
                                setupGroupData(group);
                            }
                        }
                    }
                });
    }

    private void setupGroupData(JsonArray group) {
        Log.i("test", "inside function");
        int size = group.size();
        if (size == 0) {
            showMessage("No group found");
            return;
        }
        for (int i = 0; i < size; i++) {
            Log.i("test", "inside loop begining" + i);
            JsonObject object = group.get(i).getAsJsonObject();
            String group_id = object.get("group_id").getAsString();
            String title = object.get("title").getAsString();
            String city = object.get("city").getAsString();
            String country = object.get("country").getAsString();
            String category_id = object.get("category_id").getAsString();
            String image = object.get("image").getAsString();
            String category_name = object.get("category_name").getAsString();
            MyData myData = new MyData(group_id, title, city, country, category_id, image, category_name);
            if (!myDatas.contains(myData)) {
                myDatas.add(myData);
            }
            Log.i("test", "inside loop end" + i);

        }
        myAdapter.notifyDataSetChanged();

    }

    private void showMessage(String s) {
        Toast.makeText(context, s, Toast.LENGTH_SHORT).show();
    }
}
