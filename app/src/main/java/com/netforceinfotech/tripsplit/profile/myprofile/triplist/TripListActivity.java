package com.netforceinfotech.tripsplit.profile.myprofile.triplist;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.netforceinfotech.tripsplit.R;
import com.netforceinfotech.tripsplit.general.UserSessionManager;

import java.util.ArrayList;

public class TripListActivity extends AppCompatActivity {

    Context context;
    RecyclerView recyclerView;
    MyAdapter myAdapter;
    ArrayList<MyData> myDatas = new ArrayList<>();
    UserSessionManager userSessionManager;
    TextView textViewNotFound;

    String id;
    private MaterialDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_list);
        Bundle bundle = getIntent().getExtras();
        context = this;
        userSessionManager = new UserSessionManager(context);
        setupToolBar("Trip List");
        id = bundle.getString("id");
        initView();
        getTrips(id);
        setupRecyclerView();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    private void initView() {
        textViewNotFound= (TextView) findViewById(R.id.textViewNoTrip);
        progressDialog = new MaterialDialog.Builder(context)
                .title(R.string.progress_dialog)
                .content(R.string.please_wait)
                .progress(true, 0).build();
    }

    private void getTrips(String id) {
        progressDialog.show();

        String baseUrl = getString(R.string.url);
        //http://netforce.biz/tripesplit/mobileApp/api/services.php?opt=search_trip
        String url = baseUrl + "services.php?opt=my_trip";
        Log.i("url", url);
        Ion.with(context)
                .load("POST", url)
                .setBodyParameter("user_id", id)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        progressDialog.dismiss();
                        // do stuff with the result or error
                        if (result == null) {
                            showMessage("something wrong");
                        } else {
                            Log.i("result", result.toString());
                            if (result.get("status").getAsString().equalsIgnoreCase("success")) {
                                JsonArray data = result.getAsJsonArray("data");
                                setupMytrip(data);
                            }
                        }
                    }
                });
    }

    private void setupMytrip(JsonArray data) {
        JsonObject object = data.get(0).getAsJsonObject();
        JsonArray my_splitz = object.getAsJsonArray("my_splitz");
        int size = my_splitz.size();
        if (size <= 0) {
            textViewNotFound.setVisibility(View.VISIBLE);
            return;
        }
        textViewNotFound.setVisibility(View.GONE);
        for (int i = 0; i < size; i++) {
            JsonObject jsonObject = my_splitz.get(i).getAsJsonObject();

            String trip_id = jsonObject.get("trip_id").getAsString();
            String image = jsonObject.get("image").getAsString();
            String source = jsonObject.get("source").getAsString();
            String destination = jsonObject.get("destination").getAsString();
            String departure_date = jsonObject.get("departure_date").getAsString();
            String itinerary = jsonObject.get("itinerary").getAsString();
            MyData myData = new MyData(trip_id, image, source, destination, departure_date, itinerary);
            if (!myDatas.contains(myData)) {
                myDatas.add(myData);
            }
        }
        myAdapter.notifyDataSetChanged();
    }

    private void showMessage(String s) {
        Toast.makeText(context, s, Toast.LENGTH_SHORT).show();
    }

    private void setupToolBar(String app_name) {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(app_name);
    }

    private void setupRecyclerView() {
        recyclerView = (RecyclerView) findViewById(R.id.recycler);
        recyclerView.setNestedScrollingEnabled(false);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        myAdapter = new MyAdapter(context, myDatas);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(myAdapter);
        recyclerView.smoothScrollToPosition(myDatas.size());
    }
}
