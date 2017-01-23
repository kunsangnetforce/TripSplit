package com.netforceinfotech.tripsplit.profile.myprofile.triplist;

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
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.netforceinfotech.tripsplit.R;
import com.netforceinfotech.tripsplit.general.UserSessionManager;
import com.netforceinfotech.tripsplit.search.TripDetailActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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

        View view = inflater.inflate(R.layout.row_triplist, parent, false);
        viewHolder = new MyHolder(view);
        return viewHolder;

    }


    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        final MyData dataObject = itemList.get(position);
        final MyHolder myHolder = (MyHolder) holder;

        myHolder.textViewDate.setText(getFormattedDate(dataObject.departure_date));
        myHolder.textViewDestination.setText(dataObject.destination);
        myHolder.textViewItenerary.setText(dataObject.itinerary);
        myHolder.textViewSource.setText(dataObject.source);
        Glide.with(context).load(dataObject.image).error(R.drawable.ic_error).into(myHolder.imageView);
        myHolder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, TripDetailActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("trip_id", dataObject.trip_id);
                intent.putExtras(bundle);
                context.startActivity(intent);
            }
        });
    }

    public String getFormattedDate(String date1) {
        //2017-01-02 00:00:00
        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss" +
                "");
        Date date = null;
        try {
            date = fmt.parse(date1);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        SimpleDateFormat fmtOut = new SimpleDateFormat("dd EEE yyyy HH:hh");
        return fmtOut.format(date);


    }

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