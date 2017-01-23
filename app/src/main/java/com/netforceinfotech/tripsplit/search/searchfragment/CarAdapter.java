package com.netforceinfotech.tripsplit.search.searchfragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.netforceinfotech.tripsplit.R;
import com.netforceinfotech.tripsplit.search.TripDetailActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by John on 8/29/2016.
 */
public class CarAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int SIMPLE_TYPE = 0;
    private static final int IMAGE_TYPE = 1;
    private final LayoutInflater inflater;
    private List<CarData> itemList;
    private Context context;
    ArrayList<Boolean> booleanGames = new ArrayList<>();
    CarHolder viewHolder;


    public CarAdapter(Context context, List<CarData> itemList) {
        this.itemList = itemList;
        this.context = context;
        inflater = LayoutInflater.from(context);
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.row_search, parent, false);
        viewHolder = new CarHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        CarHolder myHolder = (CarHolder) holder;
        final CarData carData = itemList.get(position);
        if (carData.trip.equalsIgnoreCase("1")) {
            myHolder.linearLayoutReturn.setVisibility(View.VISIBLE);
            myHolder.textViewDestination_return.setText(carData.depart_address);
            myHolder.textViewSource_return.setText(carData.dest_address);
            myHolder.textViewETD_date_return.setText(getFormatedDate(carData.return_etd));
            myHolder.textViewETD_time_return.setText(getFormetedTime(carData.return_etd));
            myHolder.textViewETA_return.setText(getFormetedTime(carData.return_eta));
            myHolder.textViewJourneyTime_return.setText(getFormattedTimeDiff(carData.return_eta, carData.return_etd));

        } else {
            myHolder.linearLayoutReturn.setVisibility(View.GONE);
        }

        myHolder.textViewPrice.setText(carData.currency + " " + carData.start_price);
        myHolder.textViewPax.setText(carData.pax);
        myHolder.textViewCarType.setText(carData.vehical_type);
        myHolder.textViewSource.setText(carData.depart_address);
        myHolder.textViewDestination.setText(carData.dest_address);
        String etd_date = getFormatedDate(carData.etd);
        myHolder.textViewETD_date.setText(etd_date);
        String etd_time = getFormetedTime(carData.etd);
        myHolder.textViewETD_time.setText(etd_time);
        String eta_time = getFormetedTime(carData.eta);
        myHolder.textViewETA.setText(eta_time);
        String timeDiff = getFormattedTimeDiff(carData.eta, carData.etd);
        myHolder.textViewJourneyTime.setText(timeDiff);
        myHolder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, TripDetailActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("trip_id", carData.tour_id);
                intent.putExtras(bundle);
                context.startActivity(intent);
            }
        });
    }

    private void showMessage(String s) {

        Toast.makeText(context, s, Toast.LENGTH_SHORT).show();
    }

    private String getFormattedTimeDiff(String eta, String etd) {
        Date d1 = null;
        Date d2 = null;
        SimpleDateFormat format = new SimpleDateFormat("EEE dd MMM yyyy HH:mm");

        try {
            d1 = format.parse(etd);
            d2 = format.parse(eta);

            //in milliseconds
            long diff = d2.getTime() - d1.getTime();

            long diffSeconds = diff / 1000 % 60;
            long diffMinutes = diff / (60 * 1000) % 60;
            long diffHours = diff / (60 * 60 * 1000) % 24;
            long diffDays = diff / (24 * 60 * 60 * 1000);
            String timeDiff = diffDays + "D," + diffHours + "H " + diffMinutes + "m";
            return timeDiff;
        } catch (Exception e) {
            e.printStackTrace();
            return "NA";
        }
    }

    private String getFormetedTime(String etd) {
        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = null;
        try {
            date = fmt.parse(etd);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        SimpleDateFormat fmtOut = new SimpleDateFormat("HH:mm");
        return fmtOut.format(date);
    }

    private String getFormatedDate(String etd) {
        //Fri 06, Jan 2017 17:32 +05:30
        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = null;
        try {
            date = fmt.parse(etd);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        SimpleDateFormat fmtOut = new SimpleDateFormat("EEE dd, MMM yyyy");
        return fmtOut.format(date);
    }

    @Override
    public int getItemCount() {
        return itemList.size();
//        return itemList.size();
    }


}