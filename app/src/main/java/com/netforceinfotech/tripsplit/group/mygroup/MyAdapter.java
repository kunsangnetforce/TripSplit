package com.netforceinfotech.tripsplit.group.mygroup;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.netforceinfotech.tripsplit.R;
import com.netforceinfotech.tripsplit.group.groupchat.GroupChatActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by John on 8/29/2016.
 */
public class MyAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int SIMPLE_TYPE = 0;
    private static final int IMAGE_TYPE = 1;
    private final LayoutInflater inflater;
    private List<MyData> itemList;
    private Context context;
    ArrayList<Boolean> booleanGames = new ArrayList<>();
    MyHolder viewHolder;


    public MyAdapter(Context context, List<MyData> itemList) {
        this.itemList = itemList;
        this.context = context;
        inflater = LayoutInflater.from(context);
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.row_mygroup, parent, false);
        viewHolder = new MyHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        MyHolder myHolder = (MyHolder) holder;
        myHolder.textViewTime.setText(getFormetedTime(itemList.get(position).timestamp));
        myHolder.textViewTitle.setText(itemList.get(position).title);
        myHolder.textViewCountry.setText(itemList.get(position).country);
        myHolder.textViewCity.setText(itemList.get(position).city);
        myHolder.textViewCategory.setText(itemList.get(position).category);
        try {
            Log.i("resultimage",itemList.get(position).image_url);
            Glide.with(context).load(itemList.get(position).image_url).placeholder(R.drawable.ic_picture_box).error(R.drawable.ic_picture_box).into(myHolder.imageView);
        } catch (Exception ex) {

        }
        myHolder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, GroupChatActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("group_id", itemList.get(position).key);
                bundle.putString("title", itemList.get(position).title);
                intent.putExtras(bundle);
                context.startActivity(intent);
            }
        });


    }

    private String getFormetedTime(Long timestamp) {
        SimpleDateFormat sfd = new SimpleDateFormat("dd-MM-yyyy");
        return sfd.format(new Date(timestamp));
    }

    private void showMessage(String s) {

        Toast.makeText(context, s, Toast.LENGTH_SHORT).show();
    }


    @Override
    public int getItemCount() {
        return itemList.size();
//        return itemList.size();
    }

    private String getFormetedTime(String etd) {
        SimpleDateFormat fmt = new SimpleDateFormat("EEE dd MMM yyyy HH:mm");
        Date date = null;
        try {
            date = fmt.parse(etd);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        SimpleDateFormat fmtOut = new SimpleDateFormat("hh:mm a");
        return fmtOut.format(date);
    }


}