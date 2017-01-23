package com.netforceinfotech.tripsplit.search.searchfragment;


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
import com.netforceinfotech.tripsplit.general.UserSessionManager;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 */
public class SearchListViewFragment extends Fragment {

    UserSessionManager userSessionManager;
    ArrayList<CarData> carDatas = new ArrayList<>();
    CarAdapter adapter;
    Context context;
    double dest_lat, dest_lon, source_lat, source_lon;
    String etd, type, sort;
    private MaterialDialog progressDialog;
    private String timezone;

    public SearchListViewFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_search_list_view, container, false);
        context = getActivity();
        userSessionManager = new UserSessionManager(context);
        dest_lat = getArguments().getDouble("dest_lat");
        dest_lon = getArguments().getDouble("dest_lon");
        source_lat = getArguments().getDouble("source_lat");
        source_lon = getArguments().getDouble("source_lon");
        etd = getArguments().getString("etd");
        type = getArguments().getString("type");
        sort = getArguments().getString("sort");
        Calendar cal = Calendar.getInstance();
        timezone = cal.getTimeZone().getID();
        initView(view);
        setupRecyclerView(view);
        searchTrip();
        return view;
    }

    private void initView(View view) {
        progressDialog = new MaterialDialog.Builder(context)
                .title(R.string.progress_dialog)
                .content(R.string.please_wait)
                .progress(true, 0).build();
    }

    private String getDateWithTimezone(String date1) {
        //Mon 02 Jan 2017 16:04
        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;
        try {
            date = fmt.parse(date1);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        SimpleDateFormat fmtOut = new SimpleDateFormat("yyyy-MM-dd");
        return fmtOut.format(date);
    }


    private void setupRecyclerView(View view) {
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recyclerMyGroup);
        recyclerView.setNestedScrollingEnabled(false);
        adapter = new CarAdapter(context, carDatas);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);
    }

    private void setupData(JsonArray data1) {
        int size = data1.size();
        if (size == 0) {
            showMessage(getString(R.string.no_split_found));
        }
        for (int i = 0; i < size; i++) {
            try {
                JsonObject object = data1.get(i).getAsJsonObject();

                String tour_id = object.get("tour_id").getAsString();
                String start_price = object.get("start_price").getAsString();
                String date_created = object.get("date_created").getAsString();
                String user_id = object.get("user_id").getAsString();
                String cartype = object.get("cartype").getAsString();
                String pax = object.get("pax").getAsString();
                String space = object.get("space").getAsString();
                String trip_group = object.get("trip_group").getAsString();
                String age_group_lower = object.get("age_group_lower").getAsString();
                String age_group_upper = object.get("age_group_upper").getAsString();
                String trip = object.get("trip").getAsString();
                String vehical_type = object.get("vehical_type").getAsString();
                String depart_address = object.get("depart_address").getAsString();
                String country_code = object.get("country_code").getAsString();
                String etd = object.get("etd").getAsString();
                String eta = object.get("eta").getAsString();
                String iteinerary = object.get("iteinerary").getAsString();
                String image = object.get("image").getAsString();
                String currency = object.get("currency").getAsString();
                String depart_lat = object.get("depart_lat").getAsString();
                String depart_lon = object.get("depart_lon").getAsString();
                String return_eta = object.get("return_eta").getAsString();
                String return_etd = object.get("return_etd").getAsString();
                String dest_address = object.get("dest_address").getAsString();
                String dest_lat = object.get("dest_lat").getAsString();
                String dest_lon = object.get("dest_lon").getAsString();
                CarData carData = new CarData(tour_id, start_price, date_created, user_id, cartype, pax, space, trip_group, age_group_lower, age_group_upper, trip, vehical_type, depart_address, country_code, etd, eta, iteinerary, image, currency, depart_lat, depart_lon, return_eta, return_etd, dest_address, dest_lat, dest_lon);
                if (!carDatas.contains(carData)) {
                    carDatas.add(carData);
                }
            } catch (Exception ex) {
                Log.i("position", i + "");
                ex.printStackTrace();
            }
        }
        adapter.notifyDataSetChanged();
    }

    private void showMessage(String string) {
        Toast.makeText(context, string, Toast.LENGTH_SHORT).show();
    }

    private void searchTrip() {
        /*
        * source_lat=55.946302847171445&source_lon=-3.1891679763793945
        * &dest_lat=28.599072519302414&dest_lon=77.32198219746351&etd=2016-05-11&range=4000&type=car
        * */
        progressDialog.show();

        String baseUrl = getString(R.string.url);
        //http://netforce.biz/tripesplit/mobileApp/api/services.php?opt=search_trip
        String url = baseUrl + "services.php?opt=search_trip";
        Log.i("url", url);
        Log.i("type1", type);
        Log.i("parameter", dest_lat + " : " + dest_lon + " : " + source_lat + " : " + source_lon + " : " + etd + " : " + userSessionManager.getSearchRadius() + " : " + sort + " : " + type);
        Ion.with(context)
                .load("POST", url)
                .setBodyParameter("dest_lat", dest_lat + "")
                .setBodyParameter("dest_lon", dest_lon + "")
                .setBodyParameter("source_lat", source_lat + "")
                .setBodyParameter("source_lon", source_lon + "")
                .setBodyParameter("etd", getDateWithTimezone(etd))
                .setBodyParameter("range", userSessionManager.getSearchRadius() + "")
                .setBodyParameter("sort", sort)
                .setBodyParameter("type", type)
                .setBodyParameter("timezone", timezone)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        progressDialog.dismiss();
                        // do stuff with the result or error
                        if (result == null) {
                            showMessage("something wrong");
                        } else {
                            if (result.get("status").getAsString().equalsIgnoreCase("success")) {
                                Log.i("result", result.toString());

                                JsonArray data = result.getAsJsonArray("data");
                                setupData(data);
                            }

                        }
                    }
                });
    }

    public void clearData() {
        carDatas.clear();
        adapter.notifyDataSetChanged();
    }


}
