package com.netforceinfotech.tripsplit.mysplitz;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.Glide;
import com.chauthai.swipereveallayout.ViewBinderHelper;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.netforceinfotech.tripsplit.R;
import com.netforceinfotech.tripsplit.general.UserSessionManager;
import com.netforceinfotech.tripsplit.search.TripDetailActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by John on 8/29/2016.
 */
public class MyAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int YOU = 0;
    private static final int THEM = 1;
    private final LayoutInflater inflater;
    private final MaterialDialog progressDialog;
    private List<MyData> itemList;
    private Context context;
    UserSessionManager userSessionManager;
    ArrayList<Boolean> booleanGames = new ArrayList<>();
    MyHolder viewHolder;
    // private final ViewBinderHelper viewBinderHelper = new ViewBinderHelper();


    public MyAdapter(Context context, List<MyData> itemList) {
        this.itemList = itemList;
        this.context = context;
        userSessionManager = new UserSessionManager(context);
        progressDialog = new MaterialDialog.Builder(context)
                .title(R.string.progress_dialog)
                .content(R.string.please_wait)
                .progress(true, 0).build();
        inflater = LayoutInflater.from(context);
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = inflater.inflate(R.layout.row_my, parent, false);
        viewHolder = new MyHolder(view);
        return viewHolder;

    }


    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        final MyData dataObject = itemList.get(position);
        final MyHolder myHolder = (MyHolder) holder;
        myHolder.frameDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showConfirmationPopUp(dataObject.trip_id, position);
                myHolder.swipeRevealLayout.close(true);
            }
        });
        myHolder.textViewDate.setText(getFormattedDate(dataObject.departure_date));
        myHolder.textViewDestination.setText(dataObject.destination);
        myHolder.textViewItenerary.setText(dataObject.itinerary);
        myHolder.textViewSource.setText(dataObject.source);
        Glide.with(context).load(dataObject.image).error(R.drawable.ic_picture_box).into(myHolder.imageView);
        myHolder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, TripDetailActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("trip_id", dataObject.trip_id);
                bundle.putBoolean("mysplit", true);
                intent.putExtras(bundle);
                context.startActivity(intent);
            }
        });
    }

    private void showConfirmationPopUp(final String trip_id, final int position) {
        new MaterialDialog.Builder(context)
                .title("Cancel Trip!!!")
                .content("Are you sure you want to Cancel trip?")
                .positiveText("Confirm")
                .negativeText(R.string.cancel)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        cancelTrip(trip_id, position);
                    }
                })
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }

    private void cancelTrip(String trip_id, final int position) {
        //http://netforce.biz/tripesplit/mobileApp/api/services.php?opt=cancel_trip
        String baseUrl = context.getResources().getString(R.string.url);
        String url = baseUrl + "services.php?opt=cancel_trip";
        Log.i("result_url", url);
        progressDialog.show();
        Ion.with(context)
                .load("POST", url)
                .setBodyParameter("trip_id", trip_id)
                .setBodyParameter("user_id", userSessionManager.getUserId())
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        progressDialog.dismiss();
                        if (result == null) {
                            showMessage("nothing is happening");
                        } else {

                            Log.i("result_kunsang", result.toString());
                            String status = result.get("status").getAsString();
                            if (status.equalsIgnoreCase("success")) {
                                showMessage("Cancel trip successfully");
                                removeItem(position);
                            } else {
                                showMessage("something went wrong");
                            }
                        }
                    }
                });

    }


    public String getFormattedDate(String date1) {
        //2017-01-02 00:00:00
        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = null;
        try {
            date = fmt.parse(date1);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        SimpleDateFormat fmtOut = new SimpleDateFormat("dd EEE yyyy HH:hh");
        return fmtOut.format(date);


    }

    private void showMessage(String s) {

        Toast.makeText(context, s, Toast.LENGTH_SHORT).show();
    }


    @Override
    public int getItemCount() {
        return itemList.size();
//        return itemList.size();
    }

    private String getFormetedTime(Long timestamp) {
        SimpleDateFormat sfd = new SimpleDateFormat("hh:mm a");
        return sfd.format(new Date(timestamp));
    }


    public void removeItem(int position) {
        itemList.remove(position);
        notifyDataSetChanged();

    }
}